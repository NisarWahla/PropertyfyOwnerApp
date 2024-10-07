package com.dzinemedia.ownerpropertyfyapp.adaptersCallback

import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerProperties.Data

interface VacantFragmentItemCallback {
    fun vacantFragmentItemClick(position: Int, ownerPropertyModel: Data)
}