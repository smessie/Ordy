package com.ordy.app.ui.groups.create

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.ordy.app.R

class CreateGroupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the activity layout file.
        setContentView(R.layout.activity_create_group)

        // Create the view model.
        val viewModel: CreateGroupViewModel by viewModels()
    }
}
