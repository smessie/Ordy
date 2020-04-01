package com.ordy.backend.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@Service
class TokenService {

    /* this should be an 32 character key */
    @Value("\${ENCRYPTION_KEY}")
    private lateinit var key: String
    private final val encryptionMethod = "AES"

    fun encrypt(content: String) : String {
        val secretKeySpec = SecretKeySpec(key.toByteArray(), encryptionMethod)
        val cipher = Cipher.getInstance(encryptionMethod)
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        return String(Base64.getEncoder().encode(cipher.doFinal(content.toByteArray())))
    }

    fun decrypt(token: String) : String {
        val secretKeySpec = SecretKeySpec(key.toByteArray(), encryptionMethod)
        val cipher = Cipher.getInstance(encryptionMethod)
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
        return String(cipher.doFinal(Base64.getDecoder().decode(token)))
    }
}