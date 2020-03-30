package com.ordy.backend.database.repositories

import com.ordy.backend.database.models.GroupMember
import com.ordy.backend.database.models.User
import org.springframework.data.jpa.repository.JpaRepository

interface GroupMemberRepository : JpaRepository<GroupMember, Int> {

    fun findGroupMembersByUser(user: User): List<GroupMember>
}