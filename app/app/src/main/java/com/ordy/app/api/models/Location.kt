package com.ordy.app.api.models

import com.google.gson.annotations.SerializedName

data class Location(

    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("latitude")
    val latitude: Double? = null,

    @SerializedName("longitude")
    val longitude: Double? = null,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("private")
    val private: Boolean = false,

    @SerializedName("cuisine")
    val cuisine: Cuisine? = null
)