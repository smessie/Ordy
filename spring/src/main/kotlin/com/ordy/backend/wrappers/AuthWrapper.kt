package com.ordy.backend.wrappers

class AuthLoginWrapper(
        val email: String,
        val password: String
)

class AuthRegisterWrapper(
        val username: String,
        val email: String,
        val password: String
)

class AuthTokenWrapper(
        val accessToken: String
)