package com.ordy.backend.database.repositories

import com.ordy.backend.database.models.DeviceToken
import com.ordy.backend.database.models.User
import org.springframework.data.jpa.repository.JpaRepository

interface DeviceTokenRepository : JpaRepository<DeviceToken, Int> {
    fun getByToken(token: String)
    fun getAllByUser(user: User)
}