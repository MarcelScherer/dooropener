package com.scherer.garage

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
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
const val IP_ADRESS = "ms-schneppenbach.spdns.de"
const val ADRESS_PORT = 2000

class MainActivity : AppCompatActivity() {

    var openButtonState : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main)
        var hide = supportActionBar?.hide()

        val crypto = cryptoHdl("garage", "112358", this)

        getDoor.setOnClickListener (){
            getDoor.text = "wait ..."
            getGarageAsyncTask(getDoor, this).execute();
        }

        setDoor.setOnClickListener(){
            openButtonState = openButtonState + 1
            if(openButtonState == 1) {
                var counter : Int = 5
                val thread: Thread = object : Thread() {
                    override fun run() {
                        try {
                            while (counter > 0 && openButtonState == 1) {
                                sleep(1000)
                                runOnUiThread {
                                    counter -= 1
                                    if(counter <= 0 && openButtonState == 1){
                                        openButtonState = 0
                                        setDoor.text = "open/close"
                                    }else if(counter > 0 && openButtonState == 1){
                                        setDoor.text = "countdown +" + counter.toString()
                                    }
                                }
                            }
                        } catch (e: InterruptedException) {
                        }
                    }
                }

                thread.start()
            }else{
                setDoor.text = "wait ..."
                val AsyncDoor = asyncDoorOpen(IP_ADRESS, ADRESS_PORT, this)
                if (crypto != null) {
                    AsyncDoor.openDoor(setDoor, this, crypto)
                }
            }
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
        if(internetAvailable()) {
            getDoor.text = "wait ..."
            getGarageAsyncTask(getDoor, this).execute();
        }else{
            getDoor.setText("no internet")
        }
        openButtonState = 0

        // Get AlarmManager instance
        Log.d("Scherer", "Start Timer")

        setDoor.setText("open/close ")

        // Intent part
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 123456789, intent, 0);
        // Alarm time
        val calendar: Calendar = GregorianCalendar()
        val now: Calendar = GregorianCalendar()
        calendar.set(Calendar.HOUR_OF_DAY, this.resources.getInteger(R.integer.AlarmHour))
        calendar.set(Calendar.MINUTE, this.resources.getInteger(R.integer.AlarmMinutes))
        if(calendar.before(now)){
            calendar.add(Calendar.DATE, 1);
        }
        Log.d("Meine App", "Day ..." + calendar.get(Calendar.DAY_OF_MONTH) + "." + Integer.toString(calendar.get(Calendar.MONTH) + 1));
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Set with system Alarm Service
        // Other possible functions: setExact() / setRepeating() / setWindow(), etc
        alarmManager.run {
            // Set with system Alarm Service
            // Other possible functions: setExact() / setRepeating() / setWindow(), etc
            setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
        }
    }

    class getGarageAsyncTask(private var getTextView : TextView, private var context : Context) : AsyncTask<Void, Void, Void>() {
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
            Log.d("Meine App", "post asynctask ...");
            if (server_response == 2 && status1 == open && status2 == close) {
                getTextView.setText("Garage close")
            } else if (server_response == 2 && status1 == close && status2 == open) {
                getTextView.setText("Garage open")
            } else if (server_response == 2) {
                getTextView.setText("Garage open")
            }
            else
            {
                getTextView.setText(server_response.toString() + " " + status1.toString() + " " + status2.toString())
                Toast.makeText(context,"Server Error: " + server_response.toString() + " "
                        + status1.toString() + " "
                        + status2.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }

    public fun internetAvailable(): Boolean {
        return try {
            val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("Meine App", "Fehler beim Internetcheck")
            false
        }
    }
}
