package com.scherer.garage

import android.app.Service
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.AsyncTask
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.util.*


class notification : Service() {
    public val builder = NotificationCompat.Builder(this, "GARAGE_CHANNEL_ID")

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("Scherer", "Brodcast is running now")

        // create notification builder
        builder.setSmallIcon(R.drawable.garage)
            .setContentTitle("Garage")
            .setContentText("-")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        if (Build.VERSION.SDK_INT >= 21) builder.setVibrate(LongArray(0))

        // start asynctask for get data and send notification
        GarageAsyncTask(builder,this).execute()
        return Service.START_STICKY
    }

    class GarageAsyncTask(private var privateBuilder : NotificationCompat.Builder, private var privateContext: Context) : AsyncTask<Void, Void, Void>() {
        var server_response : Int = 0
        var SERVER_IP :String = "ms-schneppenbach.spdns.de"
        var SERVER_PORT : Int = 2000
        var status1 : Short = 0
        var status2 : Short = 0
        val open : Short = 0
        val close : Short = 1

        // Backgroundtask for reciveing data
        override fun doInBackground(vararg params: Void? ): Void? {
            server_response = 1;
            try {
                Log.d("Meine App", "start socket ...");
                var socket_client : Socket= Socket(SERVER_IP, SERVER_PORT)
                val socket_input = DataInputStream(socket_client.getInputStream())
                val socket_output = DataOutputStream(socket_client.getOutputStream())
                socket_output.writeShort(1)
                status1 = socket_input.readShort()
                Log.d("Meine App", "status1:" + status1.toString());
                status2 = socket_input.readShort()
                Log.d("Meine App", "status2:" + status2.toString());
                socket_input.close();
                Log.d("Meine App", "Close Socket ...");
                server_response = 2;
            }catch (e: IOException) {
                e.printStackTrace();
                Log.d("Meine App", "Fehler beim empfgang...");
            }
            return null;
        }


        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            Log.d("Meine App", "post asynctask ...");
            if(server_response == 2 && status1 == open && status2 == close){
                privateBuilder.setContentText("Garage close")
            }else if(server_response == 2 && status1 == close && status2 == open){
                privateBuilder.setContentText("Garage open")
            }else if(server_response == 2){
                privateBuilder.setContentText("Garage not in a fix position")
            }else{
                privateBuilder.setContentText("---ERROR---")
            }

            with(NotificationManagerCompat.from(privateContext)) {
                notify(createID(), privateBuilder.build())
            }
        }

        // create ID for Notification
        fun createID(): Int {
            val now: Date = Date();
            val id = Integer.parseInt(SimpleDateFormat("ddHHmmss", Locale.US).format(now));
            return id;
        }
    }
}
