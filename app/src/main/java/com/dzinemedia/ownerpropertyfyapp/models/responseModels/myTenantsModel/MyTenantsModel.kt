package com.dzinemedia.ownerpropertyfyapp.models.responseModels.myTenantsModel


import com.google.gson.annotations.SerializedName

data class MyTenantsModel(
    @SerializedName("data")
    var `data`: List<Data?>? = listOf(),
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("success")
    var success: Boolean? = false
)