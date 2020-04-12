package com.ordy.app.ui.orders.create.location

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel
import com.ordy.app.api.models.Location
import com.ordy.app.api.util.Query

class CreateOrderLocationViewModel(repository: Repository) : RepositoryViewModel(repository) {

    /**
     * Value of the search input field.
     */
    val searchValueData: MutableLiveData<String> = MutableLiveData("")

    /**
     * Get the MutableLiveData result of the Locations fetch.
     */
    fun getLocationsMLD(): MutableLiveData<Query<List<Location>>> {
        return repository.getLocationsResult()
    }

    /**
     * Get a list with locations
     */
    fun getLocations(): Query<List<Location>> {
        return getLocationsMLD().value!!
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
}