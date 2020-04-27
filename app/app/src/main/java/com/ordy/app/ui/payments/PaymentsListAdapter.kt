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
import java.text.DateFormat
import java.util.*

abstract class PaymentsListAdapter(
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
    override fun isEnabled(position: Int) = getQuery().status == QueryStatus.SUCCESS

    override fun getCount(): Int {
        return when (getQuery().status) {
            QueryStatus.LOADING -> 4
            QueryStatus.SUCCESS -> paymentFiltered.size
            else -> 0
        }
    }

    fun update() {
        if (getQuery().status == QueryStatus.SUCCESS) {
            paymentFiltered = getQuery().requireData().filter {
                it.user.username
                    .toLowerCase(Locale.getDefault())
                    .contains(
                        getSearchValue()
                            .toLowerCase(Locale.getDefault())
                    )
            }
        }
        notifyDataSetChanged()

    }
}