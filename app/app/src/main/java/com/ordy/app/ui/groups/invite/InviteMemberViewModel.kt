package com.ordy.app.ui.groups.invite

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.ApiService
import com.ordy.app.api.ApiServiceViewModel
import com.ordy.app.api.models.User
import com.ordy.app.api.util.Query
import okhttp3.ResponseBody

class InviteMemberViewModel(apiService: ApiService) : ApiServiceViewModel(apiService) {

    val inviteResult: MutableLiveData<Query<ResponseBody>> = MutableLiveData(Query())
    val users: MutableLiveData<Query<List<User>>> = MutableLiveData(Query())

    fun getUsers(): Query<List<User>> {
        return users.value!!
    }
}