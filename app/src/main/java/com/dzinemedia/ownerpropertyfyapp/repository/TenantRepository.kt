package com.dzinemedia.ownerpropertyfyapp.repository

import com.dzinemedia.ownerpropertyfyapp.apiInterface.APIInterface
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import java.io.File

class TenantRepository(val apiInterface: APIInterface) {
    suspend fun login(email: String, password: String): LoginModel {
        return apiInterface.login(email, password)
    }

    suspend fun forgotPassword(email: String): LoginModel {
        return apiInterface.forgotPassword(email)
    }

    suspend fun verifyOTP(email: String, otp: String): LoginModel {
        return apiInterface.verifyOTP(email, otp)
    }

    suspend fun changePassword(
        email: String,
        password: String,
        confirmPassword: String
    ): LoginModel {
        return apiInterface.changePassword(email, password, confirmPassword)
    }

    suspend fun logout(token: String, tenantId: Int): LogoutModel {
        return apiInterface.logout("Bearer $token", tenantId)
    }

    suspend fun updateProfile(
        token: String,
        tenantId: Int,
        name: String,
        email: String,
        phoneNumber: String,
        fileProfile: File?,
        filePassport: File?
    ): Call<LoginModel> {
        if (fileProfile != null && filePassport != null) {
            val requestFile = fileProfile.asRequestBody("image/*".toMediaTypeOrNull())
            val bodyProfile =
                MultipartBody.Part.createFormData("image", fileProfile.name, requestFile)
            val passportRequestFile: RequestBody
            val bodyPassport: MultipartBody.Part
            if (filePassport.toString().contains(".pdf")) {
                passportRequestFile =
                    filePassport.asRequestBody("application/pdf".toMediaTypeOrNull())
                bodyPassport = MultipartBody.Part.createFormData(
                    "upload_passport",
                    filePassport.name,
                    passportRequestFile
                )
            } else {
                passportRequestFile = filePassport.asRequestBody("image/*".toMediaTypeOrNull())
                bodyPassport = MultipartBody.Part.createFormData(
                    "upload_passport",
                    filePassport.name,
                    passportRequestFile
                )
            }
            val tenantIdRequestBody =
                tenantId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val nameRequestBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val emailRequestBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
            val phoneRequestBody = phoneNumber.toRequestBody("text/plain".toMediaTypeOrNull())
            return apiInterface.updateProfile(
                "Bearer $token",
                tenantIdRequestBody,
                nameRequestBody,
                emailRequestBody,
                phoneRequestBody,
                bodyProfile,
                bodyPassport
            )
        } else if (fileProfile != null) {
            val requestFile = fileProfile.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", fileProfile.name, requestFile)
            val tenantIdRequestBody =
                tenantId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val nameRequestBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val emailRequestBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
            val phoneRequestBody = phoneNumber.toRequestBody("text/plain".toMediaTypeOrNull())
            return apiInterface.updateProfileWithProfileImg(
                "Bearer $token",
                tenantIdRequestBody,
                nameRequestBody,
                emailRequestBody,
                phoneRequestBody,
                body
            )
        } else if (filePassport != null) {
            val passportRequestFile: RequestBody
            val bodyPassport: MultipartBody.Part
            if (filePassport.toString().contains(".pdf")) {
                passportRequestFile =
                    filePassport.asRequestBody("application/pdf".toMediaTypeOrNull())
                bodyPassport = MultipartBody.Part.createFormData(
                    "upload_passport",
                    filePassport.name,
                    passportRequestFile
                )
            } else {
                passportRequestFile = filePassport.asRequestBody("image/*".toMediaTypeOrNull())
                bodyPassport = MultipartBody.Part.createFormData(
                    "upload_passport",
                    filePassport.name,
                    passportRequestFile
                )
            }
            val tenantIdRequestBody =
                tenantId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val nameRequestBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val emailRequestBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
            val phoneRequestBody = phoneNumber.toRequestBody("text/plain".toMediaTypeOrNull())
            return apiInterface.updateProfileWithPassportImg(
                "Bearer $token",
                tenantIdRequestBody,
                nameRequestBody,
                emailRequestBody,
                phoneRequestBody,
                bodyPassport
            )
        } else {
            return apiInterface.updateProfileWithoutFile(
                "Bearer $token",
                tenantId,
                name,
                email,
                phoneNumber
            )
        }
    }

    suspend fun changePassword(
        token: String,
        userId: Int,
        oldPassword: String,
        newPassword: String
    ): LoginModel {
        return apiInterface.changePassword("Bearer $token", userId, oldPassword, newPassword)
    }

    suspend fun getDashboardData(
        token: String,
        userId: Int
    ): DashboardModel {
        return apiInterface.getDashboardData("Bearer $token", userId)
    }

    suspend fun getOwnerProperties(
        token: String,
        userId: Int
    ): OwnerPropertiesModel {
        return apiInterface.getOwnerProperties("Bearer $token", userId)
    }

    suspend fun getOwnerPropertiesOverview(
        token: String,
        userId: Int,
        propertyId: Int
    ): OwnerPropertyOverviewModel {
        return apiInterface.getOwnerPropertiesOverview("Bearer $token", userId, propertyId)
    }

    suspend fun getOwnerPropertyRentalOverview(
        token: String,
        userId: Int,
        rentalId: Int,
        propertyId: Int
    ): OwnerPropertyRentalOverview {
        return apiInterface.getOwnerPropertyRentalOverview(
            "Bearer $token",
            userId,
            rentalId,
            propertyId
        )
    }

    suspend fun getOwnerPropertyRentalPaymentOverview(
        token: String,
        userId: Int,
        rentalId: Int,
        propertyId: Int,
        rentalPaymentId: Int
    ): OwnerPropertyRentalPaymentOverview {
        return apiInterface.getOwnerPropertyRentalPaymentOverview(
            "Bearer $token",
            userId,
            rentalId,
            propertyId,
            rentalPaymentId
        )
    }

    suspend fun getAllBills(token: String, ownerId: Int): BillsModel {
        return apiInterface.getAllBills("Bearer $token", ownerId)
    }

    suspend fun getAllTenants(token: String, ownerId: Int): MyTenantsModel {
        return apiInterface.getAllTenants("Bearer $token", ownerId)
    }

    suspend fun getOwnerTenantPropertiesOverview(
        token: String,
        ownerId: Int,
        tenantId: Int,
        propertyId: Int
    ): TenantOverviewModel {
        return if (propertyId > 0) {
            apiInterface.getOwnerTenantPropertiesOverview(
                "Bearer $token",
                ownerId,
                tenantId,
                propertyId
            )
        } else {
            apiInterface.getOwnerTenantOverviewWithoutPropertyId(
                "Bearer $token",
                ownerId,
                tenantId
            )
        }
    }
}