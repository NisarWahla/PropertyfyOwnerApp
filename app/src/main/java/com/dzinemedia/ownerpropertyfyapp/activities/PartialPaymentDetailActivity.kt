package com.dzinemedia.ownerpropertyfyapp.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adapters.PreviousPaymentAdapter
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.InternetCallback
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.RetrofitMessageCallback
import com.dzinemedia.ownerpropertyfyapp.apiInterface.RetrofitClient
import com.dzinemedia.ownerpropertyfyapp.customeDialog.InternetDialogFragment
import com.dzinemedia.ownerpropertyfyapp.databinding.ActivityPartialPaymentDetailBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.OwnerPropertyRentalPaymentOverviewModel.PartialPaymentHistory
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel.LoginModel
import com.dzinemedia.ownerpropertyfyapp.repository.TenantRepository
import com.dzinemedia.ownerpropertyfyapp.utility.SharedPreferencesHelper
import com.dzinemedia.ownerpropertyfyapp.utility.Utils
import com.dzinemedia.ownerpropertyfyapp.viewModel.TenantViewModel
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PartialPaymentDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPartialPaymentDetailBinding
    private var previousPaymentAdapter: PreviousPaymentAdapter? = null
    private val partialPaymentHistoryArrayList: ArrayList<PartialPaymentHistory> = ArrayList()
    private lateinit var viewModel: TenantViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_partial_payment_detail)
        initializeControls()
        setViewClickListeners()
    }

    private fun setViewClickListeners() {
        binding.back.setOnClickListener(DebounceClickHandler {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        })
    }

    private fun initializeControls() {
        val apiInterface = RetrofitClient.create()
        val repository = TenantRepository(apiInterface)
        viewModel = ViewModelProvider(
            this, TenantViewModel.TenantViewModalFactory(repository)
        )[TenantViewModel::class.java]

        previousPaymentAdapter = PreviousPaymentAdapter(partialPaymentHistoryArrayList)
        val layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        binding.previousPaymentRecyclerview.layoutManager = layoutManager
        binding.previousPaymentRecyclerview.adapter = previousPaymentAdapter

        val loginModel = SharedPreferencesHelper.getInstance(binding.root.context).getLoginModel()
        val jsonModel = Gson().fromJson(loginModel, LoginModel::class.java)
        val token = jsonModel.data?.token
        val ownerId = jsonModel.data?.id
        if (intent.hasExtra("propertyId") && intent.hasExtra("rentalId") && intent.hasExtra("rentalPaymentId")) {
            val propertyId = intent.getStringExtra("propertyId")
            val rentalId = intent.getStringExtra("rentalId")
            val rentalPaymentId = intent.getStringExtra("rentalPaymentId")
            if (token != null) {
                if (Utils.isNetworkAvailable(this)) {
                    getOwnerPropertyRentalPaymentOverview(
                        token,
                        ownerId!!.toInt(),
                        rentalId!!.toInt(),
                        propertyId!!.toInt(),
                        rentalPaymentId!!.toInt()
                    )
                } else {
                    showInternetDialog(
                        token,
                        ownerId!!.toInt(),
                        rentalId!!.toInt(),
                        propertyId!!.toInt(),
                        rentalPaymentId!!.toInt()
                    )
                }
            }
        }
    }

    private fun showInternetDialog(
        token: String, ownerId: Int, rentalId: Int, propertyId: Int, rentalPaymentId: Int
    ) {
        val internetDialog = InternetDialogFragment(object : InternetCallback {
            override fun tryAgainClick(internetDialog: InternetDialogFragment) {
                if (Utils.isNetworkAvailable(this@PartialPaymentDetailActivity)) {
                    internetDialog.dismiss()
                    getOwnerPropertyRentalPaymentOverview(
                        token, ownerId, rentalId, propertyId, rentalPaymentId
                    )
                } else {
                    internetDialog.dismiss()
                    showInternetDialog(token, ownerId, rentalId, propertyId, rentalPaymentId)
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

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun getOwnerPropertyRentalPaymentOverview(
        token: String, ownerId: Int, rentalId: Int, propertyId: Int, rentalPaymentId: Int
    ) {
        viewModel.loading.observe(this) { isProgress ->
            if (isProgress) {
                showProgress()
            } else {
                hideProgress()
            }
        }
        viewModel.ownerPropertyRentalPaymentOverviewApiLiveData.observe(this) {
            if (it.success == true) {
                binding.paymentIdValue.text = it.data?.rpId.toString()
                binding.invoiceIdValue.text = it.data?.rpId.toString()
                binding.txtLogin.text = "Invoice ID: ${it.data?.rpId.toString()}"
                binding.propertyIdValue.text = it.data?.propertyId?.mpId.toString()
                binding.rentalAmountValue.text = "AED ${it.data?.amount.toString()}"
                //return received value
                binding.receivedValue.text = "AED ${it.data?.partialSection?.paidAmount}"
                //return remaining value in api calls
                binding.remainingValue.text = "AED ${it.data?.partialSection?.remainingAmount}"
                binding.paymentStatusValue.text = it.data?.paymentStatus
                binding.dueDateValue.text = it.data?.dueDate
                binding.paymentDateValue.text = it.data?.paymentStatusDate
                //binding.addressValue.text = it.data?.propertyId?.address.toString()
                partialPaymentHistoryArrayList.clear()
                partialPaymentHistoryArrayList.addAll(it.data?.partialPaymentHistory as ArrayList<PartialPaymentHistory>)
                previousPaymentAdapter?.notifyDataSetChanged()

            } else {
                Utils.showToast(binding.root.context, it.message.toString())
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getOwnerPropertyRentalPaymentOverview(token,
                ownerId,
                rentalId,
                propertyId,
                rentalPaymentId,
                object : RetrofitMessageCallback {
                    override fun retrofitErrorMessage(message: String) {
                        Utils.showToast(binding.root.context, message)
                    }
                })
        }
    }
}