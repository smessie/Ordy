package com.ordy.app.ui.groups.invite

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel
import com.ordy.app.api.models.User
import com.ordy.app.api.util.Query

class InviteMemberViewModel(repository: Repository) : RepositoryViewModel(repository) {

    /**
     * Value of the search input field.
     */
    private val searchValueData: MutableLiveData<String> = MutableLiveData("")

    fun getUsers(): Query<List<User>> {
        return repository.getInviteableUsers().value!!
    }

    /**
     * Get the value of the search input field.
     */
    fun getSearchValue(): String {
        return searchValueData.value!!
    }

    /**
     * Update the inviteable users by the given search query
     */
    fun updateUsers(groupId: Int) {
        // Only update when the search value is not blank
        if (!getSearchValue().isBlank()) {
            repository.searchMatchingInviteUsers(groupId, getSearchValue())
        }
    }

    fun getSearchValueData(): MutableLiveData<String> {
        return searchValueData
    }
}