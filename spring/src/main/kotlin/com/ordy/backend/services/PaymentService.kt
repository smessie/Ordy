package com.ordy.backend.services

import com.ordy.backend.database.repositories.OrderItemRepository
import com.ordy.backend.database.repositories.OrderRepository
import com.ordy.backend.database.repositories.UserRepository
import com.ordy.backend.wrappers.PaymentWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class PaymentService(
        @Autowired val userRepository: UserRepository,
        @Autowired val orderRepository: OrderRepository,
        @Autowired val orderItemRepository: OrderItemRepository
) {

    /**
     * Get a list of debtors for a given user.
     * Payments for which the user has paid in advance.
     */
    fun getDebtors(userId: Int): List<PaymentWrapper> {
        val user = userRepository.findById(userId).get()

        // Get all the orders the given user has paid for in advance
        val orders = orderRepository.findOrdersByCourier(user).filter { it.deadline.before(Date()) }

        val listOfLists = orders.map { order ->
            // Get for each order a list of all ordered items
            val orderItemsOfOrder = order.orderItems

            // Divide this list in sublists per separate user
            val orderItemsPerUser = orderItemsOfOrder.groupBy { it.user }

            // Per order, a Payment is now added for each user containing all ordered items in that order of that user
            // Filter the user we are querying for (where he paid for himself)
            orderItemsPerUser.filter { it.key.id != userId }.map {
                PaymentWrapper(
                        user = it.key,
                        order = order,
                        orderItems = it.value.toSet()
                )
            }
        }
        return listOfLists.flatten()
    }

    /**
     * Get a list of debts for a given user.
     * Payments where someone else has paid for the specified user.
     */
    fun getDebts(userId: Int): List<PaymentWrapper> {
        val user = userRepository.findById(userId).get()
        val allOrderItems = orderItemRepository.findOrderItemsByUser(user)
        val orders = allOrderItems.map {
            it.order
        }.distinct()

        // Filter the user we are querying for (where he paid for himself)
        return orders.filter { it.courier.id != userId }.map { order ->
            PaymentWrapper(
                    user = order.courier,
                    order = order,
                    orderItems = order.orderItems.filter { it.user.id == userId }.toSet()
            )
        }
    }
}