package com.ordy.app.ui.orders

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel
import com.ordy.app.api.models.Order
import com.ordy.app.api.util.Query

class OrdersViewModel(repository: Repository) : RepositoryViewModel(repository) {

    /**
     * Get the MutableLiveData result of the Orders query.
     */
    fun getOrdersMLD(): MutableLiveData<Query<List<Order>>> {
        return repository.getOrdersResult()
    }

    /**
     * Get a list of orders.
     */
    fun getOrders(): Query<List<Order>> {
        return getOrdersMLD().value!!
    }

    /**
     * Refresh the list of orders
     */
    fun refreshOrders() {
        repository.refreshOrders()
    }
}