package com.ordy.app.ui

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.javafaker.Faker
import com.ordy.app.R
import com.ordy.app.api.Repository
import com.ordy.app.api.models.Group
import com.ordy.app.api.models.User
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.groups.overview.OverviewGroupActivity
import com.ordy.app.ui.groups.overview.OverviewGroupViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class GroupsOverviewTest : KoinTest {

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
    private val mockOverviewGroupViewModel: OverviewGroupViewModel by inject()

    /**
     * Viewmodel used for spying & changing method implementations.
     */
    private var spyOverviewGroupViewModel = Mockito.spy(mockOverviewGroupViewModel)

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

        // Initialize koin
        loadKoinModules(module {
            viewModel(override = true) {
                spyOverviewGroupViewModel
            }
        })
    }


    /**
     * Check if the group name and the members count in the group overview are correct
     */
    @Test
    fun displayCorrectGroupOverview() {

        val courier = User(
            id = 1,
            username = faker.name().name(),
            email = faker.name().name()
        )
        val group = Group(
            id = faker.number().randomDigit(),
            name = faker.name().name(),
            creator = courier,
            members = listOf(
                courier,
                User(
                    id = 2,
                    username = faker.name().name(),
                    email = faker.name().name()
                )
            ),
            membersCount = 2
        )

        val groupQuery: Query<Group> = Query()
        groupQuery.status = QueryStatus.SUCCESS
        groupQuery.data = group

        val groupMLD = MutableLiveData(groupQuery)

        declareMock<Repository> {
            given(refreshGroup(groupMLD, group.id)).will { }
        }

        // Mock the viewmodel
        Mockito.`when`(spyOverviewGroupViewModel.getGroupMLD()).thenReturn(groupMLD)
        Mockito.`when`(spyOverviewGroupViewModel.getGroup()).thenReturn(groupQuery)

        // Create intent to open activity
        val intent = Intent(mockContext, OverviewGroupActivity::class.java)
        intent.putExtra("group_id", group.id)

        // Launch the activity
        ActivityScenario.launch<OverviewGroupActivity>(intent)

        // Check if the group name is correct
        onView(withId(R.id.group_title))
            .check(matches(withText(group.name)))

        // Check if the member count is correct
        onView(withId(R.id.group_members_amount))
            .check(matches(withText(group.membersCount.toString())))
    }
}