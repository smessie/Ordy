package com.ordy.app.ui.orders.overview.additem

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel
import com.ordy.app.api.models.Item
import com.ordy.app.api.models.OrderItem
import com.ordy.app.api.util.Query

class AddItemOrderViewModel(repository: Repository) : RepositoryViewModel(repository) {

    /**
     * Value of the search input field.
     */
    val searchValueMLD: MutableLiveData<String> = MutableLiveData("")
    val cuisineItemsMLD: MutableLiveData<Query<List<Item>>> = MutableLiveData(Query())
    val addItemMLD: MutableLiveData<Query<OrderItem>> = MutableLiveData(Query())

    /**
     * Get the list with cuisine items.
     */
    fun getCuisineItems(): Query<List<Item>> {
        return cuisineItemsMLD.value!!
    }

    /**
     * Get the result from the add item query.
     */
    fun getAddItemResult(): Query<OrderItem> {
        return addItemMLD.value!!
    }

    /**
     * Refresh the cuisine items.
     */
    fun refreshCuisineItems(locationId: Int) {
        repository.refreshCuisineItems(cuisineItemsMLD, locationId)
    }

    /**
     * Add a new item to a given order.
     * @param orderId: Id of the order to add the item to
     * @param cuisineItemId: Id of the cuisine item (or null when a custom item name is given)
     * @param name: Custom item name (ignored when cuisineItemId is present)
     */
    fun addItem(orderId: Int, cuisineItemId: Int?, name: String?) {
        repository.addItem(addItemMLD, orderId, cuisineItemId, name)
    }
}