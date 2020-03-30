package com.ordy.backend.interceptors

import com.ordy.backend.database.repositories.UserRepository
import com.ordy.backend.exceptions.GenericException
import com.ordy.backend.services.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import java.util.logging.Logger
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthInterceptor: HandlerInterceptor{

    @Autowired private lateinit var tokenService: TokenService
    @Autowired private lateinit var userRepo: UserRepository

    private val log = Logger.getLogger("prehandle")

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, dataObject: Any) : Boolean {
        log.info("[preHandle]; ${request.contextPath}")

        val token = request.getHeader("Authorization") ?: throw GenericException(HttpStatus.UNAUTHORIZED, "Invalid token")
        val userId = tokenService.decrypt(token).toIntOrNull()

        // Check if decrypted id is numerical
        if (userId != null) {
            val optionalUsers = userRepo.findAllById(userId)

            if (optionalUsers.isNotEmpty()) {
                val optionalUser = optionalUsers[0]
                request.setAttribute("user", optionalUser)
                return true
            } else {
                throw GenericException(HttpStatus.UNAUTHORIZED, "Invalid token")
            }
        } else {
            throw GenericException(HttpStatus.UNAUTHORIZED, "Invalid token")
        }
    }

    override fun postHandle(request: HttpServletRequest, response: HttpServletResponse, dataObject: Any, model: ModelAndView?){
        // Not used yet

    }

    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, dataObject: Any, e: Exception?) {
        // Not used yet
    }
}