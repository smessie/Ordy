package com.ordy.app.ui.orders

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat.startActivity
import com.ordy.app.R
import com.ordy.app.api.models.Order
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.orders.overview.OverviewOrderActivity
import kotlinx.android.synthetic.main.list_order_card.view.*

class OrdersListAdapter(val context: Context?, var orders: Query<List<Order>>): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_order_card, parent, false)

        when(orders.status) {

            QueryStatus.LOADING -> {

                // Start the shimmer effect & show
                view.order_loading.startShimmer()
                view.order_loading.visibility = View.VISIBLE
                view.order_data.visibility = View.GONE
            }

            QueryStatus.SUCCESS -> {
                val order = orders.requireData()[position]

                // Stop the shimmer effect & hide.
                view.order_loading.stopShimmer()
                view.order_loading.visibility = View.GONE
                view.order_data.visibility = View.VISIBLE

                // Assign the data.
                view.order_location_name.text = order.location.name
                view.order_group_name.text = order.group.name
                //view.order_deadline_time.text = "Deadline: ${order.deadline}"
                view.order_deadline_time_left.text = "10 m"
                view.order_courier_name.text = order.courier.username

                // Set click handler.
                view.order.setOnClickListener {

                    val intent = Intent(view.context, OverviewOrderActivity::class.java)

                    // Pass the order as extra information:
                    intent.putExtra("order_id", order.id)

                    view.context.startActivity(intent)
                }
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

    override fun getCount(): Int {
        return when(orders.status) {
            QueryStatus.LOADING -> 4
            QueryStatus.SUCCESS -> orders.requireData().size
            else -> 0
        }
    }

}