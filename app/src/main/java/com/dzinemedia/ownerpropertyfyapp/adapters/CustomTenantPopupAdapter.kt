package com.dzinemedia.ownerpropertyfyapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.ListViewClickListener
import com.dzinemedia.ownerpropertyfyapp.databinding.PropertyLocationSearchDialogBinding
import com.dzinemedia.ownerpropertyfyapp.models.TenantSpinnerModel
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler

class CustomTenantPopupAdapter(
    context: Context, items: ArrayList<TenantSpinnerModel>, listener: ListViewClickListener
) : RecyclerView.Adapter<CustomTenantPopupAdapter.ViewHolder>() {
    private var items: ArrayList<TenantSpinnerModel> = ArrayList()
    private var listener: ListViewClickListener? = null

    init {
        this.items = items
        this.listener = listener
    }

    class ViewHolder(val binding: PropertyLocationSearchDialogBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, model: TenantSpinnerModel) {
            if (model.selection) {
                binding.iconImageView.setImageResource(0)
                binding.iconImageView.setImageResource(R.drawable.selected_menu_icon)
            } else {
                binding.iconImageView.setImageResource(0)
                binding.iconImageView.setImageResource(R.drawable.unselected_menu_icon)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding: PropertyLocationSearchDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.property_location_search_dialog,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.textLocation.text = items[position].locName
        val model = items[position]
        holder.binding.textLocation.setOnClickListener(DebounceClickHandler {
            listener?.listViewItemClick(position, holder)
        })
        holder.binding.iconImageView.setOnClickListener(DebounceClickHandler {
            listener?.listViewItemClick(position, holder)
        })
        holder.binding.root.setOnClickListener(DebounceClickHandler {
            listener?.listViewItemClick(position, holder)
        })
        holder.bind(position, model)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}