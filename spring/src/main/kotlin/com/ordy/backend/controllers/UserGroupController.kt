package com.ordy.backend.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/user/groups")
class UserGroupController {
    @GetMapping
    fun getGroups() {

    }

    @PostMapping("/{groupId}/leave")
    fun postLeaveGroup(@PathVariable groupId: Int) {

    }
}

