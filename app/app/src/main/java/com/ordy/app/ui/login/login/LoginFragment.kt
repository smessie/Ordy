package com.ordy.app.ui.login.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.ordy.app.R
import com.ordy.app.ui.login.LoginViewModel
import com.ordy.app.util.InputUtil
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Get the viewmodel from the activity.
        val viewModel: LoginViewModel by activityViewModels()

        // Open register when clicked on the goto register button.
        view.findViewById<Button>(R.id.button_goto_register).setOnClickListener {
            viewModel.openRegister()
        }

        // Attempt to login when clicked on the login button.
        view.findViewById<Button>(R.id.button_login).setOnClickListener {

            val email = InputUtil.extractText(view.findViewById(R.id.input_email))
            val password = InputUtil.extractText(view.findViewById(R.id.input_password))

            viewModel.login(email, password)
        }

        return view
    }
}
