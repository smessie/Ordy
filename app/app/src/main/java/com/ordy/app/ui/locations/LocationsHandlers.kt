package com.ordy.app.ui.locations

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import okhttp3.ResponseBody

class LocationsHandlers(
    val fragment: LocationsFragment,
    val viewModel: LocationsViewModel
) {

    /**
     * Function that reacts when "favorite button" is clicked
     * When the location was a favorite location, delete the location from the favorite locations list
     * When the location was NOT a favorite location, add the location to the favorite locations list
     */
    fun reactOnFavoriteButton(
        liveData: MutableLiveData<Query<ResponseBody>>,
        locationId: Int,
        wasFavorite: Boolean) {

        if (liveData.value?.status != QueryStatus.LOADING) {
            // if the location was a favorite, the user wants to delete it from his favorite list
            if (wasFavorite) {
                // TODO
            } else {
                viewModel.createFavoriteLocation(locationId, liveData)
            }
        }
    }
}