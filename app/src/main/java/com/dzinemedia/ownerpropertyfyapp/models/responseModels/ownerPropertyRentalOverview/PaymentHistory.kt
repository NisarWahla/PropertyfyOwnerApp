package com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview


import com.google.gson.annotations.SerializedName

data class PaymentHistory(
    @SerializedName("amount")
    var amount: String = "0",
    @SerializedName("date")
    var date: String? = "",
    @SerializedName("status")
    var status: String? = "",
    @SerializedName("tenant_name")
    var tenantName: String? = ""
): java.io.Serializable