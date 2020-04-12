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

    /**
     * Get the MutableLiveData result of the Login query.
     */
    fun getLoginMLD(): MutableLiveData<Query<LoginResponse>> {
        return repository.getLoginResult()
    }

    /**
     * Get the MutableLiveData result of the Register query.
     */
    fun getRegisterMLD(): MutableLiveData<Query<ResponseBody>> {
        return repository.getRegisterResult()
    }

    /**
     * Attempt to login a user.
     * @param email: Email entered by the user
     * @param password: Password entered by the user
     */
    fun login(email: String, password: String) {
        repository.login(email, password)
    }

    /**
     * Attempt to register a user.
     * @param username: Username entered by the user
     * @param email: Email entered by the user
     * @param password: Password entered by the user
     */
    fun register(username: String, email: String, password: String) {
        repository.register(username, email, password)
    }
}