package com.ordy.backend.controllers

import com.fasterxml.jackson.annotation.JsonView
import org.springframework.http.HttpStatus
import com.ordy.backend.database.View
import com.ordy.backend.database.models.User
import com.ordy.backend.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/user")
@ResponseStatus(HttpStatus.OK)
class UserInfoController(@Autowired val userService: UserService) {

    @GetMapping
    @JsonView(View.Detail::class)
    fun getUserInfo(@RequestAttribute userId: Int): User {
        return userService.getUserInfo(userId)
    }
}