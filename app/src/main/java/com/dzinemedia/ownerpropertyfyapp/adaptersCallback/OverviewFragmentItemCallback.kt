package com.dzinemedia.ownerpropertyfyapp.adaptersCallback

import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyOverview.RentalPayment

interface OverviewFragmentItemCallback {
    fun overviewItemClick(position: Int, rentalPlanModel: RentalPayment)
}