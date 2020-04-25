package com.ordy.backend.database.repositories

import com.ordy.backend.database.models.Image
import org.springframework.data.jpa.repository.JpaRepository

interface ImageRepository : JpaRepository<Image, Int>
