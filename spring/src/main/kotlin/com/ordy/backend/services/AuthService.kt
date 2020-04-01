package com.ordy.backend.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ordy.backend.database.models.User
import com.ordy.backend.database.repositories.UserRepository
import com.ordy.backend.exceptions.ThrowableList
import com.ordy.backend.wrappers.AuthLoginWrapper
import com.ordy.backend.wrappers.AuthRegisterWrapper
import com.ordy.backend.wrappers.AuthTokenWrapper
import com.ordy.backend.wrappers.TokenWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Service
import java.util.*
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress

@Service
class AuthService(@Autowired val userRepository: UserRepository, @Autowired val tokenService: TokenService) {
    private final val bCryptRounds = 12
    private val usernamePattern = Regex("^[^ ][A-Za-z0-9 \\-_]+[^ ]$")

    private fun hashPasswd(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(bCryptRounds))
    }

    /**
     * Returns "true" is password is correct
     */
    private fun checkPasswd(password: String, hash: String): Boolean {
        return BCrypt.checkpw(password, hash)
    }

    fun checkEmail(email: String): Boolean {
        try {
            InternetAddress(email).validate()
        } catch (e: AddressException) {
            return false
        }
        return true
    }

    fun login(loginWrapper: AuthLoginWrapper): AuthTokenWrapper {
        val users = userRepository.findByEmail(loginWrapper.email)

        // Check if the user was found & the password is correct.
        if (users.isNotEmpty() && checkPasswd(loginWrapper.password, users.first().password)) {
            return AuthTokenWrapper(
                    tokenService.encrypt(
                        jacksonObjectMapper().writeValueAsString(
                                TokenWrapper(
                                    userId = users.first().id,
                                    random = UUID.randomUUID().toString()
                                )
                        )
                    ),
                    users.first().id
            )
        } else {
            val throwableList = ThrowableList()
            throwableList.addPropertyException("email", "Email and/or password is wrong")
            throwableList.addPropertyException("password", "Email and/or password is wrong")
            throwableList.addGenericException("Unable to login")
            throw throwableList.also { it.code = HttpStatus.UNPROCESSABLE_ENTITY }
        }
    }

    fun register(registerWrapper: AuthRegisterWrapper) {

        val throwableList = ThrowableList()

        // Validate if the given email is valid.
        if (checkEmail(registerWrapper.email).not()) {
            throwableList.addPropertyException("email", "Invalid email adress")
        }

        // Validate if the username does not contain invalid characters.
        if (usernamePattern.matches(registerWrapper.username).not()) {
            throwableList.addPropertyException("username", "Can only contain letters, number, space dash and underscore")
        }

        // Validate if the username is bigger than 3 chars.
        if(registerWrapper.username.length < 3) {
            throwableList.addPropertyException("username", "Should be at least 3 characters")
        }

        // Validate if the username is smaller than 30 chars.
        if(registerWrapper.username.length > 30) {
            throwableList.addPropertyException("username", "Should be at most 30 characters")
        }

        // Validate if the email is smaller than 320 chars.
        if(registerWrapper.email.length > 320) {
            throwableList.addPropertyException("email", "Should be at most 320 characters")
        }

        // Validate if the password is bigger than 8 chars.
        if(registerWrapper.password.length < 8) {
            throwableList.addPropertyException("password", "Should be at least 8 characters")
        }

        // Validate if the password is smaller than 64 chars.
        if(registerWrapper.password.length > 64) {
            throwableList.addPropertyException("password", "Should be at most 64 characters")
        }

        // Validate if the email is already taken.
        if(userRepository.findByEmail(registerWrapper.email).isEmpty().not()) {
            throwableList.addPropertyException("email", "Email is already taken")
        }

        // Validate if the username is already taken.
        if(userRepository.findByUsername(registerWrapper.username).isEmpty().not()) {
            throwableList.addPropertyException("username", "Username is already taken")
        }

        throwableList.ifNotEmpty {
            throwableList.addGenericException("Unable to create account")
            throw throwableList.also { it.code = HttpStatus.UNPROCESSABLE_ENTITY }
        }

        val newUser = User(username = registerWrapper.username, email = registerWrapper.email, password = hashPasswd(registerWrapper.password))
        userRepository.saveAndFlush(newUser)
    }
}