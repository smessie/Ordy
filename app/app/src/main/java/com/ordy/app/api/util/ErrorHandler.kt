package com.ordy.app.api.util

import retrofit2.HttpException

class ErrorHandler {

    companion object {
        /**
         * Handle an error, received from RetroFit.
         *
         * @return Wrapped Query Error object.
         */
        fun handle(error: Throwable): QueryError {

            val queryError = QueryError()
            queryError.error = error

            // Handle HTTP Exceptions.
            if(error is HttpException) {
                queryError.message = error.message()
                queryError.description = ""
                queryError.code = error.code().toString()
                queryError.response = error.response()
            }

            // Handle all other exceptions.
            // This can be due to Runtime Errors
            else {
                queryError.message = "An unknown error has occurred"
                queryError.description = "Something went wrong, please contact a developer."
                queryError.code = "unknown"
            }

            return queryError
        }
    }
}