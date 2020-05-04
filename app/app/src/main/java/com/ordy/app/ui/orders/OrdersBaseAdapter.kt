package com.ordy.app.ui.orders

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.models.Order
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.orders.overview.OverviewOrderActivity
import com.ordy.app.util.OrderUtil
import com.ordy.app.util.TimerUtil
import kotlinx.android.synthetic.main.list_order_card.view.*
import java.text.DateFormat
import java.util.*

class OrdersBaseAdapter(
    val activity: AppCompatActivity,
    val context: Context,
    val viewModel: OrdersViewModel,
    private val orderStatus: OrdersStatus
) : BaseAdapter() {

    private var orders: Query<List<Order>> = Query()
    private var ordersFiltered: List<Order> = emptyList()

    /**
     * List containing the timers that refresh the closing countdown every second.
     */
    private var timers: MutableList<Timer> = mutableListOf()

    init {
        viewModel.getOrdersMLD().observe(activity, Observer {
            update(it)
        })
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.list_order_card,
            parent,
            false
        )

        when (orders.status) {

            QueryStatus.LOADING -> {

                // Start the shimmer effect & show
                view.order_loading.startShimmer()
                view.order_loading.visibility = View.VISIBLE
                view.order_data.visibility = View.GONE
            }

            QueryStatus.SUCCESS -> {
                val order = ordersFiltered[position]

                // Stop the shimmer effect & hide.
                view.order_loading.stopShimmer()
                view.order_loading.visibility = View.GONE
                view.order_data.visibility = View.VISIBLE

                // Formatting deadline
                val deadlineFormat =
                    DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                        .format(order.deadline)

                // Assign the data.
                view.order_location_name.text = order.location.name
                view.order_group_name.text = order.group.name
                view.order_deadline_time.text = deadlineFormat
                view.order_deadline_time_left.text = OrderUtil.timeLeftFormat(order.deadline)
                view.order_courier_name.text = order.courier.username

                // Update the closing time left every second.
                val updateTimer = TimerUtil.updateUI(activity, 0, 1000) {
                    view.order_deadline_time_left.text = OrderUtil.timeLeftFormat(order.deadline)
                }
                timers.add(updateTimer)

                // Set click handler.
                view.setOnClickListener {

                    val intent = Intent(view.context, OverviewOrderActivity::class.java)

                    // Pass the order as extra information:
                    intent.putExtra("order_id", order.id)

                    view.context.startActivity(intent)
                }
            }

            else -> {
            }
        }

        return view
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun isEnabled(position: Int): Boolean {
        return orders.status == QueryStatus.SUCCESS
    }

    override fun getCount(): Int {
        return when (orders.status) {
            QueryStatus.LOADING -> 4
            QueryStatus.SUCCESS -> ordersFiltered.size
            else -> 0
        }
    }

    fun update(orders: Query<List<Order>>) {
        this.orders = orders

        // Update the filtered orders, when the query succeeded.
        if (orders.status == QueryStatus.SUCCESS) {
            ordersFiltered = OrderUtil.filterOrdersStatus(
                orders.requireData(),
                orderStatus
            )

            // If the active orders are displayed, order from most soon to least soon.
            if (orderStatus == OrdersStatus.ACTIVE) {
                ordersFiltered = ordersFiltered.sortedBy {
                    it.deadline
                }
            }
        }

        // Clear the timers, since new timers will be created
        this.destroyTimers()

        // Notify the changes to the list view (to re-render automatically)
        notifyDataSetChanged()
    }

    fun destroy() {
       this.destroyTimers()
    }

    fun destroyTimers() {
        // Clear the timers, since new timers will be created
        this.timers.forEach { it.cancel() }
        this.timers.clear()
    }
}