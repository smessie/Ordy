package com.ordy.app.api.models

import com.google.gson.annotations.SerializedName

data class Cuisine(

    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("items")
    val items: List<Item>
)