package com.dzinemedia.ownerpropertyfyapp.models.responseModels.dashboardModel


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("available_balance")
    var availableBalance: String = "0",
    @SerializedName("for_sale")
    var forSale: String = "0",
    @SerializedName("properties")
    var properties: String = "0",
    @SerializedName("rented")
    var rented: String = "0",
    @SerializedName("total_balance")
    var totalBalance: String = "0",
    @SerializedName("vacant")
    var vacant: String = "0"
)