package com.ordy.app.ui.login.register

import com.ordy.app.api.models.actions.UserRegister
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.ui.login.LoginViewModel
import com.ordy.app.util.InputUtil
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterHandlers(val fragment: RegisterFragment, val viewModel: LoginViewModel) {

    /**
     * Attempt to register when clicked on the register button.
     */
    fun onRegisterClick() {

        val username = InputUtil.extractText(fragment.input_register_username)
        val email =  InputUtil.extractText(fragment.input_register_email)
        val password = InputUtil.extractText(fragment.input_register_password)
        val passwordRepeat = InputUtil.extractText(fragment.input_register_password_repeat)

        FetchHandler.handle(viewModel.registerResult, viewModel.apiService.register(
            UserRegister(
                username,
                email,
                password
            )
        ))
    }

    /**
     * Open login when clicked on the goto login button
     */
    fun onGotoLoginClick() {
        viewModel.isLogin.postValue(true)
    }

}