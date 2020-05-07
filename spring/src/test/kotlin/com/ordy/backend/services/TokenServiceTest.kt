package com.ordy.backend.services

import com.github.javafaker.Faker
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.util.Assert.isTrue

@ExtendWith(MockitoExtension::class)
class TokenServiceTest {

    private var tokenService: TokenService = TokenService("SECRET!!SECRET!!SECRET!!SECRET!!")
    private var faker = Faker()

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `Token should be the same when Encrypted and Decrypted`() {
        for (i in 0..10000) {
            val a = faker.internet().password(20, 60, true, true, true)
            val b: String = tokenService.encrypt(a)
            isTrue(a == tokenService.decrypt(b), "Tokens did not match")
        }
    }
}