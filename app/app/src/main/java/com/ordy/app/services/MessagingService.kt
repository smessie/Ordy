package com.ordy.app.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.Html
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ordy.app.AppPreferences
import com.ordy.app.MainActivity
import com.ordy.app.R
import com.ordy.app.ui.orders.overview.OverviewOrderActivity
import com.ordy.app.ui.profile.ProfileActivity
import java.text.SimpleDateFormat
import kotlin.random.Random


class MessagingService : FirebaseMessagingService() {

    private val CHANNEL_ID = "ORDY_NOTIFICATION_CHANNEL"
    private val CHANNEL_NAME = "Notifications"
    private val CHANNEL_DESCRIPTION = "Notifications"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {

            // Prevent the user from receiving any notifications when not logged in.
            if (AppPreferences(this).accessToken == null) {
                return
            }

            val type = remoteMessage.data["type"]
            val notificationTitle = remoteMessage.data["notificationTitle"]
            val notificationSubtitle = remoteMessage.data["notificationSubtitle"]
            val notificationContent = remoteMessage.data["notificationContent"]
            val notificationSummary = remoteMessage.data["notificationSummary"]
            val notificationDeadline = remoteMessage.data["notificationDeadline"]

            var intent: Intent? = null
            val deadline = if (notificationDeadline != null) {
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ").parse(notificationDeadline).time
            } else {
                null
            }

            val preferences = PreferenceManager.getDefaultSharedPreferences(this)

            // Check preferences to check whether the notification should be sent
            when (type) {
                "ORDER_CREATE" -> {
                    if (!preferences.getBoolean("notification_orders", true)) {
                        return
                    }
                }

                "ORDER_DEADLINE" -> {
                    if (!preferences.getBoolean("notification_deadline", true)) {
                        return
                    }
                }

                "ORDER_BILL" -> {
                    if (!preferences.getBoolean("notification_bill_picture", true)) {
                        return
                    }
                }

                "PAYMENT_DEBT" -> {
                    if (!preferences.getBoolean("notification_payments", true)) {
                        return
                    }
                }

                "INVITE_NEW" -> {
                    if (!preferences.getBoolean("notification_invites", true)) {
                        return
                    }
                }

                else -> {
                }
            }

            // Create the notification channel.
            this.createNotificationChannel()

            // Order type: ORDER_CREATE or ORDER_DEADLINE or ORDER_BILL
            if (type == "ORDER_CREATE" || type == "ORDER_DEADLINE" || type == "ORDER_BILL") {
                intent = Intent(this, OverviewOrderActivity::class.java)

                // Pass the order id as extra information
                intent.putExtra("order_id", (remoteMessage.data["orderId"] ?: "-1").toInt())
            }

            // Order type: PAYMENT
            if (type == "PAYMENT_DEBT") {
                intent = Intent(this, MainActivity::class.java)

                // Pass which tab to open
                intent.putExtra("open_tab", "payments")
            }

            // Order type: INVITE_NEW
            if (type == "INVITE_NEW") {
                intent = Intent(this, ProfileActivity::class.java)
            }

            // Create the notification.
            this.createNotification(
                title = notificationTitle ?: "",
                subtitle = notificationSubtitle ?: "",
                content = notificationContent ?: "",
                summary = notificationSummary ?: "",
                intent = intent,
                deadline = deadline
            )
        }
    }

    /**
     * Create a notification.
     * @param title Title of the notification
     * @param subtitle Content of the notification
     * @param summary Summary of the notification
     * @param intent Intent to open when the notification is clicked
     * @param deadline Deadline of the notification
     */
    private fun createNotification(
        title: String,
        subtitle: String,
        content: String,
        summary: String,
        intent: Intent? = null,
        deadline: Long? = null
    ) {

        // Build the notification style
        val notificationStyle = NotificationCompat.InboxStyle()
            .addLine(subtitle)
            .setSummaryText(summary)

        // Add the content, splitting on newline.
        content.split("\n").forEach {
            // Convert the line to HTML.
            // Or when not possible (due to lower SDK) strip the HTML tags
            val line = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(it, 1)
            } else {
                it.replace("\\<[^>]*>", "")
            }

            notificationStyle.addLine(line)
        }

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setContentTitle(title)
            .setContentText(subtitle)
            .setStyle(notificationStyle)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        // Add intent when available.
        if (intent != null) {
            // Convert the intent to a pending intent.
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            notificationBuilder.setContentIntent(pendingIntent)
        }

        // Add deadline when available
        if (deadline != null) {
            notificationBuilder.setWhen(deadline)
            notificationBuilder.setShowWhen(true)
        }

        // Spawn the notification
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(Random.nextInt(), notificationBuilder.build())
        }
    }

    /**
     * Create a new notification channel.
     * Required in API 26+ (backwards compatible with older API levels)
     */
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
