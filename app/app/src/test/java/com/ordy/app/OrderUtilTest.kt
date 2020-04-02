package com.ordy.app

import com.ordy.app.api.models.OrderItem
import com.ordy.app.api.models.Item
import com.ordy.app.api.models.User
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
     *  Test the groupItems function
     */
    @Test
    fun test_groupItems() {
        // Make random items and a test-user
        val testUser = User(1, "Ieben")
        val testItem1 = Item(1, "Pizza Calzone")
        val testItem2 = Item(2, "Spring rolls")
        val testItem3 = Item(3, "Small fries")

        // Fill list with orders
        val orderItems = arrayListOf<OrderItem>()
        orderItems.add(OrderItem(1, testItem1, "Extra olives", testUser))
        orderItems.add(OrderItem(2, testItem1, "Extra cheese", testUser))
        orderItems.add(OrderItem(3, testItem1, "Extra meat", testUser))
        orderItems.add(OrderItem(4, testItem2, "With hot sauce", testUser))
        orderItems.add(OrderItem(5, testItem2, "extra noodles", testUser))
        orderItems.add(OrderItem(6, testItem3, "With salt", testUser))

        // Check if an empty list as parameter returns empty list
        assertEquals(groupItems(arrayListOf<OrderItem>()).size, 0)

        val orderItemGroup = groupItems(orderItems)
        // Checks if the quantities and names are correct
        assertEquals(orderItemGroup.size, 3)
        assertEquals(orderItemGroup.get(0).quantity, 3)
        assertEquals(orderItemGroup.get(1).quantity, 2)
        assertEquals(orderItemGroup.get(2).quantity, 1)

        assertEquals(orderItemGroup.get(0).name, "Pizza Calzone")
        assertEquals(orderItemGroup.get(1).name, "Spring rolls")
        assertEquals(orderItemGroup.get(2).name, "Small fries")

        assertEquals(orderItemGroup.get(0).items.size, orderItemGroup.get(0).quantity)
        assertEquals(orderItemGroup.get(1).items.size, orderItemGroup.get(1).quantity)
        assertEquals(orderItemGroup.get(2).items.size, orderItemGroup.get(2).quantity)
    }

    /**
     * Test the userGroupItems function
     */
    @Test
    fun test_userGroupItems() {
        val testUser1 = User(1, "Ieben")
        val testUser2 = User(2, "Maarten")
        val testUser3 = User(3, "Francis")
        val testUser4 = User(4, "Stijn")

        val testItem1 = Item(1, "Pizza Calzone")
        val testItem2 = Item(2, "Spring rolls")
        val testItem3 = Item(3, "Small fries")

        // Check if an empty list as parameter returns empty list
        assertEquals(userGroupItems(arrayListOf<OrderItem>()).size, 0)

        // Make random OrderItems
        val orderItems = arrayListOf<OrderItem>()
        orderItems.add(OrderItem(1, testItem1, "Extra olives", testUser1))
        orderItems.add(OrderItem(2, testItem1, "Extra cheese", testUser1))
        orderItems.add(OrderItem(3, testItem1, "Extra meat", testUser1))
        orderItems.add(OrderItem(4, testItem2, "With hot sauce", testUser2))
        orderItems.add(OrderItem(5, testItem2, "extra noodles", testUser2))
        orderItems.add(OrderItem(6, testItem3, "With salt", testUser3))
        orderItems.add(OrderItem(6, testItem2, "Extra hot", testUser4))

        val orderItemUserGroup = userGroupItems(orderItems)

        assertEquals(orderItemUserGroup.size, 4)

        assertEquals(orderItemUserGroup.get(0).username, testUser1.username)
        assertEquals(orderItemUserGroup.get(1).username, testUser2.username)
        assertEquals(orderItemUserGroup.get(2).username, testUser3.username)
        assertEquals(orderItemUserGroup.get(3).username, testUser4.username)

        assertEquals(orderItemUserGroup.get(0).items.size, 3)
        assertEquals(orderItemUserGroup.get(1).items.size, 2)
        assertEquals(orderItemUserGroup.get(2).items.size, 1)
        assertEquals(orderItemUserGroup.get(3).items.size, 1)

    }

}