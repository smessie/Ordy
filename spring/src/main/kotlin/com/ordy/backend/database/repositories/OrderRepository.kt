package com.ordy.backend.database.repositories

import com.ordy.backend.database.models.Group
import com.ordy.backend.database.models.Order
import com.ordy.backend.database.models.User
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Int> {
    fun findAllByGroup(group: Group): List<Order>
    fun findOrdersByCourier(courier: User): List<Order>
    fun findAllByNotifiedIsFalse(): List<Order>
}