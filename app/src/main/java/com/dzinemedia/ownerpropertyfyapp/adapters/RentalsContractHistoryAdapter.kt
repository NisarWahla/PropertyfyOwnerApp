package com.dzinemedia.ownerpropertyfyapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.HistoryRentalItemCallback
import com.dzinemedia.ownerpropertyfyapp.databinding.RentalPaymentItemBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel.LoginModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview.RentalPlan
import com.dzinemedia.ownerpropertyfyapp.utility.SharedPreferencesHelper
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import com.google.gson.Gson

class RentalsContractHistoryAdapter(
    val rentalPlanArrayList: ArrayList<RentalPlan>,
    val listener: HistoryRentalItemCallback
) :
    RecyclerView.Adapter<RentalsContractHistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: RentalPaymentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, rentalModel: RentalPlan, listener: HistoryRentalItemCallback) {
            binding.root.setOnClickListener(DebounceClickHandler {
                listener.historyRentalItemClick(position, rentalModel)
            })
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: RentalPaymentItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.rental_payment_item,
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
            if (rentalModel.paymentStatus.equals("received", ignoreCase = true)) {
                holder.binding.checkValue.setBackgroundResource(R.drawable.status_info_received_round)
                holder.binding.checkValue.setTextColor(
                    ContextCompat.getColor(
                        holder.binding.root.context,
                        R.color.status_green_color
                    )
                )
                holder.binding.checkValue.text = rentalModel.paymentStatus.toString()
            } else if (rentalModel.paymentStatus.equals("pending", ignoreCase = true)) {
                holder.binding.checkValue.setBackgroundResource(R.drawable.pending_status_round)
                holder.binding.checkValue.setTextColor(
                    ContextCompat.getColor(
                        holder.binding.root.context,
                        R.color.payment_status_color
                    )
                )
                holder.binding.checkValue.text = rentalModel.paymentStatus.toString()
            } else if (rentalModel.paymentStatus.equals(
                    "overdue",
                    ignoreCase = true
                ) || rentalModel.paymentStatus.equals("bounced", ignoreCase = true)
            ) {
                holder.binding.checkValue.setBackgroundResource(R.drawable.overdue_status_round)
                holder.binding.checkValue.setTextColor(
                    ContextCompat.getColor(
                        holder.binding.root.context,
                        R.color.overdue_status_clr
                    )
                )
                holder.binding.checkValue.text = rentalModel.paymentStatus.toString()
            } else if (rentalModel.paymentStatus.equals("partial", ignoreCase = true)) {
                holder.binding.checkValue.setBackgroundResource(R.drawable.partial_status_round)
                holder.binding.checkValue.setTextColor(
                    ContextCompat.getColor(
                        holder.binding.root.context,
                        R.color.partial_text_color
                    )
                )
                holder.binding.checkValue.text = rentalModel.paymentStatus.toString()
            } else if (rentalModel.paymentStatus.equals("bank_deposit", ignoreCase = true)) {
                holder.binding.checkValue.setBackgroundResource(R.drawable.status_info_received_round)
                holder.binding.checkValue.setTextColor(
                    ContextCompat.getColor(
                        holder.binding.root.context,
                        R.color.status_green_color
                    )
                )
                holder.binding.checkValue.text = "Deposit"
            }
            it.bankTransfer.text = rentalModel.paymentMethod
        }
        holder.bind(position, rentalModel, listener)
    }

    override fun getItemCount(): Int {
        return rentalPlanArrayList.size
    }
}