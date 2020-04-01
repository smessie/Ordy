package com.ordy.backend.wrappers

import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonSerialize
class TokenWrapper(
        val userId: Int,
        val random: String
)