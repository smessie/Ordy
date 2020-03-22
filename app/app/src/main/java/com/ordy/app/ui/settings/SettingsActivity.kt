package com.ordy.app.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.ordy.app.R
import com.ordy.app.databinding.ActivitySettingsBinding
import com.ordy.app.ui.profile.ProfileViewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create binding for the activity.
        val binding: ActivitySettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        binding.handlers = SettingsHandlers(this, viewModel)
    }
}
