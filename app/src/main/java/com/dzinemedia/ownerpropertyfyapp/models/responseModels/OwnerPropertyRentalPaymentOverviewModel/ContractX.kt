package com.dzinemedia.ownerpropertyfyapp.models.responseModels.OwnerPropertyRentalPaymentOverviewModel


import com.google.gson.annotations.SerializedName

data class ContractX(
    @SerializedName("added_by")
    var addedBy: String = "0",
    @SerializedName("agreement_file")
    var agreementFile: List<Any>? = listOf(),
    @SerializedName("agreement_period_from")
    var agreementPeriodFrom: String? = "",
    @SerializedName("agreement_period_to")
    var agreementPeriodTo: String? = "",
    @SerializedName("attorney_power")
    var attorneyPower: Any? = Any(),
    @SerializedName("charge_period")
    var chargePeriod: String? = "",
    @SerializedName("company_id")
    var companyId: Any? = Any(),
    @SerializedName("contract_status")
    var contractStatus: String? = "",
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("deleted_at")
    var deletedAt: Any? = Any(),
    @SerializedName("id")
    var id: String = "0",
    @SerializedName("management_fee")
    var managementFee: String = "0",
    @SerializedName("other_owners")
    var otherOwners: Any? = Any(),
    @SerializedName("owner_id")
    var ownerId: OwnerId? = OwnerId(),
    @SerializedName("property_doc")
    var propertyDoc: List<Any>? = listOf(),
    @SerializedName("property_id")
    var propertyId: String = "0",
    @SerializedName("service_charge")
    var serviceCharge: Any? = Any(),
    @SerializedName("updated_at")
    var updatedAt: String? = "",
    @SerializedName("updated_by")
    var updatedBy: String = "0"
)