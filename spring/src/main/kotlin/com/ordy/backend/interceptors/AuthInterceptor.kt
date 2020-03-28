package com.ordy.backend.database.interceptors

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView

@Component
class AuthInterceptor: HandlerInterceptor{

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, dataObject: Any) : Boolean {
        println("1. from PreHandle method.")
        return true
    }

    override fun postHandle(request: HttpServletRequest, response: HttpServletResponse, dataObject: Any, model: ModelAndView?){
        println("3. from PostHandle method.")
    }

    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, dataObject: Any, e: Exception?) {
        println("4. from AfterCompletion method - Request Completed!")
    }
}