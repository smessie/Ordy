package com.ordy.app.ui.login.register

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar

import com.ordy.app.R
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.InputField
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.FragmentRegisterBinding
import com.ordy.app.ui.login.LoginViewModel
import kotlinx.android.synthetic.main.fragment_login.*
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
        binding.handlers = RegisterHandlers(this, viewModel)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.registerResult.observe(this, Observer {

            when(it.status) {

                QueryStatus.LOADING -> {
                    Snackbar.make(requireView(), "Creating account...", Snackbar.LENGTH_INDEFINITE).show()
                }

                QueryStatus.SUCCESS -> {
                    Log.i("BANAAN", "DONE LOADING")
                }

                QueryStatus.ERROR -> {
                    ErrorHandler.handleInputs(it.error, view, listOf(
                        InputField("username", this.input_register_username),
                        InputField("email", this.input_register_email),
                        InputField("password", this.input_register_password)
                    ))

                    ErrorHandler.handleGeneral(it.error, view)
                }
            }
        })
    }
}
