package com.dzinemedia.ownerpropertyfyapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.databinding.PreviousPaymentItemBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.OwnerPropertyRentalPaymentOverviewModel.PartialPaymentHistory

class PreviousPaymentAdapter(val partialPaymentList: ArrayList<PartialPaymentHistory>) :
    RecyclerView.Adapter<PreviousPaymentAdapter.ViewHolder>() {

    class ViewHolder(val binding: PreviousPaymentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {

        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: PreviousPaymentItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.previous_payment_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val partialModel = partialPaymentList[position]
        holder.binding.let {
            it.receiptNo.text = "#${partialModel.id.toString()}"
            it.amount.text = "AED ${partialModel.amount}"
            it.method.text = partialModel.paymentMethod
            it.checkNo.text = "#${partialModel.refNo}"
        }
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return partialPaymentList.size
    }
}