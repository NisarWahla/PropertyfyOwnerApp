package com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyOverview


import com.google.gson.annotations.SerializedName

data class PropertyValue(
    @SerializedName("amount")
    var amount: String = "0",
    @SerializedName("percentage")
    var percentage: String? = ""
)