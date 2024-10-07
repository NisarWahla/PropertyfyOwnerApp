package com.dzinemedia.ownerpropertyfyapp.models.responseModels.OwnerPropertyRentalPaymentOverviewModel


import com.google.gson.annotations.SerializedName

data class PartialPaymentHistory(
    @SerializedName("added_by")
    var addedBy: String? = "",
    @SerializedName("amount")
    var amount: String? = "0",
    @SerializedName("bank_name")
    var bankName: Any? = Any(),
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("due_date")
    var dueDate: String? = "",
    @SerializedName("id")
    var id: String? = "0",
    @SerializedName("owner_beneficiary")
    var ownerBeneficiary: String? = "",
    @SerializedName("payment_image")
    var paymentImage: Any? = Any(),
    @SerializedName("payment_method")
    var paymentMethod: String? = "",
    @SerializedName("payment_status")
    var paymentStatus: String? = "",
    @SerializedName("payments")
    var payments: Any? = Any(),
    @SerializedName("receipt")
    var receipt: Any? = Any(),
    @SerializedName("ref_no")
    var refNo: String? = "",
    @SerializedName("rp_id")
    var rpId: Any? = Any()
)