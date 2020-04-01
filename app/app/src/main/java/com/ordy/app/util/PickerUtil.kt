package com.ordy.app.util

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.lifecycle.MutableLiveData
import java.text.DateFormat
import java.util.*

class PickerUtil {

    companion object {

        fun openDateTimePicker(liveData: MutableLiveData<Date>, context: Context) {

            val calendar: Calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR) + 1
            val minute = 0

            val returnCalendar = Calendar.getInstance()

            val timePicker = TimePickerDialog(
                context,
                { timePicker: TimePicker, hour: Int, minute: Int ->
                    returnCalendar.set(Calendar.HOUR, hour)
                    returnCalendar.set(Calendar.MINUTE, minute)

                    Log.i("BANAAN", returnCalendar.time.toString())

                    // Update the livedata
                    liveData.postValue(returnCalendar.time)
                },
                hour,
                minute,
                true
            )

            val datePicker = DatePickerDialog(
                context,
                { datePicker: DatePicker, year: Int, month: Int, date: Int ->
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

            datePicker.show()
        }
    }
}