package com.scherer.garage;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    static int HOURS   = 21;
    static int MINUTES = 0;

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

    Calendar set_calendar_timer(int hours, int minutes){
        Calendar cur_cal = new GregorianCalendar();
        Calendar cal = new GregorianCalendar();
        //set the current time and date for this calendar
        cur_cal.setTimeInMillis(System.currentTimeMillis());
        if(cur_cal.get(Calendar.HOUR_OF_DAY) > hours){
            cal.add(Calendar.DAY_OF_YEAR, cur_cal.get(Calendar.DAY_OF_YEAR) + 1);
        }else if(    cur_cal.get(Calendar.HOUR_OF_DAY) == hours
                  && cur_cal.get(Calendar.MINUTE) > minutes){
            cal.add(Calendar.DAY_OF_YEAR, cur_cal.get(Calendar.DAY_OF_YEAR) + 1);
        }else{
            cal.add(Calendar.DAY_OF_YEAR, cur_cal.get(Calendar.DAY_OF_YEAR));
        }
        cal.set(Calendar.HOUR_OF_DAY, hours);
        cal.set(Calendar.MINUTE, minutes);
        cal.set(Calendar.SECOND, cur_cal.get(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cur_cal.get(Calendar.MILLISECOND));
        cal.set(Calendar.DATE, cur_cal.get(Calendar.DATE));
        cal.set(Calendar.MONTH, cur_cal.get(Calendar.MONTH));

        return cal;
    }
}
