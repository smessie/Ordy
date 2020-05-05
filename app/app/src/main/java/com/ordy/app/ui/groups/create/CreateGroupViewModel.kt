package com.ordy.app.ui.groups.create

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel
import com.ordy.app.api.models.Group
import com.ordy.app.api.util.Query

class CreateGroupViewModel(repository: Repository) : RepositoryViewModel(repository) {
    private val nameValueData: MutableLiveData<String> = MutableLiveData("")

    val createGroupMLD: MutableLiveData<Query<Group>> = MutableLiveData(Query())

    fun getNameValue(): String {
        return nameValueData.value!!
    }

    fun getNameValueData(): MutableLiveData<String> {
        return nameValueData
    }

    fun getCreateGroup(): Query<Group> {
        return createGroupMLD.value!!
    }

    /**
     * Create a new group.
     * @param groupName: The name that the newly created group should have
     */
    fun createGroup(groupName: String) {
        repository.createGroup(createGroupMLD, groupName)
    }
}