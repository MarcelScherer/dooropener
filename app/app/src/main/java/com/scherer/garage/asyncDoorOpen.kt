package com.scherer.garage

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.TextView
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket

class asyncDoorOpen {

    private var ip : String ? = " "
    private var port : Int ? = 0
    private var context : Context

    constructor(ip : String, port : Int, context : Context)  {
        this.ip = ip
        this.port = port
        this.context = context
    }

    class DoorOpenAsyncTask(private var getTextView : TextView, private var context : Context,
                             ip: String, port: Int, cryptoHdl: cryptoHdl) : AsyncTask<Void, Void, Void>() {
        private var server_response: Int = 0
        private var SERVER_IP: String = ip
        private var SERVER_PORT: Int = port
        private var priv_cryptoHdl = cryptoHdl

        // Backgroundtask for reciveing data
        override fun doInBackground(vararg params: Void?): Void? {
            server_response = 1;
            try {
                Log.d("asynDoorOpen", "start socket ...");
                var socket_client: Socket = Socket(SERVER_IP, SERVER_PORT)
                val socket_input = DataInputStream(socket_client.getInputStream())
                val socket_output = DataOutputStream(socket_client.getOutputStream())
                socket_output.writeShort(2)
                Log.d("asynDoorOpen", "write chipher");
                val timestamp =socket_input.readInt()
                Log.d("asynDoorOpen", "receive timestamp: " + timestamp.toString());
                val chipher : ByteArray? = priv_cryptoHdl.SignString(timestamp.toString())
                socket_output.write(chipher)
                socket_input.close();
                socket_output.close();
                socket_client.close();
                Log.d("asynDoorOpen", "Close Socket ...");
                server_response = 2;
            } catch (e: IOException) {
                e.printStackTrace();
                Log.d("asynDoorOpen", "Fehler beim empfgang...");
            }
            return null;
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            if(server_response == 2){
                getTextView.setText("open/close")
            }else{
                getTextView.setText(" ..error..")
            }
        }
    }

    public fun openDoor(getTextView : TextView, context : Context, cryptoHdl: cryptoHdl){
        this.ip?.let { this!!.port?.let { it1 ->
            DoorOpenAsyncTask(getTextView, context, it, it1, cryptoHdl).execute()
        } };
    }

}