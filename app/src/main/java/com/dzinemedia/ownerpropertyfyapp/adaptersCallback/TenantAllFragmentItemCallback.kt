package com.dzinemedia.ownerpropertyfyapp.adaptersCallback

import com.dzinemedia.ownerpropertyfyapp.models.responseModels.myTenantsModel.Data

interface TenantAllFragmentItemCallback {
    fun allTenantItemClick(position: Int, allTenantModel: Data)
}