package com.ordy.backend.controllers

import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import com.ordy.backend.database.models.Group
import com.ordy.backend.database.models.User
import com.ordy.backend.services.GroupService
import com.ordy.backend.wrappers.GroupCreateWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.logging.Logger

@RestController
@RequestMapping("/groups")
@ResponseStatus(HttpStatus.OK)
class GroupController(@Autowired val groupService: GroupService) {

    @PostMapping
    @JsonView(View.Detail::class)
    fun postGroup(@RequestBody groupCreateWrapper: GroupCreateWrapper, @RequestAttribute userId: Int): Group {
        return groupService.createGroup(userId, groupCreateWrapper)
    }

    @PatchMapping("/{groupId}")
    @JsonView(View.Detail::class)
    fun patchGroup(@PathVariable groupId: Int, @RequestBody groupCreateWrapper: GroupCreateWrapper): Group {
        return groupService.updateGroup(groupId, groupCreateWrapper)
    }

    @PostMapping("/{groupId}/invites/{userInvitedId}")
    @JsonView(View.Empty::class)
    fun postInvite(@PathVariable groupId: Int, @PathVariable userInvitedId: Int, @RequestAttribute userId: Int) {
        groupService.createInvite(groupId, userInvitedId, userId)
    }

    @DeleteMapping("/{groupId}/invites/{userId}")
    @JsonView(View.Empty::class)
    fun deleteInvite(@PathVariable groupId: Int, @PathVariable userId: Int) {
        groupService.deleteInvite(groupId, userId)
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    @JsonView(View.Empty::class)
    fun deleteMember(@PathVariable groupId: Int, @PathVariable userKickId: Int, @RequestAttribute userId: Int) {
        groupService.deleteMember(groupId, userKickId, userId)
    }
}