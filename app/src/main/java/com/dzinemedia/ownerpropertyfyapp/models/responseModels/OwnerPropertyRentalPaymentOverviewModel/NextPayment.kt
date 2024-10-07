package com.dzinemedia.ownerpropertyfyapp.models.responseModels.OwnerPropertyRentalPaymentOverviewModel


import com.google.gson.annotations.SerializedName

data class NextPayment(
    @SerializedName("amount")
    var amount: String? = "",
    @SerializedName("date")
    var date: String? = ""
)