package com.ordy.backend.services

import com.github.javafaker.Faker
import com.google.gson.GsonBuilder
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.ordy.backend.database.models.DeviceToken
import com.ordy.backend.database.models.User
import com.ordy.backend.database.repositories.DeviceTokenRepository
import com.ordy.backend.database.repositories.UserRepository
import com.ordy.backend.exceptions.ThrowableList
import com.ordy.backend.wrappers.AuthLoginWrapper
import com.ordy.backend.wrappers.AuthRegisterWrapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.http.HttpStatus
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.util.Assert
import java.util.*

class AuthServiceTest {

    @InjectMocks
    private lateinit var authService: AuthService

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var tokenService: TokenService

    @Mock
    private lateinit var deviceTokenRepository: DeviceTokenRepository

    private val faker = Faker()
    private val gson = GsonBuilder().setPrettyPrinting().create()

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Nested
    inner class Unit {

        @Test
        fun `Password hash should match with password`() {
            for (i in 1..5) {
                val password = randomPassword()
                val passwordHash: String? = ReflectionTestUtils.invokeMethod<String>(authService, "hashPasswd", password)
                Assert.isTrue(ReflectionTestUtils.invokeMethod<Boolean>(authService, "checkPasswd", password, passwordHash) ?: false,
                        "password did not match")
            }
        }

        @Test
        fun `Password hash should not match with different password`() {
            for (i in 1..5) {
                val password1 = randomPassword()
                var password2 = randomPassword()
                while (password1 == password2) {
                    password2 = randomPassword()
                }
                val password1Hash: String? = ReflectionTestUtils.invokeMethod<String>(authService, "hashPasswd", password1)
                Assert.isTrue((ReflectionTestUtils.invokeMethod<Boolean>(authService, "checkPasswd", password2, password1Hash)
                        ?: false).not(),
                        "password did match")
                val password2Hash: String? = ReflectionTestUtils.invokeMethod<String>(authService, "hashPasswd", password2)
                Assert.isTrue((ReflectionTestUtils.invokeMethod<Boolean>(authService, "checkPasswd", password1, password2Hash)
                        ?: false).not(),
                        "password did match")
            }
        }

        @Nested
        inner class Login {

            @Test
            fun `User should be able to login`() {
                val loginWrapper = getAuthLoginWrapper()

                // get the hashed password
                val passwordHash: String? = ReflectionTestUtils.invokeMethod<String>(
                        authService, "hashPasswd", loginWrapper.password
                )

                Assertions.assertNotNull(passwordHash, "Should not be null")

                val user = User(
                        username = faker.name().firstName(),
                        email = loginWrapper.email,
                        password = passwordHash!!
                )

                whenever(userRepository.findByEmail(loginWrapper.email)).thenReturn(listOf(user))
                whenever(deviceTokenRepository.getByToken(loginWrapper.deviceToken)).thenReturn(Optional.empty())
                whenever(deviceTokenRepository.save(any<DeviceToken>())).thenAnswer { it.getArgument(0) }
                whenever(tokenService.encrypt(any<String>())).thenAnswer { it.getArgument(0) }

                val authTokenWrapper = authService.login(loginWrapper)

                Assertions.assertEquals(user, authTokenWrapper.user)
            }

            @Test
            fun `User should not be able to login, unexisting email`() {
                val loginWrapper = getAuthLoginWrapper()

                // get the hashed password
                val passwordHash: String? = ReflectionTestUtils.invokeMethod<String>(
                        authService, "hashPasswd", loginWrapper.password
                )

                Assertions.assertNotNull(passwordHash, "Should not be null")

                whenever(userRepository.findByEmail(loginWrapper.email)).thenReturn(emptyList())

                try {
                    authService.login(loginWrapper)
                } catch (e: ThrowableList) {
                    print(gson.toJson(e.fullWrap()))
                    Assertions.assertEquals(e.code, HttpStatus.UNPROCESSABLE_ENTITY)
                    Assertions.assertEquals(e.inputErrors.any { it.field == "email" }, true)
                    Assertions.assertEquals(e.inputErrors.any { it.field == "password" }, true)
                }
            }

            @Test
            fun `User should not be able to login, wrong password`() {
                val loginWrapper = getAuthLoginWrapper()

                // get the hashed password
                val passwordHash: String? = ReflectionTestUtils.invokeMethod<String>(
                        authService, "hashPasswd", "${loginWrapper.password}YEET"
                )

                Assertions.assertNotNull(passwordHash, "Should not be null")

                val user = User(
                        username = faker.name().firstName(),
                        email = loginWrapper.email,
                        password = passwordHash!!
                )

                whenever(userRepository.findByEmail(loginWrapper.email)).thenReturn(listOf(user))

                try {
                    authService.login(loginWrapper)
                } catch (e: ThrowableList) {
                    print(gson.toJson(e.fullWrap()))
                    Assertions.assertEquals(e.code, HttpStatus.UNPROCESSABLE_ENTITY)
                    Assertions.assertEquals(e.inputErrors.any { it.field == "email" }, true)
                    Assertions.assertEquals(e.inputErrors.any { it.field == "password" }, true)
                }
            }
        }

        @Nested
        inner class Registration {

            @Test
            fun `User should be able to register`() {
                val registerWrapper = getAuthRegistrationWrapper()

                whenever(userRepository.findByEmail(registerWrapper.email)).thenReturn(emptyList())
                whenever(userRepository.findByUsername(registerWrapper.username)).thenReturn(emptyList())
                whenever(userRepository.saveAndFlush<User>(any())).then {
                    User(
                            username = registerWrapper.email,
                            password = registerWrapper.password,
                            email = registerWrapper.email
                    )
                }

                authService.register(registerWrapper)
            }

            @Nested
            inner class Email {

                @Test
                fun `User should not be able to register, invalid email`() {
                    val registerWrapper = getAuthRegistrationWrapper(
                            email = faker.name().name() // INVALID EMAIL
                    )

                    whenever(userRepository.saveAndFlush<User>(any())).then {
                        User(username = "", password = "", email = "")
                    }

                    try {
                        authService.register(registerWrapper)
                    } catch (e: ThrowableList) {
                        print(gson.toJson(e.fullWrap()))
                        Assertions.assertEquals(e.code, HttpStatus.UNPROCESSABLE_ENTITY)
                        Assertions.assertEquals(e.inputErrors.first.field, "email")
                    }
                }

                @Test
                fun `User should not be able to register, email too long`() {
                    val registerWrapper = getAuthRegistrationWrapper(
                            email = faker.lorem().characters(320) + faker.internet().safeEmailAddress() // INVALID EMAIL LENGTH
                    )

                    whenever(userRepository.saveAndFlush<User>(any())).then {
                        User(username = "", password = "", email = "")
                    }

                    try {
                        authService.register(registerWrapper)
                    } catch (e: ThrowableList) {
                        print(gson.toJson(e.fullWrap()))
                        Assertions.assertEquals(e.code, HttpStatus.UNPROCESSABLE_ENTITY)
                        Assertions.assertEquals(e.inputErrors.first.field, "email")
                    }
                }

                @Test
                fun `User should not be able to register, email taken`() {
                    val registerWrapper = getAuthRegistrationWrapper(
                            email = faker.internet().safeEmailAddress() // INVALID EMAIL LENGTH
                    )
                    whenever(userRepository.findByEmail(registerWrapper.email)).thenReturn(
                            listOf(User(
                                    username = registerWrapper.username,
                                    email = registerWrapper.email,
                                    password = registerWrapper.password
                            ))
                    )
                    whenever(userRepository.saveAndFlush<User>(any())).then {
                        User(username = "", password = "", email = "")
                    }

                    try {
                        authService.register(registerWrapper)
                    } catch (e: ThrowableList) {
                        print(gson.toJson(e.fullWrap()))
                        Assertions.assertEquals(e.code, HttpStatus.UNPROCESSABLE_ENTITY)
                        Assertions.assertEquals(e.inputErrors.first.field, "email")
                    }
                }
            }

            @Nested
            inner class Username {

                @Test
                fun `User should not be able to register, username too short`() {
                    val registerWrapper = getAuthRegistrationWrapper(
                            username = "bo" // INVALID USERNAME
                    )

                    whenever(userRepository.saveAndFlush<User>(any())).then {
                        User(username = "", password = "", email = "")
                    }

                    try {
                        authService.register(registerWrapper)
                    } catch (e: ThrowableList) {
                        print(gson.toJson(e.fullWrap()))
                        Assertions.assertEquals(e.code, HttpStatus.UNPROCESSABLE_ENTITY)
                        Assertions.assertEquals(e.inputErrors.first.field, "username")
                    }
                }

                @Test
                fun `User should not be able to register, username too long`() {
                    val registerWrapper = getAuthRegistrationWrapper(
                            username = faker.lorem().fixedString(100) // INVALID USERNAME
                    )

                    whenever(userRepository.saveAndFlush<User>(any())).then {
                        User(username = "", password = "", email = "")
                    }

                    try {
                        authService.register(registerWrapper)
                    } catch (e: ThrowableList) {
                        print(gson.toJson(e.fullWrap()))
                        Assertions.assertEquals(e.code, HttpStatus.UNPROCESSABLE_ENTITY)
                        Assertions.assertEquals(e.inputErrors.first.field, "username")
                    }
                }

                @Test
                fun `User should not be able to register, username contains invalid characters`() {
                    val registerWrapper = getAuthRegistrationWrapper()

                    whenever(userRepository.saveAndFlush<User>(any())).then {
                        User(username = "", password = "", email = "")
                    }

                    try {
                        authService.register(registerWrapper)
                    } catch (e: ThrowableList) {
                        print(gson.toJson(e.fullWrap()))
                        Assertions.assertEquals(e.code, HttpStatus.UNPROCESSABLE_ENTITY)
                        Assertions.assertEquals(e.inputErrors.first.field, "username")
                    }
                }

                @Test
                fun `User should not be able to register, username taken`() {
                    val registerWrapper = getAuthRegistrationWrapper()
                    whenever(userRepository.findByUsername(registerWrapper.username)).thenReturn(
                            listOf(User(
                                    username = registerWrapper.username,
                                    email = registerWrapper.email,
                                    password = registerWrapper.password
                            ))
                    )
                    whenever(userRepository.saveAndFlush<User>(any())).then {
                        User(username = "", password = "", email = "")
                    }

                    try {
                        authService.register(registerWrapper)
                    } catch (e: ThrowableList) {
                        print(gson.toJson(e.fullWrap()))
                        Assertions.assertEquals(e.code, HttpStatus.UNPROCESSABLE_ENTITY)
                        Assertions.assertEquals(e.inputErrors.first.field, "username")
                    }
                }
            }

            @Nested
            inner class Password {
                @Test
                fun `User should not be able to register, password too short`() {
                    val registerWrapper = getAuthRegistrationWrapper(
                            password = faker.lorem().characters(5) // TO SHORT PASSWORD
                    )

                    whenever(userRepository.saveAndFlush<User>(any())).then {
                        User(username = "", password = "", email = "")
                    }

                    try {
                        authService.register(registerWrapper)
                    } catch (e: ThrowableList) {
                        print(gson.toJson(e.fullWrap()))
                        Assertions.assertEquals(e.code, HttpStatus.UNPROCESSABLE_ENTITY)
                        Assertions.assertEquals(e.inputErrors.first.field, "password")
                    }
                }

                @Test
                fun `User should not be able to register, password too long`() {
                    val registerWrapper = getAuthRegistrationWrapper(
                            password = faker.lorem().characters(100) // TO LONG PASSWORD
                    )

                    whenever(userRepository.saveAndFlush<User>(any())).then {
                        User(username = "", password = "", email = "")
                    }

                    try {
                        authService.register(registerWrapper)
                    } catch (e: ThrowableList) {
                        print(gson.toJson(e.fullWrap()))
                        Assertions.assertEquals(e.code, HttpStatus.UNPROCESSABLE_ENTITY)
                        Assertions.assertEquals(e.inputErrors.first.field, "password")
                    }
                }
            }
        }
    }

