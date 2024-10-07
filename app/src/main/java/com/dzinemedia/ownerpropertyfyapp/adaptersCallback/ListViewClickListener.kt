package com.dzinemedia.ownerpropertyfyapp.adaptersCallback

import com.dzinemedia.ownerpropertyfyapp.adapters.CustomTenantPopupAdapter

interface ListViewClickListener {
    fun listViewItemClick(position: Int, holder: CustomTenantPopupAdapter.ViewHolder)
}