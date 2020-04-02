package com.ordy.app

import com.ordy.app.api.models.*
import com.ordy.app.ui.orders.OrdersStatus
import com.ordy.app.util.OrderUtil.Companion.filterOrdersStatus
import com.ordy.app.util.OrderUtil.Companion.groupItems
import com.ordy.app.util.OrderUtil.Companion.userGroupItems
import java.util.*
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests for the OrderUtil
 */
class OrderUtilTest {

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
    fun `Should be split in 3 groups per item-name`() {
        val testItem1 = Item(1, "Pizza Calzone")
        val testItem2 = Item(2, "Spring rolls")
        val testItem3 = Item(3, "Small fries")

        val testUser1 = User(1, "Ieben")
        val itemsPerItem = arrayListOf<OrderItem>()

        itemsPerItem.add(OrderItem(1, testItem1, "Extra olives", testUser1))
        itemsPerItem.add(OrderItem(2, testItem1, "Extra cheese", testUser1))
        itemsPerItem.add(OrderItem(3, testItem1, "Extra meat", testUser1))
        itemsPerItem.add(OrderItem(4, testItem2, "With hot sauce", testUser1))
        itemsPerItem.add(OrderItem(5, testItem2, "extra noodles", testUser1))
        itemsPerItem.add(OrderItem(6, testItem3, "With salt", testUser1))

        val orderItemsPerItem = groupItems(itemsPerItem)
        assertEquals(orderItemsPerItem.size, 3)
    }

    /**
     * Check if testItem1 was ordered 3 times
     */
    @Test
    fun `Item 1 should be ordered 3 times`() {
        val testItem1 = Item(1, "Pizza Calzone")
        val testItem2 = Item(2, "Spring rolls")
        val testItem3 = Item(3, "Small fries")

        val testUser1 = User(1, "Ieben")
        val itemsPerItem = arrayListOf<OrderItem>()

        itemsPerItem.add(OrderItem(1, testItem1, "Extra olives", testUser1))
        itemsPerItem.add(OrderItem(2, testItem1, "Extra cheese", testUser1))
        itemsPerItem.add(OrderItem(3, testItem1, "Extra meat", testUser1))
        itemsPerItem.add(OrderItem(4, testItem2, "With hot sauce", testUser1))
        itemsPerItem.add(OrderItem(5, testItem2, "extra noodles", testUser1))
        itemsPerItem.add(OrderItem(6, testItem3, "With salt", testUser1))

        val orderItemsPerItem = groupItems(itemsPerItem)
        assertEquals(orderItemsPerItem.get(0).quantity, 3)
    }

    /**
     * Check if testItem2 was ordered 2 times
     */
    @Test
    fun `Item 2 should be ordered 2 times`() {
        val testItem1 = Item(1, "Pizza Calzone")
        val testItem2 = Item(2, "Spring rolls")
        val testItem3 = Item(3, "Small fries")

        val testUser1 = User(1, "Ieben")
        val itemsPerItem = arrayListOf<OrderItem>()

        itemsPerItem.add(OrderItem(1, testItem1, "Extra olives", testUser1))
        itemsPerItem.add(OrderItem(2, testItem1, "Extra cheese", testUser1))
        itemsPerItem.add(OrderItem(3, testItem1, "Extra meat", testUser1))
        itemsPerItem.add(OrderItem(4, testItem2, "With hot sauce", testUser1))
        itemsPerItem.add(OrderItem(5, testItem2, "extra noodles", testUser1))
        itemsPerItem.add(OrderItem(6, testItem3, "With salt", testUser1))

        val orderItemsPerItem = groupItems(itemsPerItem)
        assertEquals(orderItemsPerItem.get(1).quantity, 2)
    }

    /**
     * Check if testItem3 was ordered 1 time
     */
    @Test
    fun `Item 3 should be ordered 1 time`() {
        val testItem1 = Item(1, "Pizza Calzone")
        val testItem2 = Item(2, "Spring rolls")
        val testItem3 = Item(3, "Small fries")

        val testUser1 = User(1, "Ieben")
        val itemsPerItem = arrayListOf<OrderItem>()

        itemsPerItem.add(OrderItem(1, testItem1, "Extra olives", testUser1))
        itemsPerItem.add(OrderItem(2, testItem1, "Extra cheese", testUser1))
        itemsPerItem.add(OrderItem(3, testItem1, "Extra meat", testUser1))
        itemsPerItem.add(OrderItem(4, testItem2, "With hot sauce", testUser1))
        itemsPerItem.add(OrderItem(5, testItem2, "extra noodles", testUser1))
        itemsPerItem.add(OrderItem(6, testItem3, "With salt", testUser1))

        val orderItemsPerItem = groupItems(itemsPerItem)
        assertEquals(orderItemsPerItem.get(2).quantity, 1)
    }

