package com.ordy.app.ui.login.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.ordy.app.AppPreferences
import com.ordy.app.MainActivity
import com.ordy.app.R
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.InputField
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.FragmentLoginBinding
import com.ordy.app.ui.login.LoginViewModel
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_login, container, false)

        // Create binding for the fragment.
        val binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.handlers = LoginHandlers(this, viewModel)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.loginResult.observe(this, Observer {

            when(it.status) {

                QueryStatus.LOADING -> {
                    Snackbar.make(requireView(), "Attempting to login...", Snackbar.LENGTH_INDEFINITE).show()
                }

                QueryStatus.SUCCESS -> {

                    val preferences = AppPreferences(requireContext())

                    // Store the given access token.
                    preferences.accessToken = it.requireData().accessToken

                    // Store the given user.
                    preferences.userId = it.requireData().user.id

                    // Open the main activity
                    val intent = Intent(this.context, MainActivity::class.java)
                    startActivity(intent)
                }

                QueryStatus.ERROR -> {
                    ErrorHandler.handle(it.error, view, listOf(
                        InputField("email", this.input_email),
                        InputField("password", this.input_password)
                    ))
                }
            }
        })
    }
}
