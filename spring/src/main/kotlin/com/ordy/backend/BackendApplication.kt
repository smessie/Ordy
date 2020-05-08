package com.ordy.backend

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication(scanBasePackages = ["com.ordy.backend"])
class BackendApplication {

    @Bean
    fun setupFirebase(): FirebaseApp {
        val options: FirebaseOptions = FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .build()

        return if (FirebaseApp.getApps().isEmpty()) {
            // Initialize if none found
            FirebaseApp.initializeApp(options)
        } else {
            // Re use existing app
            FirebaseApp.getInstance()
        }
    }
}

fun main(args: Array<String>) {
    runApplication<BackendApplication>(*args)
}