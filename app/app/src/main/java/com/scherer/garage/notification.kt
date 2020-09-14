package com.scherer.garage

import android.R
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.util.*

class notification : Service() {

    override fun onStart(intent: Intent?, startId: Int) {
        var intent = Intent(this, notification::class.java)

        if(!isOnline(this)){
            val handler = Handler()
            handler.postDelayed(Runnable {
                Log.d("Meine App", "start data_hdl_1 asyncTask ...");
                GarageAsyncTask(intent, this).execute();
            }, 1000)
        }else {
            Log.d("Meine App", "start data_hdl_2 asyncTask ...");
            GarageAsyncTask(intent, this).execute();
        }
        Log.d("Meine App", "start Service with text2 :");
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    class GarageAsyncTask(
        private var privateIntent: Intent, private var privateContext: Context
    ) : AsyncTask<Void, Void, Void>() {
        var server_response: Int = 0
        var SERVER_IP: String = IP_ADRESS
        var SERVER_PORT: Int = ADRESS_PORT
        var status1: Short = 0
        var status2: Short = 0
        val open: Short = 0
        val close: Short = 1

        // Backgroundtask for reciveing data
        override fun doInBackground(vararg params: Void?): Void? {
            server_response = 1;
            try {
                Log.d("Meine App", "start socket ...");
                var socket_client: Socket = Socket(SERVER_IP, SERVER_PORT)
                val socket_input = DataInputStream(socket_client.getInputStream())
                val socket_output = DataOutputStream(socket_client.getOutputStream())
                socket_output.writeShort(1)
                status1 = socket_input.readShort()
                Log.d("Meine App", "status1:" + status1.toString());
                status2 = socket_input.readShort()
                Log.d("Meine App", "status2:" + status2.toString());
                socket_input.close();
                socket_output.close();
                socket_client.close();
                Log.d("Meine App", "Close Socket ...");
                server_response = 2;
            } catch (e: IOException) {
                e.printStackTrace();
                Log.d("Meine App", "Fehler beim empfgang...");
            }
            return null;
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            val mActivity = MainActivity()
            Log.d("Meine App", "post asynctask ...");
            if (server_response == 2 && status1 == open && status2 == close) {
            } else if (server_response == 2 && status1 == close && status2 == open) {
                startNotification("Garage", "Garage open", privateContext);
            } else if (server_response == 2) {
                startNotification("Garage", "Garage open", privateContext);
            } else if (!mActivity.internetAvailable()) {
                startNotification("Garage", "No Internet", privateContext);
            } else {
                startNotification("Garage", "Error", privateContext);
            }
        }

        fun createID(): Int {
            val now: Date = Date();
            val id = Integer.parseInt(SimpleDateFormat("ddHHmmss", Locale.US).format(now));
            return id;
        }

        private fun startNotification( title: String, text: String, context : Context) {
            val CHANNEL_ID = "my_channel_01"
            val NOTIFICATION_CHANNEL_NAME  = "Test"
            initChannel(CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, context)
            val vibration : LongArray = longArrayOf(1000, 1000, 1000, 1000, 1000)

            val notificationIntent = Intent(context, MainActivity::class.java)
            val contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            val notification = NotificationCompat.Builder(context,CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(com.scherer.garage.R.drawable.garage)
                .setContentIntent(contentIntent)
                .setLights(Color.RED, 3000, 3000)
                .setVibrate(vibration)
                //.setLargeIcon(BitmapFactory.decodeResource(this.resources, R.mipmap.ic_launcher))
            //notification.setContentIntent(pendingIntent)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(createID(), notification.build())
        }

        private fun initChannel(channelId: String, channelName: String, context: Context) {
            if (Build.VERSION.SDK_INT < 26) {
                return
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)

            notificationManager.createNotificationChannel(channel)
        }
    }

    fun isOnline(context: Context): Boolean {
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        //should check null because in airplane mode it will be null
        return netInfo != null && netInfo.isConnected
    }
}
