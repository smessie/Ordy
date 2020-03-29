package com.ordy.backend.services

import com.ordy.backend.database.repositories.UserRepository
import com.ordy.backend.exceptions.ExceptionHandler
import com.ordy.backend.exceptions.GenericException
import com.ordy.backend.wrappers.AuthLoginWrapper
import com.ordy.backend.wrappers.AuthRegisterWrapper
import com.ordy.backend.wrappers.AuthTokenWrapper
import com.ordy.backend.services.TokenService
import com.ordy.backend.database.models.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Service

@Service
class AuthService(@Autowired userRepository: UserRepository, @Autowired tokenService: TokenService) {
    private final val bCryptRounds = 12
    private final val exceptionHandler = ExceptionHandler()

    private fun hashPasswd(password: String) : String {
        return BCrypt.hashpw(password, BCrypt.gensalt(bCryptRounds))
    }

    /* returns True is password is correct */
    private fun checkPasswd(password: String, hash: String) : Boolean {
        return BCrypt.checkpw(password, hash)
    }

    fun login(loginWrapper: AuthLoginWrapper) : AuthTokenWrapper {
        val users = userRepository.findByEmail(loginWrapper.email)

        if (!users.isEmpty()) {
            val user = users.get(0)
            if (checkPasswd(loginWrapper.password, user.password)) {
                return AuthTokenWrapper(tokenService.encrypt(user.id))
            } else {
                throw GenericException(HttpStatus.NOT_FOUND, "User not found")

            }
        }
    }

    fun register(registerWrapper: AuthRegisterWrapper) {
        val users = userRepository.findByEmail(registerWrapper.email)
        if (users.isEmpty()) {
            newUser = User()
            newUser.password = hashPasswd(registerWrapper.password)
            newUser.name = registerWrapper.name
            newUser.email = registerWrapper.email
            userRepository.save(newUser)
        } else {
            throw GenericException(HttpStatus.FORBIDDEN, "Email alread in use")
        }
    }
}