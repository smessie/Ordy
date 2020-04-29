package com.ordy.app.ui.payments.debts

import android.content.Context
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.ordy.app.api.models.Payment
import com.ordy.app.api.util.Query
import com.ordy.app.ui.payments.PaymentsBaseAdapter
import com.ordy.app.ui.payments.PaymentsFragment
import com.ordy.app.ui.payments.PaymentsViewModel
import kotlinx.android.synthetic.main.list_payment_card.view.*

class PaymentsDebtsBaseAdapter(
    context: Context,
    viewModel: PaymentsViewModel,
    fragment: PaymentsFragment,
    lifecycleOwner: LifecycleOwner
) : PaymentsBaseAdapter(context, viewModel, fragment) {

    override fun getQuery(): Query<List<Payment>> = debts
    override fun getSearchValue(): String = debtsSearchValue

    private var debts: Query<List<Payment>> = Query()
    private var debtsSearchValue: String = ""

    init {
        // Update the list adapter when the "orders" query updates
        viewModel.getDebtsMLD().observe(lifecycleOwner, Observer {

            // Notify the changes to the list view (to re-render automatically)
            update(it, debtsSearchValue)
        })

        // Observe the input field
        viewModel.getDebtsSearchMLD().observe(lifecycleOwner, Observer {
            update(debts, it)
        })
    }

    override fun specificPaymentCardSetup(payment: Payment, view: View) {
        // Hide buttons
        view.payment_notify.visibility = View.GONE
        view.payment_paid.visibility = View.GONE
    }

    override fun update(query: Query<List<Payment>>, searchValue: String) {
        super.update(query, searchValue)

        debts = query
        debtsSearchValue = searchValue
    }
}