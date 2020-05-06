package com.ordy.backend.database.repositories

import com.ordy.backend.database.models.FavoriteLocation
import com.ordy.backend.database.models.Location
import com.ordy.backend.database.models.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface FavoriteLocationRepository : JpaRepository<FavoriteLocation, Int> {
    fun findAllByUser(user: User): List<FavoriteLocation>
    fun findByLocationAndUser(location: Location, user: User): Optional<FavoriteLocation>
}