package com.ordy.app.ui.payments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.ordy.app.R
import com.ordy.app.api.models.Payment
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import kotlinx.android.synthetic.main.list_payment_card.view.*
import kotlinx.android.synthetic.main.list_payment_order_item.view.*
import java.text.DateFormat
import java.util.*

abstract class PaymentsBaseAdapter(
    val context: Context,
    val viewModel: PaymentsViewModel,
    val fragment: PaymentsFragment
) : BaseAdapter() {
    protected var paymentFiltered: List<Payment> = emptyList()

    abstract fun getQuery(): Query<List<Payment>>
    abstract fun getSearchValue(): String

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.list_payment_card,
            parent,
            false
        )

        when (getQuery().status) {

            QueryStatus.LOADING -> {
                // Start the shimmer effect and show the right layout
                view.payment_loading.startShimmer()
                view.payment_loading.visibility = View.VISIBLE
                view.payment_data.visibility = View.GONE
            }

            QueryStatus.SUCCESS -> {
                val payment = paymentFiltered[position]

                // Stop shimmering and hide
                view.payment_loading.stopShimmer()
                view.payment_loading.visibility = View.GONE
                // Display the data
                view.payment_data.visibility = View.VISIBLE

                // Set the data
                view.order_location_name.text = payment.order.location.name
                view.order_group_name.text = payment.order.group.name
                view.order_deadline_time.text = DateFormat
                    .getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                    .format(payment.order.deadline)
                view.payment_other_user_name.text = payment.user.username


                /**
                 * Do some paymentsType specific things
                 */
                specificPaymentCardSetup(payment, view)

                // Clear view
                view.payment_order_items.removeAllViews()

                // Add all the items
                for (orderItem in payment.orderItems) {
                    val orderItemView =
                        LayoutInflater.from(context).inflate(R.layout.list_payment_order_item, null)

                    // Assign the data.
                    orderItemView.order_item_name.text = orderItem.item.name
                    orderItemView.order_item_comment.text = orderItem.comment

                    // Hide the comment area when comment is empty.
                    if (orderItemView.order_item_comment.text == "") {
                        orderItemView.order_item_comment.visibility = View.GONE
                    }

                    view.payment_order_items.addView(orderItemView)
                }

            }
            else -> {
            }
        }

        return view
    }

    /**
     * Specific setup for the card.
     */
    abstract fun specificPaymentCardSetup(payment: Payment, view: View)

    override fun getItem(position: Int) = position
    override fun getItemId(position: Int) = position.toLong()
    override fun isEnabled(position: Int) = false

    override fun getCount(): Int {
        return when (getQuery().status) {
            QueryStatus.LOADING -> 4
            QueryStatus.SUCCESS -> paymentFiltered.size
            else -> 0
        }
    }

    open fun update(query: Query<List<Payment>>, searchValue: String) {
        if (query.status == QueryStatus.SUCCESS) {
            paymentFiltered = query.requireData().filter {
                it.user.username
                    .toLowerCase(Locale.getDefault())
                    .contains(
                        searchValue
                            .toLowerCase(Locale.getDefault())
                    )
            }
        }

        notifyDataSetChanged()
    }
}