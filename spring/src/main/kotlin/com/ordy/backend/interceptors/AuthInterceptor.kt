package com.ordy.backend.interceptors

import com.fasterxml.jackson.databind.ObjectMapper
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import com.ordy.backend.database.repositories.UserRepository
import com.ordy.backend.exceptions.GenericException
import com.ordy.backend.exceptions.OrdyException
import com.ordy.backend.services.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.http.HttpStatus
import javax.crypto.BadPaddingException

@Component
class AuthInterceptor: HandlerInterceptor{

    @Autowired private lateinit var tokenService: TokenService
    @Autowired private lateinit var userRepo: UserRepository

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, dataObject: Any) : Boolean {


        val token = request.getHeader("Authorization")
        if (token == null) {
            prepareResponse(response, GenericException(HttpStatus.UNAUTHORIZED, "Invalid token"))
            return false
        }

        try {
            val userId = tokenService.decrypt(token).toIntOrNull()
            // Check if decrypted id is numerical
            return if (userId != null) {
                val optionalUsers = userRepo.findAllById(userId)

                if (optionalUsers.isNotEmpty()) {
                    val optionalUser = optionalUsers[0]
                    request.setAttribute("user", optionalUser)
                    true
                } else {
                    prepareResponse(response, GenericException(HttpStatus.UNAUTHORIZED, "Invalid token"))
                    false
                }
            } else {
                prepareResponse(response, GenericException(HttpStatus.UNAUTHORIZED, "Invalid token"))
                false
            }
        } catch (e: BadPaddingException) { /* Can occur when bad token is passed */
            prepareResponse(response, GenericException(HttpStatus.UNAUTHORIZED, "Invalid token"))
            return false
        }
    }

    fun prepareResponse(response: HttpServletResponse, e: OrdyException) {
        val mapper = ObjectMapper()
        response.contentType = "application/json"
        response.status = e.code.value()
        response.writer.write(mapper.writeValueAsString(e.fullWrap()))
    }
}