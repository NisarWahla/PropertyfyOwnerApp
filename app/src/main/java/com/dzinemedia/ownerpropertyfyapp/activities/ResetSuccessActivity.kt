package com.dzinemedia.ownerpropertyfyapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.databinding.ActivityResetSuccessBinding
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler

class ResetSuccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetSuccessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reset_success)
        setViewClickListener()
        onBackPressedDispatcher.addCallback(this@ResetSuccessActivity, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        })
    }
    private fun setViewClickListener() {
        binding.btnGoToLogin.setOnClickListener(DebounceClickHandler {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finishAffinity()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        })
        binding.back.setOnClickListener(DebounceClickHandler {
            onBackPressedDispatcher.onBackPressed()
        })
    }
}