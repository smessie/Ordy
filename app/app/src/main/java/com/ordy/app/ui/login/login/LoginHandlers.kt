package com.ordy.app.ui.login.login

import com.ordy.app.api.models.actions.UserLogin
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.ui.login.LoginViewModel
import com.ordy.app.util.InputUtil
import kotlinx.android.synthetic.main.fragment_login.*

class LoginHandlers (val fragment: LoginFragment, val viewModel: LoginViewModel) {

    /**
     * Attempt to login when clicked on the login button.
     */
    fun onLoginClick() {

        val email = InputUtil.extractText(fragment.input_email)
        val password = InputUtil.extractText(fragment.input_password)

        FetchHandler.handle(viewModel.loginResult, viewModel.apiService.login(
            UserLogin(
                email,
                password
            )
        ))
    }

    /**
     * Open register when clicked on the goto register button
     */
    fun onGotoRegisterClick() {
        viewModel.isLogin.postValue(false)
    }
}