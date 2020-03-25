package com.ordy.app

import android.content.Context

class AppPreferences (val context: Context) {

    private val preferences = context.getSharedPreferences("ordy", 0)

    /**
     * Access Token for authentication
     */
    var accessToken: String?
        get() = preferences.getString("access_token", "")
        set(value) {
            preferences.edit().putString("access_token", value).commit()
        }
}