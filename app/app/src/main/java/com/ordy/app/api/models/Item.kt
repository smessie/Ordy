package com.ordy.app.api.models

import com.google.gson.annotations.SerializedName

data class Item(

    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String
)