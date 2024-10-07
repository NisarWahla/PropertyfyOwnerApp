package com.dzinemedia.ownerpropertyfyapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.TenantAllFragmentItemCallback
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentAllTenantItemBinding
import com.dzinemedia.ownerpropertyfyapp.fragments.tenantFragments.AllTenantFragment
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.myTenantsModel.Data
import com.dzinemedia.ownerpropertyfyapp.utility.Utils
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler

class TenantAllFragmentAdapter(
    val allTenantFragmentList: ArrayList<Data>,
    val allTenantFragment: AllTenantFragment,
    val listener: TenantAllFragmentItemCallback
) :
    RecyclerView.Adapter<TenantAllFragmentAdapter.ViewHolder>() {

    class ViewHolder(val binding: FragmentAllTenantItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, listener: TenantAllFragmentItemCallback, allTenantModel: Data) {
            binding.root.setOnClickListener(DebounceClickHandler {
                listener.allTenantItemClick(position, allTenantModel)
            })
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: FragmentAllTenantItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.fragment_all_tenant_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = allTenantFragmentList[position]
        holder.binding.let {
            it.tenantName.text = model.name
            it.totalProperties.text = "Properties: ${model.tProperties}"
            if (model.isActive == true) {
                it.checkValue.text = "Active"
            } else {
                it.checkValue.text = "Inactive"
            }
            it.callNow.setOnClickListener(DebounceClickHandler { view ->
                Utils.preventDoubleTap(view)
                if (allTenantFragment.checkPermissionBehave()) {
                    model.phone1?.let { it1 -> Utils.makePhoneCall(it.root.context, it1) }
                } else {
                    model.phone1?.let { phone ->
                        allTenantFragment.takePermission(phone)
                    }
                }
            })
            if (model.image != null) {
                if (model.image!!.isNotEmpty()) {
                    Glide.with(holder.binding.root.context).load(model.image!![0])
                        .into(it.ticketImg)
                }
            }
        }


        holder.bind(position, listener, model)
    }

    override fun getItemCount(): Int {
        return allTenantFragmentList.size
    }
}