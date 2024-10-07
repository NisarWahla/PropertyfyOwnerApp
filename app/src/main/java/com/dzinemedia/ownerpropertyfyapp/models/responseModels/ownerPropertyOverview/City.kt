package com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyOverview


import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("id")
    var id: String = "0",
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("state_code")
    var stateCode: String? = ""
)