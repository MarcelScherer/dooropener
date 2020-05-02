package com.scherer.garage;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    public static int HOURS   = 17;
    public static int MINUTES = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent myIntent = new Intent(MainActivity.this, MyUpdateService.class);
        PendingIntent  pendingIntent = PendingIntent.getService(MainActivity.this, 0, myIntent, 0);
        Calendar calendar = new GregorianCalendar();
        // set alarm time
        calendar = set_calendar_timer(HOURS, MINUTES);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // Start service every day at 21:00
        alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public static Calendar set_calendar_timer(int hours, int minutes){
        Calendar cur_cal =  Calendar.getInstance();
        Calendar cal = new GregorianCalendar();
        //set the current time and date for this calendar
        cur_cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.DAY_OF_YEAR, cur_cal.get(Calendar.DAY_OF_YEAR));
        if(cur_cal.get(Calendar.HOUR_OF_DAY) > hours){
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }else if(    cur_cal.get(Calendar.HOUR_OF_DAY) == hours
                  && cur_cal.get(Calendar.MINUTE) >= minutes) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        cal.set(Calendar.HOUR_OF_DAY, hours);
        cal.set(Calendar.MINUTE, minutes);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.YEAR, cur_cal.get(Calendar.YEAR));

        Log.d("Scherer","set new alarm: " );
        Log.d("Scherer","Day: " + Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
        Log.d("Scherer","Month: " + Integer.toString(cal.get(Calendar.MONTH)+1));
        Log.d("Scherer","Hour: " + Integer.toString(cal.get(Calendar.HOUR_OF_DAY)));
        Log.d("Scherer","Minutes: " + Integer.toString(cal.get(Calendar.MINUTE)));
        return cal;
    }
}
