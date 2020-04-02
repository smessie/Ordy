package com.ordy.backend.database.repositories

import com.ordy.backend.database.models.Group
import com.ordy.backend.database.models.GroupInvite
import com.ordy.backend.database.models.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface GroupInviteRepository : JpaRepository<GroupInvite, Int> {
    fun findGroupInviteByUserAndGroup(user: User, group: Group): Optional<GroupInvite>
    fun findGroupInvitesByUser(user: User): List<GroupInvite>
    fun findGroupInvitesByGroup(group: Group): List<GroupInvite>
}