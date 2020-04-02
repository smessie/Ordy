package com.ordy.app.util

import android.graphics.Color
import android.view.View
import com.google.android.material.snackbar.Snackbar

class SnackbarUtil {

    companion object {

        var snackbars: MutableMap<View, Snackbar> = mutableMapOf()

        /**
         * Open a snackbar
         */
        fun openSnackbar(
            text: String,
            view: View,
            duration: Int = Snackbar.LENGTH_INDEFINITE,
            type: SnackbarType = SnackbarType.INFO
        ) {
            val snackbar = Snackbar.make(view, text, duration)

            val color: String = when(type) {
                SnackbarType.ERROR -> "#E74C3C"
                SnackbarType.SUCCESS -> "#2ECC71"
                else -> ""
            }

            if(!color.isBlank()) {
                snackbar.setBackgroundTint(Color.parseColor(color))
            }

            snackbars[view] = snackbar

            // Show the snackbar
            snackbar.show()
        }

        /**
         * Close a snackbar
         */
        fun closeSnackbar(view: View) {

            if (snackbars.containsKey(view)) {
                snackbars[view]?.dismiss()
            }
        }
    }
}

enum class SnackbarType {
    SUCCESS,
    INFO,
    ERROR
}