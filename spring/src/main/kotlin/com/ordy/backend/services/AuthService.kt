package com.ordy.backend.services

import com.ordy.backend.database.repositories.UserRepository
import com.ordy.backend.exceptions.GenericException
import com.ordy.backend.wrappers.AuthLoginWrapper
import com.ordy.backend.wrappers.AuthRegisterWrapper
import com.ordy.backend.wrappers.AuthTokenWrapper
import com.ordy.backend.database.models.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Service

@Service
class AuthService(@Autowired val userRepository: UserRepository, @Autowired val tokenService: TokenService) {
    private final val bCryptRounds = 12

    private fun hashPasswd(password: String) : String {
        return BCrypt.hashpw(password, BCrypt.gensalt(bCryptRounds))
    }

    /* returns True is password is correct */
    private fun checkPasswd(password: String, hash: String) : Boolean {
        return BCrypt.checkpw(password, hash)
    }

    fun login(loginWrapper: AuthLoginWrapper) : AuthTokenWrapper {
        val users = userRepository.findByEmail(loginWrapper.email)

        if (users.isNotEmpty()) {
            val user = users[0]
            if (checkPasswd(loginWrapper.password, user.password)) {
                return AuthTokenWrapper(tokenService.encrypt(user.id.toString()))
            } else {
                throw GenericException(HttpStatus.NOT_FOUND, "Incorrect email, password combination")
            }
        } else {
            throw GenericException(HttpStatus.NOT_FOUND, "Incorrect email, password combination")
        }
    }

    fun register(registerWrapper: AuthRegisterWrapper) {
        val users = userRepository.findByEmail(registerWrapper.email)
        // Checks if email is in use
        if (users.isEmpty()) {
            val newUser = User(name = registerWrapper.username, email = registerWrapper.email, password = hashPasswd(registerWrapper.password))
            userRepository.saveAndFlush(newUser)
        } else {
            throw GenericException(HttpStatus.FORBIDDEN, "Email alread in use")
        }
    }
}