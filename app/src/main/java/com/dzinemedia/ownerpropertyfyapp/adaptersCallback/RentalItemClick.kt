package com.dzinemedia.ownerpropertyfyapp.adaptersCallback

import com.dzinemedia.ownerpropertyfyapp.models.responseModels.tenantOverviewModel.RentalContract

interface RentalItemClick {
    fun rentalsClick(position: Int, rentalContractModel: RentalContract)
}