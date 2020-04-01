package com.ordy.backend.controllers

import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import com.ordy.backend.database.models.GroupInvite
import com.ordy.backend.services.GroupService
import com.ordy.backend.wrappers.InviteActionWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user/invites")
class UserInviteController(@Autowired val groupService: GroupService) {

    @GetMapping
    @JsonView(View.List::class)
    fun getInvites(@RequestAttribute userId: Int): List<GroupInvite> {
        return groupService.getInvites(userId)
    }

    @PostMapping("/{groupId}")
    @JsonView(View.Empty::class)
    fun postInvite(@PathVariable groupId: Int, @RequestAttribute userId: Int, @RequestBody inviteActionWrapper: InviteActionWrapper) {
        groupService.reactOnInvite(groupId, userId, inviteActionWrapper)
    }
}