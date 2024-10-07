package com.dzinemedia.ownerpropertyfyapp.adaptersCallback

import com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel.LoginModel

interface ForgotCallback {
    fun forgotItemClick(loginModel: LoginModel)
}