package com.dzinemedia.ownerpropertyfyapp.models.responseModels.tenantOverviewModel


import com.google.gson.annotations.SerializedName

data class TenantOverviewModel(
    @SerializedName("data")
    var `data`: Data? = Data(),
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("success")
    var success: Boolean? = false
) : java.io.Serializable