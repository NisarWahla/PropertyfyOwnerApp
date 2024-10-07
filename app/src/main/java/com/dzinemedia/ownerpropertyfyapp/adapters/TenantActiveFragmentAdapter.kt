package com.dzinemedia.ownerpropertyfyapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.TenantActiveFragmentItemCallback
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentActiveTenantItemBinding
import com.dzinemedia.ownerpropertyfyapp.fragments.tenantFragments.ActiveTenantFragment
import com.dzinemedia.ownerpropertyfyapp.fragments.tenantFragments.InactiveTenantFragment
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.myTenantsModel.Data
import com.dzinemedia.ownerpropertyfyapp.utility.Utils
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler

class TenantActiveFragmentAdapter(
    val allTenantFragmentList: ArrayList<Data>,
    val fragment: Fragment,
    val listener: TenantActiveFragmentItemCallback
) :
    RecyclerView.Adapter<TenantActiveFragmentAdapter.ViewHolder>() {

    class ViewHolder(val binding: FragmentActiveTenantItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, listener: TenantActiveFragmentItemCallback, allTenantModel: Data) {
            binding.root.setOnClickListener(DebounceClickHandler {
                listener.activeTenantItemClick(position, allTenantModel)
            })
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: FragmentActiveTenantItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.fragment_active_tenant_item,
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
                if (fragment is ActiveTenantFragment) {
                    if (fragment.checkPermissionBehave()) {
                        model.phone1?.let { it1 -> Utils.makePhoneCall(it.root.context, it1) }
                    } else {
                        model.phone1?.let { phone ->
                            fragment.takePermission(phone)
                        }
                    }
                } else if (fragment is InactiveTenantFragment) {
                    if (fragment.checkPermissionBehave()) {
                        model.phone1?.let { it1 -> Utils.makePhoneCall(it.root.context, it1) }
                    } else {
                        model.phone1?.let { phone ->
                            fragment.takePermission(phone)
                        }
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