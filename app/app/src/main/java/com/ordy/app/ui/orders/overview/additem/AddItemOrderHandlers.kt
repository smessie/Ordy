package com.ordy.app.ui.orders.overview.additem

import com.ordy.app.api.models.actions.OrderAddItem
import com.ordy.app.api.util.FetchHandler

class AddItemOrderHandlers(val activity: AddItemOrderActivity, val viewModel: AddItemOrderViewModel) {

    /**
     * Add a new item to a given order
     * @param orderId Id of the order to add the item to
     * @param cuisineItemId Id of the cuisin item (or null when a custom item name is given)
     * @param name Custom item name (ignored when cuisineItemId is present)
     */
    fun addItem(orderId: Int, cuisineItemId: Int?, name: String?) {

        // Add the item to the order
        FetchHandler.handle(
            viewModel.addItemResult,
            viewModel.apiService.userAddOrderItem(
                orderId,
                OrderAddItem(
                    cuisineItemId,
                    name
                )
            )
        )
    }
}