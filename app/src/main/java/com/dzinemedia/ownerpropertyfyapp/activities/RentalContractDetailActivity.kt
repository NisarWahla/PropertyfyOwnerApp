package com.dzinemedia.ownerpropertyfyapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adapters.RentalsContractDocumentAdapter
import com.dzinemedia.ownerpropertyfyapp.adapters.RentalsContractHistoryAdapter
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.HistoryRentalItemCallback
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.InternetCallback
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.RetrofitMessageCallback
import com.dzinemedia.ownerpropertyfyapp.apiInterface.RetrofitClient
import com.dzinemedia.ownerpropertyfyapp.customeDialog.InternetDialogFragment
import com.dzinemedia.ownerpropertyfyapp.databinding.ActivityRentalContractDetailBinding
import com.dzinemedia.ownerpropertyfyapp.models.NotificationModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel.LoginModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview.RentalPlan
import com.dzinemedia.ownerpropertyfyapp.repository.TenantRepository
import com.dzinemedia.ownerpropertyfyapp.utility.SharedPreferencesHelper
import com.dzinemedia.ownerpropertyfyapp.utility.Utils
import com.dzinemedia.ownerpropertyfyapp.viewModel.TenantViewModel
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RentalContractDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRentalContractDetailBinding
    private var contractPaymentAdapter: RentalsContractHistoryAdapter? = null
    private var contractDocumentAdapter: RentalsContractDocumentAdapter? = null
    private val contractPaymentList: ArrayList<RentalPlan> = ArrayList()
    private val rentalsArrayList: ArrayList<NotificationModel> = ArrayList()
    private lateinit var viewModel: TenantViewModel
    private var rentalId: String = "0"
    private var propertyId: String = "0"
    private var isItemClicked = true
    private var token: String = ""
    private var userId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rental_contract_detail)
        initializeControls()
        setViewClickListeners()
    }

    private fun setViewClickListeners() {
        binding.back.setOnClickListener(DebounceClickHandler {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        })
        binding.tvAllMenu.setOnClickListener(DebounceClickHandler {
            showPopupMenu(it)
        })
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (Utils.isNetworkAvailable(this)) {
                getPropertyRentalsOverview(token, userId, rentalId.toInt(), propertyId.toInt())
            } else {
                if (binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                showInternetDialog(token, userId, rentalId.toInt(), propertyId.toInt())
            }
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.main_contract_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_all -> {
                    binding.tvAllMenu.text = binding.root.context.getString(R.string.all)
                    true
                }
                R.id.menu_pending -> {
                    binding.tvAllMenu.text = binding.root.context.getString(R.string.pending)
                    true
                }
                R.id.menu_partial -> {
                    binding.tvAllMenu.text = binding.root.context.getString(R.string.partial)
                    true
                }
                R.id.menu_overdue -> {
                    binding.tvAllMenu.text = binding.root.context.getString(R.string.overdue)
                    true
                }
                R.id.menu_bounce -> {
                    binding.tvAllMenu.text = binding.root.context.getString(R.string.bounced)
                    true
                }
                R.id.menu_received -> {
                    binding.tvAllMenu.text = binding.root.context.getString(R.string.menu_received)
                    true
                }
                R.id.menu_deposit -> {
                    binding.tvAllMenu.text = binding.root.context.getString(R.string.deposit)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun initializeControls() {
        val apiInterface = RetrofitClient.create()
        val repository = TenantRepository(apiInterface)
        viewModel = ViewModelProvider(
            this,
            TenantViewModel.TenantViewModalFactory(repository)
        )[TenantViewModel::class.java]
        val loginModel = SharedPreferencesHelper.getInstance(binding.root.context).getLoginModel()
        val jsonModel = Gson().fromJson(loginModel, LoginModel::class.java)
        token = jsonModel.data?.token.toString()
        userId = jsonModel.data?.id!!.toInt()
        if (intent.hasExtra("propertyId") && intent.hasExtra("rentalId")) {
            propertyId = intent.getStringExtra("propertyId")!!
            rentalId = intent.getStringExtra("rentalId")!!
            if (Utils.isNetworkAvailable(this)) {
                getPropertyRentalsOverview(token, userId, rentalId.toInt(), propertyId.toInt())
            } else {
                showInternetDialog(token, userId, rentalId.toInt(), propertyId.toInt())
            }
        }

        /////////////Payment Adapter//////////////
        contractPaymentAdapter = RentalsContractHistoryAdapter(contractPaymentList, object :
            HistoryRentalItemCallback {
            override fun historyRentalItemClick(position: Int, rentalPlanModel: RentalPlan) {
                if (isItemClicked) {
                    isItemClicked = false
                    if (rentalPlanModel.paymentStatus?.equals("partial", true) == true) {
                        val intent =
                            Intent(
                                this@RentalContractDetailActivity,
                                PartialPaymentDetailActivity::class.java
                            )
                        intent.putExtra("propertyId", rentalPlanModel.propertyId?.propertyId)
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
                            this@RentalContractDetailActivity,
                            RentalHistoryDetailActivity::class.java
                        )
                        intent.putExtra("propertyId", propertyId)
                        intent.putExtra("rentalId", rentalId)
                        intent.putExtra("rentalPaymentId", rentalPlanModel.id)
                        intent.putExtra("depositBank", rentalPlanModel.getDepositBank())
                        intent.putExtra("paymentStatus", rentalPlanModel.paymentStatus)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    isItemClicked = true
                }, 1000)
            }

        })
        val layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        binding.historyRecyclerview.layoutManager = layoutManager
        binding.historyRecyclerview.adapter = contractPaymentAdapter

        /////////////Document Adapter//////////////
        contractDocumentAdapter = RentalsContractDocumentAdapter(rentalsArrayList)
        val layoutManagerDocument =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        binding.pdfDocumentRecycler.layoutManager = layoutManagerDocument
        binding.pdfDocumentRecycler.adapter = contractDocumentAdapter
    }

    private fun showInternetDialog(
        token: String,
        userId: Int,
        rentalId: Int,
        propertyId: Int
    ) {
        val internetDialog =
            InternetDialogFragment(object : InternetCallback {
                override fun tryAgainClick(internetDialog: InternetDialogFragment) {
                    if (Utils.isNetworkAvailable(this@RentalContractDetailActivity)) {
                        internetDialog.dismiss()
                        getPropertyRentalsOverview(token, userId, rentalId, propertyId)
                    } else {
                        internetDialog.dismiss()
                        showInternetDialog(token, userId, rentalId, propertyId)
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
        userId: Int,
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
                binding.txtContractId.text = "Contract ID: ${it.data?.rcId.toString()}"
                binding.totalRentValue.text = "AED ${it.data?.rentalAmount.toString()}"
                if (it.data?.securityDeposit == null) {
                    binding.txtSecurityDepositValue.text = " "
                } else {
                    binding.txtSecurityDepositValue.text =
                        "AED ${it.data?.securityDeposit.toString()}"
                }

                binding.rentPaidValue.text = "AED ${it.data?.rentPaid.toString()}"
                binding.remaingingRentValue.text = "AED ${it.data?.rentPending.toString()}"
                binding.timeAndDate.text =
                    "${it.data?.contractDurationFrom} - ${it.data?.contractDurationTo}"
                if (it.data?.contractDurationFrom != "" && it.data?.contractDurationTo != "") {
                    val dateDifference = Utils.getDateDifference(
                        it.data?.contractDurationFrom,
                        it.data?.contractDurationTo,
                        "dd/MM/yyyy"
                    )
                    if (dateDifference == "12 months") {
                        binding.checkValue.text = "$dateDifference"
                    } else {
                        binding.checkValue.text = "$dateDifference"
                    }
                }
                contractPaymentList.clear()
                if (it.data?.rentalPlan?.size!! > 0) {
                    for (i in 0 until it.data?.rentalPlan?.size!!) {
                        if (it.data?.rentalPlan?.get(i)?.paymentStatus.equals(
                                "Pending",
                                ignoreCase = true
                            )
                        ) {
                            it.data?.rentalPlan?.get(i)?.dueDate?.let { date ->
                                val status = Utils.getDateStatus(
                                    date
                                )
                                it.data?.rentalPlan?.get(i)?.paymentStatus =
                                    status
                            }
                        }
                    }
                    contractPaymentList.addAll(it.data?.rentalPlan as ArrayList<RentalPlan>)
                } else {
                    contractPaymentList.addAll(it.data?.rentalPlan as ArrayList<RentalPlan>)
                }
                contractPaymentAdapter?.notifyDataSetChanged()

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
                userId,
                rentalId,
                propertyId,
                object : RetrofitMessageCallback {
                    override fun retrofitErrorMessage(message: String) {
                        Utils.showToast(binding.root.context, message)
                    }

                })
        }
    }
}