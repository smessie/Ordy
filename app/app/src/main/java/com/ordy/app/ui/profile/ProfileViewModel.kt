package com.ordy.app.ui.profile

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.ApiService
import com.ordy.app.api.ApiServiceViewModel
import com.ordy.app.api.models.Group
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.Query

class ProfileViewModel(apiService: ApiService) : ApiServiceViewModel(apiService) {

    val invites: MutableLiveData<Query<List<Group>>> = MutableLiveData(Query())

    /**
     * Get the invites value
     */
    fun getInvites(): Query<List<Group>> {
        return invites.value!!
    }

    /**
     * Refresh the invites
     */
    fun refreshInvites() {
        FetchHandler.handle(invites, apiService.userInvites())
    }
}