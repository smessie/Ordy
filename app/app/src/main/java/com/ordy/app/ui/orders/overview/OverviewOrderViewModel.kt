package com.ordy.app.ui.orders.overview

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.ApiService
import com.ordy.app.api.ApiServiceViewModel
import com.ordy.app.api.models.Order
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.Query

class OverviewOrderViewModel(apiService: ApiService) : ApiServiceViewModel(apiService) {

    val order: MutableLiveData<Query<Order>> = MutableLiveData(Query())
}