package com.ordy.backend.services

import com.ordy.backend.database.models.Order
import com.ordy.backend.database.models.User
import com.ordy.backend.database.repositories.GroupMemberRepository
import com.ordy.backend.database.repositories.OrderRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OrderService(@Autowired val orderRepository: OrderRepository, @Autowired val groupMemberRepository: GroupMemberRepository) {

    fun getOrders(user: User): List<Order> {

        var groups = groupMemberRepository.findGroupMembersByUser(user).map { it.group }

        return groups.flatMap { orderRepository.findAllByGroup(it) }
    }
}