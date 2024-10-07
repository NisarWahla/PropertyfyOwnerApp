package com.dzinemedia.ownerpropertyfyapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.RentedFragmentItemCallback
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.SaleFragmentItemCallback
import com.dzinemedia.ownerpropertyfyapp.databinding.RentedFragmentItemBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerProperties.Data
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler

class RentedFragmentAdapter(
    val rentedList: ArrayList<Data>,
    val listener: RentedFragmentItemCallback
) :
    RecyclerView.Adapter<RentedFragmentAdapter.ViewHolder>() {

    class ViewHolder(val binding: RentedFragmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, listener: RentedFragmentItemCallback, ownerPropertyModel: Data) {
            binding.root.setOnClickListener(DebounceClickHandler {
                listener.rentedFragmentItemClick(position, ownerPropertyModel)
            })
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: RentedFragmentItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.rented_fragment_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = rentedList[position]
        holder.binding.let {
            it.ticketNo.text = model.mpId
            it.idValue.text = model.address
            it.bankTransfer.text = "${model.rentDurationFrom} - ${model.rentDurationTo}"
            val propertyStatus = model.propertyStatus.toInt()
            if (propertyStatus == 1) {
                it.checkRefValue.text = "Rented"
            }
            if (model.images?.size!! > 0) {
                if (model.images?.get(0)?.propertyImages?.size!! > 0) {
                    Glide.with(it.root.context).load(model.images?.get(0)?.propertyImages?.get(0)).placeholder(R.drawable.img_emp).into(it.ticketImg)
                } else {
                }
            } else {
            }
        }
        holder.bind(position, listener, model)
    }

    override fun getItemCount(): Int {
        return rentedList.size
    }
}