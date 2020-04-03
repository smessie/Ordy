package com.ordy.app.ui.groups.invite

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.ApiService
import com.ordy.app.api.ApiServiceViewModel
import com.ordy.app.api.models.User
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.Query
import okhttp3.ResponseBody

class InviteMemberViewModel(apiService: ApiService) : ApiServiceViewModel(apiService) {

    /**
     * Value of the search input field.
     */
    val searchValueData: MutableLiveData<String> = MutableLiveData("")

    /**
     * List with users in the group.
     */
    val users: MutableLiveData<Query<List<User>>> = MutableLiveData(Query())

    fun getUsers(): Query<List<User>> {
        return users.value!!
    }

    /**
     * Get the value of the search input field.
     */
    fun getSearchValue(): String {
        return searchValueData.value!!
    }

    /**
     * Update the locations by the given search query
     */
    fun updateUsers(groupId: Int) {

        // Only update when the search value is not blank
        if(!getSearchValue().isBlank()) {
            FetchHandler.handle(
                users,
                apiService.searchMatchingInviteUsers(groupId, getSearchValue())
            )
        }
    }
}