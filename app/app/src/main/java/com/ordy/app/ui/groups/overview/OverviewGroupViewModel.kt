package com.ordy.app.ui.groups.overview

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel
import com.ordy.app.api.models.Group
import com.ordy.app.api.util.Query
import okhttp3.ResponseBody
import java.lang.IllegalStateException

class OverviewGroupViewModel(repository: Repository) : RepositoryViewModel(repository) {

    private val renameGroupMLD: MutableLiveData<Query<Group>> = MutableLiveData(Query())
    private val removeMemberMLD: MutableLiveData<Query<ResponseBody>> = MutableLiveData(Query())
    private val leaveGroupMLD: MutableLiveData<Query<ResponseBody>> = MutableLiveData(Query())
    private val groupMLD: MutableLiveData<Query<Group>> = MutableLiveData(Query())

    /**
     * Get livedata for renaming a group.
     */
    fun getRenameGroupMLD(): MutableLiveData<Query<Group>> {
        return this.renameGroupMLD
    }

    /**
     * Get livedata for leaving the group.
     */
    fun getLeaveGroupMLD(): MutableLiveData<Query<ResponseBody>> {
        return this.leaveGroupMLD
    }

    /**
     * Get livedata for removing a user from the group.
     */
    fun getRemoveMemberMLD(): MutableLiveData<Query<ResponseBody>> {
        return this.removeMemberMLD
    }

    /**
     * Get query for removing a user from the group.
     * @throws IllegalStateException when MLD.value is null.
     */
    fun getRemoveMember(): Query<ResponseBody> {
        return this.removeMemberMLD.value
            ?: throw IllegalStateException("RemoveMember data called when null")
    }

    /**
     * Get livedata for the current group.
     */
    fun getGroupMLD(): MutableLiveData<Query<Group>> {
        return this.groupMLD
    }

    /**
     * Get query for the current group.
     * @throws IllegalStateException when MLD.value is null.
     */
    fun getGroup(): Query<Group> {
        return this.groupMLD.value ?: throw IllegalStateException("Group data called when null")
    }

    /**
     * Refresh the group with given id.
     * @param groupId: ID of the group we want to fetch
     */
    fun refreshGroup(groupId: Int) {
        repository.refreshGroup(groupMLD, groupId)
    }

    /**
     * Let the user leave the given group.
     * @param groupId: ID of the group the user is about to leave
     */
    fun userLeaveGroup(groupId: Int) {
        repository.userLeaveGroup(leaveGroupMLD, groupId)
    }

    /**
     * Change the name of a group
     * @param groupId: ID of the group of which the name will be changed
     * @param newName: The new name that will be given to the group
     */
    fun renameGroup(groupId: Int, newName: String) {
        repository.renameGroup(renameGroupMLD, groupId, newName)
    }

    /**
     * Remove a member from a group.
     * @param userId: ID of the user that should be kicked
     * @param groupId: ID of the group the user is removed from
     */
    fun removeMemberFromGroup(userId: Int, groupId: Int) {
        repository.removeMemberFromGroup(removeMemberMLD, userId, groupId)
    }
}