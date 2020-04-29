package com.ordy.backend.services.notifications

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.ordy.backend.database.models.User
import com.ordy.backend.database.repositories.DeviceTokenRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class NotificationService(
        val deviceTokenRepository: DeviceTokenRepository
) {

    private lateinit var firebaseApp: FirebaseApp

    @PostConstruct
    private fun init() {
        val options: FirebaseOptions = FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .build()

        firebaseApp = if (FirebaseApp.getApps().isEmpty()) {
            // Initialize if none found
            FirebaseApp.initializeApp(options)
        } else {
            // Re use existing app
            FirebaseApp.getInstance()
        }
    }

    fun sendNotification(users: List<User>, content: Map<String, String>) {
        users.forEach { sendNotification(it, content) }
    }

    fun sendNotification(user: User, content: Map<String, String>) {
        deviceTokenRepository.getAllByUser(user).forEach { sendNotification(it.token, content) }
    }

    private fun sendNotification(target: String, content: Map<String, String>) {
        val message = Message.builder()
                .setToken(target)
                .putAllData(content)
                .build()

        try {
            FirebaseMessaging.getInstance(firebaseApp).send(message)
        } catch (e: FirebaseMessagingException) {
            LoggerFactory.getLogger(this::class.java).error(e.toString())
        }

    }

    fun createNotificationContent(
            title: String = "Title",
            subtitle: String = "Subtitle",
            detail: String = "Detail",
            summary: String = "Summary",
            type: NotificationType = NotificationType.INVITE_NEW,
            extra: Map<String, String> = emptyMap()): Map<String, String> {
        return mutableMapOf(
                "type" to type.toString(),
                "notificationTitle" to title,
                "notificationSubtitle" to subtitle,
                "notificationContent" to detail,
                "notificationSummary" to summary
        ).also {
            for (i in extra) {
                it[i.key] = i.value
            }
        }
    }
}