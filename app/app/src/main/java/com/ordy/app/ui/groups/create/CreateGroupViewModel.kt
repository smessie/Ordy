package com.ordy.app.ui.groups.create

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.ApiService
import com.ordy.app.api.ApiServiceViewModel
import com.ordy.app.api.models.Group
import com.ordy.app.api.util.Query

class CreateGroupViewModel(apiService: ApiService) : ApiServiceViewModel(apiService) {
    val createResult: MutableLiveData<Query<Group>> = MutableLiveData(Query())
}