    /**
     * Check if an empty list as parameter returns empty list
     */
    @Test
    fun `userGroupItems should return empty list if param is empty list`() {
        assertEquals(userGroupItems(arrayListOf<OrderItem>()).size, 0)
    }

    /**
     * Check if testUser1 still has 3 orders to his name
     */
    @Test
    fun `testUser1 should have 3 orders`() {
        val testItem1 = Item(1, "Pizza Calzone")
        val testItem2 = Item(2, "Spring rolls")
        val testItem3 = Item(3, "Small fries")

        val testUser1 = User(1, "Ieben")
        val testUser2 = User(2, "Maarten")
        val testUser3 = User(3, "Francis")
        val testUser4 = User(4, "Stijn")

        val itemsPerUser = arrayListOf<OrderItem>()

        itemsPerUser.add(OrderItem(1, testItem1, "Extra olives", testUser1))
        itemsPerUser.add(OrderItem(2, testItem1, "Extra cheese", testUser1))
        itemsPerUser.add(OrderItem(3, testItem1, "Extra meat", testUser1))
        itemsPerUser.add(OrderItem(4, testItem2, "With hot sauce", testUser2))
        itemsPerUser.add(OrderItem(5, testItem2, "extra noodles", testUser2))
        itemsPerUser.add(OrderItem(6, testItem3, "With salt", testUser3))
        itemsPerUser.add(OrderItem(6, testItem2, "Extra hot", testUser4))

        val orderItemsPerUser = userGroupItems(itemsPerUser)
        assertEquals(orderItemsPerUser.get(0).items.size, 3)
    }

    /**
     * Check if testUser2 still has 2 orders to his name
     */
    @Test
    fun `testUser2 should have 2 orders`() {
        val testItem1 = Item(1, "Pizza Calzone")
        val testItem2 = Item(2, "Spring rolls")
        val testItem3 = Item(3, "Small fries")

        val testUser1 = User(1, "Ieben")
        val testUser2 = User(2, "Maarten")
        val testUser3 = User(3, "Francis")
        val testUser4 = User(4, "Stijn")

        val itemsPerUser = arrayListOf<OrderItem>()

        itemsPerUser.add(OrderItem(1, testItem1, "Extra olives", testUser1))
        itemsPerUser.add(OrderItem(2, testItem1, "Extra cheese", testUser1))
        itemsPerUser.add(OrderItem(3, testItem1, "Extra meat", testUser1))
        itemsPerUser.add(OrderItem(4, testItem2, "With hot sauce", testUser2))
        itemsPerUser.add(OrderItem(5, testItem2, "extra noodles", testUser2))
        itemsPerUser.add(OrderItem(6, testItem3, "With salt", testUser3))
        itemsPerUser.add(OrderItem(6, testItem2, "Extra hot", testUser4))

        val orderItemsPerUser = userGroupItems(itemsPerUser)
        assertEquals(orderItemsPerUser.get(1).items.size, 2)
    }

    /**
     * Check if testUser3 still has 1 order to his name
     */
    @Test
    fun `testUser3 should have 1 order`() {
        val testItem1 = Item(1, "Pizza Calzone")
        val testItem2 = Item(2, "Spring rolls")
        val testItem3 = Item(3, "Small fries")

        val testUser1 = User(1, "Ieben")
        val testUser2 = User(2, "Maarten")
        val testUser3 = User(3, "Francis")
        val testUser4 = User(4, "Stijn")

        val itemsPerUser = arrayListOf<OrderItem>()

        itemsPerUser.add(OrderItem(1, testItem1, "Extra olives", testUser1))
        itemsPerUser.add(OrderItem(2, testItem1, "Extra cheese", testUser1))
        itemsPerUser.add(OrderItem(3, testItem1, "Extra meat", testUser1))
        itemsPerUser.add(OrderItem(4, testItem2, "With hot sauce", testUser2))
        itemsPerUser.add(OrderItem(5, testItem2, "extra noodles", testUser2))
        itemsPerUser.add(OrderItem(6, testItem3, "With salt", testUser3))
        itemsPerUser.add(OrderItem(6, testItem2, "Extra hot", testUser4))

        val orderItemsPerUser = userGroupItems(itemsPerUser)
        assertEquals(orderItemsPerUser.get(2).items.size, 1)
    }

