package com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview


import com.google.gson.annotations.SerializedName

data class PropertyValue(
    @SerializedName("amount")
    var amount: String = "0",
    @SerializedName("percentage")
    var percentage: String? = ""
): java.io.Serializable