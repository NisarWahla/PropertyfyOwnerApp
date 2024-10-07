package com.dzinemedia.ownerpropertyfyapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.databinding.ActivitySplashBinding
import com.dzinemedia.ownerpropertyfyapp.utility.SharedPreferencesHelper

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        val logoAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_animation_splash)
        binding.logoImage.startAnimation(logoAnimation)
        Handler(Looper.getMainLooper()).postDelayed({
            val tenantModel = SharedPreferencesHelper.getInstance(this).getLoginModel()
            if (tenantModel != null) {
                moveToMainScreen()
            } else {
                moveToLogin()
            }
        }, 3000)
    }

    private fun moveToMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
    private fun moveToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}