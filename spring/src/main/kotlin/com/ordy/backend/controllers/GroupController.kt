package com.ordy.backend.controllers

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
    fun postGroup(@RequestBody groupCreateWrapper: GroupCreateWrapper, @RequestHeader user: User): Group {
        return groupService.createGroup(user, groupCreateWrapper)
    }

    @PatchMapping("/{groupId}")
    fun patchGroup(@PathVariable groupId: Int, @RequestBody groupCreateWrapper: GroupCreateWrapper): Group {
        return groupService.updateGroup(groupId, groupCreateWrapper)
    }

    @PostMapping("/{groupId}/invites/{userId}")
    fun postInvite(@PathVariable groupId: Int, @PathVariable userId: Int) {
        groupService.createInvite(groupId, userId)
    }

    @DeleteMapping("/{groupId}/invites/{userId}")
    fun deleteInvite(@PathVariable groupId: Int, @PathVariable userId: Int) {
        groupService.deleteInvite(groupId, userId)
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    fun deleteMember(@PathVariable groupId: Int, @PathVariable userId: Int) {
        groupService.deleteMember(groupId, userId)
    }
}