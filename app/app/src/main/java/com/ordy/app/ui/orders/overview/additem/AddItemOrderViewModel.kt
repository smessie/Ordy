package com.ordy.app.ui.orders.overview.additem

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.ApiService
import com.ordy.app.api.ApiServiceViewModel
import com.ordy.app.api.models.Item
import com.ordy.app.api.models.OrderItem
import com.ordy.app.api.models.actions.OrderAddItem
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.Query
import okhttp3.ResponseBody

class AddItemOrderViewModel(apiService: ApiService) : ApiServiceViewModel(apiService) {

    /**
     * List with all the cuisine items.
     */
    val cuisineItems: MutableLiveData<Query<List<Item>>> = MutableLiveData(Query())

    /**
     * Value of the search input field.
     */
    val searchValueData: MutableLiveData<String> = MutableLiveData("")

    /**
     * Result of the "addItem" query.
     */
    val addItemResult: MutableLiveData<Query<OrderItem>> = MutableLiveData(Query())

    /**
     * Get the list with cuisine items.
     */
    fun getCuisineItems(): Query<List<Item>> {
        return cuisineItems.value!!
    }

    /**
     * Get the value of the search input field.
     */
    fun getSearchValue(): String {
        return searchValueData.value!!
    }

    /**
     * Get the result from the add item query.
     */
    fun getAddItemResult(): Query<OrderItem> {
        return addItemResult.value!!
    }
}