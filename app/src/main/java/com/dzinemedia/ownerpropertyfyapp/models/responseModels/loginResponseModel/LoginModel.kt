package com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel


import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("data")
    var `data`: Data? = Data(),
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("success")
    var success: Boolean? = false
)