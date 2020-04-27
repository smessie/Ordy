package com.ordy.app.util

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.format.DateFormat
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.lifecycle.MutableLiveData
import com.ordy.app.R
import com.ordy.app.api.util.ErrorHandler
import java.util.*

class PickerUtil {

    companion object {

        fun openDateTimePicker(
            liveData: MutableLiveData<Date>,
            context: Context,
            view: View,
            disablePast: Boolean
        ) {

            val calendar: Calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY) + 1
            val minute = 0

            val returnCalendar = Calendar.getInstance()

            val timePicker = TimePickerDialog(
                context,
                { _: TimePicker, hour: Int, minute: Int ->
                    returnCalendar.set(Calendar.HOUR_OF_DAY, hour)
                    returnCalendar.set(Calendar.MINUTE, minute)

                    // When the date is the current date & disablePast is true,
                    // prevent selection of a time in the past
                    if (disablePast && System.currentTimeMillis() >= returnCalendar.timeInMillis) {
                        ErrorHandler().handleRawGeneral(
                            context.getString(R.string.error_picker_past),
                            view
                        )
                    } else {
                        // Update the livedata
                        liveData.postValue(returnCalendar.time)
                    }
                },
                hour,
                minute,
                DateFormat.is24HourFormat(context)
            )

            val datePicker = DatePickerDialog(
                context,
                { _: DatePicker, year: Int, month: Int, date: Int ->
                    returnCalendar.set(Calendar.YEAR, year)
                    returnCalendar.set(Calendar.MONTH, month)
                    returnCalendar.set(Calendar.DATE, date)

                    // Show the time picker.
                    timePicker.show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE)
            )

            // When disable past is enabled, prevent the selection of a date that has already passed.
            if (disablePast) {
                datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
            }

            datePicker.show()
        }
    }
}