package com.ordy.app.ui.orders.overview.additem

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ordy.app.R
import com.ordy.app.databinding.ActivityAddItemOrderBinding
import kotlinx.android.synthetic.main.activity_add_item_order.view.*
import org.koin.android.viewmodel.ext.android.viewModel

class AddItemOrderActivity : AppCompatActivity() {

    val viewModel: AddItemOrderViewModel by viewModel()

    lateinit var baseAdapter: AddItemOrderBaseAdapter

    lateinit var handlers: AddItemOrderHandlers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create binding for the activity.
        val binding: ActivityAddItemOrderBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_add_item_order)
        handlers = AddItemOrderHandlers(this, viewModel)
        binding.handlers = handlers
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Extract the given intent variables.
        val locationId = intent.getIntExtra("location_id", -1)
        val orderId = intent.getIntExtra("order_id", -1)

        // Fetch the specific cuisine items.
        viewModel.refreshCuisineItems(locationId)

        // Create the list view adapter
        baseAdapter = AddItemOrderBaseAdapter(this, orderId, viewModel, binding.root)
        binding.root.order_cuisine_items.adapter = baseAdapter
    }
}