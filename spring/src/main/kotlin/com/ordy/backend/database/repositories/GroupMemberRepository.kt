package com.ordy.backend.database.repositories

import com.ordy.backend.database.models.GroupMember
import org.springframework.data.jpa.repository.JpaRepository

interface GroupMemberRepository : JpaRepository<GroupMember, Int>