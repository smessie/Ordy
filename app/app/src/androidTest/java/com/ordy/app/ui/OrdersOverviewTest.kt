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
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.ordy.app.R
import com.ordy.app.api.Repository
import com.ordy.app.api.models.Group
import com.ordy.app.api.models.Location
import com.ordy.app.api.models.Order
import com.ordy.app.api.models.User
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.orders.overview.OverviewOrderActivity
import com.ordy.app.ui.orders.overview.OverviewOrderViewModel
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.Mockito
import java.util.*

@RunWith(AndroidJUnit4::class)
class OrdersOverviewTest : KoinTest {

    /**
     * Java faker for faking data.
     */
    private val faker = Faker()

    /**
     * Mocked android activity context.
     */
    private lateinit var mockContext: Context

    /**
     * ViewModel that has been created using Koin injection.
     */
    private val mockOverviewOrderViewModel: OverviewOrderViewModel by inject()

    /**
     * Repository that has been created using Koin injection.
     */
    private val mockRepository: Repository by inject()

    /**
     * Mock provider for Koin testing.
     */
    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Before
    fun setup() {
        // Initialize mocks
        mockContext = InstrumentationRegistry.getInstrumentation().targetContext

        declareMock<OverviewOrderViewModel>()
        declareMock<Repository>()
    }

    /**
     * Constants
     */
    private val TEST_DEADLINE_CLOSED =
        Calendar.getInstance().apply { add(Calendar.DATE, -5) }.time
    private val TEST_DEADLINE_OPEN =
        Calendar.getInstance().apply { add(Calendar.DATE, 5) }.time

    /**
     * Should display the "Add item" button when the deadline is not passed
     */
    @Test
    fun displayAddItemButton() {
        val order = Order(
            id = 1,
            deadline = TEST_DEADLINE_OPEN,
            billUrl = faker.name().name(),
            group = Group(
                id = 1,
                name = faker.name().name(),
                creator = User(
                    id = 1,
                    username = faker.name().name(),
                    email = faker.internet().emailAddress()
                )
            ),
            courier = User(
                id = 1,
                username = faker.name().name(),
                email = faker.internet().emailAddress()
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

        // Mock the repository
        whenever(mockRepository.refreshOrder(eq(orderMLD), eq(order.id), eq(mockContext), any())).then { }

        // Mock the ViewModel
        whenever(mockOverviewOrderViewModel.getOrderMLD()).thenReturn(orderMLD)
        whenever(mockOverviewOrderViewModel.getOrder()).thenReturn(orderQuery)
        whenever(mockOverviewOrderViewModel.orderId).thenReturn(MutableLiveData(order.id))
        whenever(mockOverviewOrderViewModel.getUploadBillMLD()).thenReturn(MutableLiveData(Query()))

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

        // Mock the ViewModel
        whenever(mockOverviewOrderViewModel.getOrderMLD()).thenReturn(orderMLD)
        whenever(mockOverviewOrderViewModel.getOrder()).thenReturn(orderQuery)
        whenever(mockOverviewOrderViewModel.orderId).thenReturn(MutableLiveData(order.id))
        whenever(mockOverviewOrderViewModel.getUploadBillMLD()).thenReturn(MutableLiveData(Query()))

        // Mock the Repository
        whenever(mockRepository.refreshOrder(eq(orderMLD), eq(order.id), eq(mockContext), any())).then { }

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