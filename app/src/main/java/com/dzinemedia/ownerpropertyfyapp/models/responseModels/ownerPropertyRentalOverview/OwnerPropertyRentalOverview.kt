package com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview


import com.google.gson.annotations.SerializedName

data class OwnerPropertyRentalOverview(
    @SerializedName("data")
    var `data`: Data? = Data(),
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("success")
    var success: Boolean? = false
): java.io.Serializable