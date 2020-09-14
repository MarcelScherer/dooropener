package com.scherer.garage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("Meine App", "start AlramReveicer ...");
        // Is triggered when alarm goes off, i.e. receiving a system broadcast
        var intent = Intent(context, notification::class.java)
        if (context != null) {
            Log.d("Meine App", "start intent for data_hdl ...");
            context.startForegroundService(intent)
        }
    }
}

