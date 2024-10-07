package com.dzinemedia.ownerpropertyfyapp.adaptersCallback

import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview.RentalPlan

interface ReceivedItemCallback {
    fun receivedItemClick(position: Int, rentalModel: RentalPlan)
}