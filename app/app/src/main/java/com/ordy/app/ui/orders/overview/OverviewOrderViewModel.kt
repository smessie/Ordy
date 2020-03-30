package com.ordy.app.ui.orders.overview

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.ApiService
import com.ordy.app.api.ApiServiceViewModel
import com.ordy.app.api.models.Order
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.Query
import okhttp3.ResponseBody

class OverviewOrderViewModel(apiService: ApiService) : ApiServiceViewModel(apiService) {

    /**
     * Specific order
     */
    val order: MutableLiveData<Query<Order>> = MutableLiveData(Query())

    /**
     * Get the order value
     */
    fun getOrder(): Query<Order> {
        return order.value!!
    }

    /**
     * Refresh the order
     */
    fun refreshOrders(orderId: Int) {
        FetchHandler.handle(order, apiService.order(orderId))
    }
}