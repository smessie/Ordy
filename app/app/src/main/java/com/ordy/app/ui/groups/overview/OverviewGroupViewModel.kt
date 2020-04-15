package com.ordy.app.ui.groups.overview

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel
import com.ordy.app.api.models.Group
import com.ordy.app.api.util.Query
import okhttp3.ResponseBody

class OverviewGroupViewModel(repository: Repository) : RepositoryViewModel(repository) {

    /**
     * Refresh the group with given id.
     * @param groupId: ID of the group we want to fetch
     */
    fun refreshGroup(groupId: Int) {
        repository.refreshGroup(groupId)
    }

    /**
     * Get the MutableLiveData result of the Group fetch.
     */
    fun getGroupMLD(): MutableLiveData<Query<Group>> {
        return repository.getGroup()
    }

    /**
     * Get the MutableLiveData result of the Remove member from group query.
     */
    fun getRemoveMemberMLD(): MutableLiveData<Query<ResponseBody>> {
        return repository.getRemoveMemberResult()
    }

    /**
     * Get the MutableLiveData result of the Leave group query.
     */
    fun getLeaveGroupMLD(): MutableLiveData<Query<ResponseBody>> {
        return repository.getLeaveGroupResult()
    }

    /**
     * Get the MutableLiveData result of the Rename group query.
     */
    fun getRenameGroupMLD(): MutableLiveData<Query<Group>> {
        return repository.getRenameGroupResult()
    }

    fun getGroup(): Query<Group> {
        return getGroupMLD().value!!
    }

    /**
     * Let the user leave the given group.
     * @param groupId: ID of the group the user is about to leave
     */
    fun userLeaveGroup(groupId: Int) {
        repository.userLeaveGroup(groupId)
    }

    /**
     * Change the name of a group
     * @param groupId: ID of the group of which the name will be changed
     * @param newName: The new name that will be given to the group
     */
    fun renameGroup(groupId: Int, newName: String) {
        repository.renameGroup(groupId, newName)
    }

    /**
     * Remove a member from a group.
     * @param userId: ID of the user that should be kicked
     * @param groupId: ID of the group the user is removed from
     */
    fun removeMemberFromGroup(userId: Int, groupId: Int) {
        repository.removeMemberFromGroup(userId, groupId)
    }
}