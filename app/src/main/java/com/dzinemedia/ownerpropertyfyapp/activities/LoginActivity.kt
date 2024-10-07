package com.dzinemedia.ownerpropertyfyapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.InternetCallback
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.RetrofitMessageCallback
import com.dzinemedia.ownerpropertyfyapp.databinding.ActivityLoginBinding
import com.dzinemedia.ownerpropertyfyapp.utility.SharedPreferencesHelper
import com.dzinemedia.ownerpropertyfyapp.utility.Utils
import com.dzinemedia.ownerpropertyfyapp.apiInterface.RetrofitClient
import com.dzinemedia.ownerpropertyfyapp.customeDialog.InternetDialogFragment
import com.dzinemedia.ownerpropertyfyapp.repository.TenantRepository
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import com.dzinemedia.ownerpropertyfyapp.viewModel.TenantViewModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: TenantViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        initializeControls()
        setViewClickListener()
    }

    private fun setViewClickListener() {
        binding.btnLogin.setOnClickListener(DebounceClickHandler {
            if (Utils.isNetworkAvailable(this)) {
                hideKeyboard(it)
                loginTenant()
            } else {
                showInternetDialog()
            }
        })
        binding.forgotPassword.setOnClickListener(DebounceClickHandler {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        })
        binding.passwordEyeIcon.setOnClickListener(DebounceClickHandler {
            Utils.togglePasswordVisibility(binding.etPassword, binding.passwordEyeIcon)
        })
    }
    private fun showInternetDialog() {
        val internetDialog =
            InternetDialogFragment(object : InternetCallback {
                override fun tryAgainClick(internetDialog: InternetDialogFragment) {
                    if (Utils.isNetworkAvailable(this@LoginActivity)) {
                        internetDialog.dismiss()
                    } else {
                        showInternetDialog()
                    }
                }

            })
        internetDialog.show(supportFragmentManager, internetDialog.tag)
    }

    private fun initializeControls() {
        val apiInterface = RetrofitClient.create()
        val repository = TenantRepository(apiInterface)
        viewModel = ViewModelProvider(
            this,
            TenantViewModel.TenantViewModalFactory(repository)
        )[TenantViewModel::class.java]
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun loginTenant() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        if (emailAndPasswordValidation(email, password)) {
            viewModel.loading.observe(this) { isProgress ->
                if (isProgress) {
                    showProgress()
                } else {
                    hideProgress()
                }
            }
            viewModel.loginApiLiveData.observe(this) {
                if (it.success == true) {
                    val loginModel = Gson().toJson(it)
                    SharedPreferencesHelper.getInstance(this).saveLoginModel(loginModel)
                    moveToMainScreen()
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.login(email, password, object : RetrofitMessageCallback {
                    override fun retrofitErrorMessage(message: String) {
                        Utils.showToast(this@LoginActivity, message)
                    }

                })
            }
        }
    }

    private fun moveToMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun showProgress() {
        binding.progressApi.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progressApi.visibility = View.GONE
    }

    private fun emailAndPasswordValidation(email: String, password: String): Boolean {
        if (TextUtils.isEmpty(email.trim())) {
            binding.etEmail.error = binding.root.context.getString(R.string.please_enter_your_email)
            binding.etEmail.requestFocus()
            return false
        }
        if (TextUtils.isEmpty(password)) {
            binding.etPassword.error =
                binding.root.context.getString(R.string.please_enter_your_password)
            binding.etPassword.requestFocus()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            binding.etEmail.error =
                binding.root.context.getString(R.string.enter_valid_email_address)
            binding.etEmail.requestFocus()
            return false
        }
        if (password.length < 8) {
            binding.etPassword.error =
                binding.root.context.getString(R.string.password_must_be_at_least_six_characters)
            binding.etPassword.requestFocus()
            return false
        }
        return true
    }
}