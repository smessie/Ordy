package com.ordy.app.ui.login

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel
import com.ordy.app.api.models.LoginResponse
import com.ordy.app.api.util.Query
import okhttp3.ResponseBody
import java.lang.IllegalStateException

class LoginViewModel(repository: Repository) : RepositoryViewModel(repository) {

    /**
     * If the login or register window is opened.
     * True: login is open
     * False: register is open
     */
    private val isLoginMLD: MutableLiveData<Boolean> = MutableLiveData(true)
    private val loginMLD: MutableLiveData<Query<LoginResponse>> = MutableLiveData(Query())
    private val registerMLD: MutableLiveData<Query<ResponseBody>> = MutableLiveData(Query())

    /**
     * Get livedata for which screen is selected.
     */
    fun getIsLoginMLD(): MutableLiveData<Boolean> {
        return this.isLoginMLD
    }

    /**
     * Get value for which screen is selected.
     *  true: login is open
     *  false: register is open
     *  @throws IllegalStateException when MLD.value is null.
     */
    fun getIsLogin(): Boolean {
        return this.isLoginMLD.value ?: throw IllegalStateException("IsLogin data is null")
    }

    /**
     * Get livedata for logging in.
     */
    fun getLoginMLD(): MutableLiveData<Query<LoginResponse>> {
        return this.loginMLD
    }

    /**
     * Get livedata for registering.
     */
    fun getRegisterMLD(): MutableLiveData<Query<ResponseBody>> {
        return this.registerMLD
    }

    /**
     * Attempt to login a user.
     * @param email: Email entered by the user
     * @param password: Password entered by the user
     * @param deviceToken: Devicetoken of the user
     */
    fun login(email: String, password: String, deviceToken: String) {
        repository.login(loginMLD, email, password, deviceToken)
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