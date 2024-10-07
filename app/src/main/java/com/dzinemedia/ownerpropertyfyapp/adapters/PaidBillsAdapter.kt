package com.dzinemedia.ownerpropertyfyapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.PaidBillsItemCallback
import com.dzinemedia.ownerpropertyfyapp.databinding.PaidBillsItemBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.billsModel.Data
import com.dzinemedia.ownerpropertyfyapp.utility.Utils
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler

class PaidBillsAdapter(
    val billsList: ArrayList<Data>,
    val listener: PaidBillsItemCallback
) :
    RecyclerView.Adapter<PaidBillsAdapter.ViewHolder>() {

    class ViewHolder(val binding: PaidBillsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, billsModel: Data, listener: PaidBillsItemCallback) {
            binding.root.setOnClickListener(DebounceClickHandler {
                listener.paidBillsItemClick(position, billsModel)
            })
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: PaidBillsItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.paid_bills_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val billsModel = billsList[position]
        holder.binding.idValue.text = "Invoice: ${billsModel.bmId}"

        if (billsModel.billCategory != null) {
            if (billsModel.billCategory.equals("brokerage_fee", true)) {
                if (billsModel.paymentMethod.equals("cash", ignoreCase = true)) {
                    holder.binding.checkRefValue.text = "Cash"
                } else {
                    if (billsModel.refNo == null) {
                        holder.binding.checkRefValue.text = "Cheque/Ref: 0"
                    } else {
                        holder.binding.checkRefValue.text = "Cheque/Ref: ${billsModel.refNo}"
                    }
                }
            } else {
                holder.binding.checkRefValue.text = billsModel.paymentMethod
            }
        }
        holder.binding.transferValue.text = "AED ${billsModel.amount}"
        val splitDate = billsModel.paidDate?.split(" ")
        if (splitDate != null) {
            if (splitDate.isNotEmpty()) {
                val changeFormat = splitDate[0].let { Utils.changeDateFormat(it) }
                holder.binding.paidDate.text = "Paid on: $changeFormat"
            }
        }
        holder.binding.checkValue.text = billsModel.paidStatus
        if (billsModel.billCategory != null) {
            val billCat = billsModel.billCategory?.split("_")
            val firstWord = billCat!![0].capitalizeFirstLetter()
            val secondWord = billCat[1].capitalizeFirstLetter()
            holder.binding.bankTransfer.text = "$firstWord $secondWord"
        } else {
            holder.binding.bankTransfer.text = ""
        }
        holder.bind(position, billsModel, listener)
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