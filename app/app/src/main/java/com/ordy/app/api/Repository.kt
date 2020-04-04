package com.ordy.app.api

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.models.Group
import com.ordy.app.api.models.User
import com.ordy.app.api.models.actions.GroupCreate
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.Query
import okhttp3.ResponseBody

class Repository(val apiService: ApiService) {

    /******************************
     ***        GROUPS          ***
     ******************************/
    private val createGroupResult: MutableLiveData<Query<Group>> = MutableLiveData(Query())
    private val inviteableUsers: MutableLiveData<Query<List<User>>> = MutableLiveData(Query())

    /**
     * Create a new group.
     * @param groupName: The name that the newly created group should have
     */
    fun createGroup(groupName: String) {
        FetchHandler.handle(createGroupResult, apiService.createGroup(GroupCreate(groupName)))
    }

    /**
     * Search for all matching users that are able to get an invite for the group.
     * @param groupId: ID of the group
     * @param username: The name we want to match on in our search query
     */
    fun searchMatchingInviteUsers(groupId: Int, username: String) {
        FetchHandler.handle(
            inviteableUsers,
            apiService.searchMatchingInviteUsers(groupId, username)
        )
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
        FetchHandler.handle(
            liveData, apiService.createInviteGroup(groupId, userId)
        )
    }

    /**
     * Get the MutableLiveData result of the Create group query.
     */
    fun getCreateGroupResult(): MutableLiveData<Query<Group>> {
        return createGroupResult
    }

    /**
     * Get the MutableLiveData result of all users matched that are able to invite.
     */
    fun getInviteableUsers(): MutableLiveData<Query<List<User>>> {
        return inviteableUsers
    }

    /******************************
     ***       LOCATIONS        ***
     ******************************/


    /******************************
     ***         LOGIN          ***
     ******************************/


    /******************************
     ***        ORDERS          ***
     ******************************/


    /******************************
     ***       PAYMENTS         ***
     ******************************/


    /******************************
     ***        PROFILE         ***
     ******************************/


    /******************************
     ***        SETTINGS        ***
     ******************************/
}