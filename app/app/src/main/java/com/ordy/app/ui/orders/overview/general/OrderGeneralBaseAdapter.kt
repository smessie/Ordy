package com.ordy.app.ui.orders.overview.general

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.models.Order
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.orders.overview.OverviewOrderViewModel
import com.ordy.app.util.OrderUtil
import com.ordy.app.util.types.OrderItemGroup
import kotlinx.android.synthetic.main.list_order_item.view.*

class OrderGeneralBaseAdapter(
    val context: Context?,
    var viewModel: OverviewOrderViewModel,
    lifecycleOwner: LifecycleOwner
) :
    BaseAdapter() {

    private var order: Query<Order> = Query()
    private var orderItemGroups: List<OrderItemGroup> = emptyList()

    init {
        viewModel.getOrderMLD().observe(lifecycleOwner, Observer {
            update(it)
        })
    }

    // suppress the warning for "${orderItemGroup.quantity}x"
    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.list_order_item,
            parent,
            false
        )

        when (order.status) {

            QueryStatus.LOADING -> {

                // Start the shimmer effect & show
                view.order_item_loading.startShimmer()
                view.order_item_loading.visibility = View.VISIBLE
                view.order_item_data.visibility = View.GONE
            }

            QueryStatus.SUCCESS -> {
                val orderItemGroup = orderItemGroups[position]

                // Stop the shimmer effect & hide.
                view.order_item_loading.stopShimmer()
                view.order_item_loading.visibility = View.GONE
                view.order_item_data.visibility = View.VISIBLE

                // Assign the data.
                view.order_item_quantity.text = "${orderItemGroup.quantity}x"
                view.order_item_name.text = orderItemGroup.name
                view.order_item_comment.text =
                    orderItemGroup.items
                        .filter { it.comment != "" }
                        .joinToString("\n") { "1x ${it.comment}" }

                // Hide the comment area when comment is empty.
                if (view.order_item_comment.text == "") {
                    view.order_item_comment.visibility = View.GONE
                }

                // Hide action buttons
                view.order_item_actions.visibility = View.GONE
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

    override fun getCount(): Int {
        return when (order.status) {
            QueryStatus.LOADING -> 4
            QueryStatus.SUCCESS -> orderItemGroups.size
            else -> 0
        }
    }

    override fun isEnabled(position: Int): Boolean {
        return false
    }

    fun update(order: Query<Order>) {
        this.order = order

        // Update the order item groups, when the query succeeded.
        if (order.status == QueryStatus.SUCCESS) {
            val orderItems = order.requireData().orderItems

            orderItemGroups = OrderUtil.groupItems(orderItems!!)
        }

        // Notify the changes to the list view (to re-render automatically)
        notifyDataSetChanged()
    }
}