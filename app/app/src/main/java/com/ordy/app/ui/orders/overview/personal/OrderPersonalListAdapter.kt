package com.ordy.app.ui.orders.overview.personal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.ordy.app.R
import com.ordy.app.api.models.Order
import com.ordy.app.api.models.OrderItem
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import kotlinx.android.synthetic.main.list_order_item.view.*

class OrderPersonalListAdapter(val context: Context?, var order: Query<Order>, var showActions: Boolean): BaseAdapter() {

    var orderItems: List<OrderItem> = emptyList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_order_item, parent, false)

        when(order.status) {

            QueryStatus.LOADING -> {

                // Start the shimmer effect & show
                view.order_item_loading.startShimmer()
                view.order_item_loading.visibility = View.VISIBLE
                view.order_item_data.visibility = View.GONE
            }

            QueryStatus.SUCCESS -> {
                val orderItem = orderItems[position]

                // Stop the shimmer effect & hide.
                view.order_item_loading.stopShimmer()
                view.order_item_loading.visibility = View.GONE
                view.order_item_data.visibility = View.VISIBLE

                // Assign the data.
                view.order_item_quantity.text = "1x"
                view.order_item_name.text = orderItem.item.name
                view.order_item_comment.text = orderItem.comment

                // Hide the comment area when comment is empty.
                if(view.order_item_comment.text == "") {
                    view.order_item_comment.visibility = View.GONE
                }

                if(showActions) {
                    view.order_item_actions.visibility = View.VISIBLE
                } else {
                    view.order_item_actions.visibility = View.INVISIBLE
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
        return when(order.status) {
            QueryStatus.LOADING -> 4
            QueryStatus.SUCCESS -> orderItems.size
            else -> 0
        }
    }

    override fun isEnabled(position: Int): Boolean {
        return false
    }
}