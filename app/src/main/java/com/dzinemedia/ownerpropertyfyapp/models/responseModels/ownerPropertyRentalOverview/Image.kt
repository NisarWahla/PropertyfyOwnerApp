package com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview


import com.google.gson.annotations.SerializedName

data class Image(
    @SerializedName("id")
    var id: String = "0",
    @SerializedName("property_images")
    var propertyImages: List<Any?>? = listOf()
): java.io.Serializable