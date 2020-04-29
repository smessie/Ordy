package com.ordy.backend.services.notifications

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class NotificationService {

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

    fun sendNotification(target: String, content: Map<String, String>) {
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

    fun createNotificationContent(title: String, text: String, type: NotificationType, extra: Map<String, String> = emptyMap()): Map<String, String> {
        return mutableMapOf(
                "notificationTitle" to title,
                "notificationText" to text,
                "type" to type.toString()
        ).also {
            for (i in extra) {
                it[i.key] = i.value
            }
        }
    }
}