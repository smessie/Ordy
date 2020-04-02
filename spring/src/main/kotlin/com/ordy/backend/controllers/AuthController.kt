package com.ordy.backend.controllers

import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import com.ordy.backend.services.AuthService
import com.ordy.backend.wrappers.AuthLoginWrapper
import com.ordy.backend.wrappers.AuthRegisterWrapper
import com.ordy.backend.wrappers.AuthTokenWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
@ResponseStatus(HttpStatus.OK)
class AuthController(@Autowired val authService: AuthService) {

    @PostMapping("/login")
    @JsonView(View.Detail::class)
    fun login(@RequestBody loginWrapper: AuthLoginWrapper): AuthTokenWrapper {
        return authService.login(loginWrapper)
    }

    @PostMapping("/register")
    @JsonView(View.Empty::class)
    fun register(@RequestBody registerWrapper: AuthRegisterWrapper) {
        return authService.register(registerWrapper)
    }
}