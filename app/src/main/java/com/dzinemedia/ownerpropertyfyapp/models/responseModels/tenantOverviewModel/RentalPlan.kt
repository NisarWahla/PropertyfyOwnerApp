package com.dzinemedia.ownerpropertyfyapp.models.responseModels.tenantOverviewModel


import com.google.gson.annotations.SerializedName

data class RentalPlan(
    @SerializedName("bounced_checks")
    var bouncedChecks: String = "0",
    @SerializedName("rental_plans")
    var rentalPlans: List<RentalPlanX>? = listOf()
) : java.io.Serializable