package com.ordy.app.ui.login.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels

import com.ordy.app.R
import com.ordy.app.ui.login.LoginViewModel

class RegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        // Get the viewmodel from the activity.
        val viewModel: LoginViewModel by activityViewModels()

        // Open register when clicked on the goto register button.
        view.findViewById<Button>(R.id.button_goto_login).setOnClickListener {
            viewModel.openLogin()
        }

        return view
    }
}
