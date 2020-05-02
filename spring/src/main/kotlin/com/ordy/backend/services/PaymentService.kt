package com.ordy.backend.services

import com.ordy.backend.database.repositories.OrderItemRepository
import com.ordy.backend.database.repositories.OrderRepository
import com.ordy.backend.database.repositories.UserRepository
import com.ordy.backend.exceptions.ThrowableList
import com.ordy.backend.services.notifications.NotificationService
import com.ordy.backend.services.notifications.NotificationType
import com.ordy.backend.wrappers.PaymentUpdateWrapper
import com.ordy.backend.wrappers.PaymentWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class PaymentService(
        @Autowired val userRepository: UserRepository,
        @Autowired val orderRepository: OrderRepository,
        @Autowired val orderItemRepository: OrderItemRepository,
        @Autowired val notificationService: NotificationService
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
            val orderItemsPerUser = orderItemsOfOrder.filter { !it.paid }.groupBy { it.user }

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
        val allOrderItems = orderItemRepository.findOrderItemsByUser(user).filter { it.order.deadline.before(Date()) && !it.paid }
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

    /**
     * Mark all OrderItems in an Order for a User as paid or not paid.
     */
    fun patchOrderPayed(courier: Int, orderId: Int, userId: Int, paymentUpdateWrapper: PaymentUpdateWrapper) {
        val throwableList = ThrowableList()

        val optionalOrder = orderRepository.findById(orderId)
        if (!optionalOrder.isPresent) {
            throw throwableList.also { it.addGenericException("Order does not exist.") }
        }
        val order = optionalOrder.get()
        if (order.courier.id != courier) {
            throw throwableList.also { it.addGenericException("You cannot do this as you are not the courier.") }
        }

        if (order.orderItems.none { it.user.id == userId }) {
            throw throwableList.also { it.addGenericException("This user has not ordered anything in this order.") }
        }

        if (order.deadline.after(Date())) {
            throw throwableList.also { it.addGenericException("You cannot mark as paid before order deadline has expired.") }
        }

        // Set all OrderItem paid booleans of the given user equal to the paid value of PaymentUpdateWrapper
        val updatedOrderItems = order.orderItems.map {
            if (it.user.id == userId) {
                it.paid = paymentUpdateWrapper.paid
            }
            it
        }
        order.orderItems = updatedOrderItems.toSet()
        orderRepository.save(order)
    }

    /**
     * Notify the user that still has to pay
     */
    fun reactOnNotify(orderId: Int, userId: Int) {
        val notifiedUser = userRepository.findById(userId)
        val order = orderRepository.findById(orderId)
        val throwableList = ThrowableList()

        if (!notifiedUser.isPresent) {
            throw throwableList.also { it.addGenericException("User does not exist.") }
        }

        if (!order.isPresent) {
            throw throwableList.also { it.addGenericException("Order does not exist.") }
        }

        val orderItemsFiltered = order.get().orderItems.filter { it.user.id == notifiedUser.get().id }
        // Check whether the last notification was at least one hour ago
        if (orderItemsFiltered.any { Date().time - it.lastNotification.time < 3600000 }) {
            throw throwableList.also { it.addGenericException("A new notification can only be sent after 1 hour.") }
        }

        // Update the last time a notifictaion was sent to the user
        val updatedOrderItems = order.get().orderItems.map {
            if (it.user.id == notifiedUser.get().id) {
                it.lastNotification = Date()
            }
            it
        }
        order.get().orderItems = updatedOrderItems.toSet()
        orderRepository.save(order.get())

        notificationService.sendNotificationAsync(
                user = notifiedUser.get(),
                content = notificationService.createNotificationContent(
                        title = "Payment reminder",
                        subtitle = "You still need to pay ${order.get().courier.username}",
                        detail = "<b>Group: </b>${order.get().group.name}\n<b>Location: </b>${order.get().location.name}",
                        summary = "Payment reminder",
                        type = NotificationType.PAYMENT_DEBT
                )
        )
    }
}