package com.dzinemedia.ownerpropertyfyapp.models.responseModels.billsModel


import com.google.gson.annotations.SerializedName

data class OwnerId(
    @SerializedName("added_by")
    var addedBy: String? = "",
    @SerializedName("address")
    var address: String? = "",
    @SerializedName("city")
    var city: String? = "",
    @SerializedName("communication_type")
    var communicationType: String? = "",
    @SerializedName("company")
    var company: String? = "",
    @SerializedName("company_address")
    var companyAddress: String? = "",
    @SerializedName("company_country")
    var companyCountry: String? = "",
    @SerializedName("company_email")
    var companyEmail: Any? = Any(),
    @SerializedName("company_name")
    var companyName: String? = "",
    @SerializedName("country")
    var country: String? = "",
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("deleted_at")
    var deletedAt: Any? = Any(),
    @SerializedName("email")
    var email: String? = "",
    @SerializedName("id")
    var id: String = "0",
    @SerializedName("image")
    var image: List<String?>? = listOf(),
    @SerializedName("license")
    var license: List<Any?>? = listOf(),
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("owner_type")
    var ownerType: String? = "",
    @SerializedName("phone_1")
    var phone1: String? = "",
    @SerializedName("phone_1_call")
    var phone1Call: String = "",
    @SerializedName("phone_1_whatsapp")
    var phone1Whatsapp: String = "",
    @SerializedName("phone_2")
    var phone2: String? = "",
    @SerializedName("phone_2_call")
    var phone2Call: String = "",
    @SerializedName("phone_2_whatsapp")
    var phone2Whatsapp: String = "",
    @SerializedName("pov")
    var pov: Any? = Any(),
    @SerializedName("profile_id")
    var profileId: String? = "",
    @SerializedName("shared_certificate")
    var sharedCertificate: Any? = Any(),
    @SerializedName("special_instructions")
    var specialInstructions: String? = "",
    @SerializedName("state")
    var state: String? = "",
    @SerializedName("trade_license")
    var tradeLicense: Any? = Any(),
    @SerializedName("updated_at")
    var updatedAt: String? = "",
    @SerializedName("updated_by")
    var updatedBy: Any? = Any(),
    @SerializedName("upload_id")
    var uploadId: List<Any?>? = listOf(),
    @SerializedName("upload_mou")
    var uploadMou: Any? = Any(),
    @SerializedName("upload_passport")
    var uploadPassport: List<String?>? = listOf(),
    @SerializedName("upload_visa")
    var uploadVisa: Any? = Any()
)