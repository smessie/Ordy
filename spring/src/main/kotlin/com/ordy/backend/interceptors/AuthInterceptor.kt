package com.ordy.backend.database.interceptors

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import com.ordy.backend.database.repositories.UserRepository
import com.ordy.backend.exceptions.GenericException
import com.ordy.backend.services.TokenService
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import org.springframework.http.HttpStatus

@Component
class AuthInterceptor: HandlerInterceptor{

    @Autowired
    private final val tokenService = TokenService()

    @Autowired
    private final val userRepo = UserRepository()

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, dataObject: Any) : Boolean {
        val token = request.getHeader("authentication")
        val userId = tokenService.decrypt(token).toIntOrNull()

        // Check if decrypted id is numerical
        if (userId != null) {
            val optionalUsers = userRepo.findById(userId)

            if (!optionalUsers.isEmpty()) {
                optionalUser = optionalUsers.get(0)
                request.setAttribute("user", optionalUser)
                return true
            } else {
                throw GenericException(HttpStatus.NOT_FOUND, "Invalid token")
            }
        } else {
            throw GenericException(HttpStatus.NOT_FOUND, "Invalid token")
        }
    }

    override fun postHandle(request: HttpServletRequest, response: HttpServletResponse, dataObject: Any, model: ModelAndView?){
        // Not used yet

    }

    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, dataObject: Any, e: Exception?) {
        // Not used yet
    }
}