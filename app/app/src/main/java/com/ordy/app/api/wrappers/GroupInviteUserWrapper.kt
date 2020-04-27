package com.ordy.app.api.wrappers

import com.google.gson.annotations.SerializedName
import com.ordy.app.api.models.User

data class GroupInviteUserWrapper(
    @SerializedName("user")
    val user: User,

    @SerializedName("invited")
    val invited: Boolean
)