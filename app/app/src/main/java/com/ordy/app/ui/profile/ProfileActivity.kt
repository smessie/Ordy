package com.ordy.app.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.ordy.app.R
import com.ordy.app.ui.login.LoginViewModel

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the activity layout file.
        setContentView(R.layout.activity_profile)

        // Create the view model.
        val viewModel: ProfileViewModel by viewModels()
    }
}
