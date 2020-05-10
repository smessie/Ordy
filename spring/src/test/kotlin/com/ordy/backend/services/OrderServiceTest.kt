package com.ordy.backend.services

import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.ordy.backend.database.models.*
import com.ordy.backend.database.repositories.*
import com.ordy.backend.exceptions.GenericException
import com.ordy.backend.exceptions.ThrowableList
import com.ordy.backend.services.notifications.NotificationService
import com.ordy.backend.wrappers.OrderAddItemWrapper
import com.ordy.backend.wrappers.OrderCreateWrapper
import com.ordy.backend.wrappers.OrderUpdateItemWrapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.http.HttpStatus
import java.util.*

class OrderServiceTest {
    @InjectMocks
    private lateinit var orderService: OrderService

    @Mock
    private lateinit var orderRepository: OrderRepository

    @Mock
    private lateinit var orderItemRepository: OrderItemRepository

    @Mock
    private lateinit var itemRepository: ItemRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var groupMemberRepository: GroupMemberRepository

    @Mock
    private lateinit var groupRepository: GroupRepository

    @Mock
    private lateinit var locationRepository: LocationRepository

    @Mock
    private lateinit var imageService: ImageService

    @Mock
    private lateinit var notificationService: NotificationService

    private val faker = Faker()

    private lateinit var testUser: User
    private lateinit var testGroup: Group
    private lateinit var testUserGroups: List<GroupMember>
    private lateinit var testLocation: Location
    private lateinit var testCuisine: Cuisine
    private lateinit var testOrder: Order
    private lateinit var testItem: Item
    private lateinit var testOrderItem: OrderItem

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testUser = getRandomUser()
        testGroup = getRandomGroup()
        testUserGroups = listOf(
                GroupMember(user = testUser, group = testGroup),
                GroupMember(user = testUser, group = getRandomGroup()),
                GroupMember(user = testUser, group = getRandomGroup())
        )
        testCuisine = Cuisine(name = faker.commerce().department())
        testLocation = Location(name = faker.lorem().word(), latitude = 0.0, longitude = 0.0, address = faker.address().fullAddress(),
                private = false, cuisine = testCuisine)
        testOrder = getRandomOrder()
        testItem = Item(name = faker.food().dish())
        testOrderItem = OrderItem(order = testOrder, user = testUser, item = testItem)
        testOrder.orderItems = setOf(testOrderItem)

