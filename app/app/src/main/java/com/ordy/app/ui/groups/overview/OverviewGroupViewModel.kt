package com.ordy.app.ui.groups.overview

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.ApiService
import com.ordy.app.api.ApiServiceViewModel
import com.ordy.app.api.models.Group
import com.ordy.app.api.util.Query

class OverviewGroupViewModel(apiService: ApiService) : ApiServiceViewModel(apiService) {

    val group: MutableLiveData<Query<Group>> = MutableLiveData(Query())

}