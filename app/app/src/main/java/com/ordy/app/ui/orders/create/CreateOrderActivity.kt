package com.ordy.app.ui.orders.create

import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ordy.app.R
import com.ordy.app.databinding.ActivityCreateOrderBinding

class CreateOrderActivity : AppCompatActivity() {

    private val viewModel: CreateOrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the activity layout file.
        setContentView(R.layout.activity_create_order)

        // Create binding for the activity.
        val binding: ActivityCreateOrderBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_order)
        binding.handlers = CreateOrderHandlers(this, viewModel)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        /**
         * Add create menu to appbar.
         */
        menuInflater.inflate(R.menu.create_menu, menu)
        return true
    }
}
