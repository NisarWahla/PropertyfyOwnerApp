package com.dzinemedia.ownerpropertyfyapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.InternetCallback
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.RetrofitMessageCallback
import com.dzinemedia.ownerpropertyfyapp.apiInterface.RetrofitClient
import com.dzinemedia.ownerpropertyfyapp.customeDialog.InternetDialogFragment
import com.dzinemedia.ownerpropertyfyapp.databinding.ActivityNewPasswordBinding
import com.dzinemedia.ownerpropertyfyapp.repository.TenantRepository
import com.dzinemedia.ownerpropertyfyapp.utility.Utils
import com.dzinemedia.ownerpropertyfyapp.viewModel.TenantViewModel
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewPasswordBinding
    private lateinit var viewModel: TenantViewModel
    private var email: String? = null
    private var isResponseSuccess = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_password)
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
        if (intent.hasExtra("email")) {
            email = intent.getStringExtra("email")
        }
        onBackPressedDispatcher.addCallback(this@NewPasswordActivity, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        })
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
            onBackPressedDispatcher.onBackPressed()
        })
        binding.passwordEyeIcon.setOnClickListener(DebounceClickHandler {
            Utils.togglePasswordVisibility(binding.txtPassword, binding.passwordEyeIcon)
        })
        binding.confirmPasswordEyeIcon.setOnClickListener(DebounceClickHandler {
            Utils.togglePasswordVisibility(
                binding.txtConfirmPassword,
                binding.confirmPasswordEyeIcon
            )
        })
    }

    private fun showInternetDialog() {
        val internetDialog =
            InternetDialogFragment(object : InternetCallback {
                override fun tryAgainClick(internetDialog: InternetDialogFragment) {
                    if (Utils.isNetworkAvailable(this@NewPasswordActivity)) {
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
        val password = binding.txtPassword.text.toString()
        val confirmPassword = binding.txtConfirmPassword.text.toString()
        if (verifyValidation(password, confirmPassword)) {
            viewModel.loading.observe(this) { isProgress ->
                if (isProgress) {
                    showProgress()
                } else {
                    hideProgress()
                }
            }
            viewModel.loginApiLiveData.observe(this) {
                if (it.success == true) {
                    if (!isResponseSuccess) {
                        isResponseSuccess = true
                        val txtMessage = it.message
                        Utils.showToast(this, txtMessage.toString())
                        moveToNewPassword()
                    }
                } else {
                    Utils.showToast(this, it.message.toString())
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                email?.let {
                    viewModel.changePassword(it, password, confirmPassword, object :
                        RetrofitMessageCallback {
                        override fun retrofitErrorMessage(message: String) {
                            Utils.showToast(this@NewPasswordActivity, message)
                        }

                    })
                }
            }
        }
    }

    private fun verifyValidation(password: String, confirmPassword: String): Boolean {
        if (TextUtils.isEmpty(password.trim())) {
            binding.txtPassword.error = binding.root.context.getString(R.string.please_enter_your_password)
            binding.txtPassword.requestFocus()
            return false
        }
        if (TextUtils.isEmpty(confirmPassword.trim())) {
            binding.txtConfirmPassword.error = binding.root.context.getString(R.string.please_enter_your_confirm_password)
            binding.txtConfirmPassword.requestFocus()
            return false
        }
        if (password.contains(" ")) {
            binding.txtPassword.error =
                binding.root.context.getString(R.string.space_not_allowed)
            binding.txtPassword.requestFocus()
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
    private fun moveToNewPassword() {
        val intent = Intent(this, ResetSuccessActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}