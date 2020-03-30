package com.ordy.backend.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/user/invites")
class UserInviteController {
    @GetMapping
    fun getInvites() {

    }

    @PostMapping("/{groupId}")
    fun postInvite(@PathVariable groupId: Int) {

    }
}