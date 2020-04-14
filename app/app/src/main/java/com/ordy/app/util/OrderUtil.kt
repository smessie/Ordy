package com.ordy.app.util

import android.util.Log
import com.ordy.app.api.models.Order
import com.ordy.app.api.models.OrderItem
import com.ordy.app.ui.orders.OrdersStatus
import java.util.*
import java.util.concurrent.TimeUnit

class OrderUtil {

    companion object {

        /**
         * Get the time left between 2 dates in a simple format
         * @param date Date
         */
        fun timeLeftFormat(date: Date): String {
            val difference = this.timeLeft(date)

            val days = TimeUnit.MILLISECONDS.toDays(difference)
            val hours = TimeUnit.MILLISECONDS.toHours(difference) % 24
            val minutes = TimeUnit.MILLISECONDS.toMinutes(difference) % 60
            val seconds = TimeUnit.MILLISECONDS.toSeconds(difference) % 60

            return when {
                days >= 1 -> {
                    "${days}d ${hours}h ${minutes}m"
                }
                hours >= 1 -> {
                    "${hours}h ${minutes}m"
                }
                minutes >= 1 -> {
                    "${minutes}m"
                }
                seconds >= 1 -> {
                    "${seconds}s"
                }
                else -> {
                    "Closed"
                }
            }
        }

        /**
         * Get the time left in milliseconds for a specific date.
         * @param date Date
         */
        fun timeLeft(date: Date): Long {
            return date.time - System.currentTimeMillis()
        }

        /**
         * Get the time until in milliseconds for a specific date.
         * @param date Date
         */
        fun timeUntil(date: Date): Long {
            return System.currentTimeMillis() - date.time
        }

        /**
         * Takes a list of order items and will put them in order item groups, based on equal name.
         * This way we can express quantities of order items (eg: 2x Small Pepperoni Pizza)
         * @param orderItems List with order items
         */
        fun groupItems(orderItems: List<OrderItem>): List<OrderItemGroup> {

            val itemGroups = arrayListOf<OrderItemGroup>()

            for(orderItem in orderItems) {

                // Check if the order item already has a corresponding group.
                val match = itemGroups.find { it.name.toLowerCase() == orderItem.item.name.toLowerCase() }

                if(match !== null) {
                    match.quantity += 1
                    match.items.add(orderItem)
                } else {

                    // Create a new order item group.
                    itemGroups.add(OrderItemGroup(orderItem.item.name, 1, arrayListOf(orderItem)))
                }
            }

            return itemGroups
        }

        /**
         * Takes a list of order items and will put them in order item user groups, based on equal user.
         * @param orderItems List with order items
         */
        fun userGroupItems(orderItems: List<OrderItem>): List<OrderItemUserGroup> {

            val itemUserGroups = arrayListOf<OrderItemUserGroup>()

            for(orderItem in orderItems) {

                // Check if the order item already has a corresponding group.
                val match = itemUserGroups.find { it.username == orderItem.user.username }

                if(match !== null) {
                    match.items.add(orderItem)
                } else {

                    // Create a new order item group.
                    itemUserGroups.add(OrderItemUserGroup(orderItem.user.username, arrayListOf(orderItem)))
                }
            }

            return itemUserGroups
        }

        /**
         * Filter the orders by the given orders status
         *
         * @param orders List with orders
         * @param ordersStatus Status of the orders
         */
        fun filterOrdersStatus(orders: List<Order>, ordersStatus: OrdersStatus): List<Order> {

            val archivedDelay = 2 * 60 * 60 * 1000

            return if(ordersStatus == OrdersStatus.ACTIVE) {
                orders.filter { this.timeUntil(it.deadline) < archivedDelay }
            } else {
                orders.filter { this.timeUntil(it.deadline) >= archivedDelay}
            }
        }
    }
}

data class OrderItemGroup(
    var name: String,
    var quantity: Int,
    var items: MutableList<OrderItem>
)

data class OrderItemUserGroup(
    var username: String,
    var items: MutableList<OrderItem>
)