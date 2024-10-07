package com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview


import com.google.gson.annotations.SerializedName

data class Country(
    @SerializedName("id")
    var id: String = "0",
    @SerializedName("iso2")
    var iso2: String? = "",
    @SerializedName("name")
    var name: String? = ""
): java.io.Serializable