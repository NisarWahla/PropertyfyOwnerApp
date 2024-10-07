package com.dzinemedia.ownerpropertyfyapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adapters.RentalsContractDocumentAdapter
import com.dzinemedia.ownerpropertyfyapp.databinding.ActivityManagementDetailBinding
import com.dzinemedia.ownerpropertyfyapp.models.NotificationModel
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler

class ManagementDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagementDetailBinding
    private var contractDocumentAdapter: RentalsContractDocumentAdapter? = null
    private val rentalsArrayList: ArrayList<NotificationModel> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_management_detail)
        initializeControls()
        setViewClickListeners()
    }

    private fun setViewClickListeners() {
        binding.back.setOnClickListener(DebounceClickHandler {
            finish()
        })
    }
    private fun initializeControls() {
        //////////////RentalsContractDocumentAdapter////////
        contractDocumentAdapter = RentalsContractDocumentAdapter(rentalsArrayList)
        val layoutManagerDocument =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        binding.pdfDocumentRecycler.layoutManager = layoutManagerDocument
        binding.pdfDocumentRecycler.adapter = contractDocumentAdapter
    }
}