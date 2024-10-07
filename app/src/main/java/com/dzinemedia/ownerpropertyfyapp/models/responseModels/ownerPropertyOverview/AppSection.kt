package com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyOverview


import com.google.gson.annotations.SerializedName

data class AppSection(
    @SerializedName("available_balance")
    var availableBalance: String = "0",
    @SerializedName("bills_management")
    var billsManagement: List<Any?>? = listOf(),
    @SerializedName("inspections")
    var inspections: List<Any?>? = listOf(),
    @SerializedName("next_payment")
    var nextPayment: NextPayment? = NextPayment(),
    @SerializedName("payment_method")
    var paymentMethod: PaymentMethod? = PaymentMethod(),
    @SerializedName("property_balance")
    var propertyBalance: String = "0",
    @SerializedName("remaining_rent")
    var remainingRent: String? = "",
    @SerializedName("rent_collected")
    var rentCollected: String = "0",
    @SerializedName("rent_paid")
    var rentPaid: String? = "",
    @SerializedName("rental_contracts")
    var rentalContracts: List<RentalContract?>? = listOf(),
    @SerializedName("rental_payments")
    var rentalPayments: List<RentalPayment?>? = listOf(),
    @SerializedName("security_deposits")
    var securityDeposits: String? = ""
)