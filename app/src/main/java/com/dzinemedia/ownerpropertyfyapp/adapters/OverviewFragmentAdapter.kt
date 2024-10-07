package com.dzinemedia.ownerpropertyfyapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.OverviewFragmentItemCallback
import com.dzinemedia.ownerpropertyfyapp.databinding.OverviewItemBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyOverview.RentalPayment
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler

class OverviewFragmentAdapter(
    val overviewList: ArrayList<RentalPayment>,
    val listener: OverviewFragmentItemCallback
) :
    RecyclerView.Adapter<OverviewFragmentAdapter.ViewHolder>() {

    class ViewHolder(val binding: OverviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            position: Int,
            rentalModel: RentalPayment,
            listener: OverviewFragmentItemCallback
        ) {
            binding.root.setOnClickListener(DebounceClickHandler {
                listener.overviewItemClick(position, rentalModel)
            })
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: OverviewItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.overview_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rentalModel = overviewList[position]
        holder.binding.let {
            it.idValue.text = "ID: ${rentalModel.rpId}"
            if (rentalModel.paymentMethod.equals("cash", ignoreCase = true)) {
                it.checkRefValue.text = rentalModel.paymentMethod
            } else if (rentalModel.paymentMethod.equals("cheque", ignoreCase = true) || rentalModel.paymentMethod.equals("check", ignoreCase = true)) {
                it.checkRefValue.text = "Cheque/Ref: ${rentalModel.refNo}"
            }
            it.transferValue.text = "AED ${rentalModel.amount}"
            it.timeAndDate.text = "Date: ${rentalModel.dueDate}"
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
        return overviewList.size
    }
}