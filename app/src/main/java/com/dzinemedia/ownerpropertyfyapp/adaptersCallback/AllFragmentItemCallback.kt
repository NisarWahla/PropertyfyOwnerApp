package com.dzinemedia.ownerpropertyfyapp.adaptersCallback

import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerProperties.Data

interface AllFragmentItemCallback {
    fun allFragmentItemClick(position: Int, ownerPropertyModel: Data)
}