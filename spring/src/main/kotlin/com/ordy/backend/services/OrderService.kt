package com.ordy.backend.services

import com.ordy.backend.database.models.Item
import com.ordy.backend.database.models.Location
import com.ordy.backend.database.models.Order
import com.ordy.backend.database.models.OrderItem
import com.ordy.backend.database.repositories.*
import com.ordy.backend.exceptions.GenericException
import com.ordy.backend.exceptions.ThrowableList
import com.ordy.backend.services.notifications.NotificationService
import com.ordy.backend.services.notifications.NotificationType
import com.ordy.backend.wrappers.OrderAddItemWrapper
import com.ordy.backend.wrappers.OrderCreateWrapper
import com.ordy.backend.wrappers.OrderUpdateItemWrapper
import org.apache.tomcat.util.http.fileupload.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class OrderService(
        @Autowired val orderRepository: OrderRepository,
        @Autowired val orderItemRepository: OrderItemRepository,
        @Autowired val itemRepository: ItemRepository,
        @Autowired val userRepository: UserRepository,
        @Autowired val groupMemberRepository: GroupMemberRepository,
        @Autowired val groupRepository: GroupRepository,
        @Autowired val locationRepository: LocationRepository,
        @Autowired val imageService: ImageService,
        @Autowired val notificationService: NotificationService
) {

    @Value("\${ORDY_DOMAIN_NAME}")
    private lateinit var domainName: String

    /**
     * Function to add a "/" if the domainName doesn't have one
     */

    fun getDomainName(): String {
        if (!domainName.endsWith("/")) {
            domainName = "$domainName/"
        }
        return domainName
    }

    /**
     * Get a list of orders for a given user.
     */
    fun getOrders(userId: Int): List<Order> {
        val user = userRepository.findById(userId).get()
        val groups = groupMemberRepository.findGroupMembersByUser(user).map { it.group }

        return groups.flatMap { orderRepository.findAllByGroup(it) }.sortedBy { it.deadline }.reversed()
    }

    /**
     * Get an order by id.
     */
    fun getOrder(userId: Int, orderId: Int): Order {
        val user = userRepository.findById(userId).get()
        val order = orderRepository.findById(orderId)
        val groups = groupMemberRepository.findGroupMembersByUser(user).map { it.group }


        // Validate that the order exists
        if (!order.isPresent) {
            throw GenericException(HttpStatus.BAD_REQUEST, "Order does not exist")
        }

        // Validate that the user is part of the group that owns the order
        if (!groups.contains(order.get().group)) {
            throw GenericException(HttpStatus.UNAUTHORIZED, "You do not have access to this order")
        }

        return order.get()
    }

    /**
     * Create an order for a given user.
     */
    fun createOrder(userId: Int, orderCreate: OrderCreateWrapper): Order {
        val user = userRepository.findById(userId).get()
        val throwableList = ThrowableList()

        // Validate that the deadline is present.
        if (!orderCreate.deadline.isPresent) {
            throwableList.addPropertyException("deadline", "Deadline cannot be empty")
        }

        // Validate that the group id is present.
        if (!orderCreate.groupId.isPresent) {
            throwableList.addPropertyException("groupId", "Group cannot be empty")
        }

        throwableList.ifNotEmpty { throw throwableList }

        val group = groupRepository.findById(orderCreate.groupId.get())

        // Validate that the group is valid.
        if (!group.isPresent) {
            throwableList.addPropertyException("groupId", "Group does not exist")
        }

        throwableList.ifNotEmpty { throw throwableList }

        lateinit var location: Location

        // When the location is present: use the location
        if (orderCreate.locationId.isPresent) {
            val locationDb = locationRepository.findById(orderCreate.locationId.get())

            // Validate that the location is valid.
            if (!locationDb.isPresent) {
                throw throwableList.also { it.addPropertyException("locationId", "Location does not exist") }
            }

            location = locationDb.get()
        }

        // Otherwise: create a new custom location
        else {

            // Validate that the custom location name is valid.
            if (!orderCreate.customLocationName.isPresent) {
                throw throwableList.also { it.addPropertyException("customLocationName", "Location name cannot be empty") }
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


        // notify all users in group except creator
        notificationService.sendNotificationAsync(
                users = groupMemberRepository.findGroupMembersByGroup(group.get())
                        .map { it.user }
                        .toMutableList()
                        .also { it.remove(user) },
                content = notificationService.createNotificationContent(
                        title = "New order in ${group.get().name}",
                        subtitle = "${user.username} has created a new order for ${location.name}",
                        detail = "<b>Group: </b>${group.get().name}\n<b>Location: </b>${location.name}\n<b>Courier: </b>${user.username}",
                        summary = "New Order",
                        type = NotificationType.ORDER_CREATE,
                        extra = mapOf(
                                "orderId" to order.id.toString(),
                                "notificationDeadline" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ").format(order.deadline)
                        )
                )
        )

        return order
    }

    /**
     * Add an item to an order.
     */
    fun addItemOrder(userId: Int, orderId: Int, orderAddItem: OrderAddItemWrapper): OrderItem {
        val throwableList = ThrowableList()
        val user = userRepository.findById(userId).get()
        val order = this.getOrder(userId, orderId)

        lateinit var item: Item

        // When the itemId is present: use the item
        if (orderAddItem.itemId.isPresent) {
            val itemDb = itemRepository.findById(orderAddItem.itemId.get())

            // Validate that the item is valid.
            if (!itemDb.isPresent) {
                throw throwableList.also {
                    it.addPropertyException("itemId", "Item does not exist")
                    it.addGenericException("Item does not exist")
                }
            }

            item = itemDb.get()
        } else {

            // Validate that the custom item name is valid.
            if (!orderAddItem.customItemName.isPresent) {
                throw throwableList.also {
                    it.addPropertyException("customItemName", "Item name cannot be empty")
                    it.addGenericException("Item name cannot be empty")
                }
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
        val order = this.getOrder(userId, orderId)
        val orderItemOptional = orderItemRepository.findById(orderItemId)

        // Validate that the order item exists.
        if (!orderItemOptional.isPresent) {
            throw throwableList.also { it.addGenericException("Order item does not exist.") }
        }

        // Validate that the order item is linked to the given order.
        if (!order.orderItems.contains(orderItemOptional.get())) {
            throw throwableList.also { it.addGenericException("Order item is not linked to the order.") }
        }

        // Validate that the comment exists.
        if (!orderUpdateItem.comment.isPresent) {
            throw throwableList.also { it.addPropertyException("comment", "Comment cannot be null") }
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
        val order = this.getOrder(userId, orderId)
        val orderItemOptional = orderItemRepository.findById(orderItemId)

        // Validate that the order item exists.
        if (!orderItemOptional.isPresent) {
            throw throwableList.also { it.addGenericException("Order item does not exist.") }
        }

        // Validate that the order item is linked to the given order.
        if (!order.orderItems.contains(orderItemOptional.get())) {
            throw throwableList.also { it.addGenericException("Order item is not linked to the order.") }
        }

        // Delete the order item
        orderItemRepository.delete(orderItemOptional.get())
    }

    /**
     * upload the picture of bill in the database
     */

    fun uploadBillImage(userId: Int, orderId: Int, image: MultipartFile) {
        val throwableList = ThrowableList()

        val order = this.getOrder(userId, orderId)

        if (order.courier.id != userId) {
            throw throwableList.also { it.addGenericException("Adding a bill picture is only possible if you are the courier.") }
        }

        if (image.contentType.isNullOrBlank() || !image.contentType!!.contains("image")) {
            // The content-type of the bill picture has to be image/{type}
            throw throwableList.also { it.addGenericException("Failed to upload bill image. Please try again.") }
        }

        // if the order already had a bill picture, replace it with the new picture and delete the old one
        if (order.image !== null) {
            val previousBillImgId = order.image!!.id
            order.image = null // unlink the image from the order to be able to delete the image
            orderRepository.save(order) // for safety reasons
            imageService.deleteImage(previousBillImgId)
        }

        val newImage = imageService.saveImage(image, order)
        order.image = newImage
        order.billUrl = getDomainName() + "orders/$orderId/bill"
        orderRepository.save(order)

        val uploader = userRepository.findById(userId).get() // user is authenticated so no check is needed

        // notify all users in group except the uploader
        notificationService.sendNotificationAsync(
                users = groupMemberRepository.findGroupMembersByGroup(order.group)
                        .map { it.user }
                        .toMutableList()
                        .also { it.remove(uploader) },
                content = notificationService.createNotificationContent(
                        title = "New bill in ${order.group.name}",
                        subtitle = "${uploader.username} has uploaded a bill",
                        detail = "<b>Group: </b>${order.group.name}\n<b>Location: </b>${order.location.name}\n<b>Courier: </b>${order.courier.username}",
                        summary = "Bill added",
                        type = NotificationType.ORDER_BILL,
                        extra = mapOf("orderId" to order.id.toString())
                )
        )
    }

    /**
     * get Image with given id
     */

    fun getBillImage(userId: Int, orderId: Int, request: HttpServletRequest, response: HttpServletResponse) {
        val throwableList = ThrowableList()

        val order = this.getOrder(userId, orderId)

        if (order.image === null) {
            throw throwableList.also { it.addGenericException("This order has no bill picture.") }
        }

        val image = imageService.getImage(order.image!!.id, request)
        val byteArray = ByteArray(image.image.size)
        var i = 0

        for (wrappedByte in image.image) {
            byteArray[i++] = wrappedByte
        }

        val inputStream: InputStream = ByteArrayInputStream(byteArray)
        IOUtils.copy(inputStream, response.outputStream)
    }
}