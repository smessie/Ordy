package com.ordy.backend.services

import com.ordy.backend.database.models.User
import com.ordy.backend.wrappers.GroupCreateWrapper
import com.ordy.backend.wrappers.GroupIdWrapper
import com.ordy.backend.wrappers.GroupWrapper
import org.springframework.stereotype.Service

@Service
class GroupService {
    fun createGroup(user: User, groupWrapper: GroupCreateWrapper) : GroupIdWrapper {
        //TODO implmement
        return GroupIdWrapper(0)
    }

    fun updateGroup(groupId: Int, groupWrapper: GroupCreateWrapper) : GroupWrapper {
        //TODO
        return GroupWrapper(0, "YEET")
    }

    fun createInvite(groupId: Int, userId: Int) {
        //TODO
    }

    fun deleteInvite(groupId: Int, userId: Int) {
        //TODO
    }

    fun deleteMember(groupId: Int, userId: Int) {
        //TODO
    }
}