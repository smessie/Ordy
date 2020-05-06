package com.ordy.app.ui.groups.create

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel
import com.ordy.app.api.models.Group
import com.ordy.app.api.util.Query
import java.lang.IllegalStateException

class CreateGroupViewModel(repository: Repository) : RepositoryViewModel(repository) {
    private val nameValueData: MutableLiveData<String> = MutableLiveData("")
    private val createGroupMLD: MutableLiveData<Query<Group>> = MutableLiveData(Query())

    /**
     * Get livedata for creating a new group.
     */
    fun getCreateGroupMLD(): MutableLiveData<Query<Group>> {
        return this.createGroupMLD
    }

    /**
     * Get the name of the new group.
     */
    fun getNameValue(): String {
        return nameValueData.value!!
    }

    /**
     * Get livedata of the name of the new group.
     */
    fun getNameValueData(): MutableLiveData<String> {
        return nameValueData
    }

    /**
     * Get query of the create group request.
     * @throws IllegalStateException when MLD.value is null.
     */
    fun getCreateGroup(): Query<Group> {
        return createGroupMLD.value ?: throw IllegalStateException("CreateGroup data is null")
    }

    /**
     * Create a new group.
     * @param groupName: The name that the newly created group should have
     */
    fun createGroup(groupName: String) {
        repository.createGroup(getCreateGroupMLD(), groupName)
    }
}