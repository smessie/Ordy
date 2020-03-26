package com.ordy.app.ui.orders

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.ApiService
import com.ordy.app.api.ApiServiceViewModel
import com.ordy.app.api.models.Order
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.Query

class OrdersViewModel(apiService: ApiService) : ApiServiceViewModel(apiService) {

    val activeOrders: MutableLiveData<Query<List<Order>>> = FetchHandler.handleLive(apiService.userOrders())
}