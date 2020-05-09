package com.ordy.app.ui.orders.create

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.InputField
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.ActivityCreateOrderBinding
import com.ordy.app.ui.orders.overview.OverviewOrderActivity
import com.ordy.app.util.SnackbarUtil
import kotlinx.android.synthetic.main.activity_create_order.*
import kotlinx.android.synthetic.main.activity_create_order.view.*
import org.koin.android.viewmodel.ext.android.viewModel

class CreateOrderActivity : AppCompatActivity() {

    private val viewModel: CreateOrderViewModel by viewModel()

    private lateinit var handlers: CreateOrderHandlers

    private lateinit var adapter: CreateOrderGroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create binding for the activity.
        val binding: ActivityCreateOrderBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_create_order)

        // Create the handlers
        handlers = CreateOrderHandlers(this, binding.root, viewModel)

        binding.lifecycleOwner = this
        binding.handlers = handlers
        binding.viewModel = viewModel

        // Set the values for the group input field.
        val groupValues: AutoCompleteTextView = binding.root.input_group_values
        adapter = CreateOrderGroupAdapter(applicationContext, viewModel)
        groupValues.setAdapter(adapter)

        // Set the group when selecting a value.
        groupValues.setOnItemClickListener { _, _, position, _ ->

            if (viewModel.getGroups().status == QueryStatus.SUCCESS) {
                viewModel.setGroupValue(viewModel.getGroups().requireData()[position])
            }
        }

        viewModel.refreshGroups()

        // Set the groups of the spinner.
        viewModel.getGroupsMLD().observe(this, Observer {
            adapter.notifyDataSetChanged()

            // Show an error dialog when the user is not part of any group.
            if (it.status == QueryStatus.SUCCESS) {
                if (it.requireData().isEmpty()) {
                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.order_create_no_group_title))
                        setMessage(getString(R.string.order_create_no_group_description))
                        setPositiveButton(android.R.string.ok) { _, _ ->

                            // Close the activity
                            finish()
                        }
                    }.show()
                }
            }

            // Show an error dialog when unable to fetch groups.
            if (it.status == QueryStatus.ERROR) {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.order_create_groups_failed))
                    setMessage(it.requireError().message)
                    setPositiveButton(android.R.string.ok) { _, _ ->

                        // Close the activity
                        finish()
                    }
                }.show()
            }
        })

        // Observe the result of adding an item to the order.
        viewModel.getCreateOrderMLD().observe(this, Observer {

            when (it.status) {

                QueryStatus.LOADING -> {
                    SnackbarUtil.openSnackbar(
                        getString(R.string.order_create_loading),
                        this
                    )
                }

                QueryStatus.SUCCESS -> {
                    SnackbarUtil.closeSnackbar(findViewById<ViewGroup>(android.R.id.content))

                    // Finish this activity
                    finish()

                    // Go to the order overview activity.
                    val intent = Intent(applicationContext, OverviewOrderActivity::class.java)

                    // Pass the order as extra information:
                    intent.putExtra("order_id", it.requireData().id)

                    startActivity(intent)
                }

                QueryStatus.ERROR -> {
                    SnackbarUtil.closeSnackbar(findViewById<ViewGroup>(android.R.id.content))

                    ErrorHandler().handle(
                        it.error, this, listOf(
                            InputField("locationId", this.input_location),
                            InputField("customLocationName", this.input_location),
                            InputField("deadline", this.input_deadline),
                            InputField("groupId", this.input_group)
                        )
                    )
                }

                else -> {
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
