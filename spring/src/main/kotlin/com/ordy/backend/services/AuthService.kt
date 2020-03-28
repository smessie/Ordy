package com.ordy.backend.services

import com.ordy.backend.database.repositories.UserRepository
import com.ordy.backend.wrappers.AuthLoginWrapper
import com.ordy.backend.wrappers.AuthRegisterWrapper
import com.ordy.backend.wrappers.AuthTokenWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Service

@Service
class AuthService(@Autowired userRepository: UserRepository) {
    private final val bCryptRounds = 12

    private fun hashPasswd(password: String) : String {
        return BCrypt.hashpw(password, BCrypt.gensalt(bCryptRounds))
    }

    /* returns True is password is correct */
    private fun checkPasswd(password: String, hash: String) : Boolean {
        return BCrypt.checkpw(password, hash)
    }

    fun login(loginWrapper: AuthLoginWrapper) : AuthTokenWrapper {
        //TODO
        return AuthTokenWrapper("0")
    }

    fun register(registerWrapper: AuthRegisterWrapper) {
        //TODO
    }
}