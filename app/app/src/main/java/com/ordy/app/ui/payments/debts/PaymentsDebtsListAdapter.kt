package com.ordy.app.ui.payments.debts

import android.content.Context
import android.view.View
import com.ordy.app.api.models.Payment
import com.ordy.app.api.util.Query
import com.ordy.app.ui.payments.PaymentsFragment
import com.ordy.app.ui.payments.PaymentsListAdapter
import com.ordy.app.ui.payments.PaymentsViewModel
import kotlinx.android.synthetic.main.list_payment_card.view.*

class PaymentsDebtsListAdapter(
    context: Context,
    viewModel: PaymentsViewModel,
    fragment: PaymentsFragment
) : PaymentsListAdapter(context, viewModel, fragment) {
    override fun getQuery(): Query<List<Payment>> = viewModel.getDebts()
    override fun getSearchValue(): String = viewModel.getDebtsSearchValue()

    override fun specificPaymentCardSetup(payment: Payment, view: View) {
        // Hide buttons
        view.payment_notify.visibility = View.GONE
        view.payment_paid.visibility = View.GONE
    }
}