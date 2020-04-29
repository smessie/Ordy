package com.ordy.backend.controllers

import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import com.ordy.backend.database.models.Item
import com.ordy.backend.database.models.Location
import com.ordy.backend.services.LocationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/locations")
class LocationController(@Autowired val locationService: LocationService) {

    @GetMapping
    @JsonView(View.List::class)
    fun getLocations(@RequestParam("q") query: String): List<Location> {
        return locationService.getLocations(query)
    }

    @GetMapping("/{locationId}")
    fun getLocation(@PathVariable locationId: Int) {

    }

    @GetMapping("/{locationId}/items")
    @JsonView(View.Detail::class)
    fun getLocationItems(@PathVariable locationId: Int): List<Item> {
        return locationService.getLocationItems(locationId)
    }
}