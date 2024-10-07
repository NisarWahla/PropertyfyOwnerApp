package com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview


import com.google.gson.annotations.SerializedName

data class TenantId(
    @SerializedName("added_by")
    var addedBy: String? = "",
    @SerializedName("address")
    var address: String? = "",
    @SerializedName("city")
    var city: String? = "",
    @SerializedName("communication_type")
    var communicationType: String? = "",
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
    var image: List<Any?>? = listOf(),
    @SerializedName("licenses")
    var licenses: List<Any?>? = listOf(),
    @SerializedName("name")
    var name: String? = "",
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
    @SerializedName("profession")
    var profession: String? = "",
    @SerializedName("profile_id")
    var profileId: String? = "",
    @SerializedName("state")
    var state: String? = "",
    @SerializedName("updated_by")
    var updatedBy: Any? = Any(),
    @SerializedName("upload_id")
    var uploadId: List<Any?>? = listOf(),
    @SerializedName("upload_passport")
    var uploadPassport: List<String?>? = listOf(),
    @SerializedName("upload_visa")
    var uploadVisa: Any? = Any()
): java.io.Serializable