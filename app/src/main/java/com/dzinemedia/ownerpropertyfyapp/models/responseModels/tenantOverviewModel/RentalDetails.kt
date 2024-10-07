package com.dzinemedia.ownerpropertyfyapp.models.responseModels.tenantOverviewModel

import com.google.gson.annotations.SerializedName

data class RentalDetails(
    @SerializedName("due_date")
    var dueDate: String? = "",
    @SerializedName("next_payment")
    var nextPayment: String = "0",
    @SerializedName("payment_method")
    var paymentMethod: String? = "",
    @SerializedName("ref_no")
    var refNo: String? = "",
    @SerializedName("remaining_rent")
    var remainingRent: String = "0",
    @SerializedName("security_deposit")
    var securityDeposit: String = "0",
    @SerializedName("total_received")
    var totalReceived: String = "0",
    @SerializedName("selected_property")
    var selectedProperty: SelectedProperty? = SelectedProperty(),
    @SerializedName("total_rent")
    var totalRent: String = "0"
) : java.io.Serializable