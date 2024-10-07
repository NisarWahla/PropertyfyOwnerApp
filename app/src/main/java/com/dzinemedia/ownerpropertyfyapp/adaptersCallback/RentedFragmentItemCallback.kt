package com.dzinemedia.ownerpropertyfyapp.adaptersCallback

import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerProperties.Data

interface RentedFragmentItemCallback {
    fun rentedFragmentItemClick(position: Int, ownerPropertyModel: Data)
}