package com.ordy.backend.database.repositories

import com.ordy.backend.database.models.Group
import org.springframework.data.jpa.repository.JpaRepository

interface GroupRepository : JpaRepository<Group, Int>