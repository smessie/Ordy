package com.ordy.app.util

import com.github.javafaker.Faker
import com.ordy.app.api.models.*
import com.ordy.app.ui.orders.OrdersStatus
import com.ordy.app.util.OrderUtil.Companion.filterOrdersStatus
import com.ordy.app.util.OrderUtil.Companion.groupItems
import com.ordy.app.util.OrderUtil.Companion.userGroupItems
import org.junit.Assert.assertEquals
import org.junit.Test
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
        assertEquals(groupItems(emptyList()).size, 0)
    }

    /**
     * Check if items are ordered the correct number of times
     */
    @Test
    fun `Number of instances per item should be the same after grouping`() {
        val testItems = arrayListOf<Item>()

        // Make sure we have 3 items
        while (testItems.size < 3) {
            val newFood = faker.food().dish()
            val filtered = testItems.filter {it.name == newFood}
            if (filtered.isEmpty()) {
                testItems.add(Item(faker.number().randomDigit(), newFood))
            }
        }

        val testUser1 = User(faker.number().randomDigit(), faker.name().username(), faker.internet().emailAddress())
        val itemsPerItem = arrayListOf<OrderItem>()

        for (i in 0..2) {
            itemsPerItem.add(OrderItem(faker.number().randomDigit(), testItems[0], faker.food().ingredient(), testUser1))
        }

        for (i in 0..1) {
            itemsPerItem.add(OrderItem(faker.number().randomDigit(), testItems[1], faker.food().ingredient(), testUser1))
        }

        itemsPerItem.add(OrderItem(faker.number().randomDigit(), testItems[2], faker.food().ingredient(), testUser1))

        val orderItemsPerItem = groupItems(itemsPerItem)

        assertEquals(orderItemsPerItem.size, 3)

        assertEquals(orderItemsPerItem[0].quantity, 3)
        assertEquals(orderItemsPerItem[1].quantity, 2)
        assertEquals(orderItemsPerItem[2].quantity, 1)
    }

    /**
     * Check if an empty list as parameter returns empty list
     */
    @Test
    fun `userGroupItems should return empty list when param is empty list`() {
        assertEquals(userGroupItems(emptyList()).size, 0)
    }

    /**
     * Check if all users have the correct number of items
     */
    @Test
    fun `Users should have same number of items after grouping them`() {
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
                userList.add(User(faker.number().randomDigit(), fakeName, faker.internet().emailAddress()))
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

        assertEquals(orderItemsPerUser.size, 3)

        assertEquals(orderItemsPerUser[0].items.size, 3)
        assertEquals(orderItemsPerUser[1].items.size, 2)
        assertEquals(orderItemsPerUser[2].items.size, 1)
    }

    /**
     * Check if orders are sorted correctly based on being active/archived
     */
    @Test
    fun `Should be 1 archived and 1 active order, both with specific id's`() {
        val testUser1 = User(faker.number().randomDigit(), faker.name().username(), faker.internet().emailAddress())
        val testUser2 = User(faker.number().randomDigit(), faker.name().username(), faker.internet().emailAddress())

        val testGroup = Group(faker.number().randomDigit(), faker.name().name(), testUser1)
        val testCuisine = Cuisine(faker.number().randomDigit(), faker.name().name(), emptyList())

        val testLatitude = faker.number().randomDouble(5, -90, 90)
        val testLongitude = faker.number().randomDouble(5, -180, 180)
        val testLocation = Location(faker.number().randomDigit(), faker.name().name(), testLatitude, testLongitude, faker.address().fullAddress(), faker.bool().bool(), testCuisine)

        val orderList = arrayListOf<Order>()
        val archivedId = faker.number().randomDigit()
        val activeId = faker.number().randomDigit()

        // Make orders with deadlines far into the future/past
        orderList.add(Order(activeId, faker.date().future(90000, 10000, TimeUnit.DAYS), faker.name().name(), testGroup, testLocation, testUser2))
        orderList.add(Order(archivedId, faker.date().past(90000, 10000, TimeUnit.DAYS), faker.name().name(), testGroup, testLocation, testUser2))

        val filteredListArchived = filterOrdersStatus(orderList, OrdersStatus.ARCHIVED)
        val filteredListActive = filterOrdersStatus(orderList, OrdersStatus.ACTIVE)

        assertEquals(filteredListActive.size, 1)
        assertEquals(filteredListArchived.size, 1)

        assertEquals(filteredListActive[0].id, activeId)
        assertEquals(filteredListArchived[0].id, archivedId)
    }
}