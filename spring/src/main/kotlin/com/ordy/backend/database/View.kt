package com.ordy.backend.database

class View {

    /**
     * Used for fields that will never be returned to the API.
     */
    interface Ignore {}

    /**
     * Used for a list of entities.
     */
    interface List {}

    /**
     * Used for a more detailed overview of entities.
     */
    interface Detail {}
}

