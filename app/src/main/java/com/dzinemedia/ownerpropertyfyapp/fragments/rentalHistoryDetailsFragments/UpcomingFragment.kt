package com.dzinemedia.ownerpropertyfyapp.fragments.rentalHistoryDetailsFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.activities.RentalHistoryDetailActivity
import com.dzinemedia.ownerpropertyfyapp.adapters.UpcomingRentalPaymentAdapter
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.UpcomingItemCallback
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentUpcomingBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyOverview.RentalPayment
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
 * Use the [UpcomingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpcomingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentUpcomingBinding
    private var rentalPaymentDetailAdapter: UpcomingRentalPaymentAdapter? = null
    private val rentalPaymentList: ArrayList<RentalPlan> = ArrayList()
    private var isFragmentVisible = false
    private val TAG = "UpcomingFragment"
    private lateinit var sharedViewModel: ShareViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    companion object {
        fun newInstance(): UpcomingFragment {
            val fragment = UpcomingFragment()
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
            inflater, R.layout.fragment_upcoming, container, false
        )
        initializeControls()
        return binding.root
    }

    private fun initializeControls() {
        sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
        rentalPaymentDetailAdapter = UpcomingRentalPaymentAdapter(rentalPaymentList, object :
            UpcomingItemCallback {
            override fun upcomingItemClick(position: Int, rentalPlan: RentalPlan) {
                val intent = Intent(
                    binding.root.context,
                    RentalHistoryDetailActivity::class.java
                )
                intent.putExtra("propertyId", rentalPlan.propertyId?.propertyId)
                intent.putExtra("rentalId", rentalPlan.rentalId)
                intent.putExtra("rentalPaymentId", rentalPlan.id)
                intent.putExtra("depositBank", rentalPlan.getDepositBank())
                intent.putExtra("paymentStatus", rentalPlan.paymentStatus)
                startActivity(intent)
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        })
        val layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        binding.rentalsRecyclerview.layoutManager = layoutManager
        binding.rentalsRecyclerview.adapter = rentalPaymentDetailAdapter
        getUpcomingData()
    }

    private fun getUpcomingData() {
        sharedViewModel.getRentalPlanList().observe(viewLifecycleOwner) {
            it?.let {
                val rentalList: ArrayList<RentalPlan> = ArrayList()
                for (i in 0 until it.size) {
                    if (it[i].paymentType.equals(
                            "installment",
                            ignoreCase = true
                        ) && (it[i].paymentStatus.equals(
                            "pending",
                            ignoreCase = true
                        ) || it[i].paymentStatus.equals("bank_deposit", ignoreCase = true))
                    ) {
                        rentalList.add(it[i])
                    }
                }
                getUpcomingFrgData(rentalList)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getUpcomingFrgData(rentalList: ArrayList<RentalPlan>) {
        rentalPaymentList.clear()
        rentalPaymentList.addAll(rentalList)
        if (rentalPaymentList.size > 0) {
            binding.noData.visibility = View.GONE
        } else {
            binding.noData.visibility = View.VISIBLE
        }
        rentalPaymentDetailAdapter?.notifyDataSetChanged()
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        isFragmentVisible = menuVisible
        if (menuVisible) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                getUpcomingData()
            }
        }
    }
}