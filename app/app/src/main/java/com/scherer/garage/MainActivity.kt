package com.scherer.garage

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.util.*


const val CHANNEL_ID = "GARAGE_CHANNEL_ID"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main)
        var hide = supportActionBar?.hide()

        getDoor.setOnClickListener (){
            getDoor.text = "-"
            getGarageAsyncTask(getDoor).execute();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel name
            val name = "Channel"
            val descriptionText = "Simple channel example"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                // Disable badged notifications
                //setShowBadge(false)
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onResume() {
        super.onResume()
        stopService(Intent(this, notification::class.java))
        // get status
        getGarageAsyncTask(getDoor).execute();
        // Get AlarmManager instance
        Log.d("Scherer", "Start Timer")

        setDoor.setText("open/close ")

        // Intent part
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 123456789, intent, 0);
        // Alarm time
        val calendar: Calendar = GregorianCalendar()
        val now: Calendar = GregorianCalendar()
        calendar.set(Calendar.HOUR_OF_DAY, 21)
        calendar.set(Calendar.MINUTE, 0)
        if(calendar.before(now)){
            calendar.add(Calendar.DATE, 1);
        }
        Toast.makeText(this, "Broadcast creating :" + calendar.get(Calendar.DAY_OF_MONTH).toString()
            + "." + calendar.get(Calendar.MONTH).toString()
            + "." + calendar.get(Calendar.YEAR).toString(), Toast.LENGTH_LONG).show()
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Set with system Alarm Service
        // Other possible functions: setExact() / setRepeating() / setWindow(), etc
        alarmManager.run {
            // Set with system Alarm Service
            // Other possible functions: setExact() / setRepeating() / setWindow(), etc
            setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
        }
    }

    class getGarageAsyncTask(private var getTextView : TextView) : AsyncTask<Void, Void, Void>() {
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
            Log.d("Meine App", "post asynctask ...");
            if (server_response == 2 && status1 == open && status2 == close) {
                getTextView.setText("Garage close")
            } else if (server_response == 2 && status1 == close && status2 == open) {
                getTextView.setText("Garage open")
            } else if (server_response == 2) {
                getTextView.setText("Garage open")
            }
        }
    }
}
