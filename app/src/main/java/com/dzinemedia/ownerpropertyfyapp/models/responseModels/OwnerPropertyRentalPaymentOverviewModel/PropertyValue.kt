package com.dzinemedia.ownerpropertyfyapp.models.responseModels.OwnerPropertyRentalPaymentOverviewModel


import com.google.gson.annotations.SerializedName

data class PropertyValue(
    @SerializedName("amount")
    var amount: String? = "",
    @SerializedName("percentage")
    var percentage: String? = ""
)