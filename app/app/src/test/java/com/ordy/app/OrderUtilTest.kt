package com.ordy.app

import com.github.javafaker.Faker
import com.ordy.app.api.models.*
import com.ordy.app.ui.orders.OrdersStatus
import com.ordy.app.util.OrderUtil.Companion.filterOrdersStatus
import com.ordy.app.util.OrderUtil.Companion.groupItems
import com.ordy.app.util.OrderUtil.Companion.userGroupItems
import org.junit.Test
import org.junit.Assert.*
import java.util.concurrent.TimeUnit

/**
 * Tests for the OrderUtil
 */
class OrderUtilTest {

    private val faker = Faker()

    /**
     * Check if an empty list as parameter returns empty list
     */
    @Test
    fun `groupItems should return empty list when parameter is empty`() {
        assertEquals(groupItems(arrayListOf<OrderItem>()).size, 0)
    }

    /**
     * Check if orders are split into 3 groups
     */
    @Test
    fun `Should be split in 3 groups by item-name when there are 3 different items`() {
        val testItems = arrayListOf<Item>()

        // Make sure there are three different items
        while (testItems.size < 3) {
            val newFood = faker.food().dish()
            val filtered = testItems.filter {it.name == newFood}
            if (filtered.isEmpty()) {
                testItems.add(Item(faker.number().randomDigit(), newFood))
            }
        }

        val testUser1 = User(faker.number().randomDigit(), faker.name().username())
        val itemsPerItem = arrayListOf<OrderItem>()
        val randomAmount = faker.number().numberBetween(4, 50)

        for (i in 0..randomAmount) {
            itemsPerItem.add(OrderItem(faker.number().randomDigit(), testItems[i % 3], faker.food().ingredient(), testUser1))
        }

        val orderItemsPerItem = groupItems(itemsPerItem)
        assertEquals(orderItemsPerItem.size, 3)
    }

    /**
     * Check if testItem1 was ordered 3 times
     */
    @Test
    fun `Item 0 should be ordered 3 times`() {
        val testItems = arrayListOf<Item>()

        while (testItems.size < 3) {
            val newFood = faker.food().dish()
            val filtered = testItems.filter {it.name == newFood}
            if (filtered.isEmpty()) {
                testItems.add(Item(faker.number().randomDigit(), newFood))
            }
        }

        val testUser1 = User(faker.number().randomDigit(), faker.name().username())
        val itemsPerItem = arrayListOf<OrderItem>()

        for (i in 0..2) {
            itemsPerItem.add(OrderItem(faker.number().randomDigit(), testItems[0], faker.food().ingredient(), testUser1))
        }

        for (i in 0..1) {
            itemsPerItem.add(OrderItem(faker.number().randomDigit(), testItems[1], faker.food().ingredient(), testUser1))
        }

        itemsPerItem.add(OrderItem(faker.number().randomDigit(), testItems[2], faker.food().ingredient(), testUser1))

        val orderItemsPerItem = groupItems(itemsPerItem)
        assertEquals(orderItemsPerItem[0].quantity, 3)
    }

    /**
     * Check if testItem2 was ordered 2 times
     */
    @Test
    fun `Item 1 should be ordered 2 times`() {
        val testItems = arrayListOf<Item>()

        while (testItems.size < 3) {
            val newFood = faker.food().dish()
            val filtered = testItems.filter {it.name == newFood}
            if (filtered.isEmpty()) {
                testItems.add(Item(faker.number().randomDigit(), newFood))
            }
        }

        val testUser1 = User(faker.number().randomDigit(), faker.name().username())
        val itemsPerItem = arrayListOf<OrderItem>()

        for (i in 0..2) {
            itemsPerItem.add(OrderItem(faker.number().randomDigit(), testItems[0], faker.food().ingredient(), testUser1))
        }

        for (i in 0..1) {
            itemsPerItem.add(OrderItem(faker.number().randomDigit(), testItems[1], faker.food().ingredient(), testUser1))
        }

        itemsPerItem.add(OrderItem(faker.number().randomDigit(), testItems[2], faker.food().ingredient(), testUser1))

        val orderItemsPerItem = groupItems(itemsPerItem)
        assertEquals(orderItemsPerItem[1].quantity, 2)
    }

