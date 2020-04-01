package com.ordy.app.api.models

import com.google.gson.annotations.SerializedName

data class Group(

    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("creator")
    val creator: User,

    @SerializedName("members")
    var members: List<User> = emptyList(),

    @SerializedName("membersCount")
    var membersCount: Int = 0
)
