package com.ordy.backend.controllers

import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import com.ordy.backend.database.models.Item
import com.ordy.backend.database.models.Location
import com.ordy.backend.services.LocationService
import com.ordy.backend.wrappers.LocationWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/locations")
class LocationController(@Autowired val locationService: LocationService) {

    @GetMapping
    @JsonView(View.List::class)
    fun getLocations(@RequestAttribute userId: Int, @RequestParam("q") query: String): List<LocationWrapper> {
        return locationService.getLocations(userId, query)
    }

    @PostMapping("/{locationId}")
    @JsonView(View.Empty::class)
    fun markLocationAsFavorite(@RequestAttribute userId: Int, @PathVariable locationId: Int) {
        locationService.markAsFavorite(userId, locationId)
    }

    @DeleteMapping("/{locationId}")
    @JsonView(View.Empty::class)
    fun unMarkLocationAsFavorite(@RequestAttribute userId: Int, @PathVariable locationId: Int) {
        locationService.unMarkAsFavorite(userId, locationId)
    }

    @GetMapping("/{locationId}/items")
    @JsonView(View.Detail::class)
    fun getLocationItems(@PathVariable locationId: Int): List<Item> {
        return locationService.getLocationItems(locationId)
    }
}