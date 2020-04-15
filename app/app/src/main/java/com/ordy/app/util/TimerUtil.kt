package com.ordy.app.util

import androidx.appcompat.app.AppCompatActivity
import java.util.*

class TimerUtil {

    companion object {

        /**
         * Update the UI every given interval.
         * @param activity Activity to update
         * @param delay Initial delay for first execution
         * @param period Period between executions
         * @param action Action to execute
         */
        fun updateUI(activity: AppCompatActivity, delay: Long, period: Long, action: () -> Unit): Timer {

            val timer = Timer()

            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    activity.runOnUiThread {
                        action()
                    }
                }
            }, delay, period)

            return timer
        }
    }
}