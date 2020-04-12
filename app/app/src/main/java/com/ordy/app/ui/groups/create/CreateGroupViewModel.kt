package com.ordy.app.ui.groups.create

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel
import com.ordy.app.api.models.Group
import com.ordy.app.api.util.Query

class CreateGroupViewModel(repository: Repository) : RepositoryViewModel(repository) {
    private val nameValueData: MutableLiveData<String> = MutableLiveData("")

    fun getNameValue(): String {
        return nameValueData.value!!
    }

    fun getNameValueData(): MutableLiveData<String> {
        return nameValueData
    }

    /**
     * Get the MutableLiveData result of the Create group query.
     */
    fun getCreateGroupMLD(): MutableLiveData<Query<Group>> {
        return repository.getCreateGroupResult()
    }

    fun getCreateGroup(): Query<Group> {
        return getCreateGroupMLD().value!!
    }

    /**
     * Create a new group.
     * @param groupName: The name that the newly created group should have
     */
    fun createGroup(groupName: String) {
        repository.createGroup(groupName)
    }
}