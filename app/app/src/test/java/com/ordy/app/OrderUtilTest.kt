package com.ordy.app

import com.ordy.app.api.models.*
import com.ordy.app.ui.orders.OrdersStatus
import com.ordy.app.util.OrderItemGroup
import com.ordy.app.util.OrderItemUserGroup
import com.ordy.app.util.OrderUtil.Companion.filterOrdersStatus
import com.ordy.app.util.OrderUtil.Companion.groupItems
import com.ordy.app.util.OrderUtil.Companion.userGroupItems
import java.util.*
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import kotlin.collections.ArrayList

/**
 * Tests for the OrderUtil
 */
class OrderUtilTest {

    private lateinit var testItem1: Item
    private lateinit var testItem2: Item
    private lateinit var testItem3: Item

    private lateinit var testUser1: User
    private lateinit var testUser2: User
    private lateinit var testUser3: User
    private lateinit var testUser4: User

    private lateinit var itemsPerUser: ArrayList<OrderItem>
    private lateinit var orderItemsPerUser: List<OrderItemUserGroup>
    private lateinit var itemsPerItem: ArrayList<OrderItem>
    private lateinit var orderItemsPerItem: List<OrderItemGroup>

    private lateinit var testGroup: Group
    private lateinit var testLocation: Location
    private lateinit var testCuisine: Cuisine

    private lateinit var orderList: ArrayList<Order>
    private lateinit var filteredListActive: List<Order>
    private lateinit var filteredListArchived: List<Order>

    /**
     * Initialise extra variables needed for tests
     */
    @Before
    fun setup() {
        testItem1 = Item(1, "Pizza Calzone")
        testItem2 = Item(2, "Spring rolls")
        testItem3 = Item(3, "Small fries")

        testUser1 = User(1, "Ieben")
        testUser2 = User(2, "Maarten")
        testUser3 = User(3, "Francis")
        testUser4 = User(4, "Stijn")

        itemsPerUser = arrayListOf<OrderItem>()

        itemsPerUser.add(OrderItem(1, testItem1, "Extra olives", testUser1))
        itemsPerUser.add(OrderItem(2, testItem1, "Extra cheese", testUser1))
        itemsPerUser.add(OrderItem(3, testItem1, "Extra meat", testUser1))
        itemsPerUser.add(OrderItem(4, testItem2, "With hot sauce", testUser2))
        itemsPerUser.add(OrderItem(5, testItem2, "extra noodles", testUser2))
        itemsPerUser.add(OrderItem(6, testItem3, "With salt", testUser3))
        itemsPerUser.add(OrderItem(6, testItem2, "Extra hot", testUser4))

        orderItemsPerUser = userGroupItems(itemsPerUser)

        itemsPerItem = arrayListOf<OrderItem>()

        itemsPerItem.add(OrderItem(1, testItem1, "Extra olives", testUser1))
        itemsPerItem.add(OrderItem(2, testItem1, "Extra cheese", testUser1))
        itemsPerItem.add(OrderItem(3, testItem1, "Extra meat", testUser1))
        itemsPerItem.add(OrderItem(4, testItem2, "With hot sauce", testUser1))
        itemsPerItem.add(OrderItem(5, testItem2, "extra noodles", testUser1))
        itemsPerItem.add(OrderItem(6, testItem3, "With salt", testUser1))

        orderItemsPerItem = groupItems(itemsPerItem)

        testGroup = Group(1, "TestGroup", testUser1)
        testCuisine = Cuisine(1, "TestCuisine", arrayListOf<Item>())
        testLocation = Location(1, "TestLocation", 10.0, 10.0, "Krijgslaan 1", false, testCuisine)

        orderList = arrayListOf<Order>()
        orderList.add(Order(1, Date(1685857554619), "TestUrl", testGroup, testLocation, testUser2))
        orderList.add(Order(2, Date(1485857554619), "TestUrl", testGroup, testLocation, testUser2))

        filteredListActive = filterOrdersStatus(orderList, OrdersStatus.ACTIVE)
        filteredListArchived = filterOrdersStatus(orderList, OrdersStatus.ARCHIVED)
    }

    /**
     * Check if an empty list as parameter returns empty list
     */
    @Test
    fun whenEmptyParam_groupItemsReturnsEmpty() {
        assertEquals(groupItems(arrayListOf<OrderItem>()).size, 0)
    }

    /**
     * Check if orders are split into 3 groups
     */
    @Test
    fun orderedPerItem_shouldBeSize3() {
        assertEquals(orderItemsPerItem.size, 3)
    }

    /**
     * Check if testItem1 was ordered 3 times
     */
    @Test
    fun item1_shouldBeOrdered_3Times() {
        assertEquals(orderItemsPerItem.get(0).quantity, 3)
    }

    /**
     * Check if testItem2 was ordered 2 times
     */
    @Test
    fun item2_shouldBeOrdered_2Times() {
        assertEquals(orderItemsPerItem.get(1).quantity, 2)
    }

    /**
     * Check if testItem3 was ordered 1 time
     */
    @Test
    fun item3_shouldBeOrdered_1Time() {
        assertEquals(orderItemsPerItem.get(2).quantity, 1)
    }

    /**
     * Check if added item still has name "Pizza Calzone"
     */
    @Test
    fun firstItemName_isPizzaCalzone() {
        assertEquals(orderItemsPerItem.get(0).name, "Pizza Calzone")
    }

    /**
     * Check if added item still has name "Spring rolls"
     */
    @Test
    fun firstItemName_isSpringRolls() {
        assertEquals(orderItemsPerItem.get(1).name, "Spring rolls")
    }

    /**
     * Check if added item still has name "Small fries"
     */
    @Test
    fun firstItemName_isSmallFries() {
        assertEquals(orderItemsPerItem.get(2).name, "Small fries")
    }

    /**
     * Check if an empty list as parameter returns empty list
     */
    @Test
    fun emptyParam_forUserGroupItems_returnsEmpty() {
        assertEquals(userGroupItems(arrayListOf<OrderItem>()).size, 0)
    }

    /**
     * Check if testUser1 still has 3 orders to his name
     */
    @Test
    fun testUser1_has_3orders() {
        assertEquals(orderItemsPerUser.get(0).items.size, 3)
    }

    /**
     * Check if testUser2 still has 2 orders to his name
     */
    @Test
    fun testUser2_has_2orders() {
        assertEquals(orderItemsPerUser.get(1).items.size, 2)
    }

    /**
     * Check if testUser3 still has 1 order to his name
     */
    @Test
    fun testUser3_has_1order() {
        assertEquals(orderItemsPerUser.get(2).items.size, 1)
    }

    /**
     * Check if testUser4 still has 1 order to his name
     */
    @Test
    fun testUser4_has_1order() {
        assertEquals(orderItemsPerUser.get(3).items.size, 1)
    }

    /**
     * Check if there is only one order without expired deadline
     */
    @Test
    fun shouldBe1_activeOrder() {
        assertEquals(filteredListActive.size, 1)
    }

    /**
     * Check if there is only one order with expired deadline
     */
    @Test
    fun shouldBe1_archivedOrder() {
        assertEquals(filteredListArchived.size, 1)
    }

    /**
     * Check if order 1 was correctly chosen as active
     */
    @Test
    fun order1_shouldBe_active() {
        assertEquals(filteredListActive.get(0).id, 1)
    }

    /**
     * Check if order 2 was correctly chosen as archived
     */
    @Test
    fun order2_shouldBe_archived() {
        assertEquals(filteredListArchived.get(0).id, 2)
    }
}