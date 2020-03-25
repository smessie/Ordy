package com.ordy.backend.database.repositories

import com.ordy.backend.database.models.OrderItem
import org.springframework.data.jpa.repository.JpaRepository

interface OrderItemRepository : JpaRepository<OrderItem, Int>