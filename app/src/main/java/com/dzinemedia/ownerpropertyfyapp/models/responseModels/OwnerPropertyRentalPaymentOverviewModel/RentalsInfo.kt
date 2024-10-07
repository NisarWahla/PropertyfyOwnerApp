package com.dzinemedia.ownerpropertyfyapp.models.responseModels.OwnerPropertyRentalPaymentOverviewModel


import com.google.gson.annotations.SerializedName

data class RentalsInfo(
    @SerializedName("bounced_check")
    var bouncedCheck: String? = "",
    @SerializedName("t_rent_collect")
    var tRentCollect: String? = ""
)