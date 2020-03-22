package com.ordy.app.api.util

class Query<T> {

    /**
     * Status of the query
     */
    var status: QueryStatus = QueryStatus.INITIALIZED

    /**
     * Data received from the API.
     */
    var data: T? = null

    /**
     * Error when fetching data failed.
     */
    var error: QueryError? = null
}