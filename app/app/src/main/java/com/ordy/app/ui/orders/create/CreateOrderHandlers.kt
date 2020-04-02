package com.ordy.app.ui.orders.create

import androidx.fragment.app.DialogFragment
import com.ordy.app.R
import com.ordy.app.api.models.actions.OrderCreate
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.ui.orders.create.location.CreateOrderLocationDialog
import com.ordy.app.util.PickerUtil

class CreateOrderHandlers(val activity: CreateOrderActivity, val viewModel: CreateOrderViewModel) {

    /**
     * Open the dialog to select a location for the order.
     */
    fun openLocations() {
        // Open the pick location dialog.
        val fragmentManager = activity.supportFragmentManager

        val dialog = CreateOrderLocationDialog()
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_NoActionBar)
        dialog.show(fragmentManager, "dialog")
    }

    /**
     * Open the dialog to select the deadline for the order.
     */
    fun openDeadline() {
        PickerUtil.openDateTimePicker(viewModel.deadlineValueData, activity)
    }

    /**
     * Create a new order.
     */
    fun createOrder() {

        var locationId: Int? = null
        val customLocationName: String? = viewModel.getLocationValue().customLocationName ?: null
        var groupId: Int? = null

        // Check if the location exists.
        if(viewModel.getLocationValue().location != null) {
            locationId = viewModel.getLocationValue().location!!.id
        }

        // Check if the group exists.
        if(viewModel.groupValueData.value != null) {
            groupId = viewModel.getGroupValue().id
        }

        FetchHandler.handle(
            viewModel.createOrderResult,
            viewModel.apiService.createOrder(
                OrderCreate(
                    locationId = locationId,
                    customLocationName = customLocationName,
                    deadline = viewModel.getDeadlineValue(),
                    groupId = groupId
                )
            )
        )
    }
}