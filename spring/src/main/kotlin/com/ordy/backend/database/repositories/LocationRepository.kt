package com.ordy.backend.database.repositories

import com.ordy.backend.database.models.Location
import org.springframework.data.jpa.repository.JpaRepository

interface LocationRepository : JpaRepository<Location, Int> {
    fun findAllByName(s: String) : List<Location>

}