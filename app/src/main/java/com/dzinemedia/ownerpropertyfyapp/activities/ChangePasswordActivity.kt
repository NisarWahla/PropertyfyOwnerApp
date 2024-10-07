package com.dzinemedia.ownerpropertyfyapp.activities

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.InternetCallback
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.RetrofitMessageCallback
import com.dzinemedia.ownerpropertyfyapp.apiInterface.RetrofitClient
import com.dzinemedia.ownerpropertyfyapp.customeDialog.InternetDialogFragment
import com.dzinemedia.ownerpropertyfyapp.databinding.ActivityChangePasswordBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel.LoginModel
import com.dzinemedia.ownerpropertyfyapp.repository.TenantRepository
import com.dzinemedia.ownerpropertyfyapp.utility.SharedPreferencesHelper
import com.dzinemedia.ownerpropertyfyapp.utility.Utils
import com.dzinemedia.ownerpropertyfyapp.viewModel.TenantViewModel
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChangePasswordActivity : AppCompatActivity() {
    private var jsonModel: LoginModel? = null
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var viewModel: TenantViewModel

    private var token: String? = null
    private var ownerId: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)
        initializeControls()
        setViewClickListener()
    }

    private fun initializeControls() {
        val apiInterface = RetrofitClient.create()
        val repository = TenantRepository(apiInterface)
        viewModel = ViewModelProvider(
            this,
            TenantViewModel.TenantViewModalFactory(repository)
        )[TenantViewModel::class.java]
        val loginModel = SharedPreferencesHelper.getInstance(binding.root.context).getLoginModel()
        jsonModel = Gson().fromJson(loginModel, LoginModel::class.java)
        token = jsonModel?.data?.token
        ownerId = jsonModel?.data?.id?.toInt()
    }

    private fun setViewClickListener() {
        binding.btnGetVerificationCode.setOnClickListener(DebounceClickHandler {
            if (Utils.isNetworkAvailable(this)) {
                hideKeyboard(it)
                changePassword()
            } else {
                showInternetDialog()
            }
        })
        binding.back.setOnClickListener(DebounceClickHandler {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        })
        binding.oldPasswordEyeIcon.setOnClickListener(DebounceClickHandler {
            Utils.togglePasswordVisibility(binding.txtPassword, binding.oldPasswordEyeIcon)

        })
        binding.newPasswordEyeIcon.setOnClickListener(DebounceClickHandler {
            Utils.togglePasswordVisibility(binding.txtConfirmPassword, binding.newPasswordEyeIcon)
        })
    }

    private fun showInternetDialog() {
        val internetDialog =
            InternetDialogFragment(object : InternetCallback {
                override fun tryAgainClick(internetDialog: InternetDialogFragment) {
                    if (Utils.isNetworkAvailable(this@ChangePasswordActivity)) {
                        internetDialog.dismiss()
                    } else {
                        showInternetDialog()
                    }
                }
            })
        internetDialog.show(supportFragmentManager, internetDialog.tag)
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showProgress() {
        binding.progressApi.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progressApi.visibility = View.GONE
    }

    private fun changePassword() {
        val oldPassword = binding.txtPassword.text.toString()
        val newPassword = binding.txtConfirmPassword.text.toString()
        if (verifyValidation(oldPassword, newPassword)) {
            viewModel.loading.observe(this) { isProgress ->
                if (isProgress) {
                    showProgress()
                } else {
                    hideProgress()
                }
            }
            viewModel.loginApiLiveData.observe(this) {
                if (it.success == true) {
                    val txtMessage = it.message
                    Utils.showToast(this, txtMessage.toString())
                    setResult(RESULT_OK)
                    finish()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                } else {
                    Utils.showToast(this, it.message.toString())
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                token?.let { t ->
                    ownerId?.let { tenantId ->
                        viewModel.changePassword(t, tenantId, oldPassword, newPassword, object :
                            RetrofitMessageCallback {
                            override fun retrofitErrorMessage(message: String) {
                                Utils.showToast(this@ChangePasswordActivity, message)
                            }

                        })
                    }
                }
            }
        }
    }

    private fun verifyValidation(password: String, confirmPassword: String): Boolean {
        if (TextUtils.isEmpty(password.trim())) {
            binding.txtPassword.error =
                binding.root.context.getString(R.string.please_enter_your_password)
            binding.txtPassword.requestFocus()
            return false
        }
        if (TextUtils.isEmpty(confirmPassword.trim())) {
            binding.txtConfirmPassword.error =
                binding.root.context.getString(R.string.please_enter_your_confirm_password)
            binding.txtConfirmPassword.requestFocus()
            return false
        }
        if (confirmPassword.contains(" ")) {
            binding.txtConfirmPassword.error =
                binding.root.context.getString(R.string.space_not_allowed)
            binding.txtConfirmPassword.requestFocus()
            return false
        }
        if (password.length < 8) {
            binding.txtPassword.error =
                binding.root.context.getString(R.string.password_must_be_at_least_six_characters)
            binding.txtPassword.requestFocus()
            return false
        }
        if (confirmPassword.length < 8) {
            binding.txtConfirmPassword.error =
                binding.root.context.getString(R.string.password_must_be_at_least_six_characters)
            binding.txtConfirmPassword.requestFocus()
            return false
        }

        return true
    }

}