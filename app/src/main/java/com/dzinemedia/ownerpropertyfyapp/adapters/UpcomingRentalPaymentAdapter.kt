package com.dzinemedia.ownerpropertyfyapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.UpcomingItemCallback
import com.dzinemedia.ownerpropertyfyapp.databinding.UpcomingRentalPaymentItemBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview.RentalPlan
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler

class UpcomingRentalPaymentAdapter(
    val rentalPaymentList: ArrayList<RentalPlan>,
    val upcomingItemCallback: UpcomingItemCallback
) :
    RecyclerView.Adapter<UpcomingRentalPaymentAdapter.ViewHolder>() {

    class ViewHolder(val binding: UpcomingRentalPaymentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            position: Int,
            upcomingItemCallback: UpcomingItemCallback,
            rentalPayment: RentalPlan
        ) {
            binding.root.setOnClickListener(DebounceClickHandler {
                upcomingItemCallback.upcomingItemClick(position, rentalPayment)
            })
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: UpcomingRentalPaymentItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.upcoming_rental_payment_item,
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
            } else if (rentalModel.paymentMethod.equals(
                    "cheque",
                    ignoreCase = true
                ) || rentalModel.paymentMethod.equals("check", ignoreCase = true)
            ) {
                it.checkRefValue.text = "Cheque/Ref: ${rentalModel.refNo}"
            }
            it.transferValue.text = "AED ${rentalModel.amount}"
            it.timeAndDate.text = "Date: ${rentalModel.dueDate}"
            if (rentalModel.paymentStatus.equals("bank_deposit", ignoreCase = true)) {
                it.checkValue.text = "Deposit"
            } else {
                it.checkValue.text = rentalModel.paymentStatus
            }
            it.bankTransfer.text = rentalModel.paymentMethod
        }
        holder.bind(position, upcomingItemCallback, rentalModel)
    }

    override fun getItemCount(): Int {
        return rentalPaymentList.size
    }
}