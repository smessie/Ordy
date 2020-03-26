package com.ordy.backend.services

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.util.Assert.isTrue
import java.util.*

@SpringBootTest
class TokenServiceTest {
    @Autowired
    private lateinit var tokenService: TokenService

    @Test
    fun `Token should be the same when Encrypted and Decrypted`() {
        for (i in 0..1000000) {
            val a = UUID.randomUUID().toString() /* an easy random string */
            val b : String = tokenService.encrypt(a)
            isTrue(a == tokenService.decrypt(b), "Tokens did not match")
        }
    }
}