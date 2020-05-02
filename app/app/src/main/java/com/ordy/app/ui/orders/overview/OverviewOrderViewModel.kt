package com.ordy.app.ui.orders.overview

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel
import com.ordy.app.api.models.Order
import com.ordy.app.api.util.Query
import okhttp3.ResponseBody

class OverviewOrderViewModel(repository: Repository) : RepositoryViewModel(repository) {

    val orderId = MutableLiveData(-1)

    /**
     * Get the MutableLiveData result of the Order fetch.
     */
    fun getOrderMLD(): MutableLiveData<Query<Order>> {
        return repository.getOrder()
    }

    /**
     * Refresh the order
     */
    fun refreshOrder() {
        repository.refreshOrder(orderId.value!!)
    }

    /**
     * Remove an item from a given order.
     * @param liveData: Object to bind result to
     * @param orderId: Id of the order
     * @param orderItemId: Id of the order item
     */
    fun removeItem(liveData: MutableLiveData<Query<ResponseBody>>, orderId: Int, orderItemId: Int) {
        repository.removeItem(liveData, orderId, orderItemId)
    }

    /**
     * Update the comment of a given order.
     * @param liveData: Object to bind result to
     * @param orderId: Id of the order
     * @param orderItemId: Id of the order item
     * @param comment: Comment to set for the item
     */
    fun updateItem(
        liveData: MutableLiveData<Query<ResponseBody>>,
        orderId: Int,
        orderItemId: Int,
        comment: String
    ) {
        repository.updateItem(liveData, orderId, orderItemId, comment)
    }
}