package com.dzinemedia.ownerpropertyfyapp.adaptersCallback

import com.dzinemedia.ownerpropertyfyapp.models.responseModels.tenantOverviewModel.RentalPlanX

interface OverviewOwnerTenantItemCallback {
    fun rentalHistoryItemClick(position: Int, rentalPlanModel: RentalPlanX)
}