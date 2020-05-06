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
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
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
     * Viewmodel that has been created using Koin injection.
     */
    private val mockOverviewOrderViewModel: OverviewOrderViewModel by inject()

    /**
     * Viewmodel used for spying & changing method implementations.
     */
    private var spyOverviewOrderViewModel = spy(mockOverviewOrderViewModel)

    /**
     * Mock provider for Koin testing.
     */
    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mock(clazz.java)
    }

    @Before
    fun setup() {
        // Initialize mocks using decorators.
        MockitoAnnotations.initMocks(this)

        // Initialize mocks
        mockContext = InstrumentationRegistry.getInstrumentation().targetContext

        // Initialize koin
        loadKoinModules(module {
            viewModel(override = true) {
                spyOverviewOrderViewModel
            }
        })
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
            given(refreshOrder(orderMLD, order.id)).will { }
        }

        // Mock the viewmodel
        `when`(spyOverviewOrderViewModel.getOrderMLD()).thenReturn(orderMLD)
        `when`(spyOverviewOrderViewModel.getOrder()).thenReturn(orderQuery)

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

        // Mock the viewmodel
        `when`(mockOverviewOrderViewModel.getOrderMLD()).thenReturn(orderMLD)
        `when`(mockOverviewOrderViewModel.getOrder()).thenReturn(orderQuery)

        declareMock<Repository> {
            given(refreshOrder(orderMLD, order.id)).will {  }
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