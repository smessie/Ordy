package com.ordy.app.ui.login

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.RepositoryViewModelFactory
import com.ordy.app.ui.login.login.LoginFragment
import com.ordy.app.ui.login.register.RegisterFragment

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels {
        RepositoryViewModelFactory(
            applicationContext
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the activity layout file.
        setContentView(R.layout.activity_login)

        // Observe which fragment to show (login or register)
        viewModel.isLogin.observe(this, Observer {

            val fragmentTransaction = supportFragmentManager.beginTransaction()

            when (it) {
                true -> {
                    fragmentTransaction.replace(
                        R.id.fragment,
                        LoginFragment()
                    )
                }

                false -> {
                    fragmentTransaction.replace(R.id.fragment, RegisterFragment())
                }
            }

            fragmentTransaction.commit()
        })
    }
}
