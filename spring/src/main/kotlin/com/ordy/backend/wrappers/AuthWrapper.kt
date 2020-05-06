package com.ordy.backend.wrappers

import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import com.ordy.backend.database.models.User

class AuthLoginWrapper(
        val email: String,
        val password: String,
        val deviceToken: String
)

class AuthRegisterWrapper(
        val username: String,
        val email: String,
        val password: String
)

class AuthTokenWrapper(
        @JsonView(View.Detail::class)
        val accessToken: String,

        @JsonView(View.Detail::class)
        val user: User
)