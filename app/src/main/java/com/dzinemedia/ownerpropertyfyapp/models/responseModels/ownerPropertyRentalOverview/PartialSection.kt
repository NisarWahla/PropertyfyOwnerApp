package com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview


import com.google.gson.annotations.SerializedName

data class PartialSection(
    @SerializedName("paid_amount")
    var paidAmount: String = "0",
    @SerializedName("remaining_amount")
    var remainingAmount: String = "0"
): java.io.Serializable