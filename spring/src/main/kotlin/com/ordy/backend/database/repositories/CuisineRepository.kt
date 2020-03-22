package com.ordy.backend.database.repositories

import com.ordy.backend.database.models.Cuisine
import org.springframework.data.jpa.repository.JpaRepository

interface CuisineRepository : JpaRepository<Cuisine, Int>