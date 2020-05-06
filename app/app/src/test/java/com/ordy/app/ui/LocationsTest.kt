package com.ordy.app.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.ordy.app.api.Repository
import com.ordy.app.api.util.Query
import com.ordy.app.ui.locations.LocationsViewModel
import okhttp3.ResponseBody
import org.junit.Rule
import org.junit.Test

class LocationsTest {
    var faker = Faker()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mockRepository: Repository = mock()
    private val locationsViewModel = LocationsViewModel(mockRepository)

    @Test
    fun `Marking location as favorite calls repository with same locationId`() {
        val locationId = faker.number().numberBetween(1, 200)
        val liveData: MutableLiveData<Query<ResponseBody>> = MutableLiveData(Query())

        locationsViewModel.createFavoriteLocation(locationId, liveData)

        verify(mockRepository).markLocationAsFavorite(locationId, liveData)
    }

    @Test
    fun `Unmarking location as favorite calls repository with same locationId`() {
        val locationId = faker.number().numberBetween(1, 200)
        val liveData: MutableLiveData<Query<ResponseBody>> = MutableLiveData(Query())

        locationsViewModel.deleteFavoriteLocation(locationId, liveData)

        verify(mockRepository).unMarkLocationAsFavorite(locationId, liveData)
    }

    @Test
    fun `Updating locations should call repository with search value`() {
        locationsViewModel.searchValueData.value = faker.food().dish()

        locationsViewModel.updateLocations()

        verify(mockRepository).updateLocations(locationsViewModel.searchValueData.value!!)
    }
}