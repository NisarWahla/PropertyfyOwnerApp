package com.dzinemedia.ownerpropertyfyapp.models.responseModels.OwnerPropertyRentalPaymentOverviewModel


import com.google.gson.annotations.SerializedName

data class MaintenancesExpense(
    @SerializedName("amount")
    var amount: List<Any?>? = listOf()
)