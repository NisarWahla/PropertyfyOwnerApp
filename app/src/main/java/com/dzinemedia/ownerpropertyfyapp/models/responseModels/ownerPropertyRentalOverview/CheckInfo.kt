package com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview


import com.google.gson.annotations.SerializedName

data class CheckInfo(
    @SerializedName("bounced")
    var bounced: String = "0",
    @SerializedName("total")
    var total: String = "0"
): java.io.Serializable