package com.ordy.app.ui

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.ordy.app.R
import com.ordy.app.api.Repository
import com.ordy.app.api.models.LoginResponse
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.login.LoginActivity
import com.ordy.app.ui.login.LoginViewModel
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
class LoginTest : KoinTest {

    /**
     * Java faker for faking data.
     */
    private val faker = Faker()

    /**
     * Mocked android activity context.
     */
    private lateinit var mockContext: Context

    /**
     * ViewModel that has been created using Koin injection.
     */
    private val mockLoginViewModel: LoginViewModel by inject()

    /**
     * Repository that has been created using Koin injection.
     */
    private val mockRepository: Repository by inject()

    /**
     * Mock provider for Koin testing.
     */
    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Before
    fun setup() {
        // Initialize mocks
        mockContext = InstrumentationRegistry.getInstrumentation().targetContext

        declareMock<LoginViewModel>()
        declareMock<Repository>()
    }


    /**
     * Test the input fields and the login button.
     * When typing login credentials in appropriate fields and hitting the login button,
     * the login function should be called with the same login credentials.
     */
    @Test
    fun onLoginButtonClickShouldLogin() {
        val email = faker.internet().emailAddress()
        val password = faker.internet().password()
        val deviceToken = mockContext.getSharedPreferences("ordy", Context.MODE_PRIVATE)
            .getString("device_token", "") ?: ""

        val loginQuery: Query<LoginResponse> = Query()
        loginQuery.status = QueryStatus.INITIALIZED
        val loginMLD = MutableLiveData(loginQuery)

        whenever(mockLoginViewModel.getIsLoginMLD()).thenReturn(MutableLiveData(true))
        whenever(mockRepository.login(loginMLD, email, password, deviceToken)).then { }
        whenever(mockLoginViewModel.getLoginMLD()).thenReturn(loginMLD)

        ActivityScenario.launch(LoginActivity::class.java)

        onView(withId(R.id.input_email_text)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.input_password_text)).perform(typeText(password), closeSoftKeyboard())

        onView(withId(R.id.button_login)).perform(click())

        verify(mockLoginViewModel).login(email, password, deviceToken)
    }
}