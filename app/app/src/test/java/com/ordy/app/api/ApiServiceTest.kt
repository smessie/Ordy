package com.ordy.app.api

import com.google.gson.Gson
import com.ordy.app.api.models.*
import com.ordy.app.api.models.actions.*
import com.ordy.app.api.models.actions.enums.OrderUpdateAction
import com.ordy.app.api.wrappers.LocationWrapper
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
     * ORDERS
     */

    /**
     * @method POST
     * @endpoint "/orders"
     */
    @Test
    fun `Order should be created`() {

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
            setBody(getFile("responses/orders/order_create.json"))
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
            setBody(getFile("responses/orders/order_get.json"))
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
            setBody(getFile("responses/orders/order_user_list.json"))
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
            getFile("responses/orders/body_order_user_update.json"),
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
            setBody(getFile("responses/orders/order_user_additem.json"))
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
            getFile("responses/orders/body_order_user_additem.json"),
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
            getFile("responses/orders/body_order_user_updateitem.json"),
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

    /**
     * LOCATIONS
     */

    /**
     * @method GET
     * @endpoint "/locations/{id}"
     */
    @Test
    fun `Location should be returned`() {
        val locationId = 1

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
            setBody(getFile("responses/locations/location_get.json"))
        }
        server.enqueue(response)

        // API Call
        apiService.location(locationId).test().apply {
            assertNoErrors()
            assertComplete()

            assertValue(
                Location(
                    id = 1,
                    name = "De Frietchalet",
                    latitude = 51.021429,
                    longitude = 4.093035,
                    address = "Diepestraat 1",
                    private = false,
                    cuisine = Cuisine(
                        id = 2,
                        name = "Fries",
                        items = mutableListOf()
                    )
                )
            )
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("GET", request.method)
        Assert.assertEquals("/locations/$locationId", request.path)
    }

    /**
     * @method GET
     * @endpoint "/locations/{id}/items"
     */
    @Test
    fun `Items should be returned`() {
        val locationId = 1

        val body = listOf(
                        Item(
                            id = 3,
                            name = "Small pack of fries"
                        ),
                        Item(
                            id = 4,
                            name = "Chicken nuggets"
                        ),
                        Item(
                            id = 5,
                            name = "Mayonnaise"
                        )
                    )

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
            setBody(getFile("responses/locations/location_items_get.json"))
        }
        server.enqueue(response)

        // API Call
        apiService.locationItems(locationId).test().apply {
            assertNoErrors()
            assertComplete()

            assertValue(body)
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("GET", request.method)
        Assert.assertEquals("/locations/$locationId/items", request.path)
    }

    /**
     * @method GET
     * @endpoint "/locations"
     */
    @Test
    fun `Locations with matching names should be returned`() {
        val query = "la"

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
            setBody(getFile("responses/locations/locations_filtered_get.json"))
        }
        server.enqueue(response)

        // API Call
        apiService.locations(query).test().apply {
            assertNoErrors()
            assertComplete()

            assertValue(
                mutableListOf(
                    LocationWrapper(
                         location = Location(
                             id = 4,
                             name = "La Piazza",
                             latitude = 51.031638,
                             longitude = 4.096320,
                             address = "Kerkstraat 48",
                             cuisine = Cuisine(
                                 id = 4,
                                 name = "Italian",
                                 items = mutableListOf()
                             )
                         ),
                         favorite = true
                    ),
                    LocationWrapper(
                        location = Location(
                            id = 5,
                            name = "Naxos Island",
                            latitude = 51.031604,
                            longitude = 4.097384,
                            address = "Kerkstraat 15",
                            cuisine = Cuisine(
                                id = 5,
                                name = "Greek",
                                items = mutableListOf()
                            )
                        ),
                        favorite = false
                    )
                )
            )
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("GET", request.method)
        Assert.assertEquals("/locations?q=${query}", request.path)
    }

    /**
     * @method POST
     * @endpoint "locations/{locationId}"
     */
    @Test
    fun `Location should be marked without errors`() {
        val locationId = 1

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
        }

        server.enqueue(response)

        // API Call
        apiService.markLocationAsFavorite(locationId).test().apply {
            assertNoErrors()
            assertComplete()
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("POST", request.method)
        Assert.assertEquals("/locations/$locationId", request.path)
    }

    /**
     * @method DELETE
     * @endpoint "locations/{locationId}"
     */
    @Test
    fun `Location should be unmarked without errors`() {
        val locationId = 1

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
        }

        server.enqueue(response)

        // API Call
        apiService.unMarkLocationAsFavorite(locationId).test().apply {
            assertNoErrors()
            assertComplete()
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("DELETE", request.method)
        Assert.assertEquals("/locations/$locationId", request.path)
    }

    /**
     * USERS
     */

    /**
     * @method POST
     * @endpoint "/auth/login"
     */
    @Test
    fun `User and AccesToken should be returned`() {
        val userLogin = UserLogin("peterparker@gmail.com", "spiderman")

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
            setBody(getFile("responses/users/auth_post_login.json"))
        }
        server.enqueue(response)

        // API Call
        apiService.login(userLogin).test().apply {
            assertNoErrors()
            assertComplete()

            assertValue(
                LoginResponse(
                    accessToken = ".P?ONHHYUBBLJBJJJNJ+%OKP87543389",
                    user = User(
                        id = 1,
                        username = "Peter Parker",
                        email = "peterparker@gmail.com"
                    )
                )
            )
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("POST", request.method)
        Assert.assertEquals("/auth/login", request.path)
    }

    /**
     * @method POST
     * @endpoint "/auth/register"
     */
    @Test
    fun `User should be registered without errors`() {
        val userRegister = UserRegister("Peter Parker","peterparker@gmail.com", "spiderman")

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
        }
        server.enqueue(response)

        // API Call
        apiService.register(userRegister).test().apply {
            assertNoErrors()
            assertComplete()
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("POST", request.method)
        Assert.assertEquals("/auth/register", request.path)
    }

    /**
     * @method POST
     * @endpoint "/auth/logout"
     */
    @Test
    fun `User should be logged out without errors`() {
        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
        }
        server.enqueue(response)

        // API Call
        apiService.logout().test().apply {
            assertNoErrors()
            assertComplete()
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("POST", request.method)
        Assert.assertEquals("/auth/logout", request.path)
    }

    /**
     * PAYMENTS
     */

    /**
     * @method GET
     * @endpoint "user/payments/debtors"
     */
    @Test
    fun `Debtors of user should be returned`() {
        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
            setBody(getFile("responses/payments/payments_debtors_get.json"))
        }
        server.enqueue(response)

        // API Call
        apiService.userDebtors().test().apply {
            assertNoErrors()
            assertComplete()

            assertValue(
                mutableListOf(
                    Payment(
                        user = User(
                            id = 5,
                            username = "Bruce Banner",
                            email = "brucebanner@gmail.com"
                        ),
                        order = Order(
                            id = 1,
                            deadline = TEST_DEADLINE,
                            group = Group(
                                id = 1,
                                name = "Zeus",
                                creator = User(
                                    id = 2,
                                    username = "Elon Musk",
                                    email = "elonmusk@spacex.com"
                                )
                            ),
                            courier = User(
                                id = 1,
                                username = "Peter Parker",
                                email = "peterparker@gmail.com"
                            ),
                            location = Location(
                                id = 1,
                                name = "'t Blauw Kotje"
                            ),
                            orderItems = mutableListOf()
                        ),
                        orderItems = mutableListOf()
                    ),
                    Payment(
                        user = User(
                            id = 3,
                            username = "Tony Stark",
                            email = "tonystark@gmail.com"
                        ),
                        order = Order(
                            id = 2,
                            deadline = TEST_DEADLINE,
                            group = Group(
                                id = 1,
                                name = "Zeus",
                                creator = User(
                                    id = 2,
                                    username = "Elon Musk",
                                    email = "elonmusk@spacex.com"
                                )
                            ),
                            courier = User(
                                id = 1,
                                username = "Peter Parker",
                                    email = "peterparker@gmail.com"
                            ),
                            location = Location(
                                id = 2,
                                name = "Emily's"
                            ),
                            orderItems = mutableListOf()
                        ),
                        orderItems = mutableListOf()
                    )
                )
            )
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("GET", request.method)
        Assert.assertEquals("/user/payments/debtors", request.path)
    }

    /**
     * @method GET
     * @endpoint "user/payments/debts"
     */
    @Test
    fun `Debts of user should be returned`() {
        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
            setBody(getFile("responses/payments/payments_debts_get.json"))
        }
        server.enqueue(response)

        // API Call
        apiService.userDepts().test().apply {
            assertNoErrors()
            assertComplete()

            assertValue(
                mutableListOf(
                    Payment(
                        user = User(
                            id = 1,
                            username = "Peter Parker",
                            email = "peterparker@gmail.com"
                        ),
                        order = Order(
                            id = 1,
                            deadline = TEST_DEADLINE,
                            group = Group(
                                id = 1,
                                name = "Zeus",
                                creator = User(
                                    id = 2,
                                    username = "Elon Musk",
                                    email = "elonmusk@spacex.com"
                                )
                            ),
                            courier = User(
                                id = 2,
                                username = "Elon Musk",
                                email = "elonmusk@spacex.com"
                            ),
                            location = Location(
                                id = 1,
                                name = "'t Blauw Kotje"
                            ),
                            orderItems = mutableListOf()
                        ),
                        orderItems = mutableListOf()
                    ),
                    Payment(
                        user = User(
                            id = 1,
                            username = "Peter Parker",
                            email = "peterparker@gmail.com"
                        ),
                        order = Order(
                            id = 2,
                            deadline = TEST_DEADLINE,
                            group = Group(
                                id = 2,
                                name = "WiNa",
                                creator = User(
                                    id = 3,
                                    username = "Bill Gates",
                                    email = "billgates@microsoft.com"
                                )
                            ),
                            courier = User(
                                id = 3,
                                username = "Bill Gates",
                                email = "billgates@microsoft.com"
                            ),
                            location = Location(
                                id = 2,
                                name = "Pizza Hut"
                            ),
                            orderItems = mutableListOf()
                        ),
                        orderItems = mutableListOf()
                    )
                )
            )
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("GET", request.method)
        Assert.assertEquals("/user/payments/debts", request.path)
    }

    /**
     * @method "PATCH"
     * @endpoint "user/payments/{orderId}/{userId}"
     */
    @Test
    fun `Paid-status should be updated`() {
        val userId = 1
        val orderId = 1
        val body = PaymentUpdate(
            paid = true
        )

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
        }
        server.enqueue(response)

        // API Call
        apiService.userSetPaid(orderId, userId, body).test().apply {
            assertNoErrors()
            assertComplete()
        }

        JSONAssert.assertEquals(
                getFile("responses/payments/payments_paid_patch.json"),
                Gson().toJson(body),
                false
        )
    }

    /**
     * @method "POST"
     * @endpoint "user/payments/{orderId}/{userId}/notification"
     */
    @Test
    fun `User should be notified without errors`() {
        val userId = 1
        val orderId = 1

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
        }
        server.enqueue(response)

        // API Call
        apiService.userNotifyDeptor(orderId, userId).test().apply {
            assertNoErrors()
            assertComplete()
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("POST", request.method)
        Assert.assertEquals("/user/payments/${orderId}/${userId}/notification", request.path)
    }

}