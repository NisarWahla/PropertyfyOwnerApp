package com.dzinemedia.ownerpropertyfyapp.adaptersCallback

import com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel.LoginModel

interface VerifyOtpCallback {
    fun verifyData(responseModel: LoginModel)
}