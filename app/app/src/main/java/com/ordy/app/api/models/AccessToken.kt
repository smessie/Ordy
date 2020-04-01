package com.ordy.app.api.models

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("user")
    val user: User
)