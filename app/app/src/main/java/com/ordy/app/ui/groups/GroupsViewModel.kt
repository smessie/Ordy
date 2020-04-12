package com.ordy.app.ui.groups

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel
import com.ordy.app.api.models.Group
import com.ordy.app.api.util.Query

class GroupsViewModel(repository: Repository) : RepositoryViewModel(repository) {

    /**
     * Refresh the list of groups the user is in.
     */
    fun refreshGroups() {
        repository.refreshGroups()
    }

    /**
     * Get the MutableLiveData result of the Groups fetch.
     */
    fun getGroupsMLD(): MutableLiveData<Query<List<Group>>> {
        return repository.getGroups()
    }

    fun getGroups(): Query<List<Group>> {
        return getGroupsMLD().value!!
    }
}