package com.ordy.app.ui.settings

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ordy.app.AppPreferences
import com.ordy.app.R
import com.ordy.app.databinding.ActivitySettingsBinding
import org.koin.android.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create binding for the activity.
        val binding: ActivitySettingsBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_settings)
        binding.handlers = SettingsHandlers(this, viewModel)

        // Set the correct state of the switches based on the AppPreferences
        binding.deadlinesSwitch.isChecked = AppPreferences(this).wantsDeadlineNotifications!!
        binding.paymentsSwitch.isChecked = AppPreferences(this).wantsPaymentsNotifications!!
        binding.ordersSwitch.isChecked = AppPreferences(this).wantsOrdersNotifications!!
        binding.invitesSwitch.isChecked = AppPreferences(this).wantsInvitesNotifications!!

        // Set listeners for changes in the switches
        binding.deadlinesSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            AppPreferences(this).wantsDeadlineNotifications = isChecked
        }

        binding.paymentsSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            AppPreferences(this).wantsPaymentsNotifications = isChecked
        }

        binding.ordersSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            AppPreferences(this).wantsOrdersNotifications = isChecked
        }

        binding.invitesSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            AppPreferences(this).wantsInvitesNotifications = isChecked
        }
    }
}
