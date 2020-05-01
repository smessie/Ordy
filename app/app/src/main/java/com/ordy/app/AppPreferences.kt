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
     * User ID of the logged in user
     */
    var userId: Int?
        get() = preferences.getInt("user_id", -1)
        set(value) {
            if (value != null) {
                preferences.edit().putInt("user_id", value).commit()
            }
        }

    /**
     * Boolean that determines whether the user gets notifications for payments
     */
    var wantsPaymentsNotifications: Boolean?
        get() = preferences.getBoolean(userId.toString() + "/" + "notif_payments", true)
        set(value) {
            if (value != null) {
                preferences.edit().putBoolean(userId.toString() + "/" + "notif_payments", value).commit()
            }
        }

    /**
     * Boolean that determines whether the user gets notifications for new orders
     */
    var wantsOrdersNotifications: Boolean?
        get() = preferences.getBoolean(userId.toString() + "/" + "notif_orders", true)
        set(value) {
            if (value != null) {
                preferences.edit().putBoolean(userId.toString() + "/" + "notif_orders", value).commit()
            }
        }

    /**
     * Boolean that determines whether the user gets notifications for new invites
     */
    var wantsInvitesNotifications: Boolean?
        get() = preferences.getBoolean(userId.toString() + "/" + "notif_invites", true)
        set(value) {
            if (value != null) {
                preferences.edit().putBoolean(userId.toString() + "/" + "notif_invites", value).commit()
            }
        }

    /**
     * Boolean that determines whether the user gets notifications when the order deadline draws near
     */
    var wantsDeadlineNotifications: Boolean?
        get() = preferences.getBoolean(userId.toString() + "/" + "notif_deadline", true)
        set(value) {
            if (value != null) {
                preferences.edit().putBoolean(userId.toString() + "/" + "notif_deadline", value).commit()
            }
        }
}