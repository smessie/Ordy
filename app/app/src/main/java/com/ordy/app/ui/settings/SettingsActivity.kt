package com.ordy.app.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ordy.app.R
import com.ordy.app.databinding.ActivitySettingsBinding
import com.ordy.app.ui.settings.fragments.MainSettingsFragment

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create binding for the activity.
        val binding: ActivitySettingsBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_settings)
        binding.handlers = SettingsHandlers(this)

        // Launch the settings screen
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, MainSettingsFragment())
            .commit()
    }
}