    @Nested
    inner class Integration {

        @Nested
        inner class Login {

            /**
             * A Piece of generic code that has to be executed at the start of every test
             */
            private fun setup(): AuthLoginWrapper {
                val loginWrapper = getAuthLoginWrapper()

                // get the hashed password
                val passwordHash: String? = ReflectionTestUtils.invokeMethod<String>(
                        authService, "hashPasswd", loginWrapper.password
                )

                Assertions.assertNotNull(passwordHash, "Should not be null")

                val user = User(
                        username = faker.name().firstName(),
                        email = loginWrapper.email,
                        password = passwordHash!!
                )

                whenever(userRepository.findByEmail(loginWrapper.email)).thenReturn(listOf(user))
                whenever(deviceTokenRepository.getByToken(loginWrapper.deviceToken)).thenReturn(Optional.empty())
                whenever(deviceTokenRepository.save(any<DeviceToken>())).thenAnswer { it.getArgument(0) }
                whenever(tokenService.encrypt(any<String>())).thenAnswer { it.getArgument(0) }

                authService.login(loginWrapper)

                return loginWrapper
            }

            @Test
            fun `userRepository-findByEmail should Be called`() {
                val loginWrapper = setup()
                verify(userRepository).findByEmail(loginWrapper.email)
            }

            @Test
            fun `deviceTokenRepository-getByToken should Be called`() {
                val loginWrapper = setup()
                verify(deviceTokenRepository).getByToken(loginWrapper.deviceToken)
            }

            @Test
            fun `deviceTokenRepository-save should Be called`() {
                setup()
                verify(deviceTokenRepository).save(any<DeviceToken>())
            }

            @Test
            fun `tokenService-encrypt should Be called`() {
                setup()
                verify(tokenService).encrypt(any<String>())
            }
        }

        @Nested
        inner class Register {

            /**
             * A Piece of generic code that has to be executed at the start of every test
             */
            private fun setup(): AuthRegisterWrapper {
                val registerWrapper = getAuthRegistrationWrapper()

                whenever(userRepository.findByEmail(registerWrapper.email)).thenReturn(emptyList())
                whenever(userRepository.findByUsername(registerWrapper.username)).thenReturn(emptyList())
                whenever(userRepository.saveAndFlush<User>(any())).then {
                    User(
                            username = registerWrapper.email,
                            password = registerWrapper.password,
                            email = registerWrapper.email
                    )
                }

                authService.register(registerWrapper)

                return registerWrapper
            }

            @Test
            fun `userRepository-findByEmail should be called`() {
                val registerWrapper = setup()
                verify(userRepository).findByEmail(registerWrapper.email)

            }

            @Test
            fun `userRepository-findByUsername should be called`() {
                val registerWrapper = setup()
                verify(userRepository).findByUsername(registerWrapper.username)
            }

            @Test
            fun `userRepository-saveAndFlush should be called`() {
                setup()
                verify(userRepository).saveAndFlush<User>(any())
            }
        }
    }


    private fun randomPassword() = faker.internet().password(8, 64, true, true, true)

    private fun getAuthRegistrationWrapper(
            username: String = faker.name().firstName(),
            email: String = faker.internet().safeEmailAddress(),
            password: String = faker.internet().password(8, 64, true, true, true)
    ): AuthRegisterWrapper {
        return AuthRegisterWrapper(username, email, password)
    }

    private fun getAuthLoginWrapper(
            email: String = faker.internet().safeEmailAddress(),
            password: String = faker.internet().password(8, 64, true, true, true),
            deviceToken: String = faker.internet().password(64, 128, true, true, true)
    ): AuthLoginWrapper {
        return AuthLoginWrapper(email, password, deviceToken)
    }
}