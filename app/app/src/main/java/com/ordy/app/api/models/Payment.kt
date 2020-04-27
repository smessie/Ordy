package com.ordy.app.api.models

import com.google.gson.annotations.SerializedName

data class Payment(

    @SerializedName("user")
    val user: User,

    @SerializedName("order")
    val order: Order,

    @SerializedName("orderItems")
    val orderItems: List<OrderItem>
)