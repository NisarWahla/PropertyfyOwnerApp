package com.dzinemedia.ownerpropertyfyapp.adaptersCallback

import com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel.LoginModel

interface ResendOtpCallback {
    fun resendData(responseModel: LoginModel)
}