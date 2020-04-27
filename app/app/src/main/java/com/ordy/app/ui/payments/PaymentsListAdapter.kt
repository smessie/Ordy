package com.ordy.app.ui.payments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.models.Payment
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.util.SnackbarUtil
import kotlinx.android.synthetic.main.list_payment_card.view.*
import okhttp3.ResponseBody
import java.text.DateFormat

class PaymentsListAdapter(
    val context: Context,
    val viewModel: PaymentsViewModel,
    val fragment: PaymentsFragment,
    private val paymentsType: PaymentsType
) : BaseAdapter() {

    private val queryFun: () -> Query<List<Payment>> = if (paymentsType == PaymentsType.Debtors) {
        { viewModel.getDebtors() }
    } else {
        { viewModel.getDebts() }
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.list_payment_card,
            parent,
            false
        )

        when (queryFun().status) {

            QueryStatus.LOADING -> {
                // Start the shimmer effect and show the right layout
                view.payment_loading.startShimmer()
                view.payment_loading.visibility = View.VISIBLE
                view.payment_data.visibility = View.GONE
            }

            QueryStatus.SUCCESS -> {
                val payment = queryFun().requireData()[position]

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
                if (paymentsType == PaymentsType.Debtors) {
                    addPaidAction(payment, view)

                    view.payment_notify.setOnClickListener {
                        // TODO Notify
                    }
                } else {
                    // Hide buttons
                    view.payment_notify.visibility = View.GONE
                    view.payment_paid.visibility = View.GONE
                }

            }
            else -> {
            }
        }

        return view
    }

    override fun getItem(position: Int) = position
    override fun getItemId(position: Int) = position.toLong()
    override fun isEnabled(position: Int) = queryFun().status == QueryStatus.SUCCESS

    override fun getCount(): Int {
        return when (queryFun().status) {
            QueryStatus.LOADING -> 4
            QueryStatus.SUCCESS -> queryFun().requireData().size
            else -> 0
        }
    }

    private fun addPaidAction(payment: Payment, view: View) {
        // Result of the update item query
        val updatePaidResult = MutableLiveData<Query<ResponseBody>>(Query())

        // Set click handler on remove button
        view.payment_paid.setOnClickListener {
            // Prompt for confirmation
            AlertDialog.Builder(context).apply {
                setTitle("Mark as paid?")
                setMessage("This will remove ${payment.user.username}'s dept from the list.")

                // Mark as paid when confirmed
                setPositiveButton(android.R.string.ok) { _, _ ->
                    viewModel.markAsPaid(
                        updatePaidResult,
                        payment.order.id,
                        payment.user.id
                    )
                }

                // Close the window on cancel
                setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
            }.show()
        }

        updatePaidResult.observe(fragment, Observer {
            when (it.status) {
                QueryStatus.LOADING -> {
                    SnackbarUtil.openSnackbar(
                        "Marking as paid...",
                        fragment.requireView()
                    )
                }
                QueryStatus.SUCCESS -> {
                    SnackbarUtil.closeSnackbar(fragment.requireView())
                    viewModel.refreshDebtors()
                }
                QueryStatus.ERROR -> {
                    SnackbarUtil.closeSnackbar(fragment.requireView())
                    ErrorHandler.handle(it.error, fragment.requireView(), listOf())
                }
                else -> {
                }
            }
        })

    }
}