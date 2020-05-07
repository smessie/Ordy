package com.ordy.backend.services

import com.github.javafaker.Faker
import com.ordy.backend.database.repositories.DeviceTokenRepository
import com.ordy.backend.database.repositories.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.util.Assert

@ExtendWith(MockitoExtension::class)
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

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `Password hash should match with password`() {
        for (i in 1..50) {
            val a = randomPassword()
            val b: String? = ReflectionTestUtils.invokeMethod<String>(authService, "hashPasswd", a)
            Assert.isTrue(ReflectionTestUtils.invokeMethod<Boolean>(authService, "checkPasswd", a, b) ?: false,
                    "pasword did not match")
        }
    }

    @Test
    fun `Password hash should not match with different password`() {
        for (i in 1..50) {
            val a = randomPassword()
            var a2 = randomPassword()
            while (a == a2) {
                a2 = randomPassword()
            }
            val b: String? = ReflectionTestUtils.invokeMethod<String>(authService, "hashPasswd", a)
            Assert.isTrue((ReflectionTestUtils.invokeMethod<Boolean>(authService, "checkPasswd", a2, b) ?: false).not(),
                    "pasword did match")
            val b2: String? = ReflectionTestUtils.invokeMethod<String>(authService, "hashPasswd", a2)
            Assert.isTrue((ReflectionTestUtils.invokeMethod<Boolean>(authService, "checkPasswd", a, b2) ?: false).not(),
                    "pasword did match")
        }
    }

    private fun randomPassword() = faker.internet().password(8, 64, true, true, true)
}