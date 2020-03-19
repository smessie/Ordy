package com.ordy.app.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.ordy.app.R
import com.ordy.app.ui.profile.ProfileViewModel

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the activity layout file.
        setContentView(R.layout.activity_settings)

        // Create the view model.
        val viewModel: SettingsViewModel by viewModels()
    }
}
