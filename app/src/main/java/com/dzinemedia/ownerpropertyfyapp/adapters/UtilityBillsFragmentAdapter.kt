package com.dzinemedia.ownerpropertyfyapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.databinding.UtiltyBillsFragmentItemBinding
import com.dzinemedia.ownerpropertyfyapp.models.NotificationModel

class UtilityBillsFragmentAdapter(val notificationList: ArrayList<NotificationModel>) :
    RecyclerView.Adapter<UtilityBillsFragmentAdapter.ViewHolder>() {

    class ViewHolder(val binding: UtiltyBillsFragmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {

        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: UtiltyBillsFragmentItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.utilty_bills_fragment_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return notificationList.size + 10
    }
}