package com.ordy.app.ui.orders

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.ApiService
import com.ordy.app.api.ApiServiceViewModel
import com.ordy.app.api.models.Order
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.Query

class OrdersViewModel(apiService: ApiService) : ApiServiceViewModel(apiService) {

    val orders: MutableLiveData<Query<List<Order>>> = FetchHandler.handleLive(apiService.userOrders())

    /**
     * Get a list of orders
     */
    fun getOrders(): Query<List<Order>> {
        return orders.value!!
    }
}