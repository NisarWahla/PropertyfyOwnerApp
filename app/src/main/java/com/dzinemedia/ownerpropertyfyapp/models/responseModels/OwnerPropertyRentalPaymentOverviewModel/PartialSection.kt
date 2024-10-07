package com.dzinemedia.ownerpropertyfyapp.models.responseModels.OwnerPropertyRentalPaymentOverviewModel


import com.google.gson.annotations.SerializedName

data class PartialSection(
    @SerializedName("paid_amount")
    var paidAmount: String = "0",
    @SerializedName("remaining_amount")
    var remainingAmount: String = "0"
)