        whenever(userRepository.findById(testUser.id)).thenReturn(Optional.of(testUser))
        whenever(groupMemberRepository.findGroupMembersByUser(testUser)).thenReturn(testUserGroups)
        whenever(orderRepository.findById(testOrder.id)).thenReturn(Optional.of(testOrder))
    }

    private fun getRandomUser(): User {
        return User(id = faker.number().numberBetween(1, 1000000), username = faker.name().username(), email = faker.internet().emailAddress(), password = faker.internet().password())
    }

    private fun getRandomGroup(): Group {
        return Group(name = giveValidGroupName(), creator = testUser)
    }

    private fun getRandomOrder(
            courier: User = testUser,
            deadline: Date = Date(Date().time + 24 * 60 * 60 * 1000),
            group: Group = testGroup,
            location: Location = testLocation): Order {

        return Order(
                courier = courier,
                deadline = deadline,
                group = group,
                location = location
        )
    }

    private fun giveValidGroupName(): String {
        val groupNameRegex = Regex("^[A-z0-9 ]+$")

        var name = faker.name().name()
        while (!groupNameRegex.matches(name)) {
            name = faker.name().name()
        }

        return name
    }

    @Test
    fun `Should return all orders a user has`() {
        val pastOrder = getRandomOrder(deadline = Date(Date().time - 24 * 60 * 60 * 1000))
        whenever(orderRepository.findAllByGroup(testGroup)).thenReturn(listOf(pastOrder, testOrder))

        val results = orderService.getOrders(testUser.id)

        assertEquals(2, results.size)
        // Extra check to see if result is sorted correctly.
        assertEquals(testOrder, results[0])
        assertEquals(pastOrder, results[1])
    }

    @Test
    fun `Should return a specific order of a user`() {
        val result = orderService.getOrder(testUser.id, testOrder.id)

        assertEquals(testOrder, result)
    }

    @Test
    fun `Should not be able to get order because the user is not part of the group from the order`() {
        whenever(groupMemberRepository.findGroupMembersByUser(testUser)).thenReturn(emptyList())

        try {
            orderService.getOrders(testUser.id)
        } catch (e: GenericException) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.code)
            assertEquals("You do not have access to this order", e.message)
        }
    }

    @Test
    fun `Should not return the order because order does not exist`() {
        whenever(orderRepository.findById(anyInt())).thenReturn(Optional.empty())

        try {
            var invalidOrderId = faker.number().numberBetween(1, 1000000)
            while (invalidOrderId == testOrder.id) {
                invalidOrderId = faker.number().numberBetween(1, 1000000)
            }
            orderService.getOrder(testUser.id, invalidOrderId)
        } catch (e: GenericException) {
            assertEquals(HttpStatus.BAD_REQUEST, e.code)
            assertEquals("Order does not exist", e.message)
        }
    }

    @Test
    fun `Should create a new order`() {
        val orderCreateWrapper = OrderCreateWrapper(
                deadline = Optional.of(Date(Date().time + 24 * 60 * 60 * 1000)),
                groupId = Optional.of(testGroup.id),
                locationId = Optional.of(testLocation.id),
                customLocationName = Optional.empty()
        )

        whenever(groupRepository.findById(testGroup.id)).thenReturn(Optional.of(testGroup))
        whenever(locationRepository.findById(anyInt())).thenReturn(Optional.of(testLocation))
        whenever(orderRepository.saveAndFlush<Order>(any())).thenReturn(testOrder)

        val newOrder = orderService.createOrder(testUser.id, orderCreateWrapper)

        assertNotNull(newOrder, "Order creation failed.")
        assertEquals(orderCreateWrapper.deadline.get(), newOrder.deadline)
        assertEquals(orderCreateWrapper.groupId.get(), newOrder.group.id)
        assertEquals(orderCreateWrapper.locationId.get(), newOrder.location.id)
    }

    @Test
    fun `Should not be able to create new order because no location was given`() {
        val orderCreateWrapper = OrderCreateWrapper(
                deadline = Optional.of(Date(Date().time + 24 * 60 * 60 * 1000)),
                groupId = Optional.of(testGroup.id),
                locationId = Optional.empty(),
                customLocationName = Optional.empty()
        )

        whenever(groupRepository.findById(testGroup.id)).thenReturn(Optional.of(testGroup))
        whenever(locationRepository.findById(anyInt())).thenReturn(Optional.of(testLocation))
        whenever(orderRepository.saveAndFlush<Order>(any())).thenReturn(testOrder)

        try {
            orderService.createOrder(testUser.id, orderCreateWrapper)
        } catch (e: ThrowableList) {
            assertEquals("Location name cannot be empty", e.inputErrors[0].message)
        }
    }

    @Test
    fun `Should add an item to the order`() {
        val orderAddItemWrapper = OrderAddItemWrapper(itemId = Optional.of(testItem.id), customItemName = Optional.empty())

        whenever(itemRepository.findById(anyInt())).thenReturn(Optional.of(testItem))
        whenever(orderItemRepository.save<OrderItem>(any())).thenReturn(OrderItem(item = testItem, user = testUser, order = testOrder))

        val orderItem = orderService.addItemOrder(testUser.id, testOrder.id, orderAddItemWrapper)

        assertNotNull(orderItem)
        assertEquals(orderAddItemWrapper.itemId.get(), orderItem.item.id)
    }

    @Test
    fun `Should not be able to add item order because no item was given`() {
        val orderAddItemWrapper = OrderAddItemWrapper(itemId = Optional.empty(), customItemName = Optional.empty())

        whenever(itemRepository.findById(anyInt())).thenReturn(Optional.empty())

        try {
            orderService.addItemOrder(testUser.id, testOrder.id, orderAddItemWrapper)
        } catch (e: ThrowableList) {
            assertEquals("Item name cannot be empty", e.inputErrors[0].message)
            assertEquals("Item name cannot be empty", e.generalErrors[0].message)
        }
    }

    @Test
    fun `Should add a custom item to the order`() {
        val orderAddItemWrapper = OrderAddItemWrapper(itemId = Optional.empty(), customItemName = Optional.of(faker.food().dish()))

        whenever(itemRepository.save<Item>(any())).thenReturn(Item(name = orderAddItemWrapper.customItemName.get()))
        whenever(orderItemRepository.save<OrderItem>(any())).thenReturn(OrderItem(item = testItem, user = testUser, order = testOrder))

        val orderItem = orderService.addItemOrder(testUser.id, testOrder.id, orderAddItemWrapper)

        assertNotNull(orderItem)
        assertEquals(orderAddItemWrapper.customItemName.get(), orderItem.item.name)
    }

    @Test
    fun `Should update an item of an order`() {
        val orderUpdateItemWrapper = OrderUpdateItemWrapper(comment = Optional.of(faker.lorem().characters(5, 20)))

        whenever(orderItemRepository.findById(testOrderItem.id)).thenReturn(Optional.of(testOrderItem))

        orderService.updateItemOrder(testUser.id, testOrder.id, testOrderItem.id, orderUpdateItemWrapper)

        verify(orderItemRepository).save<OrderItem>(any())
    }

    @Test
    fun `Should not be able to update the comment because comment is empty`() {
        val orderUpdateItemWrapper = OrderUpdateItemWrapper(comment = Optional.empty())

        whenever(orderItemRepository.findById(testOrderItem.id)).thenReturn(Optional.of(testOrderItem))

        try {
            orderService.updateItemOrder(testUser.id, testOrder.id, testOrderItem.id, orderUpdateItemWrapper)
        } catch (e: ThrowableList) {
            assertEquals("Comment cannot be null", e.inputErrors[0].message)
        }

        verify(orderItemRepository, never()).save<OrderItem>(any())
    }

    @Test
    fun `Should delete an orderItem`() {
        whenever(orderItemRepository.findById(testOrderItem.id)).thenReturn(Optional.of(testOrderItem))

        orderService.deleteItemOrder(testUser.id, testOrder.id, testOrderItem.id)

        verify(orderItemRepository).delete(any())
    }

    @Test
    fun `Should not be able to delete orderItem because the order has no orderItems`() {
        testOrder.orderItems = emptySet()
        whenever(orderItemRepository.findById(testOrderItem.id)).thenReturn(Optional.of(testOrderItem))

        try {
            orderService.deleteItemOrder(testUser.id, testOrder.id, testOrderItem.id)
        } catch (e: ThrowableList) {
            assertEquals("Order item is not linked to the order.", e.generalErrors[0].message)
        }
    }
}