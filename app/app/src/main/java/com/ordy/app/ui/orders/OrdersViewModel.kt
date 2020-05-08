package com.ordy.app.ui.orders

import android.content.Context
import android.view.View
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
     * Refresh the list of orders
     */
    fun refreshOrders(context: Context, view: View) {
        repository.refreshOrders(context, view)
    }
}