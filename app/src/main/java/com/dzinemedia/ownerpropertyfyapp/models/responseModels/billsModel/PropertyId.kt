package com.dzinemedia.ownerpropertyfyapp.models.responseModels.billsModel


import com.google.gson.annotations.SerializedName

data class PropertyId(
    @SerializedName("added_by")
    var addedBy: String = "0",
    @SerializedName("address")
    var address: String? = "",
    @SerializedName("area_type")
    var areaType: String? = "",
    @SerializedName("building")
    var building: String? = "",
    @SerializedName("cities")
    var cities: Cities? = Cities(),
    @SerializedName("city")
    var city: String? = "",
    @SerializedName("contracts")
    var contracts: List<Contract?>? = listOf(),
    @SerializedName("countries")
    var countries: Countries? = Countries(),
    @SerializedName("country")
    var country: String? = "",
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("custom_mp_id")
    var customMpId: String? = "",
    @SerializedName("deleted_at")
    var deletedAt: Any? = Any(),
    @SerializedName("expected_rent_price")
    var expectedRentPrice: Long? = 0,
    @SerializedName("expected_sale_price")
    var expectedSalePrice: Any? = Any(),
    @SerializedName("floor_number")
    var floorNumber: String? = "",
    @SerializedName("furnish_details")
    var furnishDetails: List<String?>? = listOf(),
    @SerializedName("furnish_type")
    var furnishType: String? = "",
    @SerializedName("id")
    var id: String = "0",
    @SerializedName("images")
    var images: List<Image?>? = listOf(),
    @SerializedName("is_draft")
    var isDraft: String = "0",
    @SerializedName("last_rented")
    var lastRented: String? = "",
    @SerializedName("last_vacant")
    var lastVacant: String? = "",
    @SerializedName("managed_by")
    var managedBy: Any? = Any(),
    @SerializedName("minimum_rent_price")
    var minimumRentPrice: String = "0",
    @SerializedName("minimum_sale_price")
    var minimumSalePrice: Any? = Any(),
    @SerializedName("mp_id")
    var mpId: String? = "",
    @SerializedName("owner_id")
    var ownerId: String = "0",
    @SerializedName("prev_property_value")
    var prevPropertyValue: String = "0",
    @SerializedName("property_amenities")
    var propertyAmenities: List<String?>? = listOf(),
    @SerializedName("property_area")
    var propertyArea: String? = "",
    @SerializedName("property_balance")
    var propertyBalance: String = "0",
    @SerializedName("property_details")
    var propertyDetails: String? = "",
    @SerializedName("property_for")
    var propertyFor: String? = "",
    @SerializedName("property_info")
    var propertyInfo: PropertyInfo? = PropertyInfo(),
    @SerializedName("property_status")
    var propertyStatus: String = "0",
    @SerializedName("property_sub_area")
    var propertySubArea: String? = "",
    @SerializedName("property_tenants")
    var propertyTenants: String = "0",
    @SerializedName("property_type")
    var propertyType: String? = "",
    @SerializedName("property_value")
    var propertyValue: String? = "",
    @SerializedName("rent_duration_from")
    var rentDurationFrom: String? = "",
    @SerializedName("rent_duration_to")
    var rentDurationTo: String? = "",
    @SerializedName("rent_year")
    var rentYear: String? = "",
    @SerializedName("rentals")
    var rentals: List<Rental?>? = listOf(),
    @SerializedName("unit_number")
    var unitNumber: String? = "",
    @SerializedName("updated_at")
    var updatedAt: String? = "",
    @SerializedName("updated_by")
    var updatedBy: String = "0"
)