    /**
     * Check if testUser4 still has 1 order to his name
     */
    @Test
    fun `testUser4 should have 1 order`() {
        val testItem1 = Item(1, "Pizza Calzone")
        val testItem2 = Item(2, "Spring rolls")
        val testItem3 = Item(3, "Small fries")

        val testUser1 = User(1, "Ieben")
        val testUser2 = User(2, "Maarten")
        val testUser3 = User(3, "Francis")
        val testUser4 = User(4, "Stijn")

        val itemsPerUser = arrayListOf<OrderItem>()

        itemsPerUser.add(OrderItem(1, testItem1, "Extra olives", testUser1))
        itemsPerUser.add(OrderItem(2, testItem1, "Extra cheese", testUser1))
        itemsPerUser.add(OrderItem(3, testItem1, "Extra meat", testUser1))
        itemsPerUser.add(OrderItem(4, testItem2, "With hot sauce", testUser2))
        itemsPerUser.add(OrderItem(5, testItem2, "extra noodles", testUser2))
        itemsPerUser.add(OrderItem(6, testItem3, "With salt", testUser3))
        itemsPerUser.add(OrderItem(6, testItem2, "Extra hot", testUser4))

        val orderItemsPerUser = userGroupItems(itemsPerUser)
        assertEquals(orderItemsPerUser.get(3).items.size, 1)
    }

    /**
     * Check if there is only one order without expired deadline
     */
    @Test
    fun `Should be 1 active order`() {
        val testUser1 = User(1, "Ieben")
        val testUser2 = User(2, "Maarten")

        val testGroup = Group(1, "TestGroup", testUser1)
        val testCuisine = Cuisine(1, "TestCuisine", arrayListOf<Item>())
        val testLocation = Location(1, "TestLocation", 10.0, 10.0, "Krijgslaan 1", false, testCuisine)

        val orderList = arrayListOf<Order>()
        orderList.add(Order(1, Date(1685857554619), "TestUrl", testGroup, testLocation, testUser2))
        orderList.add(Order(2, Date(1485857554619), "TestUrl", testGroup, testLocation, testUser2))

        val filteredListActive = filterOrdersStatus(orderList, OrdersStatus.ACTIVE)
        assertEquals(filteredListActive.size, 1)
    }

    /**
     * Check if there is only one order with expired deadline
     */
    @Test
    fun `Should be 1 archived order`() {
        val testUser1 = User(1, "Ieben")
        val testUser2 = User(2, "Maarten")

        val testGroup = Group(1, "TestGroup", testUser1)
        val testCuisine = Cuisine(1, "TestCuisine", arrayListOf<Item>())
        val testLocation = Location(1, "TestLocation", 10.0, 10.0, "Krijgslaan 1", false, testCuisine)

        val orderList = arrayListOf<Order>()
        orderList.add(Order(1, Date(1685857554619), "TestUrl", testGroup, testLocation, testUser2))
        orderList.add(Order(2, Date(1485857554619), "TestUrl", testGroup, testLocation, testUser2))

        val filteredListArchived = filterOrdersStatus(orderList, OrdersStatus.ARCHIVED)
        assertEquals(filteredListArchived.size, 1)
    }

    /**
     * Check if order 1 was correctly chosen as active
     */
    @Test
    fun `Order 1 should be active`() {
        val testUser1 = User(1, "Ieben")
        val testUser2 = User(2, "Maarten")

        val testGroup = Group(1, "TestGroup", testUser1)
        val testCuisine = Cuisine(1, "TestCuisine", arrayListOf<Item>())
        val testLocation = Location(1, "TestLocation", 10.0, 10.0, "Krijgslaan 1", false, testCuisine)

        val orderList = arrayListOf<Order>()
        orderList.add(Order(1, Date(1685857554619), "TestUrl", testGroup, testLocation, testUser2))
        orderList.add(Order(2, Date(1485857554619), "TestUrl", testGroup, testLocation, testUser2))

        val filteredListActive = filterOrdersStatus(orderList, OrdersStatus.ACTIVE)
        assertEquals(filteredListActive.get(0).id, 1)
    }

    /**
     * Check if order 2 was correctly chosen as archived
     */
    @Test
    fun `Order 2 should be archived`() {
        val testUser1 = User(1, "Ieben")
        val testUser2 = User(2, "Maarten")

        val testGroup = Group(1, "TestGroup", testUser1)
        val testCuisine = Cuisine(1, "TestCuisine", arrayListOf<Item>())
        val testLocation = Location(1, "TestLocation", 10.0, 10.0, "Krijgslaan 1", false, testCuisine)

        val orderList = arrayListOf<Order>()
        orderList.add(Order(1, Date(1685857554619), "TestUrl", testGroup, testLocation, testUser2))
        orderList.add(Order(2, Date(1485857554619), "TestUrl", testGroup, testLocation, testUser2))

        val filteredListArchived = filterOrdersStatus(orderList, OrdersStatus.ARCHIVED)
        assertEquals(filteredListArchived.get(0).id, 2)
    }
}