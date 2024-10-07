package com.dzinemedia.ownerpropertyfyapp.models.responseModels.OwnerPropertyRentalPaymentOverviewModel


import com.google.gson.annotations.SerializedName

data class BillsManagement(
    @SerializedName("added_by")
    var addedBy: String? = "",
    @SerializedName("amount")
    var amount: String = "0",
    @SerializedName("bank_name")
    var bankName: String? = "",
    @SerializedName("bill_category")
    var billCategory: String? = "",
    @SerializedName("bill_type")
    var billType: String? = "",
    @SerializedName("bm_id")
    var bmId: String? = "",
    @SerializedName("company_id")
    var companyId: String? = "",
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("due_date")
    var dueDate: String? = "",
    @SerializedName("id")
    var id: String = "0",
    @SerializedName("paid_date")
    var paidDate: String? = "",
    @SerializedName("paid_status")
    var paidStatus: String? = "",
    @SerializedName("payment_method")
    var paymentMethod: String? = "",
    @SerializedName("property_id")
    var propertyId: PropertyIdX? = PropertyIdX(),
    @SerializedName("received_date")
    var receivedDate: String? = "",
    @SerializedName("received_status")
    var receivedStatus: String? = "",
    @SerializedName("ref_no")
    var refNo: Any? = Any(),
    @SerializedName("rental_id")
    var rentalId: String? = "",
    @SerializedName("tenant_id")
    var tenantId: TenantId? = TenantId(),
    @SerializedName("updated_by")
    var updatedBy: String? = "",
    @SerializedName("upload_receipt")
    var uploadReceipt: List<Any>? = listOf()
)