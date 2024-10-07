package com.dzinemedia.ownerpropertyfyapp.models.responseModels.myTenantsModel


import com.google.gson.annotations.SerializedName

data class Rental(
    @SerializedName("added_by")
    var addedBy: String = "0",
    @SerializedName("contract_duration_from")
    var contractDurationFrom: String? = "",
    @SerializedName("contract_duration_to")
    var contractDurationTo: String? = "",
    @SerializedName("contract_status")
    var contractStatus: String? = "",
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("deleted_at")
    var deletedAt: Any? = Any(),
    @SerializedName("id")
    var id: String = "0",
    @SerializedName("installments")
    var installments: String = "0",
    @SerializedName("next_payment")
    var nextPayment: String? = "",
    @SerializedName("payment_period")
    var paymentPeriod: String? = "",
    @SerializedName("payment_type")
    var paymentType: String? = "",
    @SerializedName("property_id")
    var propertyId: String = "0",
    @SerializedName("rc_id")
    var rcId: String? = "",
    @SerializedName("rent_period")
    var rentPeriod: String = "0",
    @SerializedName("rental_amount")
    var rentalAmount: String = "0",
    @SerializedName("security_deposit")
    var securityDeposit: String = "0",
    @SerializedName("tenant_id")
    var tenantId: String = "0",
    @SerializedName("updated_at")
    var updatedAt: String? = "",
    @SerializedName("updated_by")
    var updatedBy: Any? = Any()
)