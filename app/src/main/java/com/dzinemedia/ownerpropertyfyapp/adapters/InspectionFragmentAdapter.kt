package com.dzinemedia.ownerpropertyfyapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.databinding.InspectionFragmentItemBinding
import com.dzinemedia.ownerpropertyfyapp.models.NotificationModel

class InspectionFragmentAdapter(val notificationList: ArrayList<NotificationModel>) :
    RecyclerView.Adapter<InspectionFragmentAdapter.ViewHolder>() {

    class ViewHolder(val binding: InspectionFragmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {

        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: InspectionFragmentItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.inspection_fragment_item,
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