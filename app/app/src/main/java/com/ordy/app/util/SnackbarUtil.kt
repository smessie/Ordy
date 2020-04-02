package com.ordy.app.util

import android.view.View
import com.google.android.material.snackbar.Snackbar

class SnackbarUtil {

    companion object {

        var snackbars: MutableMap<View, Snackbar> = mutableMapOf()

        /**
         * Open a snackbar
         */
        fun openSnackbar(view: View, text: String, duration: Int = Snackbar.LENGTH_INDEFINITE) {
            val snackbar = Snackbar.make(view, text, duration)
            snackbars[view] = snackbar

            // Dismiss the snackbar
            snackbar.setAction("x") {
                // Leave empty for dismiss of snackbar
            }

            // Show the snackbar
            snackbar.show()
        }

        /**
         * Close a snackbar
         */
        fun closeSnackbar(view: View) {

            if(snackbars.containsKey(view)) {
                snackbars[view]?.dismiss()
            }
        }
    }
}