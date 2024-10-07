package com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyOverview


import com.google.gson.annotations.SerializedName

data class TenantsDetail(
    @SerializedName("duration")
    var duration: String? = "",
    @SerializedName("phone")
    var phone: String = "",
    @SerializedName("rent")
    var rent: String? = "",
    @SerializedName("status")
    var status: String? = "",
    @SerializedName("tenant_name")
    var tenantName: String? = ""
)