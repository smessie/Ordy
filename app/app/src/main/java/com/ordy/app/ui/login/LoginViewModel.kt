package com.ordy.app.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ordy.app.api.apiService
import com.ordy.app.api.models.actions.UserLogin
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.Query
class LoginViewModel : ViewModel() {

    /**
     * If the login or register window is opened.
     * True: login is open
     * False: register is open
     */
    val isLogin: MutableLiveData<Boolean> = MutableLiveData(true)

    val loginResult: MutableLiveData<Query<Void>> = MutableLiveData(Query())

    /**
     * Attempt to login the user.
     *
     * @param email Given email
     * @param password Given password
     */
    fun login(email: String, password: String) {
        FetchHandler.handle(loginResult, apiService.login(
            UserLogin(
                email,
                password
            )
        ))
    }

    /**
     * Attempt to register a new user.
     *
     * @param username Given username
     * @param email Given email
     * @param password Given password
     * @param passwordRepeat Second password value, used for checking if it is equal to password.
     */
    fun register(username: String, email: String, password: String, passwordRepeat: String) {

    }

    /**
     * Open the register fragment.
     */
    fun openRegister() {
        isLogin.postValue(false)
    }

    /**
     * Open the login fragment.
     */
    fun openLogin() {
        isLogin.postValue(true)
    }
}