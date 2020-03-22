package com.ordy.app.ui.groups.create

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ordy.app.R
import com.ordy.app.databinding.ActivityCreateGroupBinding

class CreateGroupActivity : AppCompatActivity() {

    private val viewModel: CreateGroupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create binding for the activity.
        val binding: ActivityCreateGroupBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_group)
        binding.handlers = CreateGroupHandlers(this, viewModel)
    }
}
