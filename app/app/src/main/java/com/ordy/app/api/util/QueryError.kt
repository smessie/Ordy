package com.ordy.app.api.util

import retrofit2.Response

class QueryError {

    /**
     * Message of the error to display.
     */
    var message: String = ""

    /**
     * Description of the error.
     * Will be set when certain error codes are catched and modified.
     */
    var description: String = ""

    /**
     * Status code of the HTTP response.
     */
    var code: String = ""

    /**
     * Raw HTTP Response from the server
     */
    var response: Response<*>? = null

    /**
     * Original error, as received from RetroFit.
     */
    var error: Throwable? = null

    /**
     * List with general errors.
     */
    var generalErrors: List<QueryGeneralError> = emptyList()

    /**
     * List with input errors.
     */
    var inputErrors: List<QueryInputError> = emptyList()
}