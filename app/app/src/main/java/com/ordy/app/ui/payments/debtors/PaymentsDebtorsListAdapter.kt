package com.ordy.app.ui.payments.debtors

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.ordy.app.api.models.Payment
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.payments.PaymentsFragment
import com.ordy.app.ui.payments.PaymentsListAdapter
import com.ordy.app.ui.payments.PaymentsViewModel
import com.ordy.app.util.SnackbarUtil
import kotlinx.android.synthetic.main.list_payment_card.view.*
import okhttp3.ResponseBody

class PaymentsDebtorsListAdapter(
    context: Context,
    viewModel: PaymentsViewModel,
    fragment: PaymentsFragment
) : PaymentsListAdapter(context, viewModel, fragment) {
    override fun getQuery(): Query<List<Payment>> = viewModel.getDebtors()
    override fun getSearchValue(): String = viewModel.getDebtorsSearchValue()

    /**
     * Apply some specific setup to the card
     */
    override fun specificPaymentCardSetup(payment: Payment, view: View) {
        addPaidAction(payment, view)
        // TODO notify
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