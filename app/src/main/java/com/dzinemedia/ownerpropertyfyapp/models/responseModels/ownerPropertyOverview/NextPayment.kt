package com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyOverview


import com.google.gson.annotations.SerializedName

data class NextPayment(
    @SerializedName("amount")
    var amount: String = "0",
    @SerializedName("date")
    var date: String? = ""
)