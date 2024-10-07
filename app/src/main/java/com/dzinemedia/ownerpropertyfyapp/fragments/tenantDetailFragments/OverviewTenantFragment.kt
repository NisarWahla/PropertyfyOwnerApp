package com.dzinemedia.ownerpropertyfyapp.fragments.tenantDetailFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.activities.RentalHistoryDetailActivity
import com.dzinemedia.ownerpropertyfyapp.activities.RentalsPaymentsDetailActivity
import com.dzinemedia.ownerpropertyfyapp.adapters.OverviewTenantContractHistoryAdapter
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.OverviewOwnerTenantItemCallback
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentOverviewTenantBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.tenantOverviewModel.RentalPlanX
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.tenantOverviewModel.TenantOverviewModel
import com.dzinemedia.ownerpropertyfyapp.viewModel.ShareViewModel
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
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
 * Use the [OverviewTenantFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OverviewTenantFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentOverviewTenantBinding
    private var overviewTenantContractHistoryAdapter: OverviewTenantContractHistoryAdapter? = null
    private val overviewArrayList: ArrayList<RentalPlanX> = ArrayList()
    private var isFragmentVisible = false
    var propertyId: String = ""
    var rentalId: String = ""
    private val TAG = "OverviewFragment"
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
        fun newInstance(): OverviewTenantFragment {
            val fragment = OverviewTenantFragment()
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
            inflater, R.layout.fragment_overview_tenant, container, false
        )
        setAdapter()
        setViewClickListener()
        return binding.root
    }

    private fun setViewClickListener() {
        binding.txtSeeAll.setOnClickListener(DebounceClickHandler {
            if (isItemClicked) {
                isItemClicked = false
                val intent =
                    Intent(requireActivity(), RentalsPaymentsDetailActivity::class.java)
                intent.putExtra("propertyId", propertyId)
                intent.putExtra("rentalId", rentalId)
                startActivity(intent)
                requireActivity().overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }
            Handler(Looper.getMainLooper()).postDelayed({
                isItemClicked = true
            }, 1000)
        })
    }

    private fun setAdapter() {
        sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
        overviewTenantContractHistoryAdapter =
            OverviewTenantContractHistoryAdapter(
                overviewArrayList,
                object : OverviewOwnerTenantItemCallback {
                    override fun rentalHistoryItemClick(
                        position: Int,
                        rentalPlanModel: RentalPlanX
                    ) {
                        val intent = Intent(
                            binding.root.context,
                            RentalHistoryDetailActivity::class.java
                        )
                        intent.putExtra("rentalPlanX", rentalPlanModel)
                        startActivity(intent)
                        requireActivity().overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                    }
                })
        val layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        binding.historyRecyclerview.layoutManager = layoutManager
        binding.historyRecyclerview.adapter = overviewTenantContractHistoryAdapter
        sharedViewModel.getOwnerTenantPropertiesOverview().observe(viewLifecycleOwner) {
            it?.let {
                initializeControls(it)
            }
        }

    }


    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    fun initializeControls(propertiesOverviewModel: TenantOverviewModel) {
        if (activity != null && isFragmentVisible) {
            Log.i(TAG, "initializeControls: start")
            binding.totalRentValue.text =
                "${propertiesOverviewModel.data?.rentalDetails?.totalRent}"
            binding.rentPaidValue.text =
                "AED ${propertiesOverviewModel.data?.rentalDetails?.totalReceived}"
            binding.txtLoginDetails.text =
                "AED ${propertiesOverviewModel.data?.rentalDetails?.securityDeposit}"
            binding.remaingingRentValue.text =
                "AED ${propertiesOverviewModel.data?.rentalDetails?.remainingRent}"
            binding.nextValue.text =
                "AED ${propertiesOverviewModel.data?.rentalDetails?.nextPayment}"
            binding.timeAndDate.text =
                "Due: ${propertiesOverviewModel.data?.rentalDetails?.dueDate}"
            binding.checkValue.text =
                propertiesOverviewModel.data?.rentalDetails?.paymentMethod
            binding.refValue.text =
                "Ref: ${propertiesOverviewModel.data?.rentalDetails?.refNo}"
            if (propertiesOverviewModel.data?.rentalPlan?.rentalPlans?.isNotEmpty() == true) {
                overviewArrayList.clear()
                propertiesOverviewModel.data?.rentalPlan?.rentalPlans?.let {
                    for (i in it.indices) {
                        if (!it[i].paymentStatus.equals(
                                "received",
                                ignoreCase = true
                            ) && !it[i].paymentType.equals("security", true)
                        ) {
                            overviewArrayList.add(it[i])
                        }
                    }
                }
            }
            propertyId = propertiesOverviewModel.data?.rentalDetails?.selectedProperty?.id.toString()
            Log.i(TAG, "initializeControls: $propertyId")
            //rental id
            if (propertiesOverviewModel.data?.rentalPlan?.rentalPlans?.isNotEmpty() == true) {
                rentalId =
                    propertiesOverviewModel.data?.rentalPlan?.rentalPlans?.get(0)?.rentalId.toString()
            }
            Log.i(TAG, "initializeControls: $rentalId")
            overviewTenantContractHistoryAdapter?.notifyDataSetChanged()
            Log.i(TAG, "initializeControls: end")
        }
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        isFragmentVisible = menuVisible
        if (menuVisible) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                sharedViewModel.refreshObserverResult()
            }
        }

    }
}