package com.ordy.app.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.ordy.app.R
import com.ordy.app.databinding.ActivityProfileBinding
import com.ordy.app.ui.login.LoginViewModel

class ProfileActivity : AppCompatActivity() {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create binding for the activity.
        val binding: ActivityProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        binding.handlers = ProfileHandlers(this, viewModel)
    }
}
