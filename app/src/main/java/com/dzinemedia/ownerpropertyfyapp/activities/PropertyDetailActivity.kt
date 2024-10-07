package com.dzinemedia.ownerpropertyfyapp.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adapters.PropertyDetailPagerAdapter
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.InternetCallback
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.RetrofitMessageCallback
import com.dzinemedia.ownerpropertyfyapp.apiInterface.RetrofitClient
import com.dzinemedia.ownerpropertyfyapp.customeDialog.InternetDialogFragment
import com.dzinemedia.ownerpropertyfyapp.databinding.ActivityPropertyDetailBinding
import com.dzinemedia.ownerpropertyfyapp.fragments.propertyDetailFragments.*
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel.LoginModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyOverview.OwnerPropertyOverviewModel
import com.dzinemedia.ownerpropertyfyapp.repository.TenantRepository
import com.dzinemedia.ownerpropertyfyapp.utility.SharedPreferencesHelper
import com.dzinemedia.ownerpropertyfyapp.utility.Utils
import com.dzinemedia.ownerpropertyfyapp.viewModel.TenantViewModel
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PropertyDetailActivity : AppCompatActivity() {
    private var propertyId: String = ""
    private lateinit var binding: ActivityPropertyDetailBinding
    private var viewPagerAdapter: PropertyDetailPagerAdapter? = null
    private lateinit var viewModel: TenantViewModel
    var overviewFrgInstance: OverviewFragment? = null
    var infoFrgInstance: InfoFragment? = null
    var rentalsFrgInstance: RentalsFragment? = null
    var managementFrgInstance: ManagementFragment? = null
    var maintenanceFrgInstance: MaintenanceFragment? = null
    var utilityFrgInstance: UtilityBillsFragment? = null
    var serviceChargesFrgInstance: ServiceChargesFragment? = null
    var inspectionFrgInstance: InspectionsFragment? = null
    private var ownerPropertyOverviewModel: OwnerPropertyOverviewModel? = null
    private var jsonModel: LoginModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_property_detail)
        initializeControls()
        setViewClickListeners()
    }

    private fun setViewClickListeners() {
        binding.back.setOnClickListener(DebounceClickHandler {
            finish()
        })
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (Utils.isNetworkAvailable(this@PropertyDetailActivity)) {
                jsonModel?.data?.token?.let { token ->
                    jsonModel?.data?.id?.let { ownerId ->
                        getOwnerPropertiesOverview(token, ownerId.toInt(), propertyId.toInt())
                    }
                }
            } else {
                if (binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                jsonModel?.data?.token?.let { token ->
                    jsonModel?.data?.id?.let { ownerId ->
                        showInternetDialog(token, ownerId.toInt(), propertyId.toInt())
                    }
                }
            }
        }
    }

    private fun showInternetDialog(token: String, ownerId: Int, propertyId: Int) {
        val internetDialog = InternetDialogFragment(object : InternetCallback {
            override fun tryAgainClick(internetDialog: InternetDialogFragment) {
                if (Utils.isNetworkAvailable(this@PropertyDetailActivity)) {
                    internetDialog.dismiss()
                    getOwnerPropertiesOverview(token, ownerId, propertyId)
                } else {
                    internetDialog.dismiss()
                    showInternetDialog(token, ownerId, propertyId)
                }
            }
        })
        internetDialog.show(supportFragmentManager, internetDialog.tag)
    }

    private fun initializeControls() {
        val apiInterface = RetrofitClient.create()
        val repository = TenantRepository(apiInterface)
        viewModel = ViewModelProvider(
            this, TenantViewModel.TenantViewModalFactory(repository)
        )[TenantViewModel::class.java]
        viewPagerAdapter = PropertyDetailPagerAdapter(this)
        registerFragmentWithPager()
        binding.viewPager2.offscreenPageLimit = 7
        binding.viewPager2.adapter = viewPagerAdapter

        // Attach TabLayout to ViewPager
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = "Overview"
                1 -> tab.text = "Info"
                2 -> tab.text = "Rentals"
                3 -> tab.text = "Management"
                4 -> tab.text = "Maintenance"
                5 -> tab.text = "UtilityBills"
                6 -> tab.text = "ServiceCharges"
                7 -> tab.text = "Inspections"
                else -> tab.text = "Tab $position"
            }
        }.attach()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        val loginModel = SharedPreferencesHelper.getInstance(binding.root.context).getLoginModel()
        jsonModel = Gson().fromJson(loginModel, LoginModel::class.java)
        if (intent.hasExtra("propertyId")) {
            propertyId = intent.getStringExtra("propertyId")!!
        }
        jsonModel?.data?.token?.let { token ->
            jsonModel?.data?.id?.let { ownerId ->
                getOwnerPropertiesOverview(token, ownerId.toInt(), propertyId.toInt())
            }
        }
    }

    fun getOwnerPropertiesOverviewModel(): OwnerPropertyOverviewModel? {
        return ownerPropertyOverviewModel
    }

    private fun registerFragmentWithPager() {
        overviewFrgInstance = OverviewFragment.newInstance()
        infoFrgInstance = InfoFragment.newInstance()
        rentalsFrgInstance = RentalsFragment.newInstance()
        managementFrgInstance = ManagementFragment.newInstance()
        maintenanceFrgInstance = MaintenanceFragment.newInstance()
        utilityFrgInstance = UtilityBillsFragment.newInstance()
        serviceChargesFrgInstance = ServiceChargesFragment.newInstance()
        inspectionFrgInstance = InspectionsFragment.newInstance()

        viewPagerAdapter?.addFrag(overviewFrgInstance!!)
        viewPagerAdapter?.addFrag(infoFrgInstance!!)
        viewPagerAdapter?.addFrag(rentalsFrgInstance!!)
        viewPagerAdapter?.addFrag(managementFrgInstance!!)
        viewPagerAdapter?.addFrag(maintenanceFrgInstance!!)
        viewPagerAdapter?.addFrag(utilityFrgInstance!!)
        viewPagerAdapter?.addFrag(serviceChargesFrgInstance!!)
        viewPagerAdapter?.addFrag(inspectionFrgInstance!!)
    }

    private fun showProgress() {
        binding.shimmerLayout.visibility = View.VISIBLE
        binding.originalLayout.visibility = View.GONE
        when (binding.tabLayout.selectedTabPosition) {
            0 -> {
                binding.shimmerOverviewFragment.shimmerLayout.visibility = View.VISIBLE
                binding.shimmerInfoFragment.shimmerLayout.visibility = View.GONE
                binding.shimmerRentalsFragment.shimmerLayout.visibility = View.GONE
            }
            1 -> {
                binding.shimmerOverviewFragment.shimmerLayout.visibility = View.GONE
                binding.shimmerInfoFragment.shimmerLayout.visibility = View.VISIBLE
                binding.shimmerRentalsFragment.shimmerLayout.visibility = View.GONE
            }
            2 -> {
                binding.shimmerOverviewFragment.shimmerLayout.visibility = View.GONE
                binding.shimmerInfoFragment.shimmerLayout.visibility = View.GONE
                binding.shimmerRentalsFragment.shimmerLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun hideProgress() {
        binding.shimmerLayout.visibility = View.GONE
        binding.originalLayout.visibility = View.VISIBLE
        when (binding.tabLayout.selectedTabPosition) {
            0 -> {
                binding.shimmerOverviewFragment.shimmerLayout.visibility = View.GONE
                binding.shimmerInfoFragment.shimmerLayout.visibility = View.GONE
                binding.shimmerRentalsFragment.shimmerLayout.visibility = View.GONE
            }
            1 -> {
                binding.shimmerOverviewFragment.shimmerLayout.visibility = View.GONE
                binding.shimmerInfoFragment.shimmerLayout.visibility = View.GONE
                binding.shimmerRentalsFragment.shimmerLayout.visibility = View.GONE
            }
            2 -> {
                binding.shimmerOverviewFragment.shimmerLayout.visibility = View.GONE
                binding.shimmerInfoFragment.shimmerLayout.visibility = View.GONE
                binding.shimmerRentalsFragment.shimmerLayout.visibility = View.GONE
            }
        }
    }

    private fun getOwnerPropertiesOverview(token: String, ownerId: Int, propertyId: Int) {
        viewModel.loading.observe(this) { isProgress ->
            if (isProgress) {
                showProgress()
            } else {
                hideProgress()
            }
        }
        viewModel.ownerPropertyOverviewApiLiveData.observe(this) {
            if (it.success == true) {
                if (binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                ownerPropertyOverviewModel = it
                overviewFrgInstance?.getOverviewFrgData(it)
                infoFrgInstance?.getInfoFrgData(it)
                rentalsFrgInstance?.getRentalsFrgData(it)
            } else {
                if (binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                Utils.showToast(binding.root.context, it.message.toString())
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getOwnerPropertiesOverview(
                token,
                ownerId,
                propertyId,
                object : RetrofitMessageCallback {
                    override fun retrofitErrorMessage(message: String) {
                        Utils.showToast(binding.root.context, message)
                    }
                })
        }
    }
}