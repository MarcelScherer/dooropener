package com.scherer.garage

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import java.util.*

class BootAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        // Intent part
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 123456789, intent, 0);
        // Alarm time
        val calendar: Calendar = GregorianCalendar()
        val now: Calendar = GregorianCalendar()
        calendar.set(Calendar.HOUR_OF_DAY, context.resources.getInteger(R.integer.AlarmHour))
        calendar.set(Calendar.MINUTE, context.resources.getInteger(R.integer.AlarmMinutes))
        if (calendar.before(now)) {
            calendar.add(Calendar.DATE, 1);
        }
        Log.d(
            "Meine App", "Day ..." + calendar.get(Calendar.DAY_OF_MONTH) + "." + Integer.toString(
                calendar.get(
                    Calendar.MONTH
                ) + 1
            )
        );
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Set with system Alarm Service
        // Other possible functions: setExact() / setRepeating() / setWindow(), etc
        alarmManager.run {
            // Set with system Alarm Service
            // Other possible functions: setExact() / setRepeating() / setWindow(), etc
            setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )

        }
    }
}
