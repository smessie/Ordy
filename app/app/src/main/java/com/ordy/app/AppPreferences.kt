package com.ordy.app

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("ApplySharedPref")
class AppPreferences (val context: Context) {

    private val preferences = context.getSharedPreferences("ordy", Context.MODE_PRIVATE)

    /**
     * Access Token for authentication
     */
    var accessToken: String?
        get() = preferences.getString("access_token", "")
        set(value) {
            preferences.edit().putString("access_token", value).commit()
        }

    /**
     * Device Token for notifications
     */
    var deviceToken: String?
        get() = preferences.getString("device_token", "")
        set(value) {
            preferences.edit().putString("device_token", value).commit()
        }

    /**
     * User ID of the logged in user
     */
    var userId: Int?
        get() = preferences.getInt("user_id", -1)
        set(value) {
            if (value != null) {
                preferences.edit().putInt("user_id", value).commit()
            }
        }
}