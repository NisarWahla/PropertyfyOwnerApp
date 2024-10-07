package com.dzinemedia.ownerpropertyfyapp.apiInterface

import com.dzinemedia.ownerpropertyfyapp.models.responseModels.OwnerPropertyRentalPaymentOverviewModel.OwnerPropertyRentalPaymentOverview
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.billsModel.BillsModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.dashboardModel.DashboardModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel.LoginModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.myTenantsModel.MyTenantsModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerProperties.OwnerPropertiesModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyOverview.OwnerPropertyOverviewModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview.OwnerPropertyRentalOverview
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.tenantOverviewModel.TenantOverviewModel
import com.dzinemedia.tenantpropertyapp.models.ResponseModel.logoutModel.LogoutModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface APIInterface {

    @FormUrlEncoded
    @POST("owner/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginModel

    @FormUrlEncoded
    @POST("owner/verify-email")
    suspend fun forgotPassword(
        @Field("email") email: String
    ): LoginModel

    @FormUrlEncoded
    @POST("owner/verify-otp")
    suspend fun verifyOTP(
        @Field("email") email: String,
        @Field("otp") otp: String
    ): LoginModel

    @FormUrlEncoded
    @POST("owner/forgot-password")
    suspend fun changePassword(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("password_confirmation") confirmPassword: String
    ): LoginModel

    @FormUrlEncoded
    @POST("owner/logout")
    suspend fun logout(
        @Header("Authorization") token: String,
        @Field("user_id") tenantId: Int
    ): LogoutModel

    @Multipart
    @POST("owner/update-profile")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Part("user_id") tenantId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone_1") phoneNumber: RequestBody,
        @Part fileProfile: MultipartBody.Part?,
        @Part filePassport: MultipartBody.Part?
    ): Call<LoginModel>
    @Multipart
    @POST("owner/update-profile")
    fun updateProfileWithProfileImg(
        @Header("Authorization") token: String,
        @Part("user_id") tenantId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone_1") phoneNumber: RequestBody,
        @Part fileProfile: MultipartBody.Part?
    ): Call<LoginModel>
    @Multipart
    @POST("owner/update-profile")
    fun updateProfileWithPassportImg(
        @Header("Authorization") token: String,
        @Part("user_id") tenantId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone_1") phoneNumber: RequestBody,
        @Part filePassport: MultipartBody.Part?
    ): Call<LoginModel>

    @FormUrlEncoded
    @POST("owner/update-profile")
    fun updateProfileWithoutFile(
        @Header("Authorization") token: String,
        @Field("user_id") tenantId: Int,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone_1") phoneNumber: String
    ): Call<LoginModel>

    @FormUrlEncoded
    @POST("owner/change-password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Field("user_id") userId: Int,
        @Field("old_password") oldPassword: String,
        @Field("new_password") newPassword: String
    ): LoginModel

    @FormUrlEncoded
    @POST("owner/dashboard")
    suspend fun getDashboardData(
        @Header("Authorization") token: String,
        @Field("user_id") ownerId: Int
    ): DashboardModel

    @FormUrlEncoded
    @POST("owner/properties")
    suspend fun getOwnerProperties(
        @Header("Authorization") token: String,
        @Field("user_id") ownerId: Int
    ): OwnerPropertiesModel

    @FormUrlEncoded
    @POST("owner/properties/overview")
    suspend fun getOwnerPropertiesOverview(
        @Header("Authorization") token: String,
        @Field("user_id") ownerId: Int,
        @Field("property_id") propertyId: Int
    ): OwnerPropertyOverviewModel

    @FormUrlEncoded
    @POST("owner/rentals/overview")
    suspend fun getOwnerPropertyRentalOverview(
        @Header("Authorization") token: String,
        @Field("user_id") rentalPaymentId: Int,
        @Field("rental_id") rentalId: Int,
        @Field("property_id") propertyId: Int
    ): OwnerPropertyRentalOverview

    @FormUrlEncoded
    @POST("owner/rentals/payment/overview")
    suspend fun getOwnerPropertyRentalPaymentOverview(
        @Header("Authorization") token: String,
        @Field("user_id") ownerId: Int,
        @Field("rental_id") rentalId: Int,
        @Field("property_id") propertyId: Int,
        @Field("rental_payment_id") rentalPaymentId: Int
    ): OwnerPropertyRentalPaymentOverview

    @FormUrlEncoded
    @POST("owner/bills")
    suspend fun getAllBills(
        @Header("Authorization") token: String,
        @Field("user_id") ownerId: Int
    ): BillsModel

    @FormUrlEncoded
    @POST("owner/tenants")
    suspend fun getAllTenants(
        @Header("Authorization") token: String,
        @Field("user_id") ownerId: Int
    ): MyTenantsModel

    @FormUrlEncoded
    @POST("owner/tenants/overview")
    suspend fun getOwnerTenantPropertiesOverview(
        @Header("Authorization") token: String,
        @Field("user_id") ownerId: Int,
        @Field("tenant_id") tenantId: Int,
        @Field("property_id") propertyId: Int
    ): TenantOverviewModel

    @FormUrlEncoded
    @POST("owner/tenants/overview")
    suspend fun getOwnerTenantOverviewWithoutPropertyId(
        @Header("Authorization") token: String,
        @Field("user_id") ownerId: Int,
        @Field("tenant_id") tenantId: Int
    ): TenantOverviewModel
}