package com.ordy.backend.database.repositories

import com.ordy.backend.database.models.Item
import org.springframework.data.jpa.repository.JpaRepository

interface ItemRepository : JpaRepository<Item, Int>