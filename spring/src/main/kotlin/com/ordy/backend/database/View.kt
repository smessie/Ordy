package com.ordy.backend.database

class View {

    /**
     * Used internally, will never be returned to the API.
     */
    interface Ignore {}

    /**
     * Used for nothing. Will return nothing.
     */
    interface Empty {}

    /**
     * Used for just returning the id
     */
    interface Id {}

    /**
     * Used for a list of objects
     */
    interface List : Id {}

    /**
     * Used for a detailed view of a single object
     */
    interface Detail : List {}
}