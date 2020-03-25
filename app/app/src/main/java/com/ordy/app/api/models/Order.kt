package com.ordy.app.api.models

import com.google.gson.annotations.SerializedName
import java.time.LocalTime

data class Order(

    @SerializedName("id")
    val id: Int,

    @SerializedName("deadline")
    val deadline: LocalTime,

    @SerializedName("billUrl")
    val billUrl: String,

    @SerializedName("group")
    val group: Group,

    @SerializedName("location")
    val location: Location,

    @SerializedName("courier")
    val courier: User
)