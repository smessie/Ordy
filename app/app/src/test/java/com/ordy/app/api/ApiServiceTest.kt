package com.ordy.app.api

import com.google.gson.Gson
import com.ordy.app.api.models.*
import com.ordy.app.api.models.actions.OrderAddItem
import com.ordy.app.api.models.actions.OrderCreate
import com.ordy.app.api.models.actions.OrderUpdate
import com.ordy.app.api.models.actions.OrderUpdateItem
import com.ordy.app.api.models.actions.enums.OrderUpdateAction
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.text.SimpleDateFormat

class ApiServiceTest {

    private lateinit var server: MockWebServer
    private lateinit var apiService: ApiService

    /**
     * Constants
     */
    private val TEST_DEADLINE =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ")
            .parse("2020-04-26T16:00:27+0000")!!

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()

        apiService = ApiServiceProvider()
            .builder()
            .baseUrl(server.url("/"))
            .build()
            .create(ApiService::class.java)
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    /**
     * Get the contents of a file as string.
     */
    private fun getFile(filename: String): String {
        val file = this.javaClass.classLoader?.getResourceAsStream(filename)

        return file?.bufferedReader()?.readText() ?: ""
    }

    /**
     * @method POST
     * @endpoint "/orders"
     */
    @Test
    fun `Order should be created`() {

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
            setBody(getFile("responses/order_create.json"))
        }
        server.enqueue(response)

        // Body
        val body = OrderCreate(
            groupId = 1,
            locationId = 1,
            customLocationName = "",
            deadline = TEST_DEADLINE
        )

        // API Call
        apiService.createOrder(body).test().apply {
            assertNoErrors()
            assertComplete()

            assertValue(
                Order(
                    id = 1,
                    deadline = TEST_DEADLINE,
                    group = Group(
                        id = 1,
                        name = "Zeus",
                        creator = User(
                            id = 1,
                            username = "Elon Musk"
                        )
                    ),
                    courier = User(
                        id = 1,
                        username = "Elon Musk"
                    ),
                    location = Location(
                        id = 1,
                        name = "Emily",
                        address = "Zwijnaardse Steenweg"
                    )
                )
            )
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("POST", request.method)
        Assert.assertEquals("/orders", request.path)
    }

    /**
     * @method GET
     * @endpoint "/orders/{id}"
     */
    @Test
    fun `Order should be returned`() {
        val orderId = 1

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
            setBody(getFile("responses/order_get.json"))
        }
        server.enqueue(response)

        // API Call
        apiService.order(orderId).test().apply {
            assertNoErrors()
            assertComplete()

            assertValue(
                Order(
                    id = 1,
                    deadline = TEST_DEADLINE,
                    billUrl = "https://ordy.ga/test.png",
                    group = Group(
                        id = 1,
                        name = "Zeus",
                        creator = User(
                            id = 1,
                            username = "Elon Musk",
                            email = "elonmusk@spacex.com"
                        )
                    ),
                    courier = User(
                        id = 1,
                        username = "Elon Musk",
                        email = "elonmusk@spacex.com"
                    ),
                    location = Location(
                        id = 1,
                        name = "'t Blauw Kotje",
                        latitude = 51.0311638,
                        longitude = 3.7134913,
                        address = "Somewhere over the rainbow"
                    ),
                    orderItems = mutableListOf()
                )
            )
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("GET", request.method)
        Assert.assertEquals("/orders/$orderId", request.path)
    }

    /**
     * @method GET
     * @endpoint "/user/orders"
     */
    @Test
    fun `User orders should be returned`() {

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
            setBody(getFile("responses/order_user_list.json"))
        }
        server.enqueue(response)

        // API Call
        apiService.userOrders().test().apply {
            assertNoErrors()
            assertComplete()

            assertValue(
                listOf(
                    Order(
                        id = 1,
                        deadline = TEST_DEADLINE,
                        group = Group(
                            id = 1,
                            name = "Zeus",
                            creator = User(
                                id = 1,
                                username = "Elon Musk"
                            )
                        ),
                        courier = User(
                            id = 1,
                            username = "Elon Musk"
                        ),
                        location = Location(
                            id = 1,
                            name = "'t Blauw Kotje",
                            address = "Somewhere over the rainbow"
                        )
                    )
                )
            )
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("GET", request.method)
        Assert.assertEquals("/user/orders", request.path)
    }

    /**
     * @method PATCH
     * @endpoint "/user/orders/{id}"
     */
    @Test
    fun `User orders should be updated`() {
        val orderId = 1

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
        }
        server.enqueue(response)

        // Body
        val body = OrderUpdate(
            action = OrderUpdateAction.VOLUNTEER
        )

        // API Call
        apiService.userUpdateOrders(orderId, body).test().apply {
            assertNoErrors()
            assertComplete()
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("PATCH", request.method)
        Assert.assertEquals("/user/orders/$orderId", request.path)

        JSONAssert.assertEquals(
            getFile("responses/body_order_user_update.json"),
            Gson().toJson(body),
            false
        )
    }

    /**
     * @method POST
     * @endpoint "/user/orders/{orderId}/items"
     */
    @Test
    fun `Item should be added to order`() {
        val orderId = 1

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
            setBody(getFile("responses/order_user_additem.json"))
        }
        server.enqueue(response)

        // Body
        val body = OrderAddItem(
            itemId = 1,
            customItemName = "Test"
        )

        // API Call
        apiService.userAddOrderItem(orderId, body).test().apply {
            assertNoErrors()
            assertComplete()

            assertValue(
                OrderItem(
                    id = 1,
                    comment = "Extra salt",
                    paid = false,
                    item = Item(
                        id = 10,
                        name = "Test"
                    ),
                    user = User(
                        id = 1,
                        username = "Elon Musk",
                        email = "elonmusk@spacex.com"
                    )
                )
            )
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("POST", request.method)
        Assert.assertEquals("/user/orders/$orderId/items", request.path)

        JSONAssert.assertEquals(
            getFile("responses/body_order_user_additem.json"),
            Gson().toJson(body),
            false
        )
    }

    /**
     * @method PATCH
     * @endpoint "/user/orders/{orderId}/items/{orderItemId}"
     */
    @Test
    fun `Order item should be updated`() {
        val orderId = 1
        val orderItemId = 2

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
        }
        server.enqueue(response)

        // Body
        val body = OrderUpdateItem(
            comment = "Extra salt"
        )

        // API Call
        apiService.userUpdateOrderItem(orderId, orderItemId, body).test().apply {
            assertNoErrors()
            assertComplete()
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("PATCH", request.method)
        Assert.assertEquals("/user/orders/$orderId/items/$orderItemId", request.path)

        JSONAssert.assertEquals(
            getFile("responses/body_order_user_updateitem.json"),
            Gson().toJson(body),
            false
        )
    }

    /**
     * @method DELETE
     * @endpoint "/user/orders/{orderId}/items/{orderItemId}"
     */
    @Test
    fun `Order item should be deleted`() {
        val orderId = 1
        val orderItemId = 2

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
        }
        server.enqueue(response)

        // API Call
        apiService.userDeleteOrderItem(orderId, orderItemId).test().apply {
            assertNoErrors()
            assertComplete()
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("DELETE", request.method)
        Assert.assertEquals("/user/orders/$orderId/items/$orderItemId", request.path)
    }
}