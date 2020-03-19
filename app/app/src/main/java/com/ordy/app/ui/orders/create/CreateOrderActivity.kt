package com.ordy.app.ui.orders.create

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import com.ordy.app.R

class CreateOrderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the activity layout file.
        setContentView(R.layout.activity_create_order)

        // Create the view model.
        val viewModel: CreateOrderViewModel by viewModels()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        /**
         * Add create menu to appbar.
         */
        menuInflater.inflate(R.menu.create_menu, menu)
        return true
    }
}
