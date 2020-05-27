package com.scherer.garage

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.*


class notification : Service() {

    override fun onCreate() {
        Log.d("Meine App", "onCreate Service");
        super.onCreate()

    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        Log.d("Meine App", "onStart Service");
        val notifyString:String = intent?.getStringExtra("NotificationText") ?: "-"

        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = "my_channel_01"
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )
            Log.d("Meine App", "create Notification");
            val vibration : LongArray = longArrayOf(1000, 1000, 1000, 1000, 1000)

            val notificationIntent = Intent(this, MainActivity::class.java)
            val contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT            )
            // create notification builder
            val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Garage")
                .setContentText(notifyString)
                .setOngoing(false)
                .setVibrate(vibration)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(contentIntent)
                .setLights(Color.RED, 3000, 3000)
            .setSmallIcon(R.drawable.garage).build()
            Log.d("Meine App", "start Notification");
            startForeground( createID(), notification)
        }

 //       Handler().postDelayed({
 //           stopSelf()
 //       }, 1000*60)
    }

    fun createID(): Int {
        val now: Date = Date();
        val id = Integer.parseInt(SimpleDateFormat("ddHHmmss", Locale.US).format(now));
        return id;
    }

    //We need to declare the receiver with onReceive function as below
    protected var stopServiceReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            stopSelf()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

