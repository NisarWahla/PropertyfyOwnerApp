package com.dzinemedia.ownerpropertyfyapp.adaptersCallback

import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview.RentalPlan

interface PartialItemCallback {
    fun partialItemClick(position: Int, rentalPlan: RentalPlan)
}