package com.ordy.app.ui.login.register

import com.ordy.app.R
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.ui.login.LoginViewModel
import com.ordy.app.util.InputUtil
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterHandlers(val fragment: RegisterFragment, val viewModel: LoginViewModel) {

    /**
     * Attempt to register when clicked on the register button.
     */
    fun onRegisterClick() {

        val username = InputUtil.extractText(fragment.input_register_username)
        val email = InputUtil.extractText(fragment.input_register_email)
        val password = InputUtil.extractText(fragment.input_register_password)
        val passwordRepeat = InputUtil.extractText(fragment.input_register_password_repeat)

        if (password == passwordRepeat) {
            viewModel.register(username, email, password)
        } else {
            ErrorHandler.handleRawGeneral(
                fragment.requireContext().getString(R.string.error_passwords_match),
                fragment.requireView()
            )
        }
    }

    /**
     * Open login when clicked on the goto login button
     */
    fun onGotoLoginClick() {
        viewModel.isLogin.postValue(true)
    }

}