    /**
     * Check if testItem3 was ordered 1 time
     */
    @Test
    fun `Item 2 should be ordered 1 time`() {
        val testItems = arrayListOf<Item>()

        while (testItems.size < 3) {
            val newFood = faker.food().dish()
            val filtered = testItems.filter {it.name == newFood}
            if (filtered.isEmpty()) {
                testItems.add(Item(faker.number().randomDigit(), newFood))
            }
        }

        val testUser1 = User(faker.number().randomDigit(), faker.name().username())
        val itemsPerItem = arrayListOf<OrderItem>()

        for (i in 0..2) {
            itemsPerItem.add(OrderItem(faker.number().randomDigit(), testItems[0], faker.food().ingredient(), testUser1))
        }

        for (i in 0..1) {
            itemsPerItem.add(OrderItem(faker.number().randomDigit(), testItems[1], faker.food().ingredient(), testUser1))
        }

        itemsPerItem.add(OrderItem(faker.number().randomDigit(), testItems[2], faker.food().ingredient(), testUser1))

        val orderItemsPerItem = groupItems(itemsPerItem)
        assertEquals(orderItemsPerItem[2].quantity, 1)
    }

    /**
     * Check if an empty list as parameter returns empty list
     */
    @Test
    fun `userGroupItems should return empty list when param is empty list`() {
        assertEquals(userGroupItems(arrayListOf<OrderItem>()).size, 0)
    }

    /**
     * Check if first test user still has 3 orders to his name
     */
    @Test
    fun `Test user 0 should have 3 orders`() {
        val testItems = arrayListOf<Item>()

        // Create fake items to choose from
        for (i in 0..faker.number().numberBetween(4, 40)) {
            testItems.add(Item(faker.number().randomDigit(), faker.food().dish()))
        }

        val itemsAmount = testItems.size

        val userList = arrayListOf<User>()

        // Make sure there are 3 different users
        while (userList.size < 3) {
            val fakeName = faker.name().firstName()
            val filtered = userList.filter { it.username == fakeName }
            if (filtered.isEmpty()) {
                userList.add(User(faker.number().randomDigit(), fakeName))
            }
        }

        val itemsPerUser = arrayListOf<OrderItem>()

        for (i in 0..2) {
            itemsPerUser.add(
                OrderItem(
                    faker.number().randomDigit(),
                    testItems[faker.number().numberBetween(0, itemsAmount - 1)],
                    faker.food().ingredient(),
                    userList[0]
                )
            )
        }

        for (i in 0..1) {
            itemsPerUser.add(
                OrderItem(
                    faker.number().randomDigit(),
                    testItems[faker.number().numberBetween(0, itemsAmount - 1)],
                    faker.food().ingredient(),
                    userList[1]
                )
            )
        }

        itemsPerUser.add(
            OrderItem(
                faker.number().randomDigit(),
                testItems[faker.number().numberBetween(0, itemsAmount - 1)],
                faker.food().ingredient(),
                userList[2]
            )
        )

        val orderItemsPerUser = userGroupItems(itemsPerUser)
        assertEquals(orderItemsPerUser[0].items.size, 3)
    }

    /**
     * Check if second test user still has 2 orders to his name
     */
    @Test
    fun `Test user 1 should have 2 orders`() {
        val testItems = arrayListOf<Item>()

        for (i in 0..faker.number().numberBetween(4, 40)) {
            testItems.add(Item(faker.number().randomDigit(), faker.food().dish()))
        }

        val itemsAmount = testItems.size

        val userList = arrayListOf<User>()

        while (userList.size < 3) {
            val fakeName = faker.name().firstName()
            val filtered = userList.filter { it.username == fakeName }
            if (filtered.isEmpty()) {
                userList.add(User(faker.number().randomDigit(), fakeName))
            }
        }

        val itemsPerUser = arrayListOf<OrderItem>()

        for (i in 0..2) {
            itemsPerUser.add(
                OrderItem(
                    faker.number().randomDigit(),
                    testItems[faker.number().numberBetween(0, itemsAmount - 1)],
                    faker.food().ingredient(),
                    userList[0]
                )
            )
        }

        for (i in 0..1) {
            itemsPerUser.add(
                OrderItem(
                    faker.number().randomDigit(),
                    testItems[faker.number().numberBetween(0, itemsAmount - 1)],
                    faker.food().ingredient(),
                    userList[1]
                )
            )
        }

        itemsPerUser.add(
            OrderItem(
                faker.number().randomDigit(),
                testItems[faker.number().numberBetween(0, itemsAmount - 1)],
                faker.food().ingredient(),
                userList[2]
            )
        )

        val orderItemsPerUser = userGroupItems(itemsPerUser)
        assertEquals(orderItemsPerUser[1].items.size, 2)
    }

