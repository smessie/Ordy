package com.ordy.app.ui.orders.overview.additem

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.ApiService
import com.ordy.app.api.ApiServiceViewModel
import com.ordy.app.api.models.Item
import com.ordy.app.api.util.Query

class AddItemOrderViewModel(apiService: ApiService) : ApiServiceViewModel(apiService) {

    val cuisineItems: MutableLiveData<Query<List<Item>>> = MutableLiveData(Query())

    val searchFieldValue: MutableLiveData<String> = MutableLiveData("")
}