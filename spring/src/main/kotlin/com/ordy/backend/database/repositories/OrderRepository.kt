package com.ordy.backend.database.repositories

import com.ordy.backend.database.models.Group
import com.ordy.backend.database.models.Order
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Int> {
    fun findAllByGroup(group: Group): List<Order>
}