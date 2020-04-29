package com.ordy.backend.services

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
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
}