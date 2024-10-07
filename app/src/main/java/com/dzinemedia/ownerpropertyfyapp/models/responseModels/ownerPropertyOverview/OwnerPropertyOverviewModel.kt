package com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyOverview


import com.google.gson.annotations.SerializedName

data class OwnerPropertyOverviewModel(
    @SerializedName("data")
    var `data`: Data? = Data(),
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("success")
    var success: Boolean? = false
)