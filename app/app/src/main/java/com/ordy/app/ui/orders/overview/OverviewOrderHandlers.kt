package com.ordy.app.ui.orders.overview

import androidx.fragment.app.DialogFragment
import com.ordy.app.R
import com.ordy.app.ui.orders.overview.viewbill.ViewBillDialog

class OverviewOrderHandlers(
    val activity: OverviewOrderActivity,
    val viewModel: OverviewOrderViewModel
) {

    /**
     * Show the bill image for a specific order.
     */
    fun showBill() {
        // Open the pick location dialog.
        val fragmentManager = activity.supportFragmentManager

        val dialog = ViewBillDialog()
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_NoActionBar)
        dialog.show(fragmentManager, "dialog")
    }
}