package com.ordy.backend.database.repositories

import com.ordy.backend.database.models.DeviceToken
import com.ordy.backend.database.models.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface DeviceTokenRepository : JpaRepository<DeviceToken, Int> {
    fun getByToken(token: String): Optional<DeviceToken>
    fun getAllByUser(user: User): List<DeviceToken>
}