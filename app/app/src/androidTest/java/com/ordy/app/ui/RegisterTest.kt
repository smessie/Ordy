package com.ordy.app.ui

import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.ordy.app.R
import com.ordy.app.api.Repository
import com.ordy.app.api.util.Query
import com.ordy.app.ui.login.LoginActivity
import com.ordy.app.ui.login.LoginViewModel
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class RegisterTest : KoinTest {

    /**
     * Java faker for faking data.
     */
    private val faker = Faker()

    /**
     * ViewModel that has been created using Koin injection.
     */
    private val mockLoginViewModel: LoginViewModel by inject()

    /**
     * Mock provider for Koin testing.
     */
    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Before
    fun setup() {
        declareMock<LoginViewModel>()
        declareMock<Repository>()
    }

    /**
     * Test the input fields and the register button.
     * When typing register credentials in appropriate fields and hitting the register button,
     * the register function should be called with the same register credentials.
     */
    @Test
    fun onRegisterButtonClickShouldRegister() {
        val username = faker.name().username()
        val email = faker.internet().emailAddress()
        val password = faker.internet().password()

        val registerMLD = MutableLiveData(Query<ResponseBody>())

        // Mock data for clearLogin function
        whenever(mockLoginViewModel.getEmailLoginData()).thenReturn(MutableLiveData(""))
        whenever(mockLoginViewModel.getPasswordLoginData()).thenReturn(MutableLiveData(""))

        whenever(mockLoginViewModel.getIsLoginMLD()).thenReturn(MutableLiveData(false))
        whenever(mockLoginViewModel.register(username, email, password)).then { }
        whenever(mockLoginViewModel.getRegisterMLD()).thenReturn(registerMLD)

        ActivityScenario.launch(LoginActivity::class.java)

        onView(withId(R.id.input_register_username_text)).perform(
            typeText(username),
            closeSoftKeyboard()
        )
        onView(withId(R.id.input_register_email_text)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.input_register_password_text)).perform(
            typeText(password),
            closeSoftKeyboard()
        )
        onView(withId(R.id.input_register_password_repeat_text)).perform(
            typeText(password),
            closeSoftKeyboard()
        )

        onView(withId(R.id.button_login)).perform(click())

        verify(mockLoginViewModel).register(username, email, password)
    }
}