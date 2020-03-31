package com.ordy.backend.controllers

import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import com.ordy.backend.database.models.Group
import com.ordy.backend.services.GroupService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user/invites")
class UserInviteController(@Autowired val groupService: GroupService) {

    @GetMapping
    fun getInvites() {

    }

    @PostMapping("/{groupId}")
    @JsonView(View.Detail::class)
    fun postInvite(@PathVariable groupId: Int) {

    }
}