package com.dzinemedia.ownerpropertyfyapp.adapters

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.RentalRowClick
import com.dzinemedia.ownerpropertyfyapp.databinding.RentalsFragmentItemBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyOverview.RentalContract
import com.dzinemedia.ownerpropertyfyapp.utility.Utils
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler

class RentalsFragmentAdapter(
    val notificationList: ArrayList<RentalContract>,
    val listener: RentalRowClick
) :
    RecyclerView.Adapter<RentalsFragmentAdapter.ViewHolder>() {

    class ViewHolder(val binding: RentalsFragmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, listener: RentalRowClick, rentalContract: RentalContract) {
            binding.root.setOnClickListener(DebounceClickHandler {
                listener.rentalsClick(position, rentalContract)
            })
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: RentalsFragmentItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.rentals_fragment_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rentalsModel = notificationList[position]
        holder.binding.idValue.text = "Contract ID: ${rentalsModel.rcId}"
        holder.binding.duration.text =
            "Duration: ${rentalsModel.contractDurationFrom} - ${rentalsModel.contractDurationTo}"
        if (rentalsModel.contractDurationFrom != "" && rentalsModel.contractDurationTo != "") {
            val dateDifference = Utils.getDateDifference(
                rentalsModel.contractDurationFrom,
                rentalsModel.contractDurationTo,
                "dd/MM/yyyy"
            )
            if (dateDifference == "12 months") {
                holder.binding.timeAndDate.text = "Term: 1 years"
            } else {
                holder.binding.timeAndDate.text = "Term: $dateDifference"
            }
        }
        holder.binding.duration.ellipsize = TextUtils.TruncateAt.MARQUEE
        holder.binding.duration.isSingleLine = true
        holder.binding.duration.isSelected = true
        holder.binding.rentalsAmount.text = "AED ${rentalsModel.rentalAmount}"
        if (rentalsModel.isActive == true) {
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
        }

        holder.bind(position, listener, rentalsModel)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }
}