package com.ordy.backend.database.repositories

import com.ordy.backend.database.models.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Int> {
    fun findByEmail(email: String) : List<User>
    fun findById(id: Int) : List<User>
}