package com.ordy.app.ui.login

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.ApiService
import com.ordy.app.api.ApiServiceViewModel
import com.ordy.app.api.models.LoginResponse
import com.ordy.app.api.util.Query
import okhttp3.ResponseBody

class LoginViewModel(apiService: ApiService) : ApiServiceViewModel(apiService) {

    /**
     * If the login or register window is opened.
     * True: login is open
     * False: register is open
     */
    val isLogin: MutableLiveData<Boolean> = MutableLiveData(true)

    val loginResult: MutableLiveData<Query<LoginResponse>> = MutableLiveData(Query())

    val registerResult: MutableLiveData<Query<ResponseBody>> = MutableLiveData(Query())
}