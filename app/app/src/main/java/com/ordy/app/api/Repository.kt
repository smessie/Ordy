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
    private val group: MutableLiveData<Query<Group>> = MutableLiveData(Query())
    private val leaveGroupResult: MutableLiveData<Query<ResponseBody>> = MutableLiveData(Query())
    private val removeMemberResult: MutableLiveData<Query<ResponseBody>> = MutableLiveData(Query())
    private val groups: MutableLiveData<Query<List<Group>>> = MutableLiveData(Query())

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
     * Refresh the group with given id.
     * @param groupId: ID of the group we want to fetch
     */
    fun refreshGroup(groupId: Int) {
        FetchHandler.handle(group, apiService.group(groupId))
    }

    /**
     * Refresh the list of groups the user is in.
     */
    fun refreshGroups() {
        FetchHandler.handle(groups, apiService.userGroups())
    }

    /**
     * Let the user leave the given group.
     * @param groupId: ID of the group the user is about to leave
     */
    fun userLeaveGroup(groupId: Int) {
        FetchHandler.handle(
            leaveGroupResult,
            apiService.userLeaveGroup(groupId)
        )
    }

    /**
     * Remove a member from a group.
     * @param userId: ID of the user that should be kicked
     * @param groupId: ID of the group the user is removed from
     */
    fun removeMemberFromGroup(userId: Int, groupId: Int) {
        FetchHandler.handle(
            removeMemberResult, apiService.deleteMemberGroup(groupId, userId)
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

    /**
     * Get the MutableLiveData result of the Group fetch.
     */
    fun getGroup(): MutableLiveData<Query<Group>> {
        return group
    }

    /**
     * Get the MutableLiveData result of the Leave group query.
     */
    fun getLeaveGroupResult(): MutableLiveData<Query<ResponseBody>> {
        return leaveGroupResult
    }

    /**
     * Get the MutableLiveData result of the Remove member from group query.
     */
    fun getRemoveMemberResult(): MutableLiveData<Query<ResponseBody>> {
        return removeMemberResult
    }

    /**
     * Get the MutableLiveData result of the Groups fetch.
     */
    fun getGroups(): MutableLiveData<Query<List<Group>>> {
        return groups
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