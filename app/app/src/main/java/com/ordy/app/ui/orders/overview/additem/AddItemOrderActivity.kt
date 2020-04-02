package com.ordy.app.ui.orders.overview.additem

import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.ordy.app.R
import com.ordy.app.api.ApiServiceViewModelFactory
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.ActivityAddItemOrderBinding
import com.ordy.app.util.SnackbarUtil

class AddItemOrderActivity : AppCompatActivity() {

    val viewModel: AddItemOrderViewModel by viewModels { ApiServiceViewModelFactory(applicationContext) }

    lateinit var listAdapter: AddItemOrderListAdapter

    lateinit var handlers: AddItemOrderHandlers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create binding for the activity.
        val binding: ActivityAddItemOrderBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_item_order)
        handlers = AddItemOrderHandlers(this, viewModel)
        binding.handlers = handlers
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Extract the given intent variables.
        val locationId = intent.getIntExtra("location_id", -1)
        val orderId = intent.getIntExtra("order_id", -1)

        // Fetch the specific cuisine items.
        FetchHandler.handle(viewModel.cuisineItems, viewModel.apiService.locationItems(locationId))

        // Create the list view adapter
        listAdapter = AddItemOrderListAdapter(this, orderId, viewModel)
        binding.root.findViewById<ListView>(R.id.order_cuisine_items).adapter = listAdapter

        // Update the list adapter when the "cuisine" query updates
        viewModel.cuisineItems.observe(this, Observer {

            // Catch possible errors.
            if(viewModel.getCuisineItems().status == QueryStatus.ERROR) {
                    AlertDialog.Builder(this).apply {
                        setTitle("Unable to fetch predefined items")
                        setMessage(viewModel.getCuisineItems().requireError().message)
                        setPositiveButton(android.R.string.ok) { _, _ ->

                            // Close the activity
                            finish()
                        }
                    }.show()
            }

            // Update the orders
            listAdapter.update()
        })

        // Update the "search value" of the list adapter when a change is observed
        viewModel.searchValueData.observe(this, Observer {

            // Update the list adapter
            listAdapter.update()
        })

        // Observe the result of adding an item to the order.
        viewModel.addItemResult.observe(this, Observer {

            when(it.status) {

                QueryStatus.LOADING -> {
                    SnackbarUtil.openSnackbar(
                        binding.root,
                        "Adding item..."
                    )
                }

                QueryStatus.SUCCESS -> {
                    SnackbarUtil.closeSnackbar(binding.root)

                    // Go back to the order overview activity.
                    finish()
                }

                QueryStatus.ERROR -> {
                    SnackbarUtil.closeSnackbar(binding.root)

                    ErrorHandler.handle(it.error, binding.root)
                }
            }
        })
    }
}