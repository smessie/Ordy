package com.ordy.app.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.ordy.app.AppPreferences
import com.ordy.app.R
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.ui.login.login.LoginFragment
import com.ordy.app.ui.login.register.RegisterFragment
import org.koin.android.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the activity layout file.
        setContentView(R.layout.activity_login)

        // Observe which fragment to show (login or register)
        viewModel.getIsLoginMLD().observe(this, Observer {

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

        // Get the device token from Firebase.
        // This token will be passed with the login request.
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // Get the device token.
                    val deviceToken = it.result?.token

                    // Store the device token in persistent storage.
                    AppPreferences(this).deviceToken = deviceToken
                }

                else {
                    // Send an error message.
                    ErrorHandler().handleRawGeneral(
                        "Unable to fetch device token. Make sure you have a connection to the internet.",
                        window.decorView.rootView
                    )

                    // Set the device token to the empty string
                    // This way the backend will prevent the user from logging in.
                    AppPreferences(this).deviceToken = ""
                }
            }
    }

    override fun onBackPressed() {

        // If the register screen is showed, clicking on back will open the login screen again.
        if(!viewModel.getIsLogin()) {
            viewModel.getIsLoginMLD().postValue(true)
        }
        // If the login screen is showed, clicking on back will close the app.
        else {

            // Close the app.
            finishAffinity()
        }
    }
}
