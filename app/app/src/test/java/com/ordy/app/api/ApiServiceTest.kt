package com.ordy.app.api

import com.google.gson.Gson
import com.ordy.app.api.models.*
import com.ordy.app.api.models.actions.*
import com.ordy.app.api.models.actions.enums.InviteActionOptions
import com.ordy.app.api.models.actions.enums.OrderUpdateAction
import com.ordy.app.api.wrappers.GroupInviteUserWrapper
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
            setBody(getFile("responses/location_get.json"))
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
            setBody(getFile("responses/location_items_get.json"))
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
            setBody(getFile("responses/locations_filtered_get.json"))
        }
        server.enqueue(response)

        // API Call
        apiService.locations(query).test().apply {
            assertNoErrors()
            assertComplete()

            assertValue(
                mutableListOf(
                    Location(
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
                    Location(
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
     * @endpoint "/groups"
     */
    @Test
    fun `Group should be created`() {

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
            setBody(getFile("responses/group_create.json"))
        }
        server.enqueue(response)

        // Body
        val body = GroupCreate(
            name = "Zeus"
        )

        // API Call
        apiService.createGroup(body).test().apply {
            assertNoErrors()
            assertComplete()

            assertValue(
                Group(
                    id = 1,
                    name = "Zeus",
                    creator = User(
                        id = 1,
                        username = "Elon Musk"
                    )
                )
            )
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("POST", request.method)
        Assert.assertEquals("/groups", request.path)
    }

    /**
     * @method GET
     * @endpoint "/groups/{groupId}"
     */
    @Test
    fun `Group should be returned`() {
        val groupId = 1

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
            setBody(getFile("responses/group_get.json"))
        }
        server.enqueue(response)

        // API Call
        apiService.group(groupId).test().apply {
            assertNoErrors()
            assertComplete()

            assertValue(
                Group(
                    id = 1,
                    name = "Zeus",
                    creator = User(
                        id = 1,
                        username = "Elon Musk"
                    )
                )
            )
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("GET", request.method)
        Assert.assertEquals("/groups/$groupId", request.path)
    }

    /**
     * @method PATCH
     * @endpoint "/groups/{groupId}"
     */
    @Test
    fun `Name of group should be changed`() {
        // TODO
    }

    /**
     * @method GET
     * @endpoint "/groups/{groupId}/invites/search/{username}"
     */
    @Test
    fun `Give all matching users without users in given group`() {
        val groupId = 0
        val username = "e"

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
            setBody(getFile("responses/user_invites_filtered_get.json"))
        }
        server.enqueue(response)

        // API Call
        apiService.searchMatchingInviteUsers(groupId, username).test().apply {
            assertNoErrors()
            assertComplete()

            assertValue(
                mutableListOf(
                    GroupInviteUserWrapper(
                        user = User(
                            id = 1,
                            username = "Elon Musk"
                        ),
                        invited = false
                    ),
                    GroupInviteUserWrapper(
                        User(
                            id = 2,
                            username = "Test"
                        ),
                        invited = true
                    )
                )
            )
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("GET", request.method)
        Assert.assertEquals("/groups/$groupId/invites/search/$username", request.path)
    }

    /**
     * @method POST
     * @endpoint "/groups/{groupId}/invites/{userId}"
     */
    @Test
    fun `User should be invited to group`() {
        val groupId = 1
        val userId = 2

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
            setBody("")
        }
        server.enqueue(response)

        // API Call
        apiService.createInviteGroup(groupId, userId).test().apply {
            assertNoErrors()
            assertComplete()
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("POST", request.method)
        Assert.assertEquals("/groups/$groupId/invites/$userId", request.path)
    }

    /**
     * @method DELETE
     * @endpoint "/groups/{groupId}/invites/{userId}"
     */
    @Test
    fun `Should delete a GroupInvite`() {
        val groupId = 1
        val userId = 2

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
        }
        server.enqueue(response)

        // API Call
        apiService.deleteInviteGroup(groupId, userId).test().apply {
            assertNoErrors()
            assertComplete()
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("DELETE", request.method)
        Assert.assertEquals("/groups/$groupId/invites/$userId", request.path)
    }


    /**
     * @method DELETE
     * @endpoint "/groups/{groupId}/members/{userId}"
     */
    @Test
    fun `Should delete a member from group`() {
        val groupId = 1
        val userId = 3

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
        }
        server.enqueue(response)

        // API Call
        apiService.deleteMemberGroup(groupId, userId).test().apply {
            assertNoErrors()
            assertComplete()
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("DELETE", request.method)
        Assert.assertEquals("/groups/$groupId/members/$userId", request.path)
    }


    /**
     * @method GET
     * @endpoint "/user/groups"
     */
    @Test
    fun `Should return all groups of the user`() {

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
            setBody(getFile("responses/group_user_list.json"))
        }
        server.enqueue(response)

        // API Call
        apiService.userGroups().test().apply {
            assertNoErrors()
            assertComplete()

            assertValue(
                mutableListOf(
                    Group(
                        id = 1,
                        name = "Zeus",
                        creator = User(
                            id = 1,
                            username = "Elon Musk"
                        )
                    ),
                    Group(
                        id = 2,
                        name = "TeslaLovers",
                        creator = User(
                            id = 2,
                            username = "Test"
                        )
                    )
                )
            )
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("GET", request.method)
        Assert.assertEquals("/user/groups", request.path)
    }

    /**
     * @method GET
     * @endpoint "/user/invites"
     */
    @Test
    fun `Should return all invites a user has`() {

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
            setBody(getFile("responses/invite_user_list.json"))
        }
        server.enqueue(response)

        // API Call
        apiService.userInvites().test().apply {
            assertNoErrors()
            assertComplete()

            assertValue(
                mutableListOf(
                    GroupInvite(
                        id = 1,
                        user = User(
                            id = 2,
                            username = "Test"
                        ),
                        group = Group(
                            id = 1,
                            name = "Zeus",
                            creator = User(
                                id = 1,
                                username = "Elon Musk"
                            )
                        )
                    ),
                    GroupInvite(
                        id = 2,
                        user = User(
                            id = 2,
                            username = "Test"
                        ),
                        group = Group(
                            id = 3,
                            name = "groupOfTesters",
                            creator = User(
                                id = 3,
                                username = "TestChief"
                            )
                        )
                    )
                )
            )
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("GET", request.method)
        Assert.assertEquals("/user/invites", request.path)
    }

    /**
     * @method POST
     * @endpoint "/user/invites/{groupId}"
     */
    @Test
    fun `Should accept or deny a group invite`() {
        val groupId = 1

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
            setBody(getFile("responses/body_invite_action.json"))
        }
        server.enqueue(response)

        // Body
        val body = InviteAction(
            action = InviteActionOptions.ACCEPT
        )

        // API Call
        apiService.userActionInvites(body, groupId).test().apply {
            assertNoErrors()
            assertComplete()
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("POST", request.method)
        Assert.assertEquals("/user/invites/$groupId", request.path)
    }

    /**
     * @method POST
     * @endpoint "/user/groups/{groupId}/leave"
     */
    @Test
    fun `User should leave from group`() {
        val groupId = 1

        // Response
        val response = MockResponse().apply {
            setResponseCode(200)
            setBody(getFile(""))
        }
        server.enqueue(response)

        // API Call
        apiService.userLeaveGroup(groupId).test().apply {
            assertNoErrors()
            assertComplete()
        }

        // Request
        val request = server.takeRequest()

        Assert.assertEquals("POST", request.method)
        Assert.assertEquals("/user/groups/$groupId/leave", request.path)
    }

}