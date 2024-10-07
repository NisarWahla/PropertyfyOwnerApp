package com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview


import com.google.gson.annotations.SerializedName

data class PaymentMethod(
    @SerializedName("method")
    var method: String? = "",
    @SerializedName("ref_no")
    var refNo: String? = ""
): java.io.Serializable