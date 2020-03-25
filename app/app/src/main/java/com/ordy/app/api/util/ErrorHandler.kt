package com.ordy.app.api.util

import android.graphics.Color
import android.view.View
import android.view.animation.AnimationUtils
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.ordy.app.R
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

            // Handle HTTP Exceptions.
            if(error is HttpException) {
                queryError.message = error.message()
                queryError.description = ""
                queryError.code = error.code().toString()
                queryError.response = error.response()

                // General errors & input errors (when the error body is defined)
                if(queryError.response != null && queryError.response!!.errorBody() != null) {
                    val errorBody = queryError.response!!.errorBody()

                    if(errorBody != null) {
                        // Convert the error body.
                        val errorResult = Gson().fromJson(errorBody.charStream(), ErrorResult::class.java)

                        if(errorResult != null) {
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
        fun handle(queryError: QueryError?, view: View?, fields: List<InputField>) {

            // Handle input errors.
            this.handleInputs(queryError, view, fields)

            // Handle general errors.
            this.handleGeneral(queryError, view)

            // If no general error or input error is specified, but an error occurred anyway.
            if(queryError != null
                && queryError.inputErrors.isEmpty()
                && queryError.generalErrors.isEmpty()
                && view != null) {

                // Create and show a snackbar with the error message.
                val snackbar = Snackbar.make(view, queryError.message, Snackbar.LENGTH_LONG)
                snackbar.view.setBackgroundColor(Color.parseColor("#e74c3c"))
                snackbar.show()
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
        fun handleInputs(queryError: QueryError?, view: View?, fields: List<InputField>) {

            if(queryError?.inputErrors != null) {

                for(field in fields) {

                    val inputError = queryError.inputErrors.find { it.field == field.name }

                    // Check if the input field has an error message.
                    if(inputError != null) {
                        field.input.error = inputError.message

                        // Add shake animation to input field.
                        if(view != null) {
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
        fun handleGeneral(queryError: QueryError?, view: View?) {

            if(queryError?.generalErrors != null && view != null && queryError.generalErrors.isNotEmpty()) {

                // Create and show a snackbar with the error message.
                val snackbar = Snackbar.make(view, queryError.generalErrors[0].message, Snackbar.LENGTH_LONG)
                snackbar.view.setBackgroundColor(Color.parseColor("#e74c3c"))
                snackbar.show()
            }
        }

        private data class ErrorResult(
            val generalErrors: List<QueryGeneralError>?,
            val inputErrors: List<QueryInputError>?
        )
    }
}