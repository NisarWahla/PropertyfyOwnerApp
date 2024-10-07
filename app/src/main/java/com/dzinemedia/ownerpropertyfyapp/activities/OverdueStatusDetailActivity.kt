package com.dzinemedia.ownerpropertyfyapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.InternetCallback
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.RetrofitMessageCallback
import com.dzinemedia.ownerpropertyfyapp.apiInterface.RetrofitClient
import com.dzinemedia.ownerpropertyfyapp.customeDialog.InternetDialogFragment
import com.dzinemedia.ownerpropertyfyapp.databinding.ActivityOverdueStatusDetailBinding
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

class OverdueStatusDetailActivity : AppCompatActivity() {
    private var url: String? = ""
    private lateinit var binding: ActivityOverdueStatusDetailBinding
    private lateinit var viewModel: TenantViewModel
    private val TAG = "OverdueStatusDetailActivity"
    private var paymentStatus: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_overdue_status_detail)
        initializedControls()
        setViewClickListeners()
    }

    private fun initializedControls() {
        val apiInterface = RetrofitClient.create()
        val repository = TenantRepository(apiInterface)
        viewModel = ViewModelProvider(
            this,
            TenantViewModel.TenantViewModalFactory(repository)
        )[TenantViewModel::class.java]
        val loginModel = SharedPreferencesHelper.getInstance(binding.root.context).getLoginModel()
        val jsonModel = Gson().fromJson(loginModel, LoginModel::class.java)
        val token = jsonModel.data?.token
        val ownerId = jsonModel.data?.id
        if (intent.hasExtra("propertyId") && intent.hasExtra("rentalId") && intent.hasExtra("rentalPaymentId")) {
            val propertyId = intent.getStringExtra("propertyId")
            val rentalId = intent.getStringExtra("rentalId")
            val rentalPaymentId = intent.getStringExtra("rentalPaymentId")
            val depositBank: String = intent.getStringExtra("depositBank")!!
            paymentStatus = intent.getStringExtra("paymentStatus")
            if (token != null) {
                if (Utils.isNetworkAvailable(this)) {
                    getPropertyRentalPaymentOverview(
                        token,
                        ownerId!!.toInt(),
                        rentalId!!.toInt(),
                        propertyId!!.toInt(),
                        rentalPaymentId!!.toInt(),
                        depositBank
                    )
                } else {
                    showInternetDialog(
                        token,
                        ownerId!!.toInt(),
                        rentalId!!.toInt(),
                        propertyId!!.toInt(),
                        rentalPaymentId!!.toInt(),
                        depositBank
                    )
                }
            }
        }
    }

    private fun showInternetDialog(
        token: String,
        ownerId: Int,
        rentalId: Int,
        propertyId: Int,
        rentalPaymentId: Int,
        depositBank: String
    ) {
        val internetDialog =
            InternetDialogFragment(object : InternetCallback {
                override fun tryAgainClick(internetDialog: InternetDialogFragment) {
                    if (Utils.isNetworkAvailable(this@OverdueStatusDetailActivity)) {
                        internetDialog.dismiss()
                        getPropertyRentalPaymentOverview(
                            token,
                            ownerId,
                            rentalId,
                            propertyId,
                            rentalPaymentId,
                            depositBank
                        )
                    } else {
                        internetDialog.dismiss()
                        showInternetDialog(
                            token,
                            ownerId,
                            rentalId,
                            propertyId,
                            rentalPaymentId,
                            depositBank
                        )
                    }
                }
            })
        internetDialog.show(supportFragmentManager, internetDialog.tag)
    }

    private fun setViewClickListeners() {
        binding.back.setOnClickListener(DebounceClickHandler {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        })
        binding.proofImg.setOnClickListener(DebounceClickHandler {
            val intent = Intent(this, PDFViewerActivity::class.java)
            intent.putExtra("url", url)
            val options = ActivityOptionsCompat.makeCustomAnimation(
                this,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            startActivity(intent, options.toBundle())
        })
    }

    private fun showProgress() {
        hideOverduePendingPaymentField()
        binding.originalLayout.visibility = View.GONE
        binding.shimmerLayout.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        hideOverduePendingPaymentField()
        binding.shimmerLayout.visibility = View.GONE
        binding.originalLayout.visibility = View.VISIBLE

    }

    private fun hideOverduePendingPaymentField() {
        if (paymentStatus.equals("overdue", true)) {
            binding.shimmerPaymentDateValue.visibility = View.GONE
            binding.shimmerPaymentDate.visibility = View.GONE
            binding.shimmerBounceProof.visibility = View.GONE
            binding.shimmerProofImg.visibility = View.GONE
            binding.shimmerViewSix.visibility - View.GONE
            binding.shimmerViewSeven.visibility - View.GONE
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun getPropertyRentalPaymentOverview(
        token: String,
        ownerId:Int,
        rentalId: Int,
        propertyId: Int,
        rentalPaymentId: Int,
        depositBank: String
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
                binding.txtLogin.text = "Invoice ID: ${it.data?.rpId.toString()}"
                binding.propertyIdValue.text = it.data?.propertyId?.mpId.toString()
                binding.beneficiaryValue.text = it.data?.ownerBeneficiary.toString()
                if (it.data?.refNo.toString() == "" || it.data?.refNo.toString()
                        .equals(null) || it.data?.refNo.toString() == "null"
                ) {
                    binding.checkNoValue.text = "0"
                } else {
                    binding.checkNoValue.text = it.data?.refNo.toString()
                }
                binding.bounceDateValue.text = it.data?.dueDate.toString()
                binding.rentalAmountValue.text = "AED ${it.data?.amount.toString()}"
                if (it.data?.paymentStatus.equals(
                        "Pending",
                        ignoreCase = true
                    )
                ) {
                    it.data?.dueDate?.let { date ->
                        val status = Utils.getDateStatus(
                            date
                        )
                        it.data?.paymentStatus =
                            status
                    }
                }
                binding.paymentStatusValue.text = it.data?.paymentStatus.toString()
                binding.addressValue.text = it.data?.propertyId?.address.toString()
                binding.ownerValue.text =
                    it.data?.propertyId?.contracts?.get(0)?.ownerId?.name.toString()
                binding.rentalAmountValueTwo.text =
                    "(${it.data?.propertyId?.tenantsDetails?.get(0)?.tenantName})"
                binding.payerValue.text = it.data?.propertyId?.tenantsDetails?.get(0)?.tenantName
                if (it.data?.bankName.toString() == "" || it.data?.bankName.toString().equals(null) || it.data?.bankName.toString() == "null") {
                    binding.checkNoValueTwo.text = ""
                    binding.bankValue.text = ""
                } else {
                    binding.checkNoValueTwo.text = "(${it.data?.bankName})"
                    binding.bankValue.text = it.data?.bankName.toString()
                }
                if (it.data?.paymentStatus.equals("overdue", true)) {
                    binding.paymentDateValue.visibility = View.GONE
                    binding.paymentDate.visibility = View.GONE
                    binding.bounceProof.visibility = View.GONE
                    binding.proofImg.visibility = View.GONE
                    binding.viewSeven.visibility = View.GONE
                } else {
                    binding.paymentDateValue.text = it.data?.paymentStatusDate
                    if (it.data?.receipt?.isNotEmpty() == true) {
                        binding.proofImg.visibility = View.VISIBLE
                        url = it.data?.receipt?.get(0).toString()
                        if (url?.contains(".pdf") == true) {
                            Log.i(TAG, "getPropertyRentalPaymentOverview: PDF")
                        } else {
                            Glide.with(binding.root.context).load(it.data?.receipt?.get(0))
                                .diskCacheStrategy(
                                    DiskCacheStrategy.ALL
                                ).into(binding.proofImg)
                        }
                    } else {
                        binding.proofImg.visibility = View.GONE
                    }
                }

            } else {
                Utils.showToast(binding.root.context, it.message.toString())
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getOwnerPropertyRentalPaymentOverview(
                token,
                ownerId,
                rentalId,
                propertyId,
                rentalPaymentId,
                object : RetrofitMessageCallback {
                    override fun retrofitErrorMessage(message: String) {
                        Utils.showToast(binding.root.context, message)
                    }
                }
            )
        }
    }

/*    @SuppressLint("SetTextI18n")
    private fun initializedControls() {
        if (intent.hasExtra("rentalPlan")) {
            rentalPlan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra("rentalPlan", RentalPlan::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getSerializableExtra("rentalPlan") as? RentalPlan
            }
        }
        rentalPlan?.let {
            binding.paymentIdValue.text = it.rpId.toString()
            binding.txtLogin.text = "Rental ID: ${it.rpId.toString()}"
            binding.propertyIdValue.text = it.propertyId?.mpId.toString()
            binding.beneficiaryValue.text = it.ownerBeneficiary.toString()
            binding.checkNoValue.text = it.refNo.toString()
            binding.bounceDateValue.text = it.dueDate.toString()
            binding.rentalAmountValue.text = "AED ${it.amount.toString()}"
            if (it.paymentStatus.equals(
                    "Pending",
                    ignoreCase = true
                )
            ) {
                it.dueDate?.let { date ->
                    val status = Utils.getDateStatus(
                        date
                    )
                    it.paymentStatus =
                        status
                }
            }
            binding.paymentStatusValue.text = it.paymentStatus.toString()
            binding.addressValue.text = it.propertyId?.address.toString()
            binding.ownerValue.text =
                it.propertyId?.contracts?.get(0)?.ownerId?.name.toString()
            binding.rentalAmountValueTwo.text =
                "(${it.propertyId?.tenantsDetails?.get(0)?.tenantName})"
            binding.payerValue.text = it.propertyId?.tenantsDetails?.get(0)?.tenantName
            if (it.depositBank == null || it.depositBank?.equals("null") == true) {
                binding.bankValue.text = ""
                binding.checkNoValueTwo.text = ""
            } else {
                binding.bankValue.text = it.depositBank.toString()
                binding.checkNoValueTwo.text = "(${it.depositBank.toString()})"
            }
            Glide.with(this).load(it.paymentImage).into(binding.proofImg)
            if (it.paymentStatus.equals("overdue", true)) {
                binding.paymentDateValue.visibility = View.GONE
                binding.paymentDate.visibility = View.GONE
                binding.viewSeven.visibility = View.GONE
                binding.bounceDate.text = "Overdue Date"
                binding.bounceProof.visibility = View.GONE
                binding.proofImg.visibility = View.GONE
            } else {
                binding.paymentDateValue.text = it.paymentStatusDate
            }
        }
    }

    private fun setViewClickListeners() {
        binding.back.setOnClickListener(DebounceClickHandler {
            finish()
        })
    }*/
}