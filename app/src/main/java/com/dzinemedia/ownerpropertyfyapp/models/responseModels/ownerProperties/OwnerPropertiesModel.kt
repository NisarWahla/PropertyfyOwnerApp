package com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerProperties


import com.google.gson.annotations.SerializedName

data class OwnerPropertiesModel(
    @SerializedName("data")
    var `data`: List<Data?>? = listOf(),
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("success")
    var success: Boolean? = false
)