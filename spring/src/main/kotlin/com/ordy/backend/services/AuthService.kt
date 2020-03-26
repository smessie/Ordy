package com.ordy.backend.services

import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Service
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@Service
class AuthService {
    private final val bCryptRounds = 12

    private fun hashPasswd(password: String) : String {
        return BCrypt.hashpw(password, BCrypt.gensalt(bCryptRounds))
    }

    /* returns True is password is correct */
    private fun checkPasswd(password: String, hash: String) : Boolean {
        return BCrypt.checkpw(password, hash)
    }
}