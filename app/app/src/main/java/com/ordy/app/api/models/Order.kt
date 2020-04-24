package com.ordy.app.api.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class Order(

    @SerializedName("id")
    val id: Int,

    @SerializedName("deadline")
    val deadline: Date,

    @SerializedName("billUrl")
    val billUrl: String?,

    @SerializedName("group")
    val group: Group,

    @SerializedName("location")
    val location: Location,

    @SerializedName("courier")
    val courier: User,

    @SerializedName("orderItems")
    val orderItems: MutableList<OrderItem> = mutableListOf()
)