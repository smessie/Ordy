package com.ordy.app.ui.groups.create

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel

class CreateGroupViewModel(repository: Repository) : RepositoryViewModel(repository) {
    private val nameValueData: MutableLiveData<String> = MutableLiveData("")

    fun getNameValue(): String {
        return nameValueData.value!!
    }

    fun getNameValueData(): MutableLiveData<String> {
        return nameValueData
    }
}