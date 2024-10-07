package com.dzinemedia.ownerpropertyfyapp.adaptersCallback

import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview.RentalPlan

interface UpcomingItemCallback {
    fun upcomingItemClick(position: Int, rentalPayment: RentalPlan)
}