package com.ordy.app.ui.payments.debtors

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.ordy.app.R
import com.ordy.app.api.models.Payment
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.payments.PaymentsBaseAdapter
import com.ordy.app.ui.payments.PaymentsFragment
import com.ordy.app.ui.payments.PaymentsViewModel
import com.ordy.app.util.SnackbarUtil
import com.ordy.app.util.types.SnackbarType
import kotlinx.android.synthetic.main.list_payment_card.view.*
import okhttp3.ResponseBody

class PaymentsDebtorsBaseAdapter(
    context: Context,
    viewModel: PaymentsViewModel,
    fragment: PaymentsFragment,
    lifecycleOwner: LifecycleOwner
) : PaymentsBaseAdapter(context, viewModel, fragment) {

    override fun getQuery(): Query<List<Payment>> = debtors
    override fun getSearchValue(): String = debtorsSearchValue

    private var debtors: Query<List<Payment>> = Query()
    private var debtorsSearchValue = ""

    init {
        // Observe the debtors
        viewModel.getDebtorsMLD().observe(lifecycleOwner, Observer {
            update(it, debtorsSearchValue)
        })

        // Observe the input field
        viewModel.getDebtorsSearchMLD().observe(lifecycleOwner, Observer {
            update(debtors, it)
        })
    }

    /**
     * Apply some specific setup to the card
     */
    override fun specificPaymentCardSetup(payment: Payment, view: View) {
        addPaidAction(payment, view)
        addNotifyAction(payment, view)
    }

    private fun addPaidAction(payment: Payment, view: View) {
        // Result of the update item query
        val updatePaidResult = MutableLiveData<Query<ResponseBody>>(Query())

        // Set click handler on remove button
        view.payment_paid.setOnClickListener {
            // Prompt for confirmation
            AlertDialog.Builder(context).apply {
                setTitle(fragment.getString(R.string.mark_confirm_text))
                setMessage(
                    fragment.getString(
                        R.string.mark_confirm_text_decription,
                        payment.user.username
                    )
                )

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
                        fragment.getString(R.string.mark_snackbar_text),
                        fragment.requireView()
                    )
                }
                QueryStatus.SUCCESS -> {
                    SnackbarUtil.closeSnackbar(fragment.requireView())
                    viewModel.refreshDebtors()
                }
                QueryStatus.ERROR -> {
                    SnackbarUtil.closeSnackbar(fragment.requireView())
                    ErrorHandler().handle(it.error, fragment.requireView(), listOf())
                }
                else -> {
                }
            }
        })

    }

    private fun addNotifyAction(payment: Payment, view: View) {
        val notifyResult = MutableLiveData<Query<ResponseBody>>(Query())

        view.payment_notify.setOnClickListener {
            viewModel.notifyDebtor(
                notifyResult,
                payment.order.id,
                payment.user.id
            )
        }

        notifyResult.observe(fragment, Observer {
            when (it.status) {
                QueryStatus.LOADING -> {
                    SnackbarUtil.openSnackbar(
                        fragment.getString(R.string.notify_snackbar_loading, payment.user.username),
                        fragment.requireView()
                    )
                    view.payment_notify.isEnabled = false
                }
                QueryStatus.SUCCESS -> {
                    SnackbarUtil.closeSnackbar(fragment.requireView())
                    view.payment_notify.isEnabled = true

                    SnackbarUtil.openSnackbar(
                        fragment.getString(R.string.notify_snackbar_success),
                        fragment.requireView(),
                        Snackbar.LENGTH_SHORT,
                        SnackbarType.SUCCESS
                    )
                }
                QueryStatus.ERROR -> {
                    SnackbarUtil.closeSnackbar(fragment.requireView())
                    view.payment_notify.isEnabled = true
                    ErrorHandler().handle(it.error, fragment.requireView(), listOf())
                }
                else -> {
                }
            }
        })
    }

    override fun update(query: Query<List<Payment>>, searchValue: String) {
        super.update(query, searchValue)

        debtors = query
        debtorsSearchValue = searchValue
    }
}