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

    /**
     * Get the data if present.
     *
     * @throws IllegalStateException if data is not present.
     */
    fun requireData(): T {

        return (this.data ?: throw IllegalStateException("Data is not present on Query object"))
    }

    /**
     * Get the error if present.
     *
     * @throws IllegalStateException if error is not present.
     */
    fun requireError(): QueryError {

        return (this.error ?: throw IllegalStateException("Error is not present on Query object"))
    }

    /**
     * Constructor with query status
     */
    constructor(status: QueryStatus?) {
        if (status != null) {
            this.status = status
        }
    }

    /**
     * Primary constructor
     */
    constructor()
}