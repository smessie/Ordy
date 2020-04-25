package com.ordy.app.api.util

import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.ordy.app.R
import com.ordy.app.util.SnackbarType
import com.ordy.app.util.SnackbarUtil
import retrofit2.HttpException

class ErrorHandler {

    companion object {

        /**
         * Parse an error, received from RetroFit.
         *
         * @return Wrapped Query Error object.
         */
        fun parse(error: Throwable): QueryError {

            val queryError = QueryError()
            queryError.error = error

            // Print the error message to the console for debugging
            error.printStackTrace()

            Log.i("Error Handler", "----------------------------------------------")
            Log.i("Error Handler", "An error occurred:")

            // Handle HTTP Exceptions.
            if (error is HttpException) {
                queryError.message = if (error.message().isEmpty())
                    "An unknown error has occurred"
                else error.message()
                queryError.description = ""
                queryError.code = error.code().toString()
                queryError.response = error.response()

                Log.i("Error Handler", "Message: ${queryError.message}")
                Log.i("Error Handler", "Code: ${queryError.code}")

                // General errors & input errors (when the error body is defined)
                if (queryError.response != null && queryError.response!!.errorBody() != null) {
                    val errorBody = queryError.response!!.errorBody()

                    if (errorBody != null) {
                        // Convert the error body.
                        val errorResult =
                            Gson().fromJson(errorBody.charStream(), ErrorResult::class.java)

                        if (errorResult != null) {
                            // General errors (when defined)
                            if (errorResult.generalErrors != null) {
                                queryError.generalErrors = errorResult.generalErrors
                            }

                            // Input errors (when defined)
                            if (errorResult.inputErrors != null) {
                                queryError.inputErrors = errorResult.inputErrors
                            }
                        }
                    }
                }
            }

            // Handle all other exceptions.
            // This can be due to Runtime Errors
            else {
                queryError.message = error.message ?: "An unknown error has occurred"
                queryError.description = "Something went wrong, please contact a developer."
                queryError.code = "unknown"
            }

            Log.i("Error Handler", "Stacktrace: ")
            error.printStackTrace()
            Log.i("Error Handler", "----------------------------------------------")


            return queryError
        }

        /**
         * Handle input errors & general errors.
         * Will also display the error message when no general error or field error is specified,
         * but when an error occurred anyway.
         *
         * @param queryError QueryError object
         * @param view Current view
         * @param fields List of input fields
         */
        fun handle(queryError: QueryError?, view: View?, fields: List<InputField> = emptyList()) {

            // Do not handle the error when it was already displayed before.
            if(queryError != null) {
                if(queryError.displayedError) {
                    return
                } else {
                    // Set the error as displayed.
                    queryError.displayedError = true
                }
            }

            // Handle input errors.
            this.handleInputs(queryError, view, fields)

            // Handle general errors.
            this.handleGeneral(queryError, view)

            // If no general error or input error is specified, but an error occurred anyway.
            if (queryError != null
                && queryError.inputErrors.isEmpty()
                && queryError.generalErrors.isEmpty()
                && view != null
            ) {

                var message = queryError.message

                // Filter a connection error message and throw a custom error instead
                if(message.startsWith("Unable to resolve host")) {

                    // Check if the user has no internet connection
                    message = view.context.getString(R.string.error_connection)
                }

                // Create and show a snackbar with the error message.
                this.handleRawGeneral(message, view)
            }
        }

        /**
         * Handle input errors.
         * Will display an error message underneath the fields that have an error.
         *
         * @param queryError QueryError object
         * @param view Current view
         * @param fields List of input fields
         */
        private fun handleInputs(queryError: QueryError?, view: View?, fields: List<InputField>) {

            if (queryError != null) {

                for (field in fields) {

                    val inputError = queryError.inputErrors.find { it.field == field.name }

                    // Check if the input field has an error message.
                    if (inputError != null) {
                        field.input.error = inputError.message

                        // Add shake animation to input field.
                        if (view != null) {
                            val shake = AnimationUtils.loadAnimation(view.context, R.anim.shake)
                            field.input.startAnimation(shake)
                        }
                    }

                    // If not, clear any possible previous error message.
                    else {
                        field.input.error = ""
                    }
                }
            }
        }

        /**
         * Handle general errors
         * Will display an error snackbar at the bottom of the screen.
         *
         * @param queryError QueryError object
         * @param view Current view to display the toast
         */
        private fun handleGeneral(queryError: QueryError?, view: View?) {

            if (queryError != null && view != null && queryError.generalErrors.isNotEmpty()) {
                handleRawGeneral(queryError.generalErrors[0].message, view)
            }
        }

        /**
         * Handle general errors
         * Will display an error snackbar at the bottom of the screen.
         *
         * @param message Raw message in String format
         * @param view Current view to display the toast
         */
        fun handleRawGeneral(message: String, view: View) {
            // Create and show a snackbar with the error message.
            SnackbarUtil.openSnackbar(message, view, Snackbar.LENGTH_LONG, SnackbarType.ERROR)
        }

        private data class ErrorResult(
            val generalErrors: List<QueryGeneralError>?,
            val inputErrors: List<QueryInputError>?
        )
    }
}