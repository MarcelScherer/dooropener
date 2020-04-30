package com.scherer.garage;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class MyUpdateService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // This is the Notification Channel ID. More about this in the next section
        String NOTIFICATION_CHANNEL_ID = "channel_id";
        //Notification Channel ID passed as a parameter here will be ignored for all the Android versions below 8.0
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle("This is heading");
        builder.setContentText("This is description");
        Notification notification = builder.build();
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return  null;
    }

}
