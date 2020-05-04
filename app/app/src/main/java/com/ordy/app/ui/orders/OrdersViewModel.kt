package com.ordy.app.ui.orders

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel
import com.ordy.app.api.models.Order
import com.ordy.app.api.util.Query
import java.util.*

class OrdersViewModel(repository: Repository) : RepositoryViewModel(repository) {

    /**
     * Get the MutableLiveData result of the Orders query.
     */
    fun getOrdersMLD(): MutableLiveData<Query<List<Order>>> {
        return repository.getOrdersResult()
    }

    /**
     * Refresh the list of orders
     */
    fun refreshOrders() {
        repository.refreshOrders()
    }
}