package com.dzinemedia.tenantpropertyapp.models.ResponseModel.logoutModel


import com.google.gson.annotations.SerializedName

data class LogoutModel(
    @SerializedName("data")
    var `data`: String? = "",
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("success")
    var success: Boolean? = false
)