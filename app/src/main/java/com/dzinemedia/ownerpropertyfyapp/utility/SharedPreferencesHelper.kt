package com.dzinemedia.ownerpropertyfyapp.utility

import android.content.Context
import android.content.SharedPreferences
import com.dzinemedia.ownerpropertyfyapp.utility.Constants.KEY_LOGIN_MODEL
import com.dzinemedia.ownerpropertyfyapp.utility.Constants.PREFS_NAME

class SharedPreferencesHelper private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        @Volatile
        private var instance: SharedPreferencesHelper? = null

        fun getInstance(context: Context): SharedPreferencesHelper {
            return instance ?: synchronized(this) {
                instance ?: SharedPreferencesHelper(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    fun saveLoginModel(loginModel: String) {
        sharedPreferences.edit().putString(KEY_LOGIN_MODEL, loginModel).apply()
    }

    fun getLoginModel(): String? {
        return sharedPreferences.getString(KEY_LOGIN_MODEL, null)
    }

    fun clearAllPref() {
        sharedPreferences.edit().clear().apply()
    }

}