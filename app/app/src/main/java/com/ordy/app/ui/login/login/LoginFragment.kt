package com.ordy.app.ui.login.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.ordy.app.MainActivity
import com.ordy.app.R
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.InputField
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.FragmentLoginBinding
import com.ordy.app.ui.login.LoginViewModel
import com.ordy.app.util.InputUtil
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.fragment_login.view.input_email

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
                    // Open the main activity
                    val intent = Intent(this.context, MainActivity::class.java)
                    startActivity(intent)
                }

                QueryStatus.ERROR -> {
                    ErrorHandler.handleInputs(it.error, view, listOf(
                        InputField("email", this.input_email),
                        InputField("password", this.input_password)
                    ))

                    ErrorHandler.handleGeneral(it.error, view)
                }
            }
        })
    }
}
