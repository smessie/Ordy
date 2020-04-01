package com.ordy.backend.database.repositories

import com.ordy.backend.database.models.Group
import com.ordy.backend.database.models.GroupMember
import com.ordy.backend.database.models.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface GroupMemberRepository : JpaRepository<GroupMember, Int> {
    fun findGroupMembersByUser(user: User): List<GroupMember>
    fun findGroupMemberByUserAndGroup(user: User, group: Group): Optional<GroupMember>
}