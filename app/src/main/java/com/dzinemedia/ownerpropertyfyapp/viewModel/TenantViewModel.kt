package com.dzinemedia.ownerpropertyfyapp.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.ForgotCallback
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.ResendOtpCallback
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.RetrofitMessageCallback
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.VerifyOtpCallback
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.OwnerPropertyRentalPaymentOverviewModel.OwnerPropertyRentalPaymentOverview
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.billsModel.BillsModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.dashboardModel.DashboardModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel.LoginModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.myTenantsModel.MyTenantsModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerProperties.OwnerPropertiesModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyOverview.OwnerPropertyOverviewModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview.OwnerPropertyRentalOverview
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.tenantOverviewModel.TenantOverviewModel
import com.dzinemedia.ownerpropertyfyapp.repository.TenantRepository
import com.dzinemedia.tenantpropertyapp.models.ResponseModel.logoutModel.LogoutModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class TenantViewModel(private val repository: TenantRepository) : ViewModel() {
    private val TAG = "TenantViewModel"

    private val loginApiResponseLiveData = MutableLiveData<LoginModel>()
    val loginApiLiveData: LiveData<LoginModel> get() = loginApiResponseLiveData

    private val dashboardApiResponseLiveData = MutableLiveData<DashboardModel>()
    val dashboardApiLiveData: LiveData<DashboardModel> get() = dashboardApiResponseLiveData

    private val ownerApiResponseLiveData = MutableLiveData<OwnerPropertiesModel>()
    val ownerApiLiveData: LiveData<OwnerPropertiesModel> get() = ownerApiResponseLiveData

    private val ownerOverviewApiResponseLiveData = MutableLiveData<OwnerPropertyOverviewModel>()
    val ownerPropertyOverviewApiLiveData: LiveData<OwnerPropertyOverviewModel> get() = ownerOverviewApiResponseLiveData

    private val ownerPropertyRentalApiResponseLiveData =
        MutableLiveData<OwnerPropertyRentalOverview>()
    val ownerPropertyRentalOverviewApiLiveData: LiveData<OwnerPropertyRentalOverview> get() = ownerPropertyRentalApiResponseLiveData
    private val ownerPropertyRentalPaymentApiResponseLiveData =
        MutableLiveData<OwnerPropertyRentalPaymentOverview>()
    val ownerPropertyRentalPaymentOverviewApiLiveData: LiveData<OwnerPropertyRentalPaymentOverview> get() = ownerPropertyRentalPaymentApiResponseLiveData

    private val errorResponseLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = errorResponseLiveData

    private val logoutResponseLiveData = MutableLiveData<LogoutModel>()
    val logoutLiveData: LiveData<LogoutModel> get() = logoutResponseLiveData

    private val billsResponseLiveData = MutableLiveData<BillsModel>()
    val billsLiveData: LiveData<BillsModel> get() = billsResponseLiveData

    private val tenantResponseLiveData = MutableLiveData<MyTenantsModel>()
    val tenantsLiveData: LiveData<MyTenantsModel> get() = tenantResponseLiveData

    private val ownerTenantPropertiesResponseLiveData = MutableLiveData<TenantOverviewModel>()
    val tenantsOverviewLiveData: LiveData<TenantOverviewModel> get() = ownerTenantPropertiesResponseLiveData

    var job: Job? = null

    // MutableLiveData to indicate loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _isLoading

    suspend fun login(
        email: String, password: String, listener: RetrofitMessageCallback
    ) {
        _isLoading.postValue(true)
        job = viewModelScope.launch(Dispatchers.Main) {

            try {
                val response = repository.login(email, password)
                if (response.success == true) {
                    Log.i(TAG, "login: ${response.message}")
                    loginApiResponseLiveData.postValue(response)
                } else {
                    Log.i(TAG, "error: ${response.message}")
                    response.message?.let { listener.retrofitErrorMessage(it) }
                }
                _isLoading.postValue(false)

            } catch (e: Exception) {
                // Handle error and update the MutableLiveData
                _isLoading.postValue(false)
                Log.i(TAG, "login: ${e.message}")
            } finally {
                // Update loading state
                _isLoading.postValue(false)
            }
        }
        job?.join()
    }

    suspend fun forgotPassword(
        email: String, listener: RetrofitMessageCallback, forgotListener: ForgotCallback
    ) {
        _isLoading.postValue(true)
        job = viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = repository.forgotPassword(email)
                if (response.success == true) {
                    forgotListener.forgotItemClick(response)
                } else {
                    response.message?.let {
                        listener.retrofitErrorMessage(it)
                    }
                }
                _isLoading.postValue(false)

            } catch (e: Exception) {
                // Handle error and update the MutableLiveData
                _isLoading.postValue(false)
                Log.i(TAG, "login: ${e.message}")
            } finally {
                // Update loading state
                _isLoading.postValue(false)
            }
        }
        job?.join()
    }

    suspend fun verifyOTP(
        email: String,
        otp: String, listener: RetrofitMessageCallback, verifyListener: VerifyOtpCallback
    ) {
        _isLoading.postValue(true)
        job = viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = repository.verifyOTP(email, otp)
                if (response.success == true) {
                    verifyListener.verifyData(response)
                } else {
                    response.message?.let { listener.retrofitErrorMessage(it) }
                }
                _isLoading.postValue(false)

            } catch (e: Exception) {
                // Handle error and update the MutableLiveData
                _isLoading.postValue(false)
                Log.i(TAG, "login: ${e.message}")
            } finally {
                // Update loading state
                _isLoading.postValue(false)
            }
        }
        job?.join()
    }

    suspend fun resendOtp(
        email: String, listener: RetrofitMessageCallback, resendData: ResendOtpCallback
    ) {
        _isLoading.postValue(true)
        job = viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = repository.forgotPassword(email)
                if (response.success == true) {
                    resendData.resendData(response)
                } else {
                    response.message?.let {
                        listener.retrofitErrorMessage(it)
                    }
                }
                _isLoading.postValue(false)

            } catch (e: Exception) {
                // Handle error and update the MutableLiveData
                _isLoading.postValue(false)
                Log.i(TAG, "login: ${e.message}")
            } finally {
                // Update loading state
                _isLoading.postValue(false)
            }
        }
        job?.join()
    }

    suspend fun changePassword(
        email: String,
        password: String, confirmPassword: String, listener: RetrofitMessageCallback
    ) {
        _isLoading.postValue(true)
        job = viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = repository.changePassword(email, password, confirmPassword)
                if (response.success == true) {
                    loginApiResponseLiveData.postValue(response)
                } else {
                    response.message?.let { listener.retrofitErrorMessage(it) }
                }
                _isLoading.postValue(false)

            } catch (e: Exception) {
                // Handle error and update the MutableLiveData
                _isLoading.postValue(false)
                Log.i(TAG, "login: ${e.message}")
            } finally {
                // Update loading state
                _isLoading.postValue(false)
            }
        }
        job?.join()
    }
    suspend fun logout(
        token: String,
        tenantId: Int
    ) {
        _isLoading.postValue(true)
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.logout(token, tenantId)
                logoutResponseLiveData.postValue(response)
                _isLoading.postValue(false)

            } catch (e: Exception) {
                // Handle error and update the MutableLiveData
                _isLoading.postValue(false)
            } finally {
                // Update loading state
                _isLoading.postValue(false)
            }
        }
        job?.join()
    }
    suspend fun updateProfile(
        token: String,
        tenantId: Int,
        name: String,
        email: String,
        phoneNumber: String,
        file: File?,
        passportFile: File?
    ) {
        _isLoading.postValue(true)
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                val response =
                    repository.updateProfile(
                        token,
                        tenantId,
                        name,
                        email,
                        phoneNumber,
                        file,
                        passportFile
                    )
                loginApiResponseLiveData.postValue(response.execute().body())
                _isLoading.postValue(false)

            } catch (e: Exception) {
                // Handle error and update the MutableLiveData
                _isLoading.postValue(false)
                Log.i(TAG, "login: ${e.message}")
            } finally {
                // Update loading state
                _isLoading.postValue(false)
            }
        }
        job?.join()
    }
    suspend fun changePassword(
        token: String,
        userId: Int,
        oldPassword: String,
        newPassword: String, listener: RetrofitMessageCallback
    ) {
        _isLoading.postValue(true)
        job = viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = repository.changePassword(token, userId, oldPassword, newPassword)
                if (response.success == true) {
                    loginApiResponseLiveData.postValue(response)
                } else {
                    response.message?.let { listener.retrofitErrorMessage(it) }
                }
                _isLoading.postValue(false)

            } catch (e: Exception) {
                // Handle error and update the MutableLiveData
                _isLoading.postValue(false)
                Log.i(TAG, "login: ${e.message}")
            } finally {
                // Update loading state
                _isLoading.postValue(false)
            }
        }
        job?.join()
    }

    suspend fun getDashboardData(
        token: String,
        userId: Int, listener: RetrofitMessageCallback
    ) {
        _isLoading.postValue(true)
        job = viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = repository.getDashboardData(token, userId)
                if (response.success == true) {
                    dashboardApiResponseLiveData.postValue(response)
                } else {
                    response.message?.let { listener.retrofitErrorMessage(it) }
                }
                _isLoading.postValue(false)

            } catch (e: Exception) {
                // Handle error and update the MutableLiveData
                _isLoading.postValue(false)
                Log.i(TAG, "login: ${e.message}")
            } finally {
                // Update loading state
                _isLoading.postValue(false)
            }
        }
        job?.join()
    }

    suspend fun getOwnerProperties(
        token: String,
        userId: Int, listener: RetrofitMessageCallback
    ) {
        _isLoading.postValue(true)
        job = viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = repository.getOwnerProperties(token, userId)
                if (response.success == true) {
                    ownerApiResponseLiveData.postValue(response)
                } else {
                    response.message?.let { listener.retrofitErrorMessage(it) }
                }
                _isLoading.postValue(false)

            } catch (e: Exception) {
                // Handle error and update the MutableLiveData
                _isLoading.postValue(false)
                Log.i(TAG, "login: ${e.message}")
            } finally {
                // Update loading state
                _isLoading.postValue(false)
            }
        }
        job?.join()
    }

    suspend fun getOwnerPropertiesOverview(
        token: String,
        userId: Int, propertyId: Int, listener: RetrofitMessageCallback
    ) {
        _isLoading.postValue(true)
        job = viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = repository.getOwnerPropertiesOverview(token, userId,propertyId)
                if (response.success == true) {
                    ownerOverviewApiResponseLiveData.postValue(response)
                } else {
                    response.message?.let { listener.retrofitErrorMessage(it) }
                }
                _isLoading.postValue(false)

            } catch (e: Exception) {
                // Handle error and update the MutableLiveData
                _isLoading.postValue(false)
                Log.i(TAG, "login: ${e.message}")
            } finally {
                // Update loading state
                _isLoading.postValue(false)
            }
        }
        job?.join()
    }

    suspend fun getOwnerPropertyRentalOverview(
        token: String,
        userId: Int, rentalId: Int, propertyId: Int, listener: RetrofitMessageCallback
    ) {
        _isLoading.postValue(true)
        job = viewModelScope.launch(Dispatchers.Main) {
            try {
                val response =
                    repository.getOwnerPropertyRentalOverview(token, userId, rentalId, propertyId)
                if (response.success == true) {
                    ownerPropertyRentalApiResponseLiveData.postValue(response)
                } else {
                    response.message?.let { listener.retrofitErrorMessage(it) }
                }
                _isLoading.postValue(false)

            } catch (e: Exception) {
                // Handle error and update the MutableLiveData
                _isLoading.postValue(false)
            } finally {
                // Update loading state
                _isLoading.postValue(false)
            }
        }
        job?.join()
    }

    suspend fun getOwnerPropertyRentalPaymentOverview(
        token: String,
        userId: Int, rentalId: Int,
        propertyId: Int, rentalPaymentId: Int, listener: RetrofitMessageCallback
    ) {
        _isLoading.postValue(true)
        job = viewModelScope.launch(Dispatchers.Main) {
            try {
                val response =
                    repository.getOwnerPropertyRentalPaymentOverview(
                        token,
                        userId,
                        rentalId,
                        propertyId, rentalPaymentId
                    )
                if (response.success == true) {
                    ownerPropertyRentalPaymentApiResponseLiveData.postValue(response)
                } else {
                    response.message?.let { listener.retrofitErrorMessage(it) }
                }
                _isLoading.postValue(false)

            } catch (e: Exception) {
                // Handle error and update the MutableLiveData
                _isLoading.postValue(false)
            } finally {
                // Update loading state
                _isLoading.postValue(false)
            }
        }
        job?.join()
    }


    suspend fun getAllBills(
        token: String,
        ownerId: Int
    ) {
        _isLoading.postValue(true)
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getAllBills(token, ownerId)
                Log.i("getProperties", "getProperties: $response")
                billsResponseLiveData.postValue(response)
                _isLoading.postValue(false)

            } catch (e: Exception) {
                // Handle error and update the MutableLiveData
                _isLoading.postValue(false)
                Log.i("getProperties", "getProperties: ${e.message}")
            } finally {
                // Update loading state
                _isLoading.postValue(false)
            }
        }
        job?.join()
    }
    suspend fun getAllTenants(
        token: String,
        ownerId: Int,
        listener: RetrofitMessageCallback
    ) {
        _isLoading.postValue(true)
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getAllTenants(token, ownerId)
                if (response.success == true) {
                    tenantResponseLiveData.postValue(response)
                } else {
                    response.message?.let { listener.retrofitErrorMessage(it) }
                }
                _isLoading.postValue(false)

            } catch (e: Exception) {
                // Handle error and update the MutableLiveData
                _isLoading.postValue(false)
                Log.i("getProperties", "getProperties: ${e.message}")
            } finally {
                // Update loading state
                _isLoading.postValue(false)
            }
        }
        job?.join()
    }
    suspend fun getOwnerTenantPropertiesOverview(
        token: String,
        ownerId: Int,
        tenantId: Int, propertyId: Int,
        listener: RetrofitMessageCallback
    ) {
        _isLoading.postValue(true)
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getOwnerTenantPropertiesOverview(token, ownerId, tenantId, propertyId)
                if (response.success == true) {
                    ownerTenantPropertiesResponseLiveData.postValue(response)
                } else {
                    response.message?.let { listener.retrofitErrorMessage(it) }
                }
                _isLoading.postValue(false)

            } catch (e: Exception) {
                // Handle error and update the MutableLiveData
                _isLoading.postValue(false)
                Log.i("getProperties", "getProperties: ${e.message}")
            } finally {
                // Update loading state
                _isLoading.postValue(false)
            }
        }
        job?.join()
    }


    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    class TenantViewModalFactory(private val context: TenantRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TenantViewModel::class.java)) {
                return TenantViewModel(context) as T
            }
            throw IllegalArgumentException("ViewModel Failed")
        }

    }
}