package com.ordy.app.ui.profile

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.ApiService
import com.ordy.app.api.ApiServiceViewModel
import com.ordy.app.api.models.GroupInvite
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.Query

class ProfileViewModel(apiService: ApiService) : ApiServiceViewModel(apiService) {

    val invites: MutableLiveData<Query<List<GroupInvite>>> = MutableLiveData(Query())

    /**
     * Get the invites value
     */
    fun getInvites(): Query<List<GroupInvite>> {
        return invites.value!!
    }

    /**
     * Refresh the invites
     */
    fun refreshInvites() {
        FetchHandler.handle(invites, apiService.userInvites())
    }
}