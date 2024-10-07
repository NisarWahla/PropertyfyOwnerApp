package com.dzinemedia.ownerpropertyfyapp.fragments.rentalHistoryDetailsFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.activities.OverdueStatusDetailActivity
import com.dzinemedia.ownerpropertyfyapp.adapters.OverdueRentalPaymentAdapter
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.OverdueItemCallback
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentOverdueBinding
import com.dzinemedia.ownerpropertyfyapp.models.NotificationModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview.RentalPlan
import com.dzinemedia.ownerpropertyfyapp.utility.Utils
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
 * Use the [OverdueFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OverdueFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentOverdueBinding
    private var overdueRentalPaymentAdapter: OverdueRentalPaymentAdapter? = null
    private val rentalPaymentList: ArrayList<RentalPlan> = ArrayList()
    private var isFragmentVisible = false
    private lateinit var sharedViewModel: ShareViewModel
    private var isItemClicked = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    companion object {
        fun newInstance(): OverdueFragment {
            val fragment = OverdueFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_overdue, container, false
        )
        initializeControls()
        return binding.root
    }
    private fun initializeControls(){
        sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
        overdueRentalPaymentAdapter =
            OverdueRentalPaymentAdapter(rentalPaymentList, object : OverdueItemCallback {
                override fun overdueItemClick(position: Int, rentalPlanModel: RentalPlan) {
                    if (isItemClicked) {
                        isItemClicked = false
                        val intent =
                            Intent(binding.root.context, OverdueStatusDetailActivity::class.java)
                        intent.putExtra("propertyId", rentalPlanModel.propertyId?.propertyId)
                        intent.putExtra("rentalId", rentalPlanModel.rentalId)
                        intent.putExtra("rentalPaymentId", rentalPlanModel.id)
                        intent.putExtra("depositBank", rentalPlanModel.getDepositBank())
                        intent.putExtra("paymentStatus", rentalPlanModel.paymentStatus)
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
        binding.overdueRecyclerview.layoutManager = layoutManager
        binding.overdueRecyclerview.adapter = overdueRentalPaymentAdapter
    }

    private fun getOverdueData() {
        if (activity != null && isFragmentVisible) {
            sharedViewModel.getRentalPlanList().observe(viewLifecycleOwner) {
                it?.let {
                    val rentalList: ArrayList<RentalPlan> = ArrayList()
                    for (i in 0 until it.size) {
                        if (it[i].paymentType.equals("installment", ignoreCase = true) && (it[i].paymentStatus.equals("bounced", ignoreCase = true) || it[i].paymentStatus.equals("overdue", ignoreCase = true))
                        ) {
                            rentalList.add(it[i])
                        }
                    }
                    getOverdueFrgData(rentalList)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getOverdueFrgData(rentalList: ArrayList<RentalPlan>) {
        rentalPaymentList.clear()
        rentalPaymentList.addAll(rentalList)
        if (rentalPaymentList.size > 0) {
            binding.noData.visibility = View.GONE
        } else {
            binding.noData.visibility = View.VISIBLE
        }
        overdueRentalPaymentAdapter?.notifyDataSetChanged()
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        isFragmentVisible = menuVisible
        if (menuVisible) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                getOverdueData()
            }
        }
    }
}