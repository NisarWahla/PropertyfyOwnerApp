package com.dzinemedia.ownerpropertyfyapp.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adapters.RentalPaymentPagerAdapter
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.InternetCallback
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.RetrofitMessageCallback
import com.dzinemedia.ownerpropertyfyapp.apiInterface.RetrofitClient
import com.dzinemedia.ownerpropertyfyapp.customeDialog.InternetDialogFragment
import com.dzinemedia.ownerpropertyfyapp.databinding.ActivityRentalsPaymentsDetailBinding
import com.dzinemedia.ownerpropertyfyapp.fragments.rentalHistoryDetailsFragments.OverdueFragment
import com.dzinemedia.ownerpropertyfyapp.fragments.rentalHistoryDetailsFragments.PartialFragment
import com.dzinemedia.ownerpropertyfyapp.fragments.rentalHistoryDetailsFragments.ReceivedFragment
import com.dzinemedia.ownerpropertyfyapp.fragments.rentalHistoryDetailsFragments.UpcomingFragment
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel.LoginModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview.RentalPlan
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

class RentalsPaymentsDetailActivity : AppCompatActivity() {
    private var ownerId: String? = "0"
    private var token: String? = null
    private lateinit var binding: ActivityRentalsPaymentsDetailBinding
    var propertyId: String? = "0"
    var rentalId: String? = "0"
    private lateinit var viewModel: TenantViewModel
    private val rentalPlanArrayList: ArrayList<RentalPlan> = ArrayList()
    var overdueFrgInstance: OverdueFragment? = null
    var upcomingFragment: UpcomingFragment? = null
    var partialFragment: PartialFragment? = null
    var receivedFragment: ReceivedFragment? = null
    private val TAG = "RentalsPaymentsDetailAc"
    private lateinit var sharedViewModel: ShareViewModel

    //adapter
    var viewPagerAdapter: RentalPaymentPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rentals_payments_detail)
        initializeControls()
        setViewClickListeners()
    }

    private fun setViewClickListeners() {
        binding.back.setOnClickListener(DebounceClickHandler {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        })
        binding.swipeRefreshLayout.setOnRefreshListener {
            token?.let { token ->
                ownerId?.let { ownerId ->
                    if (Utils.isNetworkAvailable(this)) {
                        rentalId?.let { propertyId?.let { it1 -> getPropertyRentalsOverview(token, ownerId.toInt(), it.toInt(), it1.toInt()) } }
                    } else {
                        if (binding.swipeRefreshLayout.isRefreshing) {
                            binding.swipeRefreshLayout.isRefreshing = false
                        }
                        rentalId?.let { propertyId?.let { it1 -> showInternetDialog(token, ownerId.toInt(), it.toInt(), it1.toInt()) } }
                    }
                }
            }
        }
    }

    private fun initializeControls() {
        val apiInterface = RetrofitClient.create()
        val repository = TenantRepository(apiInterface)
        viewModel = ViewModelProvider(
            this,
            TenantViewModel.TenantViewModalFactory(repository)
        )[TenantViewModel::class.java]

        sharedViewModel = ViewModelProvider(this)[ShareViewModel::class.java]
        viewPagerAdapter = RentalPaymentPagerAdapter(this)
        registerFragmentWithPager()
        binding.viewPager2.offscreenPageLimit = 3
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
                0 -> tab.text = "Upcoming"
                1 -> tab.text = "Overdue/Bounced"
                2 -> tab.text = "Partial"
                3 -> tab.text = "Received"
                else -> tab.text = "Tab $position"
            }
        }.attach()
        val loginModel = SharedPreferencesHelper.getInstance(binding.root.context).getLoginModel()
        val jsonModel = Gson().fromJson(loginModel, LoginModel::class.java)
        token = jsonModel.data?.token
        ownerId = jsonModel.data?.id

        if (intent.hasExtra("propertyId") && intent.hasExtra("rentalId")) {
            propertyId = intent.getStringExtra("propertyId")
            rentalId = intent.getStringExtra("rentalId")
            if (rentalId.equals("")) {
                rentalId = "0"
            } else if (propertyId.equals("")) {
                propertyId = "0"
            }
            token?.let { token ->
                ownerId?.let { ownerId ->
                    if (Utils.isNetworkAvailable(this)) {
                        getPropertyRentalsOverview(
                            token,
                            ownerId.toInt(),
                            rentalId!!.toInt(),
                            propertyId!!.toInt()
                        )
                    } else {
                        showInternetDialog(
                            token,
                            ownerId.toInt(),
                            rentalId!!.toInt(),
                            propertyId!!.toInt()
                        )
                    }
                }
            }
        }
    }

    private fun showInternetDialog(
        token: String,
        ownerId: Int,
        rentalId: Int,
        propertyId: Int
    ) {
        val internetDialog =
            InternetDialogFragment(object : InternetCallback {
                override fun tryAgainClick(internetDialog: InternetDialogFragment) {
                    if (Utils.isNetworkAvailable(this@RentalsPaymentsDetailActivity)) {
                        internetDialog.dismiss()
                        getPropertyRentalsOverview(token, ownerId, rentalId, propertyId)
                    } else {
                        showInternetDialog(token, ownerId, rentalId, propertyId)
                    }
                }
            })
        internetDialog.show(supportFragmentManager, internetDialog.tag)
    }

    private fun showProgress() {
        binding.originalLayout.visibility = View.GONE
        binding.shimmerLayout.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.shimmerLayout.visibility = View.GONE
        binding.originalLayout.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun getPropertyRentalsOverview(
        token: String,
        ownerId: Int,
        rentalId: Int,
        propertyId: Int
    ) {
        viewModel.loading.observe(this) { isProgress ->
            if (isProgress) {
                showProgress()
            } else {
                hideProgress()
            }
        }
        viewModel.ownerPropertyRentalOverviewApiLiveData.observe(this) {
            if (it.success == true) {
                if (binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                rentalPlanArrayList.clear()
                rentalPlanArrayList.addAll(it.data?.rentalPlan as ArrayList<RentalPlan>)
                if (rentalPlanArrayList.size > 0) {
                    for (i in 0 until rentalPlanArrayList.size) {
                        if (rentalPlanArrayList[i].paymentStatus.equals(
                                "Pending",
                                ignoreCase = true
                            )
                        ) {
                            rentalPlanArrayList[i].dueDate?.let {
                                val status = Utils.getDateStatus(
                                    it
                                )
                                rentalPlanArrayList[i].paymentStatus =
                                    status
                            }
                        }
                    }

                    sharedViewModel.setRentalPlanList(rentalPlanArrayList)
                } else {
                    sharedViewModel.setRentalPlanList(rentalPlanArrayList)
                }
                /*val sharedViewModel = ViewModelProvider(this).get(ShareViewModel::class.java)
                sharedViewModel.sharedData.value = "nisar"*/

                binding.txtPropertyId.text =
                    "Property ${it.data?.propertyId?.mpId}"

            } else {
                if (binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                Utils.showToast(binding.root.context, it.message.toString())
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getOwnerPropertyRentalOverview(
                token,
                ownerId,
                rentalId,
                propertyId,
                object : RetrofitMessageCallback {
                    override fun retrofitErrorMessage(message: String) {
                        Utils.showToast(binding.root.context, message)
                    }

                })
        }
    }

    private fun registerFragmentWithPager() {
        upcomingFragment = UpcomingFragment.newInstance()
        overdueFrgInstance = OverdueFragment.newInstance()
        partialFragment = PartialFragment.newInstance()
        receivedFragment = ReceivedFragment.newInstance()

        viewPagerAdapter?.addFrag(upcomingFragment!!)
        viewPagerAdapter?.addFrag(overdueFrgInstance!!)
        viewPagerAdapter?.addFrag(partialFragment!!)
        viewPagerAdapter?.addFrag(receivedFragment!!)
    }
}