package com.dzinemedia.ownerpropertyfyapp.fragments.rentalHistoryDetailsFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.activities.ReceivedStatusDetailActivity
import com.dzinemedia.ownerpropertyfyapp.adapters.ReceivedRentalPaymentAdapter
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.ReceivedItemCallback
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentReceivedBinding
import com.dzinemedia.ownerpropertyfyapp.models.NotificationModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview.RentalPlan
import com.dzinemedia.ownerpropertyfyapp.viewModel.ShareViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReceivedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReceivedFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentReceivedBinding
    private var receivedRentalPaymentAdapter: ReceivedRentalPaymentAdapter? = null
    private val rentalPaymentList: ArrayList<RentalPlan> = ArrayList()
    private var isFragmentVisible = false
    private lateinit var sharedViewModel: ShareViewModel
    private val TAG = "ReceivedFragment"
    private var isItemClicked = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    companion object {
        fun newInstance(): ReceivedFragment {
            val fragment = ReceivedFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_received, container, false
        )
        initializeControls()
        return binding.root
    }

    private fun initializeControls() {
        sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
        receivedRentalPaymentAdapter =
            ReceivedRentalPaymentAdapter(rentalPaymentList, object : ReceivedItemCallback {
                override fun receivedItemClick(position: Int, rentalPlan: RentalPlan) {
                    if (isItemClicked) {
                        isItemClicked = false
                        val intent =
                            Intent(requireActivity(), ReceivedStatusDetailActivity::class.java)
                        intent.putExtra("propertyId", rentalPlan.propertyId?.propertyId)
                        intent.putExtra("rentalId", rentalPlan.rentalId)
                        intent.putExtra("rentalPaymentId", rentalPlan.id)
                        intent.putExtra("depositBank", rentalPlan.getDepositBank())
                        val options = ActivityOptionsCompat.makeCustomAnimation(
                            requireActivity(),
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        startActivity(intent, options.toBundle())
                    }
                    Handler(Looper.getMainLooper()).postDelayed({
                        isItemClicked = true
                    }, 1000)
                }
            })
        val layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        binding.receivedRecyclerview.layoutManager = layoutManager
        binding.receivedRecyclerview.adapter = receivedRentalPaymentAdapter
    }

    private fun getReceivedData() {
        if (isFragmentVisible) {
            sharedViewModel.getRentalPlanList().observe(viewLifecycleOwner) {
                it?.let {
                    val rentalList: ArrayList<RentalPlan> = ArrayList()
                    for (i in 0 until it.size) {
                        Log.i(TAG, "initializeControls: ${it[i].paymentStatus}")
                        if (it[i].paymentType.equals(
                                "installment",
                                ignoreCase = true
                            ) && it[i].paymentStatus.equals("received", ignoreCase = true)
                        ) {
                            rentalList.add(it[i])
                        }
                    }
                    getReceivedFrgData(rentalList)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getReceivedFrgData(rentalList: ArrayList<RentalPlan>) {
        rentalPaymentList.clear()
        rentalPaymentList.addAll(rentalList)
        if (rentalPaymentList.size > 0) {
            binding.noData.visibility = View.GONE
        } else {
            binding.noData.visibility = View.VISIBLE
        }
        receivedRentalPaymentAdapter?.notifyDataSetChanged()
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        isFragmentVisible = menuVisible
        if (menuVisible) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                getReceivedData()
            }
        }
    }
}