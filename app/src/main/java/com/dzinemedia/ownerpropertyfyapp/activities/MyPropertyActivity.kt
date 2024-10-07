package com.dzinemedia.ownerpropertyfyapp.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adapters.MyPropertyPagerAdapter
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.InternetCallback
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.RetrofitMessageCallback
import com.dzinemedia.ownerpropertyfyapp.apiInterface.RetrofitClient
import com.dzinemedia.ownerpropertyfyapp.customeDialog.InternetDialogFragment
import com.dzinemedia.ownerpropertyfyapp.databinding.ActivityMyPropertyBinding
import com.dzinemedia.ownerpropertyfyapp.fragments.myPropertyFragments.AllFragment
import com.dzinemedia.ownerpropertyfyapp.fragments.myPropertyFragments.RentedFragment
import com.dzinemedia.ownerpropertyfyapp.fragments.myPropertyFragments.SaleFragment
import com.dzinemedia.ownerpropertyfyapp.fragments.myPropertyFragments.VacantFragment
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel.LoginModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerProperties.OwnerPropertiesModel
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

class MyPropertyActivity : AppCompatActivity() {
    private var viewPagerAdapter: MyPropertyPagerAdapter? = null
    private lateinit var binding: ActivityMyPropertyBinding
    private lateinit var viewModel: TenantViewModel
    private var ownerPropertyModel: OwnerPropertiesModel? = null
    private var jsonModel: LoginModel? = null
    var allFrgInstance: AllFragment? = null
    var rentedFrgInstance: RentedFragment? = null
    var vacantFrgInstance: VacantFragment? = null
    var saleFrgInstance: SaleFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_property)
        initializeControls()
        setViewClickListeners()
    }

    private fun setViewClickListeners() {
        binding.back.setOnClickListener(DebounceClickHandler {
            finish()
        })
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (Utils.isNetworkAvailable(this@MyPropertyActivity)) {
                jsonModel?.data?.token?.let { token ->
                    jsonModel?.data?.id?.let { ownerId ->
                        getOwnerProperties(token, ownerId.toInt())
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
                    if (Utils.isNetworkAvailable(this@MyPropertyActivity)) {
                        internetDialog.dismiss()
                        getOwnerProperties(token, ownerId)
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
        viewPagerAdapter = MyPropertyPagerAdapter(this)
        registerFragmentWithPager()
        binding.viewPager2.offscreenPageLimit = 1
        binding.viewPager2.adapter = viewPagerAdapter

        // Attach TabLayout to ViewPager
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = "All"
                1 -> tab.text = "Rented"
                2 -> tab.text = "Vacant"
                3 -> tab.text = "Sale"
                else -> tab.text = "Tab $position"
            }
        }.attach()
        val loginModel = SharedPreferencesHelper.getInstance(binding.root.context).getLoginModel()
        jsonModel = Gson().fromJson(loginModel, LoginModel::class.java)
        jsonModel?.data?.token?.let { token ->
            jsonModel?.data?.id?.let { ownerId ->
                getOwnerProperties(token, ownerId.toInt())
            }
        }
    }

    private fun registerFragmentWithPager() {
        allFrgInstance = AllFragment.newInstance()
        rentedFrgInstance = RentedFragment.newInstance()
        vacantFrgInstance = VacantFragment.newInstance()
        saleFrgInstance = SaleFragment.newInstance()

        viewPagerAdapter?.addFrag(allFrgInstance!!)
        viewPagerAdapter?.addFrag(rentedFrgInstance!!)
        viewPagerAdapter?.addFrag(vacantFrgInstance!!)
        viewPagerAdapter?.addFrag(saleFrgInstance!!)
    }
    private fun showProgress() {
        binding.shimmerLayout.visibility = View.VISIBLE
        binding.originalLayout.visibility = View.GONE
    }

    private fun hideProgress() {
        binding.shimmerLayout.visibility = View.GONE
        binding.originalLayout.visibility = View.VISIBLE
    }

    fun getOwnerPropertyModel(): OwnerPropertiesModel? {
        return ownerPropertyModel
    }

    private fun getOwnerProperties(token: String, ownerId: Int) {
        viewModel.loading.observe(this) { isProgress ->
            if (isProgress) {
                showProgress()
            } else {
                hideProgress()
            }
        }
        viewModel.ownerApiLiveData.observe(this) {
            if (it.success == true) {
                if (binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                ownerPropertyModel = it
                allFrgInstance?.getAllFrgData(it)
                rentedFrgInstance?.getRentedFrgData(it)
                vacantFrgInstance?.getVacantFrgData(it)
                saleFrgInstance?.getSaleFrgData(it)
                Log.i("TAG Owner", "getOwnerProperties: ${it.data?.size}")
            } else {
                if (binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                Utils.showToast(binding.root.context, it.message.toString())
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getOwnerProperties(token, ownerId, object : RetrofitMessageCallback {
                override fun retrofitErrorMessage(message: String) {
                    Utils.showToast(binding.root.context, message)
                }
            })
        }

    }
}