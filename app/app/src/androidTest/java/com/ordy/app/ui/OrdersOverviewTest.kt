package com.ordy.app.ui

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.github.javafaker.Faker
import com.ordy.app.R
import com.ordy.app.api.Repository
import com.ordy.app.api.models.Group
import com.ordy.app.api.models.Location
import com.ordy.app.api.models.Order
import com.ordy.app.api.models.User
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.orders.overview.OverviewOrderActivity
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import java.text.SimpleDateFormat

@RunWith(AndroidJUnit4::class)
class OrdersOverviewTest : KoinTest {

    @get:Rule
    val activityRule = ActivityTestRule(OverviewOrderActivity::class.java)

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mock(clazz.java)
    }

    private lateinit var mockContext: Context
    private val faker = Faker()

    @Before
    fun setup() {
        mockContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    /**
     * Constants
     */
    private val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ")

    private val TEST_DEADLINE_CLOSED = formatter.parse("2020-01-01T00:00:00+0000")!!
    private val TEST_DEADLINE = formatter.parse("3020-04-26T16:00:27+0000")!!

    /**
     * Should display the "Add item" button when the deadline is not passed
     */
    @Test
    fun displayAddItemButton() {
        val order = Order(
            id = 1,
            deadline = TEST_DEADLINE,
            billUrl = faker.name().name(),
            group = Group(
                id = 1,
                name = faker.name().name(),
                creator = User(
                    id = 1,
                    username = faker.name().name(),
                    email = faker.name().name()
                )
            ),
            courier = User(
                id = 1,
                username = faker.name().name(),
                email = faker.name().name()
            ),
            location = Location(
                id = 1,
                name = faker.business().toString(),
                latitude = faker.number().randomNumber().toDouble(),
                longitude = faker.number().randomNumber().toDouble(),
                address = faker.address().fullAddress()
            ),
            orderItems = mutableListOf()
        )

        val orderQuery: Query<Order> = Query()
        orderQuery.status = QueryStatus.SUCCESS
        orderQuery.data = order

        val orderMLD = MutableLiveData(orderQuery)

        declareMock<Repository> {
            given(getOrder()).willReturn(orderMLD)
            given(refreshOrder(order.id)).will {  }
        }

        // Create intent to open activity
        val intent = Intent(mockContext, OverviewOrderActivity::class.java)
        intent.putExtra("order_id", order.id)

        // Launch the activity
        ActivityScenario.launch<OverviewOrderActivity>(intent)

        // Check if the button is visible
        onView(withId(R.id.order_items_add))
            .check(matches(isDisplayed()))
    }

    /**
     * Should hide the "Add item" button when the deadline is passed
     */
    @Test
    fun hideAddItemButton() {
        val order = Order(
            id = 1,
            deadline = TEST_DEADLINE_CLOSED,
            billUrl = faker.name().name(),
            group = Group(
                id = 1,
                name = faker.name().name(),
                creator = User(
                    id = 1,
                    username = faker.name().name(),
                    email = faker.name().name()
                )
            ),
            courier = User(
                id = 1,
                username = faker.name().name(),
                email = faker.name().name()
            ),
            location = Location(
                id = 1,
                name = faker.business().toString(),
                latitude = faker.number().randomNumber().toDouble(),
                longitude = faker.number().randomNumber().toDouble(),
                address = faker.address().fullAddress()
            ),
            orderItems = mutableListOf()
        )

        val orderQuery: Query<Order> = Query()
        orderQuery.status = QueryStatus.SUCCESS
        orderQuery.data = order

        val orderMLD = MutableLiveData(orderQuery)

        declareMock<Repository> {
            given(getOrder()).willReturn(orderMLD)
            given(refreshOrder(order.id)).will {  }
        }

        // Create intent to open activity
        val intent = Intent(mockContext, OverviewOrderActivity::class.java)
        intent.putExtra("order_id", order.id)

        // Launch the activity
        ActivityScenario.launch<OverviewOrderActivity>(intent)

        // Check if the button is visible
        onView(withId(R.id.order_items_add))
            .check(matches(not(isDisplayed())))
    }
}