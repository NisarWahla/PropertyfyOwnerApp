package com.dzinemedia.ownerpropertyfyapp.adaptersCallback

import com.dzinemedia.ownerpropertyfyapp.models.responseModels.billsModel.Data

interface PaidBillsItemCallback {
    fun paidBillsItemClick(position: Int, billsModel: Data)
}