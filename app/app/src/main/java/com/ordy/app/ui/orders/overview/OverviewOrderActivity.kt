package com.ordy.app.ui.orders.overview

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.ApiServiceViewModelFactory
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.ActivityOverviewOrderBinding
import kotlinx.android.synthetic.main.activity_overview_order.*

class OverviewOrderActivity : AppCompatActivity() {

    private val viewModel: OverviewOrderViewModel by viewModels { ApiServiceViewModelFactory(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create binding for the activity.
        val binding: ActivityOverviewOrderBinding = DataBindingUtil.setContentView(this, R.layout.activity_overview_order)
        binding.handlers = OverviewOrderHandlers(this, viewModel)

        // Set the action bar elevation to 0, since the order extends the action bar.
        if(supportActionBar != null) {
            supportActionBar!!.elevation = 0F
        }

        // Extract the "order_id" from the given intent variables.
        val orderId = intent.getIntExtra("order_id", -1)

        // Fetch the specific order.
        FetchHandler.handle(viewModel.order, viewModel.apiService.order(orderId))

        // Observe the changes of the fetch.
        viewModel.order.observe(this, Observer {

            when(it.status) {

                QueryStatus.LOADING -> {
                    Log.i("TAG", "NOW LOADING")
                }

                QueryStatus.SUCCESS -> {
                    val order = it.requireData()

                    order_deadline_time.text = order.deadline.toString()
                    order_title.text = "Order: ${order.location.name}"
                    order_location_name.text = order.location.name
                    order_courier_name.text = order.courier.username
                }

                QueryStatus.ERROR -> {
                    // TODO: error handling.
                }
            }
        })
    }
}
