package com.ordy.app

import com.ordy.app.api.models.OrderItem
import com.ordy.app.api.models.Item
import com.ordy.app.api.models.User
import com.ordy.app.util.OrderUtil.Companion.groupItems
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

        val orderItemGroup = groupItems(orderItems)
        // Checks if the quantities and names are correct
        assertEquals(orderItemGroup.size, 3)
        assertEquals(orderItemGroup.get(0).quantity, 3)
        assertEquals(orderItemGroup.get(1).quantity, 2)
        assertEquals(orderItemGroup.get(2).quantity, 1)
        assertEquals(orderItemGroup.get(0).name, "Pizza Calzone")
        assertEquals(orderItemGroup.get(1).name, "Spring rolls")
        assertEquals(orderItemGroup.get(2).name, "Small fries")

    }
}