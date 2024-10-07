package com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("added_by")
    var addedBy: String? = "",
    @SerializedName("check_info")
    var checkInfo: CheckInfo? = CheckInfo(),
    @SerializedName("contract_duration_from")
    var contractDurationFrom: String? = "",
    @SerializedName("contract_duration_to")
    var contractDurationTo: String? = "",
    @SerializedName("contract_status")
    var contractStatus: String? = "",
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("id")
    var id: String = "0",
    @SerializedName("installments")
    var installments: String = "0",
    @SerializedName("is_active")
    var isActive: Boolean? = false,
    @SerializedName("next_amount")
    var nextAmount: String = "0",
    @SerializedName("next_payment")
    var nextPayment: String? = "",
    @SerializedName("payment_period")
    var paymentPeriod: String? = "",
    @SerializedName("payment_type")
    var paymentType: String? = "",
    @SerializedName("pending_rent")
    var pendingRent: String = "0",
    @SerializedName("property_id")
    var propertyId: PropertyId? = PropertyId(),
    @SerializedName("rc_id")
    var rcId: String? = "",
    @SerializedName("received_rent")
    var receivedRent: String = "0",
    @SerializedName("remaining_days")
    var remainingDays: String? = "",
    @SerializedName("rent_paid")
    var rentPaid: String? = "",
    @SerializedName("rent_pending")
    var rentPending: String? = "",
    @SerializedName("rent_period")
    var rentPeriod: String? = "",
    @SerializedName("rental_amount")
    var rentalAmount: String = "0",
    @SerializedName("rental_plan")
    var rentalPlan: List<RentalPlan>? = listOf(),
    @SerializedName("security_deposit")
    var securityDeposit: String = "0",
    @SerializedName("tenant_id")
    var tenantId: TenantId? = TenantId(),
    @SerializedName("updated_by")
    var updatedBy: Any? = Any(),
    @SerializedName("yearly_rental_amount")
    var yearlyRentalAmount: Any? = Any()
): java.io.Serializable