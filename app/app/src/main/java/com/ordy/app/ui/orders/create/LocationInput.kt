package com.ordy.app.ui.orders.create

import com.ordy.app.api.models.Location

data class LocationInput(
    val location: Location? = null,
    val customLocationName: String? = null
)