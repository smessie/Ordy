package com.ordy.app.api.util

data class QueryInputError(

    /**
     * Field of the input error.
     */
    val field: String,

    /**
     * Message of the input error.
     */
    val message: String
)