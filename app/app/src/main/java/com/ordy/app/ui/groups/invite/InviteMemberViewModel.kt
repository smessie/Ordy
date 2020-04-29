package com.ordy.app.ui.groups.invite

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel
import com.ordy.app.api.util.Query
import com.ordy.app.api.wrappers.GroupInviteUserWrapper
import okhttp3.ResponseBody

class InviteMemberViewModel(repository: Repository) : RepositoryViewModel(repository) {

    /**
     * private field to hold the ID's of users who are already invited
     */

    private var invitedUsers: MutableList<Int> = mutableListOf()


    /**
     * add the id of an user to the "already invited users"-list
     */

    fun markUserAsInvited(userId: Int) {
        invitedUsers.add(userId)
    }

    /**
     * Remove the id of an user to the "already invited users"-list
     */

    fun cancelUserInvite(userId: Int) {
        invitedUsers.remove(userId)
    }

    /**
     * check if a user is already invited
     */

    fun isUserInvited(userId: Int): Boolean {
        return invitedUsers.contains(userId)
    }

    /**
     * Value of the search input field.
     */
    private val searchValueData: MutableLiveData<String> = MutableLiveData("")

    /**
     * Get the value of the search input field.
     */
    private fun getSearchValue(): String {
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

    /**
     * Get the MutableLiveData result of all users matched that are able to invite.
     */
    fun getInviteableUsersMLD(): MutableLiveData<Query<List<GroupInviteUserWrapper>>> {
        return repository.getInviteableUsers()
    }

    /**
     * Send an invite for a group to an user.
     * @param userId: ID of the user we want to invite
     * @param groupId: ID of the group we want to send an invite for
     * @param liveData: Object where we want to store the result of our query in
     */
    fun sendInviteToUserFromGroup(
        userId: Int,
        groupId: Int,
        liveData: MutableLiveData<Query<ResponseBody>>
    ) {
        repository.sendInviteToUserFromGroup(userId, groupId, liveData)
    }

    /**
     * Send an invite for a group to an user.
     * @param userId: ID of the user we want to invite
     * @param groupId: ID of the group we want to send an invite for
     * @param liveData: Object where we want to store the result of our query in
     */
    fun deleteInviteOfUserFromGroup(
        userId: Int,
        groupId: Int,
        liveData: MutableLiveData<Query<ResponseBody>>
    ) {
        repository.deleteInviteOfUserFromGroup(userId, groupId, liveData)
    }
}