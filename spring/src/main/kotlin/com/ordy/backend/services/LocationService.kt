package com.ordy.backend.services

import com.ordy.backend.database.models.Item
import com.ordy.backend.database.repositories.LocationRepository
import com.ordy.backend.database.repositories.UserRepository
import com.ordy.backend.exceptions.GenericException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class LocationService(
        @Autowired val userRepository: UserRepository,
        @Autowired val locationRepository: LocationRepository
) {

    /**
     * Get a list with predefined location items for the given location
     */
    fun getLocationItems(userId: Int, locationId: Int): List<Item> {
        var user = userRepository.findById(userId).get()
        var location = locationRepository.findById(locationId)

        // Validate that the location is present.
        if(!location.isPresent) {
            throw GenericException(HttpStatus.BAD_REQUEST, "Location does not exist")
        }

        return location.get().cuisine?.items?.toList() ?: emptyList()
    }
}