package com.ordy.app.ui.login.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels

import com.ordy.app.R
import com.ordy.app.databinding.FragmentRegisterBinding
import com.ordy.app.ui.login.LoginViewModel

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
        binding.handlers = RegisterHandlers(this, viewModel)

        return binding.root
    }
}
