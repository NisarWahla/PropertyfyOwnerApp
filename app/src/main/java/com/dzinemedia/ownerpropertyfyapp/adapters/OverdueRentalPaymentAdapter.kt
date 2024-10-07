package com.dzinemedia.ownerpropertyfyapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.OverdueItemCallback
import com.dzinemedia.ownerpropertyfyapp.databinding.OverdueRentalPaymentItemBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel.LoginModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview.RentalPlan
import com.dzinemedia.ownerpropertyfyapp.utility.SharedPreferencesHelper
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import com.google.gson.Gson

class OverdueRentalPaymentAdapter(
    val rentalPlanArrayList: ArrayList<RentalPlan>,
    val listener: OverdueItemCallback
) :
    RecyclerView.Adapter<OverdueRentalPaymentAdapter.ViewHolder>() {

    class ViewHolder(val binding: OverdueRentalPaymentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, listener: OverdueItemCallback, rentalPlan: RentalPlan) {
            binding.root.setOnClickListener(DebounceClickHandler {
                listener.overdueItemClick(position,rentalPlan)
            })
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: OverdueRentalPaymentItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.overdue_rental_payment_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rentalModel = rentalPlanArrayList[position]
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
        holder.bind(position, listener, rentalModel)
    }

    override fun getItemCount(): Int {
        return rentalPlanArrayList.size
    }
}