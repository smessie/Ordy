package com.ordy.app.ui.orders.overview

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.ordy.app.R
import com.ordy.app.api.ApiServiceViewModelFactory
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.ActivityOverviewOrderBinding
import com.ordy.app.ui.orders.overview.general.OrderGeneralFragment
import com.ordy.app.ui.orders.overview.personal.OrderPersonalFragment
import com.ordy.app.ui.orders.overview.users.OrderUsersFragment
import com.ordy.app.util.OrderUtil
import com.ordy.app.util.TabsAdapter
import com.ordy.app.util.TabsEntry
import kotlinx.android.synthetic.main.activity_overview_order.*
import java.text.SimpleDateFormat

class OverviewOrderActivity : AppCompatActivity() {

    private val viewModel: OverviewOrderViewModel by viewModels { ApiServiceViewModelFactory(applicationContext) }

    private lateinit var tabsAdapter: TabsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create binding for the activity.
        val binding: ActivityOverviewOrderBinding = DataBindingUtil.setContentView(this, R.layout.activity_overview_order)
        binding.handlers = OverviewOrderHandlers(this, viewModel)

        // Set the action bar elevation to 0, since the order extends the action bar.
        if(supportActionBar != null) {
            supportActionBar!!.elevation = 0F
        }

        /**
         * Setup the tabsbar.
         */

        // Create the tabs adapter.
        tabsAdapter = TabsAdapter(supportFragmentManager)
        tabsAdapter.addTabsEntry(TabsEntry(OrderPersonalFragment(), "Your items"))
        tabsAdapter.addTabsEntry(TabsEntry(OrderGeneralFragment(), "Overview"))
        tabsAdapter.addTabsEntry(TabsEntry(OrderUsersFragment(), "Users"))

        // Link the adapter to the viewpager.
        val viewPager: ViewPager = binding.root.findViewById(R.id.tabs_view)
        viewPager.adapter = tabsAdapter

        // Link the viewpager to the tablayout.
        val tabs: TabLayout = binding.root.findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

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

                    order_deadline_time.text = SimpleDateFormat("HH:mm").format(order.deadline)
                    order_deadline_time_left.text = OrderUtil.timeLeftFormat(order.deadline)
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
