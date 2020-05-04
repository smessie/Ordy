package com.ordy.backend.wrappers

import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import com.ordy.backend.database.models.Location

class LocationWrapper (

        @JsonView(View.List::class)
        val location: Location,

        @JsonView(View.List::class)
        val favorite: Boolean
)