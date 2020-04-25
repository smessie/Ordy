package com.ordy.app.ui.orders.overview.users

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.ordy.app.R
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.orders.overview.OverviewOrderViewModel
import com.ordy.app.util.OrderItemUserGroup
import com.ordy.app.util.OrderUtil
import kotlinx.android.synthetic.main.list_order_item.view.*
import kotlinx.android.synthetic.main.list_order_item_user.view.*

class OrderUsersListAdapter(val context: Context?, val viewModel: OverviewOrderViewModel) :
    BaseAdapter() {

    private var orderItemUserGroups: List<OrderItemUserGroup> = emptyList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.list_order_item_user,
            parent,
            false
        )

        when (viewModel.getOrder().status) {

            QueryStatus.LOADING -> {

                // Start the shimmer effect & show
                view.order_item_user_loading.startShimmer()
                view.order_item_user_loading.visibility = View.VISIBLE
                view.order_item_user_data.visibility = View.GONE
            }

            QueryStatus.SUCCESS -> {
                val orderItemUserGroup = orderItemUserGroups[position]

                // Stop the shimmer effect & hide.
                view.order_item_user_loading.stopShimmer()
                view.order_item_user_loading.visibility = View.GONE
                view.order_item_user_data.visibility = View.VISIBLE

                // Assign the data.
                view.order_item_user_name.text = orderItemUserGroup.username

                // Clear the items view to prevent duplication
                view.order_item_user_items.removeAllViews()

                // Add all the items
                for (orderItem in orderItemUserGroup.items) {

                    val orderItemView = View.inflate(context, R.layout.list_order_item, null)

                    // Stop the shimmer effect & hide.
                    orderItemView.order_item_loading.stopShimmer()
                    orderItemView.order_item_loading.visibility = View.GONE
                    orderItemView.order_item_data.visibility = View.VISIBLE

                    // Assign the data.
                    orderItemView.order_item_quantity.text = context?.getString(R.string.placeholder_item_quantity)
                    orderItemView.order_item_name.text = orderItem.item.name
                    orderItemView.order_item_comment.text = orderItem.comment

                    // Hide the comment area when comment is empty.
                    if (orderItemView.order_item_comment.text == "") {
                        orderItemView.order_item_comment.visibility = View.GONE
                    }

                    // Hide action buttons
                    orderItemView.order_item_actions.visibility = View.GONE

                    view.order_item_user_items.addView(orderItemView)
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

    override fun getCount(): Int {
        return when (viewModel.getOrder().status) {
            QueryStatus.LOADING -> 4
            QueryStatus.SUCCESS -> orderItemUserGroups.size
            else -> 0
        }
    }

    override fun isEnabled(position: Int): Boolean {
        return false
    }

    fun update() {

        // Update the order item groups, when the query succeeded.
        if (viewModel.getOrder().status == QueryStatus.SUCCESS) {

            val orderItems = viewModel.getOrder().requireData().orderItems

            orderItemUserGroups = OrderUtil.userGroupItems(orderItems!!)
        }

        // Notify the changes to the list view (to re-render automatically)
        notifyDataSetChanged()
    }
}