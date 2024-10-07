package com.dzinemedia.ownerpropertyfyapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.RentalItemClick
import com.dzinemedia.ownerpropertyfyapp.databinding.ManagementFragmentItemBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.tenantOverviewModel.RentalContract
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler

class ManagementFragmentAdapter(
    val notificationList: ArrayList<RentalContract>,
    val listener: RentalItemClick
) :
    RecyclerView.Adapter<ManagementFragmentAdapter.ViewHolder>() {

    class ViewHolder(val binding: ManagementFragmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, listener: RentalItemClick, rentalContract: RentalContract) {
            binding.root.setOnClickListener(DebounceClickHandler {
                listener.rentalsClick(position, rentalContract)
            })
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ManagementFragmentItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.management_fragment_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rentalContractModel = notificationList[position]
        holder.bind(position, listener, rentalContractModel)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }
}