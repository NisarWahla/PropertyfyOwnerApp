package com.dzinemedia.ownerpropertyfyapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.PartialItemCallback
import com.dzinemedia.ownerpropertyfyapp.databinding.PartialRentalPaymentItemBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel.LoginModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview.RentalPlan
import com.dzinemedia.ownerpropertyfyapp.utility.SharedPreferencesHelper
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import com.google.gson.Gson

class PartialRentalPaymentAdapter(val rentalPaymentList: ArrayList<RentalPlan>, val listener: PartialItemCallback) :
    RecyclerView.Adapter<PartialRentalPaymentAdapter.ViewHolder>() {

    class ViewHolder(val binding: PartialRentalPaymentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(position: Int, rentalPlan: RentalPlan, listener: PartialItemCallback) {
                binding.root.setOnClickListener(DebounceClickHandler {
                    listener.partialItemClick(position, rentalPlan)
                })
            }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: PartialRentalPaymentItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.partial_rental_payment_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rentalModel = rentalPaymentList[position]
        holder.binding.let {
            it.idValue.text = "ID: ${rentalModel.rpId}"
            if (rentalModel.paymentMethod.equals("cash", ignoreCase = true)) {
                it.checkRefValue.text = rentalModel.paymentMethod
            } else if (rentalModel.paymentMethod.equals("cheque", ignoreCase = true) || rentalModel.paymentMethod.equals("check", ignoreCase = true)) {
                it.checkRefValue.text = "Cheque/Ref: ${rentalModel.refNo}"
            }
            it.transferValue.text = "AED ${rentalModel.amount}"
            it.remainingAmount.text = "Remaining: AED ${rentalModel.partialSection?.remainingAmount}"
            it.checkValue.text = rentalModel.paymentStatus
            it.bankTransfer.text = rentalModel.paymentMethod
        }
        holder.bind(position, rentalModel, listener)

    }

    override fun getItemCount(): Int {
        return rentalPaymentList.size
    }
}