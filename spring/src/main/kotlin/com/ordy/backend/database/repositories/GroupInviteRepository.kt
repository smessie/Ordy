package com.ordy.backend.database.repositories

import com.ordy.backend.database.models.GroupInvite
import org.springframework.data.jpa.repository.JpaRepository

interface GroupInviteRepository : JpaRepository<GroupInvite, Int>