package com.ordy.app.api.models

import com.google.gson.annotations.SerializedName

data class OrderItem(

    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("comment")
    val comment: String,

    @SerializedName("user")
    val user: User
)