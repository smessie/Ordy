package com.ordy.app.ui.orders.create.location

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.ApiService
import com.ordy.app.api.ApiServiceViewModel
import com.ordy.app.api.models.Location
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.Query

class CreateOrderLocationViewModel(apiService: ApiService) : ApiServiceViewModel(apiService) {

    val locations: MutableLiveData<Query<List<Location>>> = MutableLiveData(Query())

    /**
    * Value of the search input field.
    */
    val searchValueData: MutableLiveData<String> = MutableLiveData("")

    /**
     * Get a list with locations
     */
    fun getLocations(): Query<List<Location>> {
        return locations.value!!
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
        FetchHandler.handle(
            locations,
            apiService.locations(getSearchValue())
        )
    }
}