package com.dzinemedia.ownerpropertyfyapp.models.responseModels.OwnerPropertyRentalPaymentOverviewModel

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("amount")
    var amount: String = "0",
    @SerializedName("balance")
    var balance: Any? = Any(),
    @SerializedName("bank_name")
    var bankName: Any? = Any(),
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("deleted_at")
    var deletedAt: Any? = Any(),
    @SerializedName("deposit_bank")
    var depositBank: Any? = Any(),
    @SerializedName("description")
    var description: Any? = Any(),
    @SerializedName("due_date")
    var dueDate: String? = "",
    @SerializedName("id")
    var id: String = "0",
    @SerializedName("online_payment")
    var onlinePayment: String = "0",
    @SerializedName("owner_beneficiary")
    var ownerBeneficiary: String? = "",
    @SerializedName("partial_payment_history")
    var partialPaymentHistory: List<PartialPaymentHistory>? = listOf(),
    @SerializedName("partial_section")
    var partialSection: PartialSection? = PartialSection(),
    @SerializedName("payment_image")
    var paymentImage: List<Any>? = listOf(),
    @SerializedName("payment_method")
    var paymentMethod: String? = "",
    @SerializedName("payment_status")
    var paymentStatus: String? = "",
    @SerializedName("payment_status_date")
    var paymentStatusDate: String? = "",
    @SerializedName("payment_type")
    var paymentType: String? = "",
    @SerializedName("payments")
    var payments: String? = "",
    @SerializedName("property_id")
    var propertyId: PropertyId? = PropertyId(),
    @SerializedName("receipt")
    var receipt: List<Any>? = listOf(),
    @SerializedName("ref_no")
    var refNo: String? = "",
    @SerializedName("rental_id")
    var rentalId: String = "0",
    @SerializedName("rp_id")
    var rpId: String? = "",
    @SerializedName("updated_at")
    var updatedAt: String? = "",
    @SerializedName("updated_by")
    var updatedBy: String = "0"
)