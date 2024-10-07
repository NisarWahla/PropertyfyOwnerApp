package com.dzinemedia.ownerpropertyfyapp.models.responseModels.OwnerPropertyRentalPaymentOverviewModel


import com.google.gson.annotations.SerializedName

data class PropertyInfoX(
    @SerializedName("Bathrooms")
    var bathrooms: String = "0",
    @SerializedName("Bedrooms")
    var bedrooms: String = "0",
    @SerializedName("Kitchen")
    var kitchen: String = "0",
    @SerializedName("Parkings")
    var parkings: String = "0"
)