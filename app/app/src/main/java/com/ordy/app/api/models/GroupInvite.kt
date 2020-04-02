package com.ordy.app.api.models

import com.google.gson.annotations.SerializedName

data class GroupInvite(

    @SerializedName("id")
    val id: Int,

    @SerializedName("user")
    val user: User,

    @SerializedName("group")
    val group: Group
)
