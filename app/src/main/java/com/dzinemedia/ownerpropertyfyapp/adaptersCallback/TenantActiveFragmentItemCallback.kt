package com.dzinemedia.ownerpropertyfyapp.adaptersCallback

import com.dzinemedia.ownerpropertyfyapp.models.responseModels.myTenantsModel.Data

interface TenantActiveFragmentItemCallback {
    fun activeTenantItemClick(position: Int, allTenantModel: Data)
}