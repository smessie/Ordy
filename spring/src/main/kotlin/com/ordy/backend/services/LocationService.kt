package com.ordy.backend.services

import com.ordy.backend.database.models.FavoriteLocation
import com.ordy.backend.database.models.Item
import com.ordy.backend.database.models.Location
import com.ordy.backend.database.repositories.FavoriteLocationRepository
import com.ordy.backend.database.repositories.LocationRepository
import com.ordy.backend.database.repositories.UserRepository
import com.ordy.backend.exceptions.GenericException
import com.ordy.backend.wrappers.LocationWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class LocationService(
        @Autowired val userRepository: UserRepository,
        @Autowired val locationRepository: LocationRepository,
        @Autowired val favoriteLocationRepository: FavoriteLocationRepository
) {

    /**
     * Get a list of locations
     */
    fun getLocations(userId: Int, query: String): List<LocationWrapper> {
        val user = userRepository.findById(userId).get()

        var matchingLocations = locationRepository.findFirst30ByNameContainingAndPrivateEquals(query, false)
        val userFavoriteLocations = favoriteLocationRepository.findAllByUser(user).map { it.location }

        // add favorite locations of user that are not included in matchingLocations because we only searched the first 30 matching ones
        matchingLocations += userFavoriteLocations.filter { !matchingLocations.contains(it) && it.name.contains(query, ignoreCase = true) }

        // if no query is given, show all the user his favorite locations
        if (query.isBlank()) {
            return userFavoriteLocations.map { LocationWrapper(location = it, favorite = true) }
        }

        // show all matching places with given query and show favorite locations first
        return matchingLocations.map { LocationWrapper(location = it, favorite = userFavoriteLocations.contains(it)) }
                                .sortedBy { !it.favorite }
    }

    /**
     * Get a list with predefined location items for the given location
     */
    fun getLocationItems(userId: Int, locationId: Int): List<Item> {
        var user = userRepository.findById(userId).get()
        val location = locationRepository.findById(locationId)

        // Validate that the location is present.
        if (!location.isPresent) {
            throw GenericException(HttpStatus.BAD_REQUEST, "Location does not exist.")
        }

        return location.get().cuisine?.items?.toList() ?: emptyList()
    }

    /**
     * Mark a location as favorite of user with @param: userId as id.
     */

    fun markAsFavorite(userId: Int, locationId: Int) {
        val user = userRepository.findById(userId).get()

        val locationOptional =  locationRepository.findById(locationId)

        if (locationOptional.isEmpty) {
            throw GenericException(HttpStatus.BAD_REQUEST, "Location does not exist.")
        }

        if (favoriteLocationRepository.findByLocationAndUser(locationOptional.get(), user).isPresent) {
            throw GenericException(HttpStatus.BAD_REQUEST, "Location is already added to your favorite list.")
        }

        val favoriteLocation = FavoriteLocation(location = locationOptional.get(), user = user)
        favoriteLocationRepository.save(favoriteLocation)
    }

    /**
     * Delete location from the user his "favorite locations list"
     */

    fun unMarkAsFavorite(userId: Int, locationId: Int) {
        val user = userRepository.findById(userId).get()

        val locationOptional =  locationRepository.findById(locationId)

        if (locationOptional.isEmpty) {
            throw GenericException(HttpStatus.BAD_REQUEST, "Location does not exist.")
        }

        val favoriteLocationOptional = favoriteLocationRepository.findByLocationAndUser(locationOptional.get(), user)

        if (favoriteLocationOptional.isEmpty) {
            throw GenericException(HttpStatus.BAD_REQUEST, "This location is not in your favorite locations list.")
        }

        favoriteLocationRepository.delete(favoriteLocationOptional.get())
    }

}