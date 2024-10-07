package com.dzinemedia.ownerpropertyfyapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.databinding.PendingBillsItemBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.billsModel.Data
import com.dzinemedia.ownerpropertyfyapp.utility.Utils

class PendingBillsAdapter(val billsList: ArrayList<Data>) :
    RecyclerView.Adapter<PendingBillsAdapter.ViewHolder>() {

    class ViewHolder(val binding: PendingBillsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {

        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: PendingBillsItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.pending_bills_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val billsModel = billsList[position]
        holder.binding.idValue.text = "Invoice: ${billsModel.bmId}"
        holder.binding.dueDate.text = "Due: ${billsModel.dueDate}"
        holder.binding.transferValue.text = "AED ${billsModel.amount}"
        holder.binding.propertyID.text = "Property ID: ${billsModel.propertyId?.mpId}"
        holder.binding.checkValue.text = billsModel.paidStatus
        if (billsModel.billCategory != null) {
            val billCat = billsModel.billCategory?.split("_")
            val firstWord = billCat!![0].capitalizeFirstLetter()
            val secondWord = billCat[1].capitalizeFirstLetter()
            holder.binding.bankTransfer.text = "$firstWord $secondWord"
        } else {
            holder.binding.bankTransfer.text = ""
        }
        holder.bind(position)
    }
    private fun String.capitalizeFirstLetter(): String {
        return if (this.isNotEmpty()) {
            this.substring(0, 1).uppercase() + this.substring(1)
        } else {
            this
        }
    }

    override fun getItemCount(): Int {
        return billsList.size
    }
}