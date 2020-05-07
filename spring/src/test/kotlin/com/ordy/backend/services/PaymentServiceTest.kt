package com.ordy.backend.services

import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.whenever
import com.ordy.backend.database.models.*
import com.ordy.backend.database.repositories.OrderItemRepository
import com.ordy.backend.database.repositories.OrderRepository
import com.ordy.backend.database.repositories.UserRepository
import com.ordy.backend.exceptions.ThrowableList
import com.ordy.backend.services.notifications.NotificationService
import com.ordy.backend.wrappers.PaymentUpdateWrapper
import com.ordy.backend.wrappers.PaymentWrapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentServiceTest {

    var faker = Faker()

    @InjectMocks
    private lateinit var paymentService: PaymentService

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var orderRepository: OrderRepository

    @Mock
    private lateinit var orderItemRepository: OrderItemRepository

    @Mock
    private lateinit var notificationService: NotificationService

    private lateinit var creator: User
    private lateinit var courier: User
    private lateinit var group: Group
    private lateinit var cuisine: Cuisine
    private lateinit var location: Location
    private lateinit var futureDate: Date
    private lateinit var pastDate: Date

    @BeforeAll
    fun setup() {
        MockitoAnnotations.initMocks(this)

        creator = User(id = faker.number().numberBetween(1, 1000000), username = faker.name().username(), email = faker.internet().emailAddress(), password = faker.internet().password())
        courier = User(id = faker.number().numberBetween(1, 1000000), username = faker.name().username(), email = faker.internet().emailAddress(), password = faker.internet().password())
        group = Group(name = faker.name().name(), creator = creator)
        cuisine = Cuisine(name = faker.commerce().department())
        location = Location(name = faker.lorem().word(), latitude = 0.0, longitude = 0.0, address = faker.address().fullAddress(),
                private = false, cuisine = cuisine)
        futureDate = Date(Date().time + 24 * 60 * 60 * 1000) // Add one day to now.
        pastDate = Date(Date().time - 24 * 60 * 60 * 1000) // Subtract one day from now.
    }

    /**
     * As the order is still active, it should not be included in the current debtors list.
     */
    @Test
    fun `when getting debtors With one active order Should return empty list`() {
        whenever(userRepository.findById(anyInt())).thenReturn(Optional.of(courier))

        val order = Order(deadline = futureDate, group = group, courier = courier, location = location)
        val orderItems = mutableSetOf<OrderItem>()
        for (i in 0..5) {
            val debtor = User(id = faker.number().numberBetween(1, 1000000), username = faker.name().username(), email = faker.internet().emailAddress(), password = faker.internet().password())
            orderItems.add(OrderItem(paid = false, order = order, item = Item(name = faker.food().dish()), user = debtor))
        }
        order.orderItems = orderItems

        val orders: List<Order> = listOf(order)
        whenever(orderRepository.findOrdersByCourier(courier)).thenReturn(orders)

        Assertions.assertEquals(paymentService.getDebtors(courier.id), emptyList<PaymentWrapper>(), "Only active orders does not return empty debtors list.")
    }

    /**
     * As the order is no longer active, it should be included in the current debtors list.
     */
    @Test
    fun `when getting debtors With one archived order Should return that order in list`() {
        whenever(userRepository.findById(anyInt())).thenReturn(Optional.of(courier))

        val order = Order(deadline = pastDate, group = group, courier = courier, location = location)
        val orderItems = mutableSetOf<OrderItem>()
        val payments = mutableListOf<PaymentWrapper>()
        val usedUserIds = mutableSetOf(courier.id)
        for (i in 0..5) {
            // Make sure no ID is used twice because the tested function groups on ID's
            var id = faker.number().numberBetween(1, 1000000)
            while (usedUserIds.contains(id)) {
                id = faker.number().numberBetween(1, 1000000)
            }
            usedUserIds.add(id)

            val debtor = User(id = id, username = faker.name().username(), email = faker.internet().emailAddress(), password = faker.internet().password())
            val orderItem = OrderItem(paid = false, order = order, item = Item(id = faker.number().numberBetween(1, 1000000), name = faker.food().dish()), user = debtor)

            orderItems.add(orderItem)
            payments.add(PaymentWrapper(user = debtor, order = order, orderItems = setOf(orderItem)))
        }
        order.orderItems = orderItems

        val orders: List<Order> = listOf(order)
        whenever(orderRepository.findOrdersByCourier(courier)).thenReturn(orders)

        val retrievedPayments = paymentService.getDebtors(courier.id)

        for (i in payments.indices) {
            Assertions.assertTrue(paymentWrapperIsEqual(payments[i], retrievedPayments[i]), "Only archived orders does not return correct debtors list.")
        }
    }

    @Test
    fun `when getting debtors With two orderItems from same debtor user Should group together`() {
        whenever(userRepository.findById(anyInt())).thenReturn(Optional.of(courier))

        val order = Order(deadline = pastDate, group = group, courier = courier, location = location)

        val debtor = User(id = faker.number().numberBetween(1, 1000000), username = faker.name().username(), email = faker.internet().emailAddress(), password = faker.internet().password())
        val orderItem1 = OrderItem(paid = false, order = order, item = Item(id = faker.number().numberBetween(1, 1000000), name = faker.food().dish()), user = debtor)
        val orderItem2 = OrderItem(paid = false, order = order, item = Item(id = faker.number().numberBetween(1, 1000000), name = faker.food().sushi()), user = debtor)
        val orderItems = mutableSetOf(orderItem1, orderItem2)

        order.orderItems = orderItems

        val orders: List<Order> = listOf(order)
        whenever(orderRepository.findOrdersByCourier(courier)).thenReturn(orders)

        val payments = listOf(PaymentWrapper(user = debtor, order = order, orderItems = orderItems))
        val retrievedPayments = paymentService.getDebtors(courier.id)

        for (i in payments.indices) {
            Assertions.assertTrue(paymentWrapperIsEqual(payments[i], retrievedPayments[i]), "OrderItems from same debtor in one order does not group together.")
        }
    }

    @Test
    fun `when getting debts With multiple orderItems Should only return orderItem of user`() {
        val order = Order(deadline = pastDate, group = group, courier = courier, location = location)

        val debtor1 = User(id = faker.number().numberBetween(1, 1000000), username = faker.name().username(), email = faker.internet().emailAddress(), password = faker.internet().password())
        val debtor2 = User(id = faker.number().numberBetween(1, 1000000), username = faker.name().username(), email = faker.internet().emailAddress(), password = faker.internet().password())
        val orderItem1 = OrderItem(paid = false, order = order, item = Item(id = faker.number().numberBetween(1, 1000000), name = faker.food().dish()), user = debtor1)
        val orderItem2 = OrderItem(paid = false, order = order, item = Item(id = faker.number().numberBetween(1, 1000000), name = faker.food().sushi()), user = debtor2)
        val orderItems = mutableSetOf(orderItem1, orderItem2)

        order.orderItems = orderItems

        whenever(userRepository.findById(anyInt())).thenReturn(Optional.of(debtor1))
        whenever(orderItemRepository.findOrderItemsByUser(debtor1)).thenReturn(listOf(orderItem1))

        val payments = listOf(PaymentWrapper(user = courier, order = order, orderItems = setOf(orderItem1)))
        val retrievedPayments = paymentService.getDebts(debtor1.id)

        for (i in payments.indices) {
            Assertions.assertTrue(paymentWrapperIsEqual(payments[i], retrievedPayments[i]), "Getting debts does not return the single debt of the debtor.")
        }
    }

    /**
     * As the order is still active, it should not be included in the current debtors list.
     */
    @Test
    fun `when getting debts With one active order Should return empty list`() {
        val debtor = User(id = faker.number().numberBetween(1, 1000000), username = faker.name().username(), email = faker.internet().emailAddress(), password = faker.internet().password())

        whenever(userRepository.findById(anyInt())).thenReturn(Optional.of(debtor))

        val order = Order(deadline = futureDate, group = group, courier = courier, location = location)
        val orderItems = mutableSetOf<OrderItem>()
        for (i in 0..5) {
            orderItems.add(OrderItem(paid = false, order = order, item = Item(name = faker.food().dish()), user = debtor))
        }
        order.orderItems = orderItems

        whenever(orderItemRepository.findOrderItemsByUser(debtor)).thenReturn(orderItems.toList())

        Assertions.assertEquals(paymentService.getDebts(debtor.id), emptyList<PaymentWrapper>(), "Only active orders does not return empty debts list.")
    }

    /**
     * As the orderItems are already paid, it should not be included in the current debtors list.
     */
    @Test
    fun `when getting debts With only paid orderItems in archived order Should return empty list`() {
        val debtor = User(id = faker.number().numberBetween(1, 1000000), username = faker.name().username(), email = faker.internet().emailAddress(), password = faker.internet().password())

        whenever(userRepository.findById(anyInt())).thenReturn(Optional.of(debtor))

        val order = Order(deadline = pastDate, group = group, courier = courier, location = location)
        val orderItems = mutableSetOf<OrderItem>()
        for (i in 0..5) {
            orderItems.add(OrderItem(paid = true, order = order, item = Item(name = faker.food().dish()), user = debtor))
        }
        order.orderItems = orderItems

        whenever(orderItemRepository.findOrderItemsByUser(debtor)).thenReturn(orderItems.toList())

        Assertions.assertEquals(paymentService.getDebts(debtor.id), emptyList<PaymentWrapper>(), "Only active orders does not return empty debts list.")
    }

    private fun paymentWrapperIsEqual(paymentWrapper1: PaymentWrapper, paymentWrapper2: PaymentWrapper): Boolean {
        return paymentWrapper1.order == paymentWrapper2.order && paymentWrapper1.orderItems == paymentWrapper2.orderItems && paymentWrapper1.user == paymentWrapper2.user
    }

    @Test
    fun `when patching order payed As non courier Should fail`() {
        val randomUser = User(id = faker.number().numberBetween(1, 1000000), username = faker.name().username(), email = faker.internet().emailAddress(), password = faker.internet().password())
        val order = Order(deadline = pastDate, group = group, courier = courier, location = location)
        val orderItems = mutableSetOf<OrderItem>()

        val usedUserIds = mutableSetOf(courier.id)
        for (i in 0..5) {
            val userId = faker.number().numberBetween(1, 1000000)
            usedUserIds.add(userId)
            val debtor = User(id = userId, username = faker.name().username(), email = faker.internet().emailAddress(), password = faker.internet().password())
            orderItems.add(OrderItem(paid = false, order = order, item = Item(name = faker.food().dish()), user = debtor))
        }
        order.orderItems = orderItems

        whenever(orderRepository.findById(anyInt())).thenReturn(Optional.of(order))

        Assertions.assertThrows(ThrowableList::class.java) {
            paymentService.patchOrderPayed(randomUser.id, faker.number().numberBetween(1, 1000000), usedUserIds.first(), PaymentUpdateWrapper(paid = true))
        }
    }

    @Test
    fun `when patching order payed In active order Should fail`() {
        val debtor = User(id = faker.number().numberBetween(1, 1000000), username = faker.name().username(), email = faker.internet().emailAddress(), password = faker.internet().password())
        val order = Order(deadline = futureDate, group = group, courier = courier, location = location)
        val orderItems = mutableSetOf<OrderItem>()

        for (i in 0..5) {
            orderItems.add(OrderItem(paid = false, order = order, item = Item(name = faker.food().dish()), user = debtor))
        }
        order.orderItems = orderItems

        whenever(orderRepository.findById(anyInt())).thenReturn(Optional.of(order))

        Assertions.assertThrows(ThrowableList::class.java) {
            paymentService.patchOrderPayed(courier.id, faker.number().numberBetween(1, 1000000), debtor.id, PaymentUpdateWrapper(paid = true))
        }
    }

    @Test
    fun `when patching order payed With correct input Should succeed`() {
        val debtor = User(id = faker.number().numberBetween(1, 1000000), username = faker.name().username(), email = faker.internet().emailAddress(), password = faker.internet().password())
        val order = Order(deadline = pastDate, group = group, courier = courier, location = location)
        val orderItems = mutableSetOf<OrderItem>()

        for (i in 0..5) {
            orderItems.add(OrderItem(paid = false, order = order, item = Item(name = faker.food().dish()), user = debtor))
        }
        order.orderItems = orderItems

        whenever(orderRepository.findById(anyInt())).thenReturn(Optional.of(order))

        Assertions.assertDoesNotThrow {
            paymentService.patchOrderPayed(courier.id, faker.number().numberBetween(1, 1000000), debtor.id, PaymentUpdateWrapper(paid = true))
        }
    }
}