package com.dzinemedia.ownerpropertyfyapp.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.databinding.ActivityBillsPaidDetailBinding
import com.dzinemedia.ownerpropertyfyapp.utility.Utils
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler

class BillsPaidDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBillsPaidDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bills_paid_detail)
        initializeViews()
        setViewClickListener()
    }

    @SuppressLint("SetTextI18n")
    private fun initializeViews() {
        if (intent.hasExtra("bmId")) {
            val bmId = intent.getStringExtra("bmId")
            val refNo = intent.getStringExtra("refNo")
            val paymentMethod = intent.getStringExtra("paymentMethod")
            val amountPaid = intent.getStringExtra("amountPaid")
            val paidStatus = intent.getStringExtra("paidStatus")
            val paidDate = intent.getStringExtra("paidDate")
            val payer = intent.getStringExtra("payer")
            val paymentReceipt = intent.getStringExtra("PaymentReceipt")
            val billCategory = intent.getStringExtra("billCategory")
            binding.invoiceIdValue.text = bmId
            if (refNo == null || refNo == "null") {
                binding.refCheckValue.text = "0"
            } else {
                binding.refCheckValue.text = refNo
            }
            binding.paymentMethodValue.text = paymentMethod
            binding.amountPaidValue.text = "AED $amountPaid"
            binding.paymentStatusValue.text = paidStatus
            val splitDate = paidDate?.split(" ")
            if (splitDate != null) {
                if (splitDate.isNotEmpty()) {
                    val changeFormat = splitDate[0].let { Utils.changeDateFormat(it) }
                    binding.paidDate.text = "$changeFormat"
                }
            }
            binding.payerValue.text = payer
            binding.txtLogin.text = "Bill $bmId"
            Glide.with(binding.root.context).load(paymentReceipt).diskCacheStrategy(
                DiskCacheStrategy.ALL
            ).into(binding.paymentReceiptImg)
            if (billCategory != null) {
                val billCat = billCategory.split("_")
                val firstWord = billCat[0].capitalizeFirstLetter()
                val secondWord = billCat[1].capitalizeFirstLetter()
                binding.billCategoryValue.text = "$firstWord $secondWord"
            } else {
                binding.billCategoryValue.text = ""
            }
        }
    }

    private fun String.capitalizeFirstLetter(): String {
        return if (this.isNotEmpty()) {
            this.substring(0, 1).uppercase() + this.substring(1)
        } else {
            this
        }
    }

    private fun setViewClickListener() {
        binding.back.setOnClickListener(DebounceClickHandler {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        })
    }
}