package com.ordy.backend.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/auth")
class AuthController {

    @PostMapping("/login")
    fun login() {

    }

    @PostMapping("/register")
    fun register() {

    }
}