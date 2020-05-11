package com.ordy.app.ui

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isSelected
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.ordy.app.MainActivity
import com.ordy.app.R
import com.ordy.app.api.ApiService
import com.ordy.app.api.Repository
import com.ordy.app.api.models.Location
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.api.wrappers.LocationWrapper
import com.ordy.app.ui.locations.LocationsViewModel
import io.reactivex.Observable
import okhttp3.ResponseBody
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.Mockito
import org.mockito.Mockito.times


@RunWith(AndroidJUnit4::class)
class LocationsUITest : KoinTest {

    /**
     * Java faker for faking data.
     */
    private val faker = Faker()

    /**
     * Mocked android activity context.
     */
    private lateinit var mockContext: Context

    /**
     * Intent to launch LocationsFragment
     */
    private lateinit var locationsIntent: Intent

    /**
     * ViewModel that has been created using Koin injection.
     */
    private val mockLocationsViewModel: LocationsViewModel by inject()

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

        locationsIntent = Intent(mockContext, MainActivity::class.java)
        locationsIntent.putExtra("open_tab", "locations")

        declareMock<LocationsViewModel>()
        declareMock<Repository>()
    }

    /**
     * Should update locations when changing text in search bar
     */
    @Test
    fun updateLocationsList() {
        val searchValueData = MutableLiveData("")
        val re = Regex("[^A-Za-z0-9 ]")

        // Filter out untypeable characters
        val searchString = re.replace(faker.food().dish(), "")

        val locationsQuery: Query<List<LocationWrapper>> = Query()
        locationsQuery.status = QueryStatus.SUCCESS
        locationsQuery.data = mutableListOf()

        val locationsMLD = MutableLiveData(locationsQuery)

        whenever(mockLocationsViewModel.getLocationsMLD()).thenReturn(locationsMLD)
        whenever(mockLocationsViewModel.getLocations()).thenReturn(locationsQuery)
        whenever(mockLocationsViewModel.searchValueData).thenReturn(searchValueData)

        // Launch the activity
        ActivityScenario.launch<MainActivity>(locationsIntent)

        // Update should be called for every character typed
        onView(withId(R.id.locations_search_text)).perform(typeText(searchString))
        verify(mockLocationsViewModel, times(searchString.length + 1)).updateLocations()
    }

    /**
     * Should mark location as favorite if it isn't a favorite yet
     */
    @Test
    fun markFavoriteLocation() {
        val locationId = faker.number().randomDigit()
        val searchValueData = MutableLiveData("")

        // Create location to be marked
        val locationWrapper = LocationWrapper(
            location = Location(
                id = locationId,
                name = faker.food().dish(),
                latitude = faker.number().randomNumber().toDouble(),
                longitude = faker.number().randomNumber().toDouble(),
                address = faker.address().fullAddress()
            ),
            favorite = false
        )

        val locationsQuery: Query<List<LocationWrapper>> = Query()
        locationsQuery.status = QueryStatus.SUCCESS
        locationsQuery.data = mutableListOf(locationWrapper)

        val locationsMLD = MutableLiveData(locationsQuery)

        whenever(mockLocationsViewModel.searchValueData).thenReturn(searchValueData)
        whenever(mockLocationsViewModel.getLocationsMLD()).thenReturn(locationsMLD)
        whenever(mockLocationsViewModel.getLocations()).thenReturn(locationsQuery)
        whenever(mockRepository.unMarkLocationAsFavorite(any(), any())).then {}

        // Launch the activity
        ActivityScenario.launch<MainActivity>(locationsIntent)

        // Click the button to create a favorite
        onView(withId(R.id.favorite_mark)).perform(click())
        onView(withId(R.id.favorite_mark)).check(matches(isSelected()))
    }

    /**
     * Should unmark location as favorite if it a favorite
     */
    @Test
    fun unMarkFavoriteLocation() {
        val locationId = faker.number().randomDigit()
        val searchValueData = MutableLiveData("")

        // Create location to be unmarked
        val locationWrapper = LocationWrapper(
            location = Location(
                id = locationId,
                name = faker.food().dish(),
                latitude = faker.number().randomNumber().toDouble(),
                longitude = faker.number().randomNumber().toDouble(),
                address = faker.address().fullAddress()
            ),
            favorite = true
        )

        val locationsQuery: Query<List<LocationWrapper>> = Query()
        locationsQuery.status = QueryStatus.SUCCESS
        locationsQuery.data = mutableListOf(locationWrapper)

        val locationsMLD = MutableLiveData(locationsQuery)

        whenever(mockLocationsViewModel.searchValueData).thenReturn(searchValueData)
        whenever(mockLocationsViewModel.getLocationsMLD()).thenReturn(locationsMLD)
        whenever(mockLocationsViewModel.getLocations()).thenReturn(locationsQuery)
        whenever(mockRepository.unMarkLocationAsFavorite(any(), any())).then {}

        // Launch the activity
        ActivityScenario.launch<MainActivity>(locationsIntent)

        // Click the button to create a favorite
        onView(withId(R.id.favorite_mark)).perform(click())
        onView(withId(R.id.favorite_mark)).check(matches(not(isSelected())))
    }
}