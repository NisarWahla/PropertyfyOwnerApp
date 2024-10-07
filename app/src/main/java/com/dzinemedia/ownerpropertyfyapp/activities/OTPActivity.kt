package com.dzinemedia.ownerpropertyfyapp.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.InternetCallback
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.ResendOtpCallback
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.RetrofitMessageCallback
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.VerifyOtpCallback
import com.dzinemedia.ownerpropertyfyapp.apiInterface.RetrofitClient
import com.dzinemedia.ownerpropertyfyapp.customeDialog.InternetDialogFragment
import com.dzinemedia.ownerpropertyfyapp.databinding.ActivityOtpactivityBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel.LoginModel
import com.dzinemedia.ownerpropertyfyapp.repository.TenantRepository
import com.dzinemedia.ownerpropertyfyapp.utility.Utils
import com.dzinemedia.ownerpropertyfyapp.viewModel.TenantViewModel
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OTPActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpactivityBinding
    private lateinit var viewModel: TenantViewModel
    private var email: String? = null
    private var countDownTimer: CountDownTimer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otpactivity)
        initializeControls()
        setViewClickListener()
    }

    private fun initializeControls() {
        binding.txtCounters.visibility = View.VISIBLE
        binding.didReceiveEmail.visibility = View.GONE
        binding.resend.visibility = View.GONE
        startCountdownTimer()
        val apiInterface = RetrofitClient.create()
        val repository = TenantRepository(apiInterface)
        viewModel = ViewModelProvider(
            this,
            TenantViewModel.TenantViewModalFactory(repository)
        )[TenantViewModel::class.java]
        if (intent.hasExtra("email")) {
            email = intent.getStringExtra("email")
        }

        onBackPressedDispatcher.addCallback(this@OTPActivity, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        })
    }

    private fun startCountdownTimer() {
        val oneMinuteInMillis: Long = 60_000

        countDownTimer = object : CountDownTimer(oneMinuteInMillis, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                // Update UI with remaining time
                val secondsUntilFinished = millisUntilFinished / 1000
                binding.txtCounters.text = "Resend in ${secondsUntilFinished}s"

            }

            override fun onFinish() {
                // Timer finished, perform any action needed
                countDownTimer?.cancel()
                binding.txtCounters.visibility = View.GONE
                binding.didReceiveEmail.visibility = View.VISIBLE
                binding.resend.visibility = View.VISIBLE
            }
        }

        countDownTimer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        countDownTimer = null
    }

    private fun showInternetDialog() {
        val internetDialog =
            InternetDialogFragment(object : InternetCallback {
                override fun tryAgainClick(internetDialog: InternetDialogFragment) {
                    if (Utils.isNetworkAvailable(this@OTPActivity)) {
                        internetDialog.dismiss()
                    } else {
                        showInternetDialog()
                    }
                }
            })
        internetDialog.show(supportFragmentManager, internetDialog.tag)
    }

    private fun setViewClickListener() {
        binding.btnContinue.setOnClickListener(DebounceClickHandler {
            if (Utils.isNetworkAvailable(this)) {
                hideKeyboard(it)
                verifyOTP()
            } else {
                showInternetDialog()
            }
        })
        binding.back.setOnClickListener(DebounceClickHandler {
            onBackPressedDispatcher.onBackPressed()
        })
        binding.resend.setOnClickListener(DebounceClickHandler {
            hideKeyboard(it)
            binding.customOtpView.setText("")
            resendOTP()
        })
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showProgress(){
        binding.progressApi.visibility = View.VISIBLE
    }
    private fun hideProgress(){
        binding.progressApi.visibility = View.GONE
    }

    private fun resendOTP() {
        viewModel.loading.observe(this) { isProgress ->
            if (isProgress) {
                showProgress()
            } else {
                hideProgress()
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            email?.let {
                viewModel.resendOtp(it, object : RetrofitMessageCallback {
                    override fun retrofitErrorMessage(message: String) {
                        Utils.showToast(this@OTPActivity, message)
                    }
                }, object : ResendOtpCallback {
                    override fun resendData(responseModel: LoginModel) {
                        if (responseModel.success == true) {
                            val txtMessage = responseModel.message
                            binding.txtCounters.visibility = View.VISIBLE
                            binding.didReceiveEmail.visibility = View.GONE
                            binding.resend.visibility = View.GONE
                            startCountdownTimer()
                            Utils.showToast(this@OTPActivity, txtMessage.toString())

                        } else {
                            Utils.showToast(this@OTPActivity, responseModel.message.toString())
                        }
                    }

                })
            }
        }
    }

    private fun verifyOTP() {
        val otp = binding.customOtpView.text.toString()
        if (verifyValidation(otp)) {
            viewModel.loading.observe(this) { isProgress ->
                if (isProgress) {
                    showProgress()
                } else {
                    hideProgress()
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                email?.let {
                    viewModel.verifyOTP(it, otp, object : RetrofitMessageCallback {
                        override fun retrofitErrorMessage(message: String) {
                            Utils.showToast(this@OTPActivity, message)
                        }

                    }, object : VerifyOtpCallback {
                        override fun verifyData(responseModel: LoginModel) {
                            if (responseModel.success == true) {
                                val txtMessage = responseModel.message
                                Utils.showToast(this@OTPActivity, txtMessage.toString())
                                moveToNewPassword()
                            } else {
                                Utils.showToast(this@OTPActivity, responseModel.message.toString())
                            }
                        }
                    })
                }
            }
        }
    }

    private fun verifyValidation(otp: String): Boolean {
        if (otp.length < 6) {
            binding.customOtpView.error =
                binding.root.context.getString(R.string.otp_exact_six_character)
            binding.customOtpView.requestFocus()
            return false
        }
        return true
    }

    private fun moveToNewPassword() {
        val intent = Intent(this, NewPasswordActivity::class.java)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}