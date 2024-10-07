package com.dzinemedia.ownerpropertyfyapp.models.responseModels.OwnerPropertyRentalPaymentOverviewModel


import com.google.gson.annotations.SerializedName

data class RentalIncome(
    @SerializedName("2023-07")
    var x202307: String = "0",
    @SerializedName("2023-08")
    var x202308: String = "0",
    @SerializedName("2023-09")
    var x202309: String = "0",
    @SerializedName("2023-10")
    var x202310: String = "0",
    @SerializedName("2023-11")
    var x202311: String = "0",
    @SerializedName("2023-12")
    var x202312: String = "0",
    @SerializedName("2024-01")
    var x202401: String = "0",
    @SerializedName("2024-02")
    var x202402: String = "0",
    @SerializedName("2024-03")
    var x202403: String = "0",
    @SerializedName("2024-04")
    var x202404: String = "0",
    @SerializedName("2024-05")
    var x202405: String = "0",
    @SerializedName("2024-06")
    var x202406: String = "0"
)