package com.dzinemedia.ownerpropertyfyapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.databinding.ContractDocumentItemBinding
import com.dzinemedia.ownerpropertyfyapp.models.NotificationModel
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler

class RentalsContractDocumentAdapter(val notificationList: ArrayList<NotificationModel>) :
    RecyclerView.Adapter<RentalsContractDocumentAdapter.ViewHolder>() {

    class ViewHolder(val binding: ContractDocumentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {

        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ContractDocumentItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.contract_document_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
        holder.binding.root.setOnClickListener(DebounceClickHandler {

        })
    }

    override fun getItemCount(): Int {
        return notificationList.size + 10
    }
}