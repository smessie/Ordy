package com.ordy.app.ui.orders.overview.additem

import android.os.Bundle
import android.widget.ListView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.ApiServiceViewModelFactory
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.ActivityAddItemOrderBinding
import kotlinx.android.synthetic.main.activity_add_item_order.view.*

class AddItemOrderActivity : AppCompatActivity() {

    private val viewModel: AddItemOrderViewModel by viewModels { ApiServiceViewModelFactory(applicationContext) }

    var listAdapter: AddItemOrderListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create binding for the activity.
        val binding: ActivityAddItemOrderBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_item_order)
        binding.handlers = AddItemOrderHandlers(this, viewModel)
        binding.viewmodel = viewModel

        // Extract the "location_id" from the given intent variables.
        val locationId = intent.getIntExtra("location_id", -1)

        // Fetch the specific cuisine items.
        FetchHandler.handle(viewModel.cuisineItems, viewModel.apiService.locationItems(locationId))

        // Create the list view adapter
        listAdapter = AddItemOrderListAdapter(applicationContext, Query(QueryStatus.LOADING), "")
        binding.root.findViewById<ListView>(R.id.order_cuisine_items).adapter = listAdapter

        // Update the list adapter when the "cuisine" query updates
        viewModel.cuisineItems.observe(this, Observer {

            val listAdapter = this.listAdapter ?: throw IllegalStateException("List adapter should not be null")

            // Update the orders
            listAdapter.cuisine = it
            listAdapter.update()
        })

        // Update the "search value" of the list adapter when a change is observed
        viewModel.searchFieldValue.observe(this, Observer {

            val listAdapter = this.listAdapter ?: throw IllegalStateException("List adapter should not be null")

            // Update the list adapter
            listAdapter.searchValue = it
            listAdapter.update()
        })
    }
}