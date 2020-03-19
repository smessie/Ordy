package com.ordy.app.ui.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ordy.app.api.apiService
import com.ordy.app.api.models.UserLogin
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.QueryError
import com.ordy.app.api.util.QueryHandler

class LoginViewModel : ViewModel() {

    /**
     * If the login or register window is opened.
     * True: login is open
     * False: register is open
     */
    val isLogin: MutableLiveData<Boolean> = MutableLiveData(true)

    /**
     * Attempt to login the user.
     *
     * @param email Given email
     * @param password Given password
     */
    fun login(email: String, password: String) {

        FetchHandler.handle(apiService.login(UserLogin(email, password)), object: QueryHandler<Boolean> {

            override fun onQuerySuccess(data: Boolean) {
                Log.i("BANAAN", "SUCCESS LOGIN")
            }

            override fun onQueryError(error: QueryError) {
                Log.i("BANAAN", "FAILED LOGIN")
            }
        })
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