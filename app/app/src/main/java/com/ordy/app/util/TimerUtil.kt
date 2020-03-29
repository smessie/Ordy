package com.ordy.app.util

import androidx.appcompat.app.AppCompatActivity
import java.util.*

class TimerUtil {

    companion object {

        /**
         * Update the UI every given interval.
         * @param activity Activity to update
         * @param offset Initial delay for first execution
         * @param delay Delay between executions
         * @param action Action to execute
         */
        fun updateUI(activity: AppCompatActivity, offset: Int, delay: Int, action: () -> Unit) {

            Timer().scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    activity.runOnUiThread {
                        action()
                    }
                }
            }, 0, 1000)
        }
    }
}