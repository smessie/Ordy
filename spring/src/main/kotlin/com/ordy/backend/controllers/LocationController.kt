package com.ordy.backend.controllers

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/locations")
class LocationController {
    @GetMapping
    fun getLocation(@RequestParam("q") query: String) {

    }

    @GetMapping("/{locationId}")
    fun getLocation(@PathVariable locationId: Int) {

    }

    @GetMapping("/{locationId}/items")
    fun getLocationItems(@PathVariable locationId: Int) {

    }
}