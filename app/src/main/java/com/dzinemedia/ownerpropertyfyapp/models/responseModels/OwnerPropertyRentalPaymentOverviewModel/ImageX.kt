package com.dzinemedia.ownerpropertyfyapp.models.responseModels.OwnerPropertyRentalPaymentOverviewModel


import com.google.gson.annotations.SerializedName

data class ImageX(
    @SerializedName("id")
    var id: String = "0",
    @SerializedName("property_images")
    var propertyImages: List<Any?>? = listOf()
)