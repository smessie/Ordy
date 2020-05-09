package com.ordy.app.util

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.ordy.app.R
import com.ordy.app.util.types.SnackbarType

class SnackbarUtil {

    companion object {

        private var snackbars: MutableMap<FragmentActivity, Snackbar> = mutableMapOf()

        /**
         * Open a snackbar
         */
        fun openSnackbar(
            text: String,
            activity: FragmentActivity?,
            duration: Int = Snackbar.LENGTH_INDEFINITE,
            type: SnackbarType = SnackbarType.INFO
        ) {
            if (activity != null) {

                val view = activity.findViewById<ViewGroup>(android.R.id.content)

                if (view != null) {
                    val snackbar = Snackbar.make(view, text, duration)

                    val color: String = when (type) {
                        SnackbarType.ERROR -> "#E74C3C"
                        SnackbarType.SUCCESS -> "#2ECC71"
                        else -> ""
                    }

                    if (!color.isBlank()) {
                        snackbar.setBackgroundTint(Color.parseColor(color))
                    }

                    snackbars[activity] = snackbar

                    // Spawn the snackbar above the bottom bar.
                    if (view.findViewById<BottomNavigationView>(R.id.nav_view) != null) {
                        snackbar.anchorView = view.findViewById(R.id.nav_view)
                    }

                    // Show the snackbar
                    snackbar.show()
                }
            }
        }

        /**
         * Close a snackbar
         */
        fun closeSnackbar(activity: FragmentActivity?) {

            if (snackbars.containsKey(activity)) {
                snackbars[activity]?.dismiss()
            }
        }
    }
}