package com.ordy.app.api.wrappers

import com.google.gson.annotations.SerializedName
import com.ordy.app.api.models.Location

class LocationWrapper(

    @SerializedName("location")
    val location: Location,

    @SerializedName("favorite")
    val favorite: Boolean
)