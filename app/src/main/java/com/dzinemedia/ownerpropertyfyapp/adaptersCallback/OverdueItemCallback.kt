package com.dzinemedia.ownerpropertyfyapp.adaptersCallback

import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview.RentalPlan

interface OverdueItemCallback {
    fun overdueItemClick(position: Int, rentalPlanModel: RentalPlan)
}