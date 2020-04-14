package com.ordy.backend.database.repositories

import com.ordy.backend.database.models.Order
import com.ordy.backend.database.models.OrderItem
import com.ordy.backend.database.models.User
import org.springframework.data.jpa.repository.JpaRepository

interface OrderItemRepository : JpaRepository<OrderItem, Int> {
    fun findAllByOrder(order: Order): List<OrderItem>
    fun findOrderItemsByUser(user: User): List<OrderItem>
}