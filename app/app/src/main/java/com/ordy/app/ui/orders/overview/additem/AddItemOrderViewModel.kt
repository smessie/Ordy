package com.ordy.app.ui.orders.overview.additem

import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.ApiService
import com.ordy.app.api.ApiServiceViewModel
import com.ordy.app.api.models.Item
import com.ordy.app.api.models.OrderItem
import com.ordy.app.api.models.actions.OrderAddItem
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.Query

class AddItemOrderViewModel(apiService: ApiService) : ApiServiceViewModel(apiService) {

    /**
     * List with all the cuisine items.
     */
    val cuisineItems: MutableLiveData<Query<List<Item>>> = MutableLiveData(Query())

    /**
     * Value of the search input field
     */
    val searchFieldValue: MutableLiveData<String> = MutableLiveData("")

    /**
     * Result of the "addItem" query.
     */
    val addItemResult: MutableLiveData<Query<OrderItem>> = MutableLiveData(Query())

    /**
     * Add a new item to a given order
     * @param orderId Id of the order to add the item to
     * @param cuisineItemId Id of the cuisin item (or null when a custom item name is given)
     * @param name Custom item name (ignored when cuisineItemId is present)
     */
    fun addItem(orderId: Int, cuisineItemId: Int?, name: String?) {

        FetchHandler.handle(
            addItemResult,
            apiService.userAddOrderItem(
                orderId,
                OrderAddItem(
                    cuisineItemId,
                    name
                )
            )
        )
    }
}