    /**
     * Check if third test user still has 1 order to his name
     */
    @Test
    fun `Test user 2 should have 1 order`() {
        val testItems = arrayListOf<Item>()

        for (i in 0..faker.number().numberBetween(4, 40)) {
            testItems.add(Item(faker.number().randomDigit(), faker.food().dish()))
        }

        val itemsAmount = testItems.size

        val userList = arrayListOf<User>()

        while (userList.size < 3) {
            val fakeName = faker.name().firstName()
            val filtered = userList.filter { it.username == fakeName }
            if (filtered.isEmpty()) {
                userList.add(User(faker.number().randomDigit(), fakeName))
            }
        }

        val itemsPerUser = arrayListOf<OrderItem>()

        for (i in 0..2) {
            itemsPerUser.add(
                OrderItem(
                    faker.number().randomDigit(),
                    testItems[faker.number().numberBetween(0, itemsAmount - 1)],
                    faker.food().ingredient(),
                    userList[0]
                )
            )
        }

        for (i in 0..1) {
            itemsPerUser.add(
                OrderItem(
                    faker.number().randomDigit(),
                    testItems[faker.number().numberBetween(0, itemsAmount - 1)],
                    faker.food().ingredient(),
                    userList[1]
                )
            )
        }

        itemsPerUser.add(
            OrderItem(
                faker.number().randomDigit(),
                testItems[faker.number().numberBetween(0, itemsAmount - 1)],
                faker.food().ingredient(),
                userList[2]
            )
        )

        val orderItemsPerUser = userGroupItems(itemsPerUser)
        assertEquals(orderItemsPerUser[2].items.size, 1)
    }

    /**
     * Check if there is only one order without expired deadline
     */
    @Test
    fun `Should be 1 active order`() {
        // Create fake data needed to create orders
        val testUser1 = User(faker.number().randomDigit(), faker.name().username())
        val testUser2 = User(faker.number().randomDigit(), faker.name().username())

        val testGroup = Group(faker.number().randomDigit(), faker.name().name(), testUser1)
        val testCuisine = Cuisine(faker.number().randomDigit(), faker.name().name(), arrayListOf<Item>())

        val testLatitude = faker.number().randomDouble(5, -90, 90)
        val testLongitude = faker.number().randomDouble(5, -180, 180)
        val testLocation = Location(faker.number().randomDigit(), faker.name().name(), testLatitude, testLongitude, faker.address().fullAddress(), faker.bool().bool(), testCuisine)

        val orderList = arrayListOf<Order>()
        val activeId = faker.number().randomDigit()

        // Make orders with deadlines far into the past and future
        orderList.add(Order(activeId, faker.date().future(90000, 10000, TimeUnit.DAYS), faker.name().name(), testGroup, testLocation, testUser2))
        orderList.add(Order(faker.number().randomDigit(), faker.date().past(90000, 10000, TimeUnit.DAYS), faker.name().name(), testGroup, testLocation, testUser2))

        val filteredListActive = filterOrdersStatus(orderList, OrdersStatus.ACTIVE)
        assertEquals(filteredListActive.size, 1)
    }

    /**
     * Check if there is only one order with expired deadline
     */
    @Test
    fun `Should be 1 archived order`() {
        val testUser1 = User(faker.number().randomDigit(), faker.name().username())
        val testUser2 = User(faker.number().randomDigit(), faker.name().username())

        val testGroup = Group(faker.number().randomDigit(), faker.name().name(), testUser1)
        val testCuisine = Cuisine(faker.number().randomDigit(), faker.name().name(), arrayListOf<Item>())

        val testLatitude = faker.number().randomDouble(5, -90, 90)
        val testLongitude = faker.number().randomDouble(5, -180, 180)
        val testLocation = Location(faker.number().randomDigit(), faker.name().name(), testLatitude, testLongitude, faker.address().fullAddress(), faker.bool().bool(), testCuisine)

        val orderList = arrayListOf<Order>()
        val archivedId = faker.number().randomDigit()
        orderList.add(Order(faker.number().randomDigit(), faker.date().future(90000, 10000, TimeUnit.DAYS), faker.name().name(), testGroup, testLocation, testUser2))
        orderList.add(Order(archivedId, faker.date().past(90000, 10000, TimeUnit.DAYS), faker.name().name(), testGroup, testLocation, testUser2))

        val filteredListArchived = filterOrdersStatus(orderList, OrdersStatus.ARCHIVED)
        assertEquals(filteredListArchived.size, 1)
    }

    /**
     * Check if correct order was chosen as active
     */
    @Test
    fun `Order should be active when deadline is not expired`() {
        val testUser1 = User(faker.number().randomDigit(), faker.name().username())
        val testUser2 = User(faker.number().randomDigit(), faker.name().username())

        val testGroup = Group(faker.number().randomDigit(), faker.name().name(), testUser1)
        val testCuisine = Cuisine(faker.number().randomDigit(), faker.name().name(), arrayListOf<Item>())

        // Make fake location
        val testLatitude = faker.number().randomDouble(5, -90, 90)
        val testLongitude = faker.number().randomDouble(5, -180, 180)
        val testLocation = Location(faker.number().randomDigit(), faker.name().name(), testLatitude, testLongitude, faker.address().fullAddress(), faker.bool().bool(), testCuisine)

        val orderList = arrayListOf<Order>()
        val activeId = faker.number().randomDigit()

        // Make orders with deadlines far into the past and future
        orderList.add(Order(activeId, faker.date().future(90000, 10000, TimeUnit.DAYS), faker.name().name(), testGroup, testLocation, testUser2))
        orderList.add(Order(faker.number().randomDigit(), faker.date().past(90000, 10000, TimeUnit.DAYS), faker.name().name(), testGroup, testLocation, testUser2))

        val filteredListActive = filterOrdersStatus(orderList, OrdersStatus.ACTIVE)
        assertEquals(filteredListActive.get(0).id, activeId)
    }

    /**
     * Check if correct order was chosen as archived
     */
    @Test
    fun `Order should be archived when deadline expired`() {
        val testUser1 = User(faker.number().randomDigit(), faker.name().username())
        val testUser2 = User(faker.number().randomDigit(), faker.name().username())

        val testGroup = Group(faker.number().randomDigit(), faker.name().name(), testUser1)
        val testCuisine = Cuisine(faker.number().randomDigit(), faker.name().name(), arrayListOf<Item>())

        val testLatitude = faker.number().randomDouble(5, -90, 90)
        val testLongitude = faker.number().randomDouble(5, -180, 180)
        val testLocation = Location(faker.number().randomDigit(), faker.name().name(), testLatitude, testLongitude, faker.address().fullAddress(), faker.bool().bool(), testCuisine)

        val orderList = arrayListOf<Order>()
        val archivedId = faker.number().randomDigit()
        orderList.add(Order(faker.number().randomDigit(), faker.date().future(90000, 10000, TimeUnit.DAYS), faker.name().name(), testGroup, testLocation, testUser2))
        orderList.add(Order(archivedId, faker.date().past(90000, 10000, TimeUnit.DAYS), faker.name().name(), testGroup, testLocation, testUser2))

        val filteredListArchived = filterOrdersStatus(orderList, OrdersStatus.ARCHIVED)
        assertEquals(filteredListArchived[0].id, archivedId)
    }
}