package com.ordy.app.ui.locations

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel
import com.ordy.app.api.util.Query
import com.ordy.app.api.wrappers.LocationWrapper
import okhttp3.ResponseBody

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
    fun getLocations(): Query<List<LocationWrapper>> {
        return repository.getLocationsResult().value!!
    }

    /**
     * Get the value of the search input field.
     */
    private fun getSearchValue(): String {
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
    fun getLocationsMLD(): MutableLiveData<Query<List<LocationWrapper>>> {
        return repository.getLocationsResult()
    }

    /**
     * Create a FavoriteLocation with given location and user.
     * @param locationId: ID of the location the user wants to make favorite
     * @param liveData: Object where we want to store the result of our query in
     */
    fun createFavoriteLocation(locationId: Int, liveData: MutableLiveData<Query<ResponseBody>>) {
        repository.markLocationAsFavorite(locationId, liveData)
    }

    /**
     * Delete a FavoriteLocation with given location and user
     * @param locationId: ID of the location the user wants to remove from his favorite locations list
     * @param liveData: Object where we want to store the result of our query in
     */
    fun deleteFavoriteLocation(locationId: Int, liveData: MutableLiveData<Query<ResponseBody>>) {
        repository.unMarkLocationAsFavorite(locationId, liveData)
    }
}