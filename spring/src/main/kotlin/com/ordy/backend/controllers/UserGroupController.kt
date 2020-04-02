package com.ordy.backend.controllers

import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import com.ordy.backend.services.GroupService
import com.ordy.backend.wrappers.GroupListWrapper
import com.ordy.backend.wrappers.GroupWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user/groups")
class UserGroupController(@Autowired val groupService: GroupService) {

    @GetMapping
    @JsonView(View.List::class)
    fun getGroups(@RequestAttribute userId: Int) : List<GroupListWrapper> {
        return groupService.getGroups(userId)
    }

    @PostMapping("/{groupId}/leave")
    @JsonView(View.Empty::class)
    fun postLeaveGroup(@RequestAttribute userId: Int, @PathVariable groupId: Int) {
        groupService.leaveGroup(groupId, userId)
    }
}

