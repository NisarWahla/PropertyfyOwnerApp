package com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview


import com.google.gson.annotations.SerializedName

data class PropertyIdX(
    @SerializedName("address")
    var address: String? = "",
    @SerializedName("app_section")
    var appSection: AppSection? = AppSection(),
    @SerializedName("area_type")
    var areaType: String? = "",
    @SerializedName("building")
    var building: String? = "",
    @SerializedName("city")
    var city: City? = City(),
    @SerializedName("contracts")
    var contracts: List<ContractX>? = listOf(),
    @SerializedName("country")
    var country: Country? = Country(),
    @SerializedName("custom_mp_id")
    var customMpId: String? = "",
    @SerializedName("floor_number")
    var floorNumber: String? = "",
    @SerializedName("furnish_details")
    var furnishDetails: List<String>? = listOf(),
    @SerializedName("furnish_type")
    var furnishType: String? = "",
    @SerializedName("id")
    var id: String = "0",
    @SerializedName("images")
    var images: List<Image>? = listOf(),
    @SerializedName("is_draft")
    var isDraft: String = "0",
    @SerializedName("maintenances_expense")
    var maintenancesExpense: MaintenancesExpense? = MaintenancesExpense(),
    @SerializedName("mp_id")
    var mpId: String? = "",
    @SerializedName("payment_history")
    var paymentHistory: List<PaymentHistory>? = listOf(),
    @SerializedName("property_amenities")
    var propertyAmenities: List<String>? = listOf(),
    @SerializedName("property_area")
    var propertyArea: String? = "",
    @SerializedName("property_id")
    var propertyId: String = "0",
    @SerializedName("property_info")
    var propertyInfo: PropertyInfo? = PropertyInfo(),
    @SerializedName("property_status")
    var propertyStatus: String = "0",
    @SerializedName("property_sub_area")
    var propertySubArea: String? = "",
    @SerializedName("property_value")
    var propertyValue: PropertyValue? = PropertyValue(),
    @SerializedName("rental_income")
    var rentalIncome: RentalIncome? = RentalIncome(),
    @SerializedName("rentals_info")
    var rentalsInfo: RentalsInfo? = RentalsInfo(),
    @SerializedName("tenants_details")
    var tenantsDetails: List<TenantsDetail>? = listOf(),
    @SerializedName("unit_number")
    var unitNumber: String? = ""
): java.io.Serializable