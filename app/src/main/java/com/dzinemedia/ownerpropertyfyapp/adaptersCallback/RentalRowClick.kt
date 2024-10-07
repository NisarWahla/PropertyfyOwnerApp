package com.dzinemedia.ownerpropertyfyapp.adaptersCallback

import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyOverview.RentalContract

interface RentalRowClick {
    fun rentalsClick(position: Int, rentalContract: RentalContract)
}