package com.ordy.app.ui.orders.overview

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.ordy.app.R
import com.ordy.app.api.RepositoryViewModelFactory
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.ActivityOverviewOrderBinding
import com.ordy.app.ui.orders.overview.general.OrderGeneralFragment
import com.ordy.app.ui.orders.overview.personal.OrderPersonalFragment
import com.ordy.app.ui.orders.overview.users.OrderUsersFragment
import com.ordy.app.util.OrderUtil
import com.ordy.app.util.TabsAdapter
import com.ordy.app.util.TimerUtil
import com.ordy.app.util.types.TabsEntry
import kotlinx.android.synthetic.main.activity_overview_order.*
import kotlinx.android.synthetic.main.activity_overview_order.view.*
import java.text.DateFormat
import kotlin.properties.Delegates


class OverviewOrderActivity : AppCompatActivity() {

    private val viewModel: OverviewOrderViewModel by viewModels {
        RepositoryViewModelFactory(
            applicationContext
        )
    }

    private lateinit var tabsAdapter: TabsAdapter

    private var orderId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create binding for the activity.
        val binding: ActivityOverviewOrderBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_overview_order)
        binding.handlers = OverviewOrderHandlers(this, viewModel)

        // Set the action bar elevation to 0, since the order extends the action bar.
        if (supportActionBar != null) {
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
        val viewPager: ViewPager = binding.root.tabs_view
        viewPager.adapter = tabsAdapter

        // Link the viewpager to the tablayout.
        val tabs: TabLayout = binding.root.tabs
        tabs.setupWithViewPager(viewPager)

        // Extract the "order_id" from the given intent variables.
        orderId = intent.getIntExtra("order_id", -1)

        // Fetch the specific order.
        viewModel.refreshOrder(orderId)

        // Swipe to refresh
        binding.root.order_refresh.setOnRefreshListener {
            viewModel.refreshOrder(orderId)
        }

        // Stop refreshing on load
        viewModel.getOrderMLD().observe(this, Observer {
            if (it.status == QueryStatus.SUCCESS || it.status == QueryStatus.ERROR) {
                binding.root.order_refresh.isRefreshing = false
            }
        })

        // Observe the changes of the fetch.
        viewModel.getOrderMLD().observe(this, Observer {

            when (it.status) {

                QueryStatus.LOADING -> {
                    Log.i("TAG", "NOW LOADING")
                }

                QueryStatus.SUCCESS -> {
                    val order = it.requireData()

                    order_deadline_time.text =
                        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                            .format(order.deadline)
                    order_title.text = order.location.name
                    order_location_name.text = order.location.name
                    order_courier_name.text = order.courier.username

                    // Show the bill URL when a bill is present.
                    val billUrl = viewModel.getOrder().requireData().billUrl ?: ""

                    if (!billUrl.isBlank()) {
                        order_bill_button.visibility = View.VISIBLE
                    }

                    // Update the closing time left every second.
                    TimerUtil.updateUI(this, 0, 1000) {
                        order_deadline_time_left.text = OrderUtil.timeLeftFormat(order.deadline)
                    }
                }

                QueryStatus.ERROR -> {
                    ErrorHandler.handle(it.error, binding.root)
                }

                else -> {
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()

        // Update the order.
        viewModel.refreshOrder(orderId)
    }
}
