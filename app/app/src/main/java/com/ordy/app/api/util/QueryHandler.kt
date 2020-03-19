package com.ordy.app.api.util

interface QueryHandler<T> {

    /**
     * Called when the query succeeded.
     * @param data Data received from the API.
     */
    fun onQuerySuccess(data: T)

    /**
     * Called when the query failed.
     * @param error Error object containing information about the error.
     */
    fun onQueryError(error: QueryError)
}