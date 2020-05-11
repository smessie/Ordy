package com.ordy.app.ui.orders.overview.personal

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.ordy.app.AppPreferences
import com.ordy.app.R
import com.ordy.app.api.models.Order
import com.ordy.app.api.models.OrderItem
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.orders.overview.OverviewOrderViewModel
import com.ordy.app.ui.orders.overview.addcomment.AddCommentDialog
import com.ordy.app.ui.orders.overview.addcomment.AddCommentDialogArgumentsWrapper
import com.ordy.app.util.OrderUtil
import com.ordy.app.util.SnackbarUtil
import com.ordy.app.util.TimerUtil
import kotlinx.android.synthetic.main.list_order_item.view.*
import okhttp3.ResponseBody
import java.util.*

class OrderPersonalBaseAdapter(
    val context: Context,
    private val parentView: View,
    val handlers: OrderPersonalHandlers,
    val fragment: OrderPersonalFragment,
    val viewModel: OverviewOrderViewModel,
    lifecycleOwner: LifecycleOwner
) : BaseAdapter() {

    private var order: Query<Order> = Query()
    private var orderItems: List<OrderItem> = emptyList()
    private var showActions: Boolean = true
    private var updateTimer: Timer = Timer()

    init {
        viewModel.getOrderMLD().observe(lifecycleOwner, Observer {
            update(it)
        })
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_order_item, parent, false)

        when (order.status) {

            QueryStatus.LOADING -> {

                // Start the shimmer effect & show
                view.order_item_loading.startShimmer()
                view.order_item_loading.visibility = View.VISIBLE
                view.order_item_data.visibility = View.GONE
            }

            QueryStatus.SUCCESS -> {
                val orderItem = orderItems[position]
                val order = order.requireData()

                // Stop the shimmer effect & hide.
                view.order_item_loading.stopShimmer()
                view.order_item_loading.visibility = View.GONE
                view.order_item_data.visibility = View.VISIBLE

                // Assign the data.
                view.order_item_quantity.text =
                    context.getString(R.string.placeholder_item_quantity)
                view.order_item_name.text = orderItem.item.name
                view.order_item_comment.text = orderItem.comment

                // Hide the comment area when comment is empty.
                if (orderItem.comment == "") {
                    view.order_item_comment.visibility = View.GONE
                } else {
                    view.order_item_comment.visibility = View.VISIBLE
                }

                // Add the update action
                addUpdateAction(order, orderItem, view)

                // Add the delete action
                addDeleteAction(order, orderItem, view)

                if (showActions) {
                    view.order_item_actions.visibility = View.VISIBLE
                } else {
                    view.order_item_actions.visibility = View.INVISIBLE
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
        return when (order.status) {
            QueryStatus.LOADING -> 4
            QueryStatus.SUCCESS -> orderItems.size
            else -> 0
        }
    }

    override fun isEnabled(position: Int): Boolean {
        return false
    }

    private fun addUpdateAction(order: Order, orderItem: OrderItem, view: View) {
        // Result of the update item query
        val updateResult = MutableLiveData<Query<ResponseBody>>(Query())

        // Implement the add comment action
        view.order_item_action_comment.setOnClickListener {
            val manager = fragment.parentFragmentManager

            val dialog = AddCommentDialog()

            val args = Bundle()
            args.putSerializable("args", AddCommentDialogArgumentsWrapper(order, orderItem, updateResult, viewModel))
            dialog.arguments = args

            dialog.show(manager, "AddCommentDialog")
        }

        // Implement listener of changes of update item query result
        updateResult.observe(fragment, Observer {

            when (it.status) {

                QueryStatus.LOADING -> {
                    SnackbarUtil.openSnackbar(
                        "Attempting to update item...",
                        fragment.activity
                    )
                }

                QueryStatus.SUCCESS -> {
                    SnackbarUtil.closeSnackbar(fragment.requireActivity())

                    // Update the query.
                    viewModel.refreshOrder(context, fragment.activity)
                }

                QueryStatus.ERROR -> {
                    SnackbarUtil.closeSnackbar(fragment.requireActivity())

                    ErrorHandler().handle(it.error, fragment.activity, listOf())
                }

                else -> {
                }
            }
        })
    }

    private fun addDeleteAction(order: Order, orderItem: OrderItem, view: View) {
        // Result of the delete item query
        val deleteResult = MutableLiveData<Query<ResponseBody>>(Query())

        // Implement the delete button.
        view.order_item_action_delete.setOnClickListener {

            // Prevent multiple delete requests from sending.
            if (deleteResult.value != null && deleteResult.value?.status != QueryStatus.LOADING) {
                viewModel.removeItem(
                    deleteResult,
                    order.id,
                    orderItem.id
                )
            }
        }

        // Implement listener of changes of delete query result
        deleteResult.observe(fragment, Observer {

            when (it.status) {

                QueryStatus.LOADING -> {
                    SnackbarUtil.openSnackbar(
                        "Attempting to delete item...",
                        fragment.activity
                    )
                }

                QueryStatus.SUCCESS -> {
                    SnackbarUtil.closeSnackbar(fragment.requireActivity())

                    // Delete the order item from the list view
                    this.order.requireData().orderItems?.remove(orderItem)

                    // Update the query.
                    viewModel.getOrderMLD().postValue(this.order)
                }

                QueryStatus.ERROR -> {
                    SnackbarUtil.closeSnackbar(fragment.requireActivity())

                    ErrorHandler().handle(it.error, fragment.activity, listOf())
                }

                else -> {
                }
            }
        })
    }

    fun update(order: Query<Order>) {
        this.order = order

        // Update the order items, when the query succeeded.
        if (order.status == QueryStatus.SUCCESS) {

            // Update the "add item"-button
            fragment.updateAddItemButton(this.parentView)

            // Only show the items with the same user id as the logged in user.
            if (order.requireData().orderItems != null) {
                orderItems = order.requireData().orderItems!!.filter {
                    it.user.id == AppPreferences(context).userId
                }
            }

            // Stop the previous timer.
            updateTimer.cancel()

            // Remove the action buttons when the order is closed.
            updateTimer =
                TimerUtil.updateUI(fragment.requireActivity() as AppCompatActivity, 0, 1000) {

                    // Cancel the timer when the query updates.
                    if (order.status != QueryStatus.SUCCESS) {
                        updateTimer.cancel()
                    } else {
                        val closed =
                            OrderUtil.timeLeft(order.requireData().deadline) <= 0

                        // Update the "add item"-button
                        fragment.updateAddItemButton(this.parentView)

                        // Update the action buttons when the state changes.
                        if (closed == showActions) {

                            // Update if the "remove" and "update" button should be showed.
                            showActions = !closed

                            // Notify the changes to the list view (to re-render automatically)
                            notifyDataSetChanged()
                        }
                    }
                }
        }

        // Notify the changes to the list view (to re-render automatically)
        notifyDataSetChanged()
    }

    fun destroy() {
        // Stop the update timer.
        this.updateTimer.cancel()
    }
}