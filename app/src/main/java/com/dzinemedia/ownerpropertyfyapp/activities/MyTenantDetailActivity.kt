package com.dzinemedia.ownerpropertyfyapp.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adapters.CustomTenantPopupAdapter
import com.dzinemedia.ownerpropertyfyapp.adapters.TenantDetailPagerAdapter
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.InternetCallback
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.ListViewClickListener
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.RetrofitMessageCallback
import com.dzinemedia.ownerpropertyfyapp.apiInterface.RetrofitClient
import com.dzinemedia.ownerpropertyfyapp.customeDialog.InternetDialogFragment
import com.dzinemedia.ownerpropertyfyapp.databinding.ActivityMyTenantDetailBinding
import com.dzinemedia.ownerpropertyfyapp.fragments.tenantDetailFragments.InformationTenantFragment
import com.dzinemedia.ownerpropertyfyapp.fragments.tenantDetailFragments.OverviewTenantFragment
import com.dzinemedia.ownerpropertyfyapp.fragments.tenantDetailFragments.RentalsTenantFragment
import com.dzinemedia.ownerpropertyfyapp.models.TenantSpinnerModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel.LoginModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.tenantOverviewModel.TenantOverviewModel
import com.dzinemedia.ownerpropertyfyapp.repository.TenantRepository
import com.dzinemedia.ownerpropertyfyapp.utility.SharedPreferencesHelper
import com.dzinemedia.ownerpropertyfyapp.utility.Utils
import com.dzinemedia.ownerpropertyfyapp.viewModel.ShareViewModel
import com.dzinemedia.ownerpropertyfyapp.viewModel.TenantViewModel
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyTenantDetailActivity : AppCompatActivity() {
    private var tenantId: String? = "0"
    private var ownerId: Int = 0
    private lateinit var binding: ActivityMyTenantDetailBinding
    val items: ArrayList<TenantSpinnerModel> = ArrayList()
    var lastPosition = -1
    var customAdapter: CustomTenantPopupAdapter? = null
    private val TAG = "MyTenantDetailActivity"
    private lateinit var sharedViewModel: ShareViewModel
    private var token: String? = null
    private lateinit var viewModel: TenantViewModel
    private var tenantOverviewModel: TenantOverviewModel? = null
    private var propertyId: Int = 0
    var overviewTenantFrgInstance: OverviewTenantFragment? = null
    var infoTenantFrgInstance: InformationTenantFragment? = null
    var rentalsTenantFrgInstance: RentalsTenantFragment? = null
    var viewPagerAdapter: TenantDetailPagerAdapter? = null
    var isPopulateListLoaded: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_tenant_detail)
        initializeControls()
        setViewClickListeners()
    }

    private fun setViewClickListeners() {
        binding.back.setOnClickListener(DebounceClickHandler {
            finish()
        })
        binding.dropDownLayout.setOnClickListener(DebounceClickHandler {
            showCustomPopupMenu(binding.root.context, it)
        })
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (Utils.isNetworkAvailable(this@MyTenantDetailActivity)) {
                token?.let {
                    tenantId?.let { tenantId ->
                        Log.i(TAG, "setViewClickListeners: $propertyId")
                        getOwnerTenantPropertiesOverview(
                            it,
                            ownerId,
                            tenantId.toInt(),
                            propertyId
                        )
                    }
                }
            } else {
                token?.let {
                    tenantId?.let { tenantId ->
                        showInternetDialog(
                            it, ownerId,
                            tenantId.toInt(), propertyId
                        )
                    }
                }
            }
        }
    }

    private fun showCustomPopupMenu(context: Context, anchorView: View) {
        // Inflate the custom layout for the popup window
        val inflater = LayoutInflater.from(context)
        val customLayout = inflater.inflate(R.layout.custom_popup_menu_list, null)
        val listViewOptions: RecyclerView =
            customLayout.findViewById(R.id.listView)
        val topMargin = context.resources.getDimensionPixelSize(R.dimen.popup_top_margin)
        // Create a PopupWindow with the custom layout
        val popupWindow = PopupWindow(
            customLayout,
            anchorView.width,
            calculatePopupHeight(context, items.size),
            true
        )
        popupWindow.animationStyle = android.R.style.Animation_Dialog
        popupWindow.showAsDropDown(anchorView, 0, topMargin)
        listViewOptions.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)

        customAdapter = CustomTenantPopupAdapter(this, items, object : ListViewClickListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun listViewItemClick(position: Int, holder: CustomTenantPopupAdapter.ViewHolder) {
                //if (lastPosition == -1) {
                binding.txtView.text = items[position].locName
                var selectedPropItem = items.get(position)
                for (item in items) {
                    if (items.indexOf(item) == position) {
                        item.selection = true
                        selectedPropItem = item
                    } else {
                        item.selection = false
                    }
                    items.set(items.indexOf(item), item)
                }
                customAdapter?.notifyDataSetChanged()
                popupWindow.dismiss()
                propertyId = selectedPropItem.propertyId
                if (Utils.isNetworkAvailable(this@MyTenantDetailActivity)) {
                    token?.let {
                        tenantId?.let { tenantId ->
                            getOwnerTenantPropertiesOverview(
                                it,
                                ownerId,
                                tenantId.toInt(),
                                selectedPropItem.propertyId
                            )
                        }
                    }
                } else {
                    token?.let {
                        tenantId?.let { tenantId ->
                            showInternetDialog(
                                it, ownerId,
                                tenantId.toInt(), selectedPropItem.propertyId
                            )
                        }
                    }
                }
            }
        })
        listViewOptions.adapter = customAdapter
    }

    private fun calculatePopupHeight(
        context: Context,
        itemCount: Int
    ): Int {
        val itemHeight = context.resources.getDimensionPixelSize(android.R.dimen.app_icon_size)
        val maxHeight = context.resources.getDimensionPixelSize(R.dimen.popup_max_height)
        val height = if (itemCount * itemHeight < maxHeight) {
            itemCount * itemHeight
        } else {
            maxHeight
        }
        return height
    }

    private fun initializeControls() {
        val apiInterface = RetrofitClient.create()
        val repository = TenantRepository(apiInterface)
        viewModel = ViewModelProvider(
            this@MyTenantDetailActivity, TenantViewModel.TenantViewModalFactory(repository)
        )[TenantViewModel::class.java]
        sharedViewModel = ViewModelProvider(this)[ShareViewModel::class.java]

        val loginModel =
            SharedPreferencesHelper.getInstance(binding.root.context).getLoginModel()
        val jsonModel = Gson().fromJson(loginModel, LoginModel::class.java)
        token = jsonModel.data?.token
        ownerId = jsonModel.data?.id?.toInt()!!
        Log.i(TAG, "initializeControls: $token")
        if (intent.hasExtra("tenantId")) {
            tenantId = intent.getStringExtra("tenantId")
            Log.i(TAG, "initializeControls: $propertyId")
            ownerId.let { ownerId ->
                token?.let { token ->
                    tenantId?.toInt()?.let { tenantId ->
                        getOwnerTenantPropertiesOverview(
                            token,
                            ownerId,
                            tenantId,
                            propertyId
                            )
                    }
                }
            }
        }
        viewPagerAdapter = TenantDetailPagerAdapter(this)
        registerFragmentWithPager()
        binding.viewPager2.offscreenPageLimit = 1
        binding.viewPager2.adapter = viewPagerAdapter
        binding.viewPager2.setPageTransformer { page, position ->
            when {
                position < -1 -> { // Page is off-screen to the left
                    page.alpha = 0f
                }
                position <= 1 -> { // Page is visible on the screen
                    // Example of different animations based on position
                    page.rotationY = position * 90
                    page.alpha = 1 - Math.abs(position)
                    page.scaleX = Math.max(0.85f, 1 - Math.abs(position))
                    page.scaleY = Math.max(0.85f, 1 - Math.abs(position))
                }
                else -> { // Page is off-screen to the right
                    page.alpha = 0f
                }
            }
        }

        // Attach TabLayout to ViewPager
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = "Overview"
                1 -> tab.text = "Information"
                2 -> tab.text = "Rentals"
                else -> tab.text = "Tab $position"
            }
        }.attach()
    }

    private fun registerFragmentWithPager() {
        Log.i(TAG, "registerFragmentWithPager: ")
        overviewTenantFrgInstance = OverviewTenantFragment.newInstance()
        infoTenantFrgInstance = InformationTenantFragment.newInstance()
        rentalsTenantFrgInstance = RentalsTenantFragment.newInstance()

        viewPagerAdapter?.addFrag(overviewTenantFrgInstance!!)
        viewPagerAdapter?.addFrag(infoTenantFrgInstance!!)
        viewPagerAdapter?.addFrag(rentalsTenantFrgInstance!!)
    }

    private fun showInternetDialog(token: String, ownerId: Int, tenantId: Int, propertyId: Int) {
        val internetDialog =
            InternetDialogFragment(object : InternetCallback {
                override fun tryAgainClick(internetDialog: InternetDialogFragment) {
                    if (Utils.isNetworkAvailable(this@MyTenantDetailActivity)) {
                        internetDialog.dismiss()
                        getOwnerTenantPropertiesOverview(token, ownerId, tenantId, propertyId)
                    } else {
                        internetDialog.dismiss()
                        showInternetDialog(token, ownerId, tenantId, propertyId)
                    }
                }
            })
        internetDialog.show(supportFragmentManager, internetDialog.tag)
    }

    private fun populateList() {
        items.clear()
        for (i in 0 until tenantOverviewModel?.data?.properties?.size!!) {
            if (i == 0) {
                items.add(
                    TenantSpinnerModel(
                        tenantOverviewModel?.data?.properties?.get(i)?.address.toString(),
                        tenantOverviewModel?.data?.properties?.get(i)?.id!!.toInt(),
                        true
                    )
                )
            } else {
                items.add(
                    TenantSpinnerModel(
                        tenantOverviewModel?.data?.properties?.get(i)?.address.toString(),
                        tenantOverviewModel?.data?.properties?.get(i)?.id!!.toInt(),
                        false
                    )
                )
            }
        }
        binding.txtView.ellipsize = TextUtils.TruncateAt.MARQUEE
        binding.txtView.isSingleLine = true
        binding.txtView.isSelected = true
        binding.txtView.text = items[0].locName
        propertyId = items[0].propertyId
        Log.i(TAG, "populateList: $propertyId")
    }

    fun showProgress() {
        binding.originalLayout.visibility = View.GONE
        binding.shimmerLayout.visibility = View.VISIBLE
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

    fun hideProgress() {
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

    @SuppressLint("NotifyDataSetChanged")
    private fun getOwnerTenantPropertiesOverview(
        token: String,
        ownerId: Int,
        tenantId: Int,
        propertyId: Int
    ) {
        viewModel.loading.observe(this) { isProgress ->
            if (isProgress) {
                showProgress()
            } else {
                hideProgress()
            }
        }
        viewModel.tenantsOverviewLiveData.observe(this) { itOwnerTenantPropertyOverview ->
            if (itOwnerTenantPropertyOverview.success == true) {
                if (binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                tenantOverviewModel = itOwnerTenantPropertyOverview
                if (isPopulateListLoaded) {
                    isPopulateListLoaded = false
                    populateList()
                }
                if (itOwnerTenantPropertyOverview?.data?.rentalPlan?.rentalPlans?.size!! > 0) {
                    for (i in 0 until itOwnerTenantPropertyOverview.data?.rentalPlan?.rentalPlans?.size!!) {
                        if (itOwnerTenantPropertyOverview.data?.rentalPlan?.rentalPlans?.get(i)?.paymentStatus.equals(
                                "Pending",
                                ignoreCase = true
                            )
                        ) {
                            itOwnerTenantPropertyOverview.data?.rentalPlan?.rentalPlans?.get(i)?.dueDate?.let {
                                val status = Utils.getDateStatus(
                                    it
                                )
                                itOwnerTenantPropertyOverview.data?.rentalPlan?.rentalPlans?.get(i)?.paymentStatus =
                                    status
                            }
                        }
                    }
                }
                sharedViewModel.setOwnerTenantPropertiesOverview(itOwnerTenantPropertyOverview)
                //overviewFrgInstance?.updateData(itPropertyOverview)
                //infoFrgInstance?.updateData(itPropertyOverview)
                //rentalsFrgInstance?.updateData(itPropertyOverview)

            } else {
                if (binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                Utils.showToast(
                    binding.root.context,
                    itOwnerTenantPropertyOverview.message.toString()
                )
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getOwnerTenantPropertiesOverview(
                token,
                ownerId,
                tenantId,
                propertyId,
                object : RetrofitMessageCallback {
                    override fun retrofitErrorMessage(message: String) {
                        Utils.showToast(binding.root.context, message)
                    }

                })
        }
    }
}