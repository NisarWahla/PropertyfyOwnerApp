package com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerProperties


import com.google.gson.annotations.SerializedName

data class PropertyInfo(
    @SerializedName("Bathrooms")
    var bathrooms: String = "0",
    @SerializedName("Bedrooms")
    var bedrooms: String = "0",
    @SerializedName("Kitchen")
    var kitchen: String = "0",
    @SerializedName("Parkings")
    var parkings: String = "0"
)