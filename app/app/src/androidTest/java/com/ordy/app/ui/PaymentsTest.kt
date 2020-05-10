package com.ordy.app.ui

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.whenever
import com.ordy.app.MainActivity
import com.ordy.app.R
import com.ordy.app.api.Repository
import com.ordy.app.api.models.*
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.util.types.TabsEntry
import org.hamcrest.CoreMatchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.*

@RunWith(AndroidJUnit4::class)
class PaymentsTest : KoinTest {

    /**
     * Java faker for faking data.
     */
    private val faker = Faker()

    /**
     * Mocked android activity context.
     */
    private lateinit var mockContext: Context

    /**
     * Repository that has been created using Koin injection.
     */
    private val mockRepository: Repository by inject()

    /**
     * Viewmodel used for spying & changing method implementations.
     */
    private lateinit var intent: Intent

    /**
     * Mock provider for Koin testing.
     */
    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Before
    fun setup() {
        // Initialize mocks using decorators.
        MockitoAnnotations.initMocks(this)

        // Initialize mocks
        mockContext = InstrumentationRegistry.getInstrumentation().targetContext

        intent = Intent(mockContext, MainActivity::class.java)
        intent.putExtra("open_tab", "payments")

        declareMock<Repository>()
    }

    private val MAX_ID = 10000

    @Test
    fun debtorsPaymentShouldBeVisibleAfterSearch() {
        val debtor = getFakeUser()
        val self = getFakeUser()

        val payments = listOf(
            Payment(
                user = debtor,
                order = Order(
                    id = 1,
                    deadline = Calendar.getInstance().apply { add(Calendar.DATE, -1) }.time,
                    billUrl = faker.internet().url(),
                    group = Group(
                        id = 1,
                        name = faker.name().name(),
                        creator = self
                    ),
                    courier = self,
                    location = Location(
                        id = 1,
                        name = faker.business().toString(),
                        latitude = faker.number().randomNumber().toDouble(),
                        longitude = faker.number().randomNumber().toDouble(),
                        address = faker.address().fullAddress()
                    ),
                    orderItems = mutableListOf()
                ),
                orderItems = listOf(
                    OrderItem(
                        id = faker.random().nextInt(MAX_ID),
                        item = Item(
                            id = faker.random().nextInt(MAX_ID),
                            name = faker.food().dish()
                        ),
                        comment = faker.hitchhikersGuideToTheGalaxy().quote(),
                        user = debtor
                    )
                )
            )
        )

        val debtorsQuery: Query<List<Payment>> = Query<List<Payment>>()
            .also { it.status = QueryStatus.SUCCESS }
            .also { it.data = payments }

        val debtsQuery = Query<List<Payment>>()
            .also { it.status = QueryStatus.SUCCESS }
            .also { it.data = emptyList() }

        val debtorsMLD = MutableLiveData(debtorsQuery)
        val debtsMLD = MutableLiveData(debtsQuery)

        whenever(mockRepository.userDebtorsResult).thenReturn(debtorsMLD)
        whenever(mockRepository.userDebtsResult).thenReturn(debtsMLD)

        ActivityScenario.launch<MainActivity>(intent)

        // Payment should be visible
        onView(withId(R.id.payment)).check(matches(isDisplayed()))

        // Type debtor username
        onView(withId(R.id.payments_debtors_search)).perform(typeText(debtor.username))

        // Payment should still be visible
        onView(withId(R.id.payment)).check(matches(isDisplayed()))
    }

    @Test
    fun debtsPaymentShouldBeVisibleAfterSearch() {
        val debtor = getFakeUser()
        val self = getFakeUser()

        val payments = listOf(
            Payment(
                user = debtor,
                order = Order(
                    id = 1,
                    deadline = Calendar.getInstance().apply { add(Calendar.DATE, -1) }.time,
                    billUrl = faker.internet().url(),
                    group = Group(
                        id = 1,
                        name = faker.name().name(),
                        creator = debtor
                    ),
                    courier = debtor,
                    location = Location(
                        id = 1,
                        name = faker.business().toString(),
                        latitude = faker.number().randomNumber().toDouble(),
                        longitude = faker.number().randomNumber().toDouble(),
                        address = faker.address().fullAddress()
                    ),
                    orderItems = mutableListOf()
                ),
                orderItems = listOf(
                    OrderItem(
                        id = faker.random().nextInt(MAX_ID),
                        item = Item(
                            id = faker.random().nextInt(MAX_ID),
                            name = faker.food().dish()
                        ),
                        comment = faker.hitchhikersGuideToTheGalaxy().quote(),
                        user = self
                    )
                )
            )
        )

        val debtorsQuery: Query<List<Payment>> = Query<List<Payment>>()
            .also { it.status = QueryStatus.SUCCESS }
            .also { it.data = emptyList() }

        val debtsQuery = Query<List<Payment>>()
            .also { it.status = QueryStatus.SUCCESS }
            .also { it.data = payments }

        val debtorsMLD = MutableLiveData(debtorsQuery)
        val debtsMLD = MutableLiveData(debtsQuery)

        whenever(mockRepository.userDebtorsResult).thenReturn(debtorsMLD)
        whenever(mockRepository.userDebtsResult).thenReturn(debtsMLD)

        ActivityScenario.launch<MainActivity>(intent)

        // switch tab
        onView(
            anyOf(
                instanceOf(TabsEntry::class.java),
                withText(mockContext.getString(R.string.debts_tab_title))
            )
        )
            .perform(click())

        // Payment should be visible
        onView(withId(R.id.payment)).check(matches(isDisplayed()))

        // Type creditor username
        onView(withId(R.id.payments_debts_search)).perform(typeText(debtor.username))

        // Payment should not be visible
        onView(withId(R.id.payments_debts)).check(matches(isDisplayed()))
    }

    @Test
    fun debtsPaymentShouldBeInVisibleAfterSearch() {
        val debtor = getFakeUser(username = "Yeet")
        val self = getFakeUser()

        val payments = listOf(
            Payment(
                user = debtor,
                order = Order(
                    id = 1,
                    deadline = Calendar.getInstance().apply { add(Calendar.DATE, -1) }.time,
                    billUrl = faker.internet().url(),
                    group = Group(
                        id = 1,
                        name = faker.name().name(),
                        creator = debtor
                    ),
                    courier = debtor,
                    location = Location(
                        id = 1,
                        name = faker.business().toString(),
                        latitude = faker.number().randomNumber().toDouble(),
                        longitude = faker.number().randomNumber().toDouble(),
                        address = faker.address().fullAddress()
                    ),
                    orderItems = mutableListOf()
                ),
                orderItems = listOf(
                    OrderItem(
                        id = faker.random().nextInt(MAX_ID),
                        item = Item(
                            id = faker.random().nextInt(MAX_ID),
                            name = faker.food().dish()
                        ),
                        comment = faker.hitchhikersGuideToTheGalaxy().quote(),
                        user = self
                    )
                )
            )
        )

        val debtorsQuery: Query<List<Payment>> = Query<List<Payment>>()
            .also { it.status = QueryStatus.SUCCESS }
            .also { it.data = emptyList() }

        val debtsQuery = Query<List<Payment>>()
            .also { it.status = QueryStatus.SUCCESS }
            .also { it.data = payments }

        val debtorsMLD = MutableLiveData(debtorsQuery)
        val debtsMLD = MutableLiveData(debtsQuery)

        whenever(mockRepository.userDebtorsResult).thenReturn(debtorsMLD)
        whenever(mockRepository.userDebtsResult).thenReturn(debtsMLD)

        ActivityScenario.launch<MainActivity>(intent)

        // switch tab
        onView(
            anyOf(
                instanceOf(TabsEntry::class.java),
                withText(mockContext.getString(R.string.debts_tab_title))
            )
        )
            .perform(click())

        // Payment should be visible
        onView(withId(R.id.payment)).check(matches(isDisplayed()))

        // Type creditor username
        onView(withId(R.id.payments_debts_search)).perform(typeText("WUBALUBADUBDUB"))

        // Payment should not be visible
        onView(withId(R.id.payments_debts)).check(matches(not(isDisplayed())))
    }

    @Test
    fun debtorsPaymentShouldBeInVisibleAfterSearch() {
        val debtor = getFakeUser(username = "Yeet")
        val self = getFakeUser()

        val payments = listOf(
            Payment(
                user = debtor,
                order = Order(
                    id = 1,
                    deadline = Calendar.getInstance().apply { add(Calendar.DATE, -1) }.time,
                    billUrl = faker.internet().url(),
                    group = Group(
                        id = 1,
                        name = faker.name().name(),
                        creator = self
                    ),
                    courier = self,
                    location = Location(
                        id = 1,
                        name = faker.business().toString(),
                        latitude = faker.number().randomNumber().toDouble(),
                        longitude = faker.number().randomNumber().toDouble(),
                        address = faker.address().fullAddress()
                    ),
                    orderItems = mutableListOf()
                ),
                orderItems = listOf(
                    OrderItem(
                        id = faker.random().nextInt(MAX_ID),
                        item = Item(
                            id = faker.random().nextInt(MAX_ID),
                            name = faker.food().dish()
                        ),
                        comment = faker.hitchhikersGuideToTheGalaxy().quote(),
                        user = debtor
                    )
                )
            )
        )

        val debtorsQuery: Query<List<Payment>> = Query<List<Payment>>()
            .also { it.status = QueryStatus.SUCCESS }
            .also { it.data = payments }

        val debtsQuery = Query<List<Payment>>()
            .also { it.status = QueryStatus.SUCCESS }
            .also { it.data = emptyList() }

        val debtorsMLD = MutableLiveData(debtorsQuery)
        val debtsMLD = MutableLiveData(debtsQuery)

        whenever(mockRepository.userDebtorsResult).thenReturn(debtorsMLD)
        whenever(mockRepository.userDebtsResult).thenReturn(debtsMLD)

        ActivityScenario.launch<MainActivity>(intent)

        // Payment should be visible
        onView(withId(R.id.payment)).check(matches(isDisplayed()))

        // Type debtor username
        onView(withId(R.id.payments_debtors_search)).perform(typeText("WUBALUBADUBDUB"))

        // Payment should still be visible
        onView(withId(R.id.payments_debtors)).check(matches(not(isDisplayed())))
    }

    private fun getFakeUser(
        id: Int = faker.random().nextInt(MAX_ID),
        username: String = faker.starTrek().character(),
        email: String = faker.internet().emailAddress()
    ): User {
        return User(
            id = id,
            username = username,
            email = email
        )
    }
}