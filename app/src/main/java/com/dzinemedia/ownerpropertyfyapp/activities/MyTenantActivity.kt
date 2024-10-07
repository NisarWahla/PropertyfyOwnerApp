package com.dzinemedia.ownerpropertyfyapp.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adapters.MyTenantPagerAdapter
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.InternetCallback
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.RetrofitMessageCallback
import com.dzinemedia.ownerpropertyfyapp.apiInterface.RetrofitClient
import com.dzinemedia.ownerpropertyfyapp.customeDialog.InternetDialogFragment
import com.dzinemedia.ownerpropertyfyapp.databinding.ActivityMyTenantBinding
import com.dzinemedia.ownerpropertyfyapp.fragments.tenantFragments.ActiveTenantFragment
import com.dzinemedia.ownerpropertyfyapp.fragments.tenantFragments.AllTenantFragment
import com.dzinemedia.ownerpropertyfyapp.fragments.tenantFragments.InactiveTenantFragment
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel.LoginModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.myTenantsModel.MyTenantsModel
import com.dzinemedia.ownerpropertyfyapp.repository.TenantRepository
import com.dzinemedia.ownerpropertyfyapp.utility.SharedPreferencesHelper
import com.dzinemedia.ownerpropertyfyapp.utility.Utils
import com.dzinemedia.ownerpropertyfyapp.viewModel.TenantViewModel
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyTenantActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyTenantBinding
    private var viewPagerAdapter: MyTenantPagerAdapter? = null
    private lateinit var viewModel: TenantViewModel
    private var jsonModel: LoginModel? = null
    var allTenatFrgInstance: AllTenantFragment? = null
    var activeTenantFragment: ActiveTenantFragment? = null
    var inactiveTenantFragment: InactiveTenantFragment? = null
    private var tenantsModel: MyTenantsModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_tenant)
        initializeControls()
        setViewClickListeners()
    }

    private fun setViewClickListeners() {
        binding.back.setOnClickListener(DebounceClickHandler {
            finish()
        })
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (Utils.isNetworkAvailable(this@MyTenantActivity)) {
                jsonModel?.data?.token?.let { token ->
                    jsonModel?.data?.id?.let { ownerId ->
                        getAllTenants(token, ownerId.toInt())
                    }
                }
            } else {
                if (binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                jsonModel?.data?.token?.let { token ->
                    jsonModel?.data?.id?.let { ownerId ->
                        showInternetDialog(token, ownerId.toInt())
                    }
                }
            }
        }
    }

    private fun showInternetDialog(token: String, ownerId: Int) {
        val internetDialog =
            InternetDialogFragment(object : InternetCallback {
                override fun tryAgainClick(internetDialog: InternetDialogFragment) {
                    if (Utils.isNetworkAvailable(this@MyTenantActivity)) {
                        internetDialog.dismiss()
                        getAllTenants(token, ownerId)
                    } else {
                        internetDialog.dismiss()
                        showInternetDialog(token, ownerId)
                    }
                }
            })
        internetDialog.show(supportFragmentManager, internetDialog.tag)
    }

    private fun initializeControls() {
        val apiInterface = RetrofitClient.create()
        val repository = TenantRepository(apiInterface)
        viewModel = ViewModelProvider(
            this,
            TenantViewModel.TenantViewModalFactory(repository)
        )[TenantViewModel::class.java]
        viewPagerAdapter = MyTenantPagerAdapter(this)
        registerFragmentWithPager()
        binding.viewPagerTenant.offscreenPageLimit = 1
        binding.viewPagerTenant.adapter = viewPagerAdapter

        // Attach TabLayout to ViewPager
        TabLayoutMediator(binding.tabLayoutTenant, binding.viewPagerTenant) { tab, position ->
            when (position) {
                0 -> tab.text = "All"
                1 -> tab.text = "Active"
                2 -> tab.text = "Inactive"
                else -> tab.text = "Tab $position"
            }
        }.attach()
        val loginModel = SharedPreferencesHelper.getInstance(binding.root.context).getLoginModel()
        jsonModel = Gson().fromJson(loginModel, LoginModel::class.java)
        jsonModel?.data?.token?.let { token ->
            jsonModel?.data?.id?.let { ownerId ->
                getAllTenants(token, ownerId.toInt())
            }
        }
    }


    private fun registerFragmentWithPager() {
        allTenatFrgInstance = AllTenantFragment.newInstance()
        activeTenantFragment = ActiveTenantFragment.newInstance()
        inactiveTenantFragment = InactiveTenantFragment.newInstance()

        viewPagerAdapter?.addFrag(allTenatFrgInstance!!)
        viewPagerAdapter?.addFrag(activeTenantFragment!!)
        viewPagerAdapter?.addFrag(inactiveTenantFragment!!)

    }
    private fun showProgress() {
        binding.shimmerLayout.visibility = View.VISIBLE
        binding.originalLayout.visibility = View.GONE
    }

    private fun hideProgress() {
        binding.shimmerLayout.visibility = View.GONE
        binding.originalLayout.visibility = View.VISIBLE
    }

    fun getAllTenants(): MyTenantsModel? {
        return tenantsModel
    }

    private fun getAllTenants(token: String, ownerId: Int) {
        viewModel.loading.observe(this) { isProgress ->
            if (isProgress) {
                showProgress()
            } else {
                hideProgress()
            }
        }
        viewModel.tenantsLiveData.observe(this) {
            if (it.success == true) {
                if (binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                tenantsModel = it
                allTenatFrgInstance?.getAllTenantsFrgData(it)
                activeTenantFragment?.getActiveFrgData(it)
                inactiveTenantFragment?.getInActiveFrgData(it)
                Log.i("TAG Owner", "getOwnerProperties: ${it.data?.size}")
            } else {
                if (binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                Utils.showToast(binding.root.context, it.message.toString())
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getAllTenants(token, ownerId, object : RetrofitMessageCallback {
                override fun retrofitErrorMessage(message: String) {
                    Utils.showToast(binding.root.context, message)
                }
            })
        }

    }

}