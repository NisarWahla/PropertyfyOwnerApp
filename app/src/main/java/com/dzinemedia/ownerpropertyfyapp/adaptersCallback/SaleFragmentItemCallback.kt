package com.dzinemedia.ownerpropertyfyapp.adaptersCallback

import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerProperties.Data

interface SaleFragmentItemCallback {
    fun saleFragmentItemClick(position: Int, ownerPropertyModel: Data)
}