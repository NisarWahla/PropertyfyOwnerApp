package com.dzinemedia.ownerpropertyfyapp.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adapters.BillsPagerAdapter
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.InternetCallback
import com.dzinemedia.ownerpropertyfyapp.apiInterface.RetrofitClient
import com.dzinemedia.ownerpropertyfyapp.customeDialog.InternetDialogFragment
import com.dzinemedia.ownerpropertyfyapp.databinding.ActivityBillsBinding
import com.dzinemedia.ownerpropertyfyapp.fragments.billsFragments.PaidBillsFragment
import com.dzinemedia.ownerpropertyfyapp.fragments.billsFragments.PendingBillsFragment
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel.LoginModel
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

class BillsActivity : AppCompatActivity() {
    private var viewPagerAdapter: BillsPagerAdapter? = null
    private lateinit var binding: ActivityBillsBinding
    private lateinit var viewModel: TenantViewModel
    private lateinit var sharedViewModel: ShareViewModel
    var pendingBillsFragment: PendingBillsFragment? = null
    var paidBillsFragment: PaidBillsFragment? = null
    var token: String? = ""
    var tenantId: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bills)
        initializeControls()
        setViewClickListeners()
    }

    private fun setViewClickListeners() {
        binding.back.setOnClickListener(DebounceClickHandler {
            finish()
        })
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (Utils.isNetworkAvailable(this)) {
                token?.let { sToken ->
                    tenantId?.let { sTenantId ->
                        getBills(
                            sToken,
                            sTenantId.toInt()
                        )
                    }
                }
            } else {
                if (binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                token?.let { sToken ->
                    tenantId?.let { sTenantId ->
                        showInternetDialog(sToken, sTenantId.toInt())
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
        viewPagerAdapter = BillsPagerAdapter(this)
        registerFragmentWithPager()
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
                0 -> tab.text = binding.root.context.getString(R.string.pending_bills)
                1 -> tab.text = binding.root.context.getString(R.string.paid_bills)
                else -> tab.text = "Tab $position"
            }
        }.attach()
        val loginModel = SharedPreferencesHelper.getInstance(binding.root.context).getLoginModel()
        val jsonModel = Gson().fromJson(loginModel, LoginModel::class.java)
        token = jsonModel.data?.token
        tenantId = jsonModel.data?.id
        if (Utils.isNetworkAvailable(this)) {
            token?.let { sToken ->
                tenantId?.let { sTenantId ->
                    getBills(
                        sToken,
                        sTenantId.toInt()
                    )
                }
            }
        } else {
            token?.let { sToken ->
                tenantId?.let { sTenantId ->
                    showInternetDialog(sToken, sTenantId.toInt())
                }
            }
        }
    }

    private fun showInternetDialog(token: String, tenantId: Int) {
        val internetDialog =
            InternetDialogFragment(object : InternetCallback {
                override fun tryAgainClick(internetDialog: InternetDialogFragment) {
                    if (Utils.isNetworkAvailable(this@BillsActivity)) {
                        internetDialog.dismiss()
                        getBills(token, tenantId)
                    } else {
                        internetDialog.dismiss()
                        showInternetDialog(token, tenantId)
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

    @SuppressLint("SetTextI18n")
    private fun getBills(token: String, ownerId: Int) {
        viewModel.loading.observe(this) { isProgress ->
            if (isProgress) {
                showProgress()
            } else {
                hideProgress()
            }
        }
        viewModel.billsLiveData.observe(this) {
            if (binding.swipeRefreshLayout.isRefreshing) {
                binding.swipeRefreshLayout.isRefreshing = false
            }
            sharedViewModel.setAllBillsData(it)
        }
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getAllBills(token, ownerId)
        }
    }

    private fun registerFragmentWithPager() {
        pendingBillsFragment = PendingBillsFragment.newInstance()
        paidBillsFragment = PaidBillsFragment.newInstance()

        viewPagerAdapter?.addFrag(pendingBillsFragment!!)
        viewPagerAdapter?.addFrag(paidBillsFragment!!)
    }
}