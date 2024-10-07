package com.dzinemedia.ownerpropertyfyapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.ReceivedItemCallback
import com.dzinemedia.ownerpropertyfyapp.databinding.ReceivedRentalPaymentItemBinding
import com.dzinemedia.ownerpropertyfyapp.models.NotificationModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel.LoginModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview.RentalPlan
import com.dzinemedia.ownerpropertyfyapp.utility.SharedPreferencesHelper
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import com.google.gson.Gson

class ReceivedRentalPaymentAdapter(
    val rentalPaymentList: ArrayList<RentalPlan>,
    val listener: ReceivedItemCallback
) :
    RecyclerView.Adapter<ReceivedRentalPaymentAdapter.ViewHolder>() {

    class ViewHolder(val binding: ReceivedRentalPaymentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, rentalModel: RentalPlan, listener: ReceivedItemCallback) {
            binding.root.setOnClickListener(DebounceClickHandler {
                listener.receivedItemClick(position, rentalModel)
            })
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ReceivedRentalPaymentItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.received_rental_payment_item,
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
            it.timeAndDate.text = "Date: ${rentalModel.dueDate}"
            it.checkValue.text = rentalModel.paymentStatus
            it.bankTransfer.text = rentalModel.paymentMethod
        }
        holder.bind(position, rentalModel, listener)
    }

    override fun getItemCount(): Int {
        return rentalPaymentList.size
    }
}