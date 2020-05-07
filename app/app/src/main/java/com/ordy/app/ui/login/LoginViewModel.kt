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
     * Register: Value of username input
     */
    private val userNameData: MutableLiveData<String> = MutableLiveData("")

    /**
     * Register: Value of email input
     */
    private val emailRegisterData: MutableLiveData<String> = MutableLiveData("")

    /**
     * Register: Value of password input
     */
    private val passwordRegisterData: MutableLiveData<String> = MutableLiveData("")

    /**
     * Register: Value of repeat password input
     */
    private val passwordRepeatData: MutableLiveData<String> = MutableLiveData("")

    /**
     * Login: Value of email input
     */
    private val emailLoginData: MutableLiveData<String> = MutableLiveData("")

    /**
     * Login: Value of password input
     */
    private val passwordLoginData: MutableLiveData<String> = MutableLiveData("")

    /**
     * Register: Get the username data.
     */
    fun getUserNameData(): MutableLiveData<String> {
        return userNameData
    }

    /**
     * Register: Get the register-email data.
     */
    fun getEmailRegisterData(): MutableLiveData<String> {
        return emailRegisterData
    }

    /**
     * Register: Get the register-password data.
     */
    fun getPasswordRegisterData(): MutableLiveData<String> {
        return passwordRegisterData
    }

    /**
     * Register: Get the repeat-password data.
     */
    fun getPasswordRepeatData(): MutableLiveData<String> {
        return passwordRepeatData
    }

    /**
     * Login: Get the login-email data.
     */
    fun getEmailLoginData(): MutableLiveData<String> {
        return emailLoginData
    }

    /**
     * Login: Get the login-password data.
     */
    fun getPasswordLoginData(): MutableLiveData<String> {
        return passwordLoginData
    }

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