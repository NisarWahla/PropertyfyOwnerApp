package com.dzinemedia.ownerpropertyfyapp.adapters

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.RentalItemClick
import com.dzinemedia.ownerpropertyfyapp.databinding.TenantRentalsFragmentItemBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.tenantOverviewModel.RentalContract
import com.dzinemedia.ownerpropertyfyapp.utility.Utils
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler

class TenantRentalsFragmentAdapter(
    val contractArrayList: ArrayList<RentalContract>,
    val listener: RentalItemClick
) :
    RecyclerView.Adapter<TenantRentalsFragmentAdapter.ViewHolder>() {

    class ViewHolder(val binding: TenantRentalsFragmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, listener: RentalItemClick, rentalsModel: RentalContract) {
            binding.root.setOnClickListener(DebounceClickHandler {
                listener.rentalsClick(position, rentalsModel)
            })
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: TenantRentalsFragmentItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.tenant_rentals_fragment_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rentalsModel = contractArrayList[position]
        holder.binding.idValue.text = "Contract ID: ${rentalsModel.rcId}"
        holder.binding.duration.text =
            "Duration: ${rentalsModel.contractDurationFrom} - ${rentalsModel.contractDurationTo}"
        if (rentalsModel.contractDurationFrom != "" && rentalsModel.contractDurationTo != "") {
            val (years, months, days) = Utils.calculateDifferenceBetweenDates(
                rentalsModel.contractDurationFrom,
                rentalsModel.contractDurationTo,
                "dd/MM/yyyy"
            )
            if (years > 0) {
                val finalValue: String = if (months > 0) {
                    "$years years, $months months"
                } else if (days > 0) {
                    "$years years, $months months, $days days"
                } else {
                    "$years years"
                }
                holder.binding.timeAndDate.text = finalValue
            } else if (months > 0) {
                val finalValue: String = if (days > 0) {
                    "$months months, $days days"
                } else {
                    "$months months"
                }
                holder.binding.timeAndDate.text = finalValue
            } else {
                holder.binding.timeAndDate.text = "$days days"
            }
        }
        holder.binding.duration.ellipsize = TextUtils.TruncateAt.MARQUEE
        holder.binding.duration.isSingleLine = true
        holder.binding.duration.isSelected = true
        holder.binding.rentalsAmount.text = "AED ${rentalsModel.rentalAmount}"
        /*if (rentalsModel.isActive == true) {
            holder.binding.checkValue.setTextColor(
                ContextCompat.getColor(
                    holder.binding.root.context,
                    R.color.status_green_color
                )
            )
            holder.binding.checkValue.setBackgroundResource(R.drawable.status_info_received_round)
            holder.binding.checkValue.text = "Active"
        } else {
            holder.binding.checkValue.setTextColor(
                ContextCompat.getColor(
                    holder.binding.root.context,
                    R.color.overdue_status_clr
                )
            )
            holder.binding.checkValue.setBackgroundResource(R.drawable.overdue_status_round)
            holder.binding.checkValue.text = "Expired"
        }*/
        holder.bind(position, listener, rentalsModel)
    }

    override fun getItemCount(): Int {
        return contractArrayList.size
    }
}