package com.ordy.backend.controllers

import com.ordy.backend.services.AuthService
import com.ordy.backend.wrappers.AuthLoginWrapper
import com.ordy.backend.wrappers.AuthRegisterWrapper
import com.ordy.backend.wrappers.AuthTokenWrapper
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
@ResponseStatus(HttpStatus.OK)
@OpenAPIDefinition
class AuthController(@Autowired val authService: AuthService) {

    @PostMapping("/login")
    fun login(@RequestBody loginWrapper: AuthLoginWrapper) : AuthTokenWrapper {
        return authService.login(loginWrapper)
    }

    @PostMapping("/register")
    fun register(@RequestBody registerWrapper: AuthRegisterWrapper) {
        return authService.register(registerWrapper)
    }
}