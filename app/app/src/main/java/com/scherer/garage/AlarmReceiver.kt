package com.scherer.garage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import androidx.core.content.ContextCompat
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Is triggered when alarm goes off, i.e. receiving a system broadcast
        var intent = Intent(context, notification::class.java)

        GarageAsyncTask(intent, context).execute();
        Log.d("Meine App", "start AlramReveicer asyncTask ...");
    }

    class GarageAsyncTask(
        private var privateIntent: Intent, private var privateContext: Context ) : AsyncTask<Void, Void, Void>() {
        var server_response: Int = 0
        var SERVER_IP: String = "ms-schneppenbach.spdns.de"
        var SERVER_PORT: Int = 2000
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
            var notifText : String ? = "default"
            Log.d("Meine App", "post asynctask ...");
            if (server_response == 2 && status1 == open && status2 == close) {
                notifText = "Garage close"
            } else if (server_response == 2 && status1 == close && status2 == open) {
                notifText = "Garage open"
            } else if (server_response == 2) {
                notifText = "Garage open"
            }else if(!mActivity.internetAvailable()){
                notifText = "no internet"
            } else {
                notifText = "Error"
            }
            Log.d("Meine App", "start Service with text:" + notifText);
            privateIntent.putExtra("NotificationText", notifText);
            ContextCompat.startForegroundService(privateContext, privateIntent );
        }
    }
}

