package com.ordy.app.ui.groups

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.ApiService
import com.ordy.app.api.ApiServiceViewModel
import com.ordy.app.api.models.Group
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.Query

class GroupsViewModel(apiService: ApiService) : ApiServiceViewModel(apiService) {
    val groups: MutableLiveData<Query<List<Group>>> =
        FetchHandler.handleLive(apiService.userGroups())

    fun getGroups(): Query<List<Group>> {
        return groups.value!!
    }
}