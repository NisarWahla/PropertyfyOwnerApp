package com.dzinemedia.ownerpropertyfyapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.ForgotCallback
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.InternetCallback
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.RetrofitMessageCallback
import com.dzinemedia.ownerpropertyfyapp.apiInterface.RetrofitClient
import com.dzinemedia.ownerpropertyfyapp.customeDialog.InternetDialogFragment
import com.dzinemedia.ownerpropertyfyapp.databinding.ActivityForgotPasswordBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel.LoginModel
import com.dzinemedia.ownerpropertyfyapp.repository.TenantRepository
import com.dzinemedia.ownerpropertyfyapp.utility.Utils
import com.dzinemedia.ownerpropertyfyapp.viewModel.TenantViewModel
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var viewModel: TenantViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
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
    }
    private fun setViewClickListener() {
        binding.btnVerificationCode.setOnClickListener(DebounceClickHandler {
            if (Utils.isNetworkAvailable(this)) {
                hideKeyboard(it)
                forgotPassword()
            } else {
                showInternetDialog()
            }
        })
        binding.back.setOnClickListener(DebounceClickHandler {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        })
    }

    private fun showInternetDialog() {
        val internetDialog =
            InternetDialogFragment(object : InternetCallback {
                override fun tryAgainClick(internetDialog: InternetDialogFragment) {
                    if (Utils.isNetworkAvailable(this@ForgotPasswordActivity)) {
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
        binding.emailLayout.isEnabled = false
        binding.etEmail.isEnabled = false
        binding.btnVerificationCode.isEnabled = false
        binding.back.isEnabled = false
    }

    private fun hideProgress() {
        binding.progressApi.visibility = View.GONE
    }

    private fun forgotPassword() {
        val email = binding.etEmail.text.toString()
        if (emailValidation(email)) {
            viewModel.loading.observe(this) { isProgress ->
                if (isProgress) {
                    showProgress()
                } else {
                    hideProgress()
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.forgotPassword(email, object : RetrofitMessageCallback {
                    override fun retrofitErrorMessage(message: String) {
                        Utils.showToast(this@ForgotPasswordActivity, message)
                    }

                }, object : ForgotCallback {
                    override fun forgotItemClick(loginModel: LoginModel) {
                        if (loginModel.success == true) {
                            Utils.showToast(binding.root.context, loginModel.message.toString())
                            val txtEmail = loginModel.data?.email
                            moveToOTP(txtEmail.toString())
                        } else {
                            Utils.showToast(binding.root.context, loginModel.message.toString())
                        }
                    }
                })
            }
        }
    }

    private fun moveToOTP(email: String) {
        val intent = Intent(this, OTPActivity::class.java)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

    }

    private fun emailValidation(email: String): Boolean {
        if (TextUtils.isEmpty(email.trim())) {
            binding.etEmail.error = binding.root.context.getString(R.string.please_enter_your_email)
            binding.etEmail.requestFocus()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            binding.etEmail.error =
                binding.root.context.getString(R.string.enter_valid_email_address)
            binding.etEmail.requestFocus()
            return false
        }
        return true
    }
}