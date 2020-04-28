package com.ordy.app.ui.locations

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel
import com.ordy.app.api.models.Location
import com.ordy.app.api.util.Query

class LocationsViewModel(repository: Repository) : RepositoryViewModel(repository) {

    /**
     * List that hold all the ID's of favorite locations.
     */

    private val favoriteLocations: MutableList<Int> = mutableListOf()

    /**
     * Add a location ID to the favorite list.
     */

    fun markAsFavorite(locationId: Int) {
        favoriteLocations.add(locationId)
    }

    /**
     * Remove a location ID from the favorite list.
     */

    fun unMarkAsFavorite(locationId: Int) {
        favoriteLocations.remove(locationId)
    }

    /**
     * Check if location Id is marked as favorite
     */

    fun isFavorite(locationId: Int): Boolean {
        return favoriteLocations.contains(locationId)
    }

    /**
     * Value of the search input field.
     */
    val searchValueData: MutableLiveData<String> = MutableLiveData("")

    /**
     * Get a list with locations
     */
    fun getLocations(): Query<List<Location>> {
        return repository.getLocationsResult().value!!
    }

    /**
     * Get the value of the search input field.
     */
    fun getSearchValue(): String {
        return searchValueData.value!!
    }

    /**
     * Update the locations by the given search query
     */
    fun updateLocations() {
        repository.updateLocations(getSearchValue())
    }

    /**
     * Get the MutableLiveData result of the Locations fetch.
     */
    fun getLocationsMLD(): MutableLiveData<Query<List<Location>>> {
        return repository.getLocationsResult()
    }
}