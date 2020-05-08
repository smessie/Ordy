package com.ordy.app.ui.settings.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.ordy.app.R

class MainSettingsFragment : PreferenceFragmentCompat() {

    /**
     * Create the preferences screen.
     */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_main, rootKey)
    }
}
