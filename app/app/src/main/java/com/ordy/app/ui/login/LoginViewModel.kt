package com.ordy.app.ui.login

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel
import com.ordy.app.api.models.LoginResponse
import com.ordy.app.api.util.Query
import okhttp3.ResponseBody

class LoginViewModel(repository: Repository) : RepositoryViewModel(repository) {

    /**
     * If the login or register window is opened.
     * True: login is open
     * False: register is open
     */
    val isLogin: MutableLiveData<Boolean> = MutableLiveData(true)
    val loginMLD: MutableLiveData<Query<LoginResponse>> = MutableLiveData(Query())
    val registerMLD: MutableLiveData<Query<ResponseBody>> = MutableLiveData(Query())

    /**
     * Attempt to login a user.
     * @param email: Email entered by the user
     * @param password: Password entered by the user
     */
    fun login(email: String, password: String) {
        repository.login(loginMLD, email, password)
    }

    /**
     * Attempt to register a user.
     * @param username: Username entered by the user
     * @param email: Email entered by the user
     * @param password: Password entered by the user
     */
    fun register(username: String, email: String, password: String) {
        repository.register(registerMLD, username, email, password)
    }
}