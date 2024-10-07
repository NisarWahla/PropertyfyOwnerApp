package com.dzinemedia.ownerpropertyfyapp.models.responseModels.tenantOverviewModel


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("address")
    var address: String? = "",
    @SerializedName("city")
    var city: String? = "",
    @SerializedName("communication_type")
    var communicationType: String? = "",
    @SerializedName("country")
    var country: String? = "",
    @SerializedName("email")
    var email: String? = "",
    @SerializedName("id")
    var id: String = "0",
    @SerializedName("image")
    var image: List<Any>? = listOf(),
    @SerializedName("licenses")
    var licenses: List<Any>? = listOf(),
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("phone_1")
    var phone1: String? = "",
    @SerializedName("phone_2")
    var phone2: String? = "",
    @SerializedName("profession")
    var profession: String? = "",
    @SerializedName("properties")
    var properties: List<Property>? = listOf(),
    @SerializedName("rental_contract")
    var rentalContract: List<RentalContract>? = listOf(),
    @SerializedName("rental_details")
    var rentalDetails: RentalDetails? = RentalDetails(),
    @SerializedName("rental_plan")
    var rentalPlan: RentalPlan? = RentalPlan(),
    @SerializedName("state")
    var state: String? = "",
    @SerializedName("upload_id")
    var uploadId: List<Any>? = listOf(),
    @SerializedName("upload_passport")
    var uploadPassport: List<String>? = listOf()
) : java.io.Serializable