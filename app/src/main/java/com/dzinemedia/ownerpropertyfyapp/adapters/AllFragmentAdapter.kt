package com.dzinemedia.ownerpropertyfyapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.AllFragmentItemCallback
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentAllItemBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerProperties.Data
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler

class AllFragmentAdapter(
    val dataList: ArrayList<Data>,
    val listener: AllFragmentItemCallback
) :
    RecyclerView.Adapter<AllFragmentAdapter.ViewHolder>() {

    class ViewHolder(val binding: FragmentAllItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, listener: AllFragmentItemCallback, ownerPropertyModel: Data) {
            binding.root.setOnClickListener(DebounceClickHandler {
                listener.allFragmentItemClick(position,ownerPropertyModel)
            })
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: FragmentAllItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.fragment_all_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = dataList[position]
        holder.binding.let {
            it.ticketNo.text = model.mpId
            it.idValue.text = model.address
            if (model.contracts?.isNotEmpty() == true) {
                if (model.contracts?.get(0)?.agreementPeriodFrom != null && model.contracts?.get(0)?.agreementPeriodTo != null) {
                    it.bankTransfer.text =
                        "${model.contracts?.get(0)?.agreementPeriodFrom} - ${model.contracts?.get(0)?.agreementPeriodTo}"
                } else {
                    it.bankTransfer.text = ""
                }
            } else {
                it.bankTransfer.text = ""
            }
            val propertyStatus = model.propertyStatus.toInt()
            if (propertyStatus == 0) {
                if (model.propertyFor.equals("vacant", true)) {
                    it.checkRefValue.text = "Vacant"
                    it.checkRefValue.setBackgroundResource(R.drawable.vacant_round)
                    it.checkRefValue.setTextColor(
                        ContextCompat.getColor(
                            holder.binding.root.context,
                            R.color.vacant_color
                        )
                    )
                } else if (model.propertyFor.equals("sale", true)) {
                    it.checkRefValue.setBackgroundResource(R.drawable.for_sale_round)
                    it.checkRefValue.setTextColor(
                        ContextCompat.getColor(
                            holder.binding.root.context,
                            R.color.for_sale_color
                        )
                    )
                    it.checkRefValue.text = model.propertyFor
                } else {
                    it.checkRefValue.setBackgroundResource(R.drawable.rent_and_sale_round)
                    it.checkRefValue.setTextColor(
                        ContextCompat.getColor(
                            holder.binding.root.context,
                            R.color.sale_and_rent_color
                        )
                    )
                    it.checkRefValue.text = model.propertyFor
                }
            } else {
                if (model.propertyFor.equals("sale", true)) {
                    it.checkRefValue.setBackgroundResource(R.drawable.for_sale_round)
                    it.checkRefValue.setTextColor(
                        ContextCompat.getColor(
                            holder.binding.root.context,
                            R.color.for_sale_color
                        )
                    )
                } else if (model.propertyFor.equals("rent", true)) {
                    it.checkRefValue.setBackgroundResource(R.drawable.rented_round)
                    it.checkRefValue.setTextColor(
                        ContextCompat.getColor(
                            holder.binding.root.context,
                            R.color.rented_color
                        )
                    )
                } else {
                    it.checkRefValue.setBackgroundResource(R.drawable.rent_and_sale_round)
                    it.checkRefValue.setTextColor(
                        ContextCompat.getColor(
                            holder.binding.root.context,
                            R.color.sale_and_rent_color
                        )
                    )
                }
                it.checkRefValue.text = model.propertyFor
            }
            if (model.images?.size!! > 0) {
                if (model.images?.get(0)?.propertyImages?.size!! > 0) {
                    Glide.with(it.root.context).load(model.images?.get(0)?.propertyImages?.get(0))
                        .placeholder(R.drawable.img_emp).into(it.ticketImg)
                } else {
                }
            } else {
            }
        }
        holder.bind(position,listener,model)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}