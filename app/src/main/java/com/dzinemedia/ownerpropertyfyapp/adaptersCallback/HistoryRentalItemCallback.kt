package com.dzinemedia.ownerpropertyfyapp.adaptersCallback

import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview.RentalPlan

interface HistoryRentalItemCallback {
    fun historyRentalItemClick(position: Int, rentalPlanModel: RentalPlan)
}