package com.ordy.app.ui.groups.overview

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.ApiService
import com.ordy.app.api.ApiServiceViewModel
import com.ordy.app.api.models.Group
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.Query
import okhttp3.ResponseBody

class OverviewGroupViewModel(apiService: ApiService) : ApiServiceViewModel(apiService) {

    val group: MutableLiveData<Query<Group>> = MutableLiveData(Query())
    val leaveResult: MutableLiveData<Query<ResponseBody>> = MutableLiveData(Query())
    val removeResult: MutableLiveData<Query<ResponseBody>> = MutableLiveData(Query())

    /**
     * Get the group value
     */
    fun getGroup(): Query<Group> {
        return group.value!!
    }

    /**
     * Refresh the group
     */
    fun refreshGroup(groupId: Int) {
        FetchHandler.handle(group, apiService.group(groupId))
    }
}