package com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerProperties


import com.google.gson.annotations.SerializedName

data class Cities(
    @SerializedName("id")
    var id: String = "0",
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("state_code")
    var stateCode: String? = ""
)