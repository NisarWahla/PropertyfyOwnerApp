package com.dzinemedia.ownerpropertyfyapp.fragments.propertyDetailFragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.activities.PartialPaymentDetailActivity
import com.dzinemedia.ownerpropertyfyapp.activities.PropertyDetailActivity
import com.dzinemedia.ownerpropertyfyapp.activities.RentalHistoryDetailActivity
import com.dzinemedia.ownerpropertyfyapp.activities.RentalsPaymentsDetailActivity
import com.dzinemedia.ownerpropertyfyapp.adapters.ImagePagerHorizontalAdapter
import com.dzinemedia.ownerpropertyfyapp.adapters.OverviewFragmentAdapter
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.HistoryRentalItemCallback
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.OverviewFragmentItemCallback
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentOverviewBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyOverview.OwnerPropertyOverviewModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyOverview.RentalPayment
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview.RentalPlan
import com.dzinemedia.ownerpropertyfyapp.utility.Utils
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import com.permissionx.guolindev.PermissionX
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
 * Use the [OverviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OverviewFragment : Fragment() {
    private var phoneNumber: String? = null
    private var isAllFragmentVisible: Boolean = false
    private val TAG = "OverviewFragment"

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentOverviewBinding
    private var overviewFragmentAdapter: OverviewFragmentAdapter? = null
    private val overviewArrayList: ArrayList<RentalPayment> = ArrayList()
    private val images: ArrayList<Any> = ArrayList()

    private var propertyId: String = "0"
    private var rentalId: String = "0"
    private var isItemClicked = true

    companion object {
        fun newInstance(): OverviewFragment {
            val fragment = OverviewFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_overview, container, false
        )
        initializeControls()
        setClickListeners()
        return binding.root
    }

    private fun setClickListeners() {
        binding.btnCall.setOnClickListener(DebounceClickHandler {
            Utils.preventDoubleTap(it)
            if (checkPermissionBehave()) {
                Utils.makePhoneCall(binding.root.context, phoneNumber.toString())
            } else {
                takePermission(phoneNumber.toString())
            }
        })
        binding.txtSeeAll.setOnClickListener(DebounceClickHandler {
            val intent = Intent(requireActivity(), RentalsPaymentsDetailActivity::class.java)
            intent.putExtra("rentalId", rentalId)
            intent.putExtra("propertyId", propertyId)
            startActivity(intent)
            requireActivity().overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        })
    }

    private fun initializeControls() {
        overviewFragmentAdapter = OverviewFragmentAdapter(overviewArrayList, object :
            OverviewFragmentItemCallback {
            override fun overviewItemClick(position: Int, rentalPlanModel: RentalPayment) {
                if (isItemClicked) {
                    isItemClicked = false
                    if (rentalPlanModel.paymentStatus?.equals("partial", true) == true) {
                        val intent =
                            Intent(
                                binding.root.context,
                                PartialPaymentDetailActivity::class.java
                            )
                        intent.putExtra("propertyId", rentalPlanModel.propertyId)
                        intent.putExtra("rentalId", rentalPlanModel.rentalId)
                        intent.putExtra("rentalPaymentId", rentalPlanModel.id)
                        val options = ActivityOptionsCompat.makeCustomAnimation(
                            binding.root.context,
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        startActivity(intent, options.toBundle())
                    } else {
                        val intent = Intent(
                            binding.root.context,
                            RentalHistoryDetailActivity::class.java
                        )
                        intent.putExtra("propertyId", propertyId)
                        intent.putExtra("rentalId", rentalId)
                        intent.putExtra("rentalPaymentId", rentalPlanModel.id)
                        intent.putExtra("depositBank", rentalPlanModel.getDepositBank())
                        intent.putExtra("paymentStatus", rentalPlanModel.paymentStatus)
                        startActivity(intent)
                        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    isItemClicked = true
                }, 1000)
            }

        })
        val layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        binding.overviewRecyclerview.layoutManager = layoutManager
        binding.overviewRecyclerview.adapter = overviewFragmentAdapter
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        isAllFragmentVisible = menuVisible
        if (menuVisible) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                (activity as PropertyDetailActivity).getOwnerPropertiesOverviewModel()
                    ?.let { getOverviewFrgData(it) }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getOverviewFrgData(ownerPropertyOverviewModel: OwnerPropertyOverviewModel) {
        if (activity != null && isAllFragmentVisible) {
            propertyId = ownerPropertyOverviewModel.data?.propertyId!!
            binding.txtAddress.text = ownerPropertyOverviewModel.data?.address
            binding.txtBed.text = ownerPropertyOverviewModel.data?.propertyInfo?.bedrooms.toString()
            binding.txtPark.text =
                ownerPropertyOverviewModel.data?.propertyInfo?.parkings.toString()
            binding.txtWash.text =
                ownerPropertyOverviewModel.data?.propertyInfo?.bathrooms.toString()
            if (ownerPropertyOverviewModel.data?.contracts?.size!! > 0) {
                binding.txtManager.text =
                    ownerPropertyOverviewModel.data?.contracts?.get(0)?.ownerId?.name
                phoneNumber = ownerPropertyOverviewModel.data?.contracts?.get(0)?.ownerId?.phone1
            }
            images.clear()
            images.addAll(ownerPropertyOverviewModel.data?.images?.get(0)?.propertyImages as ArrayList<Any>)
            if (images.size > 0) {
                Glide.with(binding.root.context).load(images[0]).into(binding.sliderImg)
            }
            binding.aedOneValue.text =
                ownerPropertyOverviewModel.data?.appSection?.propertyBalance.toString()
            binding.aedTwoValue.text =
                ownerPropertyOverviewModel.data?.appSection?.rentCollected.toString()
            binding.aedFour.text =
                ownerPropertyOverviewModel.data?.appSection?.availableBalance.toString()

            overviewArrayList.clear()
            if (ownerPropertyOverviewModel.data?.appSection?.rentalPayments?.size!! > 0) {
                rentalId = ownerPropertyOverviewModel.data?.appSection?.rentalPayments?.get(0)?.rentalId!!
                for (i in 0 until ownerPropertyOverviewModel.data?.appSection?.rentalPayments?.size!!) {
                    if (ownerPropertyOverviewModel.data?.appSection?.rentalPayments?.get(i)?.paymentType.equals(
                            "installment",
                            ignoreCase = true
                        ) &&
                        !ownerPropertyOverviewModel.data?.appSection?.rentalPayments?.get(i)?.paymentStatus.equals(
                            "received",
                            ignoreCase = true
                        )
                    ) {
                        ownerPropertyOverviewModel.data?.appSection?.rentalPayments?.get(i)
                            ?.let { overviewArrayList.add(it) }
                    }
                }
            }
            overviewFragmentAdapter?.notifyDataSetChanged()
        }
    }
    private fun takePermission(phoneNumber: String) {
        PermissionX.init(this).permissions(
            Manifest.permission.CALL_PHONE
        ).onExplainRequestReason { scope, deniedList ->
            scope.showRequestReasonDialog(
                deniedList,
                getString(R.string.core_fundamental),
                getString(R.string.ok),
                getString(R.string.cancel)
            )
        }.onForwardToSettings { scope, deniedList ->
            scope.showForwardToSettingsDialog(
                deniedList,
                getString(R.string.need_to_allow_necessary_permission),
                getString(R.string.ok),
                getString(R.string.cancel)
            )
        }.request { allGranted, grantedList, deniedList ->
            if (allGranted) {
                Utils.makePhoneCall(binding.root.context, phoneNumber)
            } else {
                Log.i(TAG, "takePermission: Permission denied!")
            }
        }
    }
    private fun checkPermissionBehave(): Boolean {
        val result1 = ContextCompat.checkSelfPermission(
            binding.root.context,
            Manifest.permission.CALL_PHONE
        )

        return result1 == PackageManager.PERMISSION_GRANTED
    }
}