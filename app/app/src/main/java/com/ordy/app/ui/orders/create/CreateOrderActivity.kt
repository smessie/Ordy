package com.ordy.app.ui.orders.create

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.ordy.app.R
import com.ordy.app.api.ApiServiceViewModelFactory
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.InputField
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.ActivityCreateOrderBinding
import com.ordy.app.ui.orders.overview.OverviewOrderActivity
import kotlinx.android.synthetic.main.activity_create_order.*
import java.security.acl.Group

class CreateOrderActivity : AppCompatActivity() {

    private val viewModel: CreateOrderViewModel by viewModels {
        ApiServiceViewModelFactory(
            applicationContext
        )
    }

    private lateinit var handlers: CreateOrderHandlers

    private lateinit var adapter: CreateOrderGroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create the handlers
        handlers = CreateOrderHandlers(this, viewModel)

        // Create binding for the activity.
        val binding: ActivityCreateOrderBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_create_order)
        binding.lifecycleOwner = this
        binding.handlers = handlers
        binding.viewmodel = viewModel

        // Set the values for the group input field.
        var groupValues: AutoCompleteTextView = binding.root.findViewById(R.id.input_group_values)
        adapter = CreateOrderGroupAdapter(applicationContext, viewModel)
        groupValues.setAdapter(adapter)

        // Set the group when selecting a value.
        groupValues.setOnItemClickListener { _, _, position, _ ->

            if(viewModel.getGroups().status == QueryStatus.SUCCESS) {
                viewModel.setGroupValue(viewModel.getGroups().requireData()[position])
            }
        }

        // Set the groups of the spinner.
        viewModel.groups.observe(this, Observer {
            adapter.notifyDataSetChanged()
        })

        // Observe the result of adding an item to the order.
        viewModel.createOrderResult.observe(this, Observer {

            when(it.status) {

                QueryStatus.LOADING -> {
                    Snackbar.make(binding.root, "Creating order...", Snackbar.LENGTH_LONG).show()
                }

                QueryStatus.SUCCESS -> {

                    // Go to the order overview activity.
                    val intent = Intent(applicationContext, OverviewOrderActivity::class.java)

                    // Pass the order as extra information:
                    intent.putExtra("order_id", it.requireData().id)

                    startActivity(intent)
                }

                QueryStatus.ERROR -> {
                    ErrorHandler.handle(it.error, binding.root, listOf(
                        InputField("locationId", this.input_location),
                        InputField("customLocationName", this.input_location),
                        InputField("deadline", this.input_deadline),
                        InputField("groupId", this.input_group)
                    ))
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        /**
         * Add create menu to appbar.
         */
        menuInflater.inflate(R.menu.create_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.create -> {
            // Call the function to save new order when button clicked.
            handlers.createOrder()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}
