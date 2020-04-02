package com.ordy.backend.services

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.util.Assert
import java.util.*

@SpringBootTest
class AuthServiceTest {
    @Autowired
    private lateinit var authService: AuthService

    @Test
    fun `Password hash should match with password`() {
        for (i in 1..50) {
            val a = UUID.randomUUID().toString()
            val b: String? = ReflectionTestUtils.invokeMethod<String>(authService, "hashPasswd", a)
            Assert.isTrue(ReflectionTestUtils.invokeMethod<Boolean>(authService, "checkPasswd", a, b) ?: false,
                    "pasword did not match")
        }
    }

    @Test
    fun `Password hash should not match with different password`() {
        for (i in 1..50) {
            val a = UUID.randomUUID().toString()
            val a2 = UUID.randomUUID().toString()
            Assert.isTrue(a != a2, "passwords are the same, rerun test")
            val b: String? = ReflectionTestUtils.invokeMethod<String>(authService, "hashPasswd", a)
            Assert.isTrue((ReflectionTestUtils.invokeMethod<Boolean>(authService, "checkPasswd", a2, b) ?: false).not(),
                    "pasword did match")
            val b2: String? = ReflectionTestUtils.invokeMethod<String>(authService, "hashPasswd", a2)
            Assert.isTrue((ReflectionTestUtils.invokeMethod<Boolean>(authService, "checkPasswd", a, b2) ?: false).not(),
                    "pasword did match")
        }
    }
}