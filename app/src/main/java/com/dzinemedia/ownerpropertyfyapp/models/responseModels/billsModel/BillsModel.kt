package com.dzinemedia.ownerpropertyfyapp.models.responseModels.billsModel


import com.google.gson.annotations.SerializedName

data class BillsModel(
    @SerializedName("data")
    var `data`: List<Data?>? = listOf(),
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("success")
    var success: Boolean? = false
)