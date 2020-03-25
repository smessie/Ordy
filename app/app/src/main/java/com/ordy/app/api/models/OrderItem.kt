package com.ordy.app.api.models

import com.google.gson.annotations.SerializedName

data class OrderItem(

    @SerializedName("id")
    val id: Int,

    @SerializedName("comment")
    val comment: String
)