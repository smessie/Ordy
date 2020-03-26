package com.ordy.backend.controllers

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/groups")
class GroupController {
    @PostMapping
    fun postGroup() {

    }

    @PatchMapping("/{groupId}")
    fun patchGroup(@PathVariable groupId: Int)  {

    }

    @PostMapping("/{groupId}/invites/{userId}")
    fun postInvite(@PathVariable groupId: Int, @PathVariable userId: Int) {

    }
}