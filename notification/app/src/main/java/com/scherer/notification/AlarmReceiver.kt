package com.scherer.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Is triggered when alarm goes off, i.e. receiving a system broadcast
        Log.d("Scherer", "Brodcast is running now")

        val builder = NotificationCompat.Builder(context, "GARAGE_CHANNEL_ID")
            .setSmallIcon(R.drawable.garage)
            .setContentTitle("Garage")
            .setContentText("the garage is detected as open")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        if (Build.VERSION.SDK_INT >= 21) builder.setVibrate(LongArray(0))

        with(NotificationManagerCompat.from(context)) {
            notify(createID(), builder.build())
        }
    }

    // create ID for Notification
    fun createID():Int{
        val now: Date = Date();
        val id = Integer.parseInt(SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
        return id;
    }
}
