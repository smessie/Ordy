package com.ordy.app.util

import android.util.Log
import com.ordy.app.api.models.Order
import com.ordy.app.api.models.OrderItem
import com.ordy.app.ui.orders.OrdersStatus
import java.util.*

class OrderUtil {

    companion object {

        /**
         * Get the time left between 2 dates in a simple format
         * @param date Date
         */
        fun timeLeftFormat(date: Date): String {
            val differenceSec = this.timeLeft(date)

            val hours = differenceSec / 3600
            val minutes = (differenceSec % 3600) / 60
            val seconds = differenceSec % 60

            return when {
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
         * Get the time left in seconds for a specific date.
         * @param date Date
         */
        fun timeLeft(date: Date): Long {
            val difference = date.time - Date().time

            return difference / 1000
        }

        /**
         * Get the time since in seconds for a specific date.
         * @param date Date
         */
        fun timeSince(date: Date): Long {
            val difference = Date().time - date.time

            return difference / 1000
        }

        /**
         * Takes a list of order items and will put them in order item groups, based on equal name.
         * This way we can express quantities of order items (eg: 2x Small Pepperoni Pizza)
         * @param items List with order items
         */
        fun groupItems(items: List<OrderItem>): List<OrderItemGroup> {

            val itemGroups = arrayListOf<OrderItemGroup>()

            for(item in items) {

                // Check if the order item already has a corresponding group.
                val match = itemGroups.find { it.name.toLowerCase() == item.name.toLowerCase() }

                if(match !== null) {
                    match.quantity += 1
                    match.items.add(item)
                } else {

                    // Create a new order item group.
                    itemGroups.add(OrderItemGroup(item.name, 1, arrayListOf(item)))
                }
            }

            return itemGroups
        }

        /**
         * Takes a list of order items and will put them in order item user groups, based on equal user.
         * @param items List with order items
         */
        fun userGroupItems(items: List<OrderItem>): List<OrderItemUserGroup> {

            val itemUserGroups = arrayListOf<OrderItemUserGroup>()

            for(item in items) {

                // Check if the order item already has a corresponding group.
                val match = itemUserGroups.find { it.username == item.user.username }

                if(match !== null) {
                    match.items.add(item)
                } else {

                    // Create a new order item group.
                    itemUserGroups.add(OrderItemUserGroup(item.user.username, arrayListOf(item)))
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

            val archivedDelay = 12 * 60 * 60;

            return if(ordersStatus == OrdersStatus.ACTIVE) {
                orders.filter { this.timeSince(it.deadline) < archivedDelay}
            } else {
                orders.filter { this.timeSince(it.deadline) >= archivedDelay}
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