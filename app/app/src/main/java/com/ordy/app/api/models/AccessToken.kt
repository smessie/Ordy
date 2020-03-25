package com.ordy.app.api.models

import com.google.gson.annotations.SerializedName

data class AccessToken(

    @SerializedName("accessToken")
    val accessToken: String
)