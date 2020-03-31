package com.ordy.backend.services

import com.ordy.backend.database.models.*
import com.ordy.backend.database.repositories.*
import com.ordy.backend.exceptions.GenericException
import com.ordy.backend.exceptions.ThrowableList
import com.ordy.backend.wrappers.OrderAddItemWrapper
import com.ordy.backend.wrappers.OrderCreateWrapper
import com.ordy.backend.wrappers.OrderUpdateItemWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrderService(
        @Autowired val orderRepository: OrderRepository,
        @Autowired val orderItemRepository: OrderItemRepository,
        @Autowired val itemRepository: ItemRepository,
        @Autowired val userRepository: UserRepository,
        @Autowired val groupMemberRepository: GroupMemberRepository,
        @Autowired val groupRepository: GroupRepository,
        @Autowired val locationRepository: LocationRepository
) {

    /**
     * Get a list of orders for a given user.
     */
    fun getOrders(userId: Int): List<Order> {
        var user = userRepository.findById(userId).get()
        var groups = groupMemberRepository.findGroupMembersByUser(user).map { it.group }

        return groups.flatMap { orderRepository.findAllByGroup(it) }.sortedBy { it.deadline }.reversed()
    }

    /**
     * Get an order by id.
     */
    fun getOrder(userId: Int, orderId: Int): Order {
        var user = userRepository.findById(userId).get()
        val order = orderRepository.findById(orderId)
        var groups = groupMemberRepository.findGroupMembersByUser(user).map { it.group }


        // Validate that the order exists
        if(!order.isPresent) {
            throw GenericException(HttpStatus.BAD_REQUEST, "Order does not exist")
        }

        // Validate that the user is part of the group that owns the order
        if(!groups.contains(order.get().group)) {
            throw GenericException(HttpStatus.UNAUTHORIZED, "You do not have access to this order")
        }

        return order.get()
    }

    /**
     * Create an order for a given user.
     */
    fun createOrder(userId: Int, orderCreate: OrderCreateWrapper): Order {
        var user = userRepository.findById(userId).get()
        val throwableList = ThrowableList()

        // Validate that the deadline is present.
        if(!orderCreate.deadline.isPresent) {
            throwableList.addPropertyException("deadline", "Deadline cannot be empty")
        }

        // Validate that the group id is present.
        if(!orderCreate.groupId.isPresent) {
            throwableList.addPropertyException("groupId", "Group cannot be empty")
        }

        throwableList.ifNotEmpty { throw throwableList }

        val group = groupRepository.findById(orderCreate.groupId.get())

        // Validate that the group is valid.
        if(!group.isPresent) {
            throwableList.addPropertyException("groupId", "Group does not exist")
        }

        throwableList.ifNotEmpty { throw throwableList }

        lateinit var location: Location

        // When the location is present: use the location
        if(orderCreate.locationId.isPresent) {
            val locationDb = locationRepository.findById(orderCreate.locationId.get())

            // Validate that the location is valid.
            if(!locationDb.isPresent) {
                throw throwableList.also{it.addPropertyException("locationId", "Location does not exist")}
            }

            location = locationDb.get()
        }

        // Otherwise: create a new custom location
        else {

            // Validate that the custom location name is valid.
            if(!orderCreate.customLocationName.isPresent) {
                throw throwableList.also{it.addPropertyException("customLocationName", "Location name cannot be empty")}
            }

            location = Location(
                    name = orderCreate.customLocationName.get(),
                    private = true,
                    latitude = null,
                    longitude = null,
                    cuisine = null
            )
            locationRepository.save(location)
        }

        val order = Order(
                deadline = orderCreate.deadline.get(),
                group = group.get(),
                location = location,
                courier = user
        )

        orderRepository.save(order)
        return order
    }

    /**
     * Add an item to an order.
     */
    fun addItemOrder(userId: Int, orderId: Int, orderAddItem: OrderAddItemWrapper): OrderItem {
        val throwableList = ThrowableList()
        var user = userRepository.findById(userId).get()
        var order = this.getOrder(userId, orderId)

        lateinit var item: Item

        // When the itemId is present: use the item
        if(orderAddItem.itemId.isPresent) {
            val itemDb = itemRepository.findById(orderAddItem.itemId.get())

            // Validate that the item is valid.
            if(!itemDb.isPresent) {
                throw throwableList.also{it.addPropertyException("itemId", "Item does not exist")}
            }

            item = itemDb.get()
        }

        else {

            // Validate that the custom item name is valid.
            if(!orderAddItem.customItemName.isPresent) {
                throw throwableList.also{it.addPropertyException("customItemName", "Item name cannot be empty")}
            }

            // Create a new item.
            item = Item(
                    name = orderAddItem.customItemName.get()
            )

            itemRepository.save(item)
        }

        // Create the order item
        val orderItem = OrderItem(
                order = order,
                item = item,
                user = user
        )

        orderItemRepository.save(orderItem)

        return orderItem
    }

    /**
     * Update an item of an order
     */
    fun updateItemOrder(userId: Int, orderId: Int, orderItemId: Int, orderUpdateItem: OrderUpdateItemWrapper) {
        val throwableList = ThrowableList()
        var order = this.getOrder(userId, orderId)
        var orderItemOptional = orderItemRepository.findById(orderItemId)

        // Validate that the order item exists.
        if(!orderItemOptional.isPresent) {
            throw throwableList.also{it.addGenericException("Order item does not exist")}
        }

        // Validate that the order item is linked to the given order.
        if(!order.orderItems.contains(orderItemOptional.get())) {
            throw throwableList.also{it.addGenericException("Order item is not linked to the order")}
        }

        // Validate that the comment exists.
        if(!orderUpdateItem.comment.isPresent) {
            throw throwableList.also{it.addPropertyException("comment", "Comment cannot be null")}
        }

        // Update the order item
        val orderItem = orderItemOptional.get()
        orderItem.comment = orderUpdateItem.comment.get()
        orderItemRepository.save(orderItem)
    }

    /**
     * Delete an item of an order.
     */
    fun deleteItemOrder(userId: Int, orderId: Int, orderItemId: Int) {
        val throwableList = ThrowableList()
        var order = this.getOrder(userId, orderId)
        var orderItemOptional = orderItemRepository.findById(orderItemId)

        // Validate that the order item exists.
        if(!orderItemOptional.isPresent) {
            throw throwableList.also{it.addGenericException("Order item does not exist")}
        }

        // Validate that the order item is linked to the given order.
        if(!order.orderItems.contains(orderItemOptional.get())) {
            throw throwableList.also{it.addGenericException("Order item is not linked to the order")}
        }

        // Delete the order item
        orderItemRepository.delete(orderItemOptional.get())
    }
}