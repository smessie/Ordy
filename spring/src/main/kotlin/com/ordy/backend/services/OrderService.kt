package com.ordy.backend.services

import com.ordy.backend.database.models.Location
import com.ordy.backend.database.models.Order
import com.ordy.backend.database.models.User
import com.ordy.backend.database.repositories.*
import com.ordy.backend.exceptions.GenericException
import com.ordy.backend.exceptions.ThrowableList
import com.ordy.backend.wrappers.OrderCreateWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class OrderService(
        @Autowired val orderRepository: OrderRepository,
        @Autowired val userRepository: UserRepository,
        @Autowired val groupMemberRepository: GroupMemberRepository,
        @Autowired val groupRepository: GroupRepository,
        @Autowired val locationRepository: LocationRepository
) {

    /**
     * Get a list of orders for a given user.
     * @param user User
     */
    fun getOrders(userId: Int): List<Order> {
        var user = userRepository.findById(userId).get()
        var groups = groupMemberRepository.findGroupMembersByUser(user).map { it.group }

        return groups.flatMap { orderRepository.findAllByGroup(it) }
    }

    /**
     * Get an order by id.
     * @param user User
     */
    fun getOrder(userId: Int, orderId: Int): Order {
        var user = userRepository.findById(userId).get()
        val order = orderRepository.findById(orderId)
        var groups = groupMemberRepository.findGroupMembersByUser(user).map { it.group }


        // Validate that the order exists
        if(!order.isPresent) {
            throw GenericException(HttpStatus.UNAUTHORIZED, "Order does not exist")
        }

        // Validate that the user is part of the group that owns the order
        if(!groups.contains(order.get().group)) {
            throw GenericException(HttpStatus.UNAUTHORIZED, "You do not have access to this order")
        }

        return order.get()
    }

    /**
     * Create an order for a given user.
     * @param user User
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
                throwableList.addPropertyException("locationId", "Location does not exist")
                throwableList.ifNotEmpty { throw throwableList }
            }

            location = locationDb.get()
        }

        // Otherwise: create a new custom location
        else {

            // Validate that the custom location name is valid.
            if(!orderCreate.customLocationName.isPresent) {
                throwableList.addPropertyException("customLocationName", "Location name cannot be empty")
                throwableList.ifNotEmpty { throw throwableList }
            }

            location = Location(
                    name = orderCreate.customLocationName.get(),
                    private = true,
                    latitude = null,
                    longitude = null,
                    cuisine = null
            )
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
}