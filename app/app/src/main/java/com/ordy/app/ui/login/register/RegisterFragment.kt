package com.ordy.app.ui.login.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.InputField
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.FragmentRegisterBinding
import com.ordy.app.ui.login.LoginViewModel
import com.ordy.app.util.SnackbarUtil
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment() {

    private val viewModel: LoginViewModel by activityViewModels()

    /**
     * Called when view is created.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_register, container, false)

        // Create binding for the fragment.
        val binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.handlers = RegisterHandlers(this, viewModel)
        binding.viewModel = viewModel

        return binding.root
    }


    /**
     * Clears the data of the login-screen
     */
    private fun clearLogin() {
        viewModel.getEmailLoginData().value = ""
        viewModel.getPasswordLoginData().value = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        clearLogin()

        viewModel.getRegisterMLD().observe(this, Observer {

            when (it.status) {

                QueryStatus.LOADING -> {
                    SnackbarUtil.openSnackbar(
                        "Creating account...",
                        activity
                    )
                }

                QueryStatus.SUCCESS -> {
                    SnackbarUtil.closeSnackbar(requireActivity())

                    // Go to the login fragment.
                    viewModel.getIsLoginMLD().postValue(true)
                }

                QueryStatus.ERROR -> {
                    SnackbarUtil.closeSnackbar(requireActivity())

                    ErrorHandler().handle(
                        it.error, activity, listOf(
                            InputField("username", this.input_register_username),
                            InputField("email", this.input_register_email),
                            InputField("password", this.input_register_password)
                        )
                    )
                }

                else -> {
                }
            }
        })
    }
}
