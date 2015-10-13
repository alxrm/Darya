package com.rm.darya.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rm.darya.util.updating.CurrencyUpdateReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by alex
 */

public class TimeUtil {

    public static void setAlarm(Context context) {
        Intent alarmIntent = new Intent(context, CurrencyUpdateReceiver.class);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                alarmIntent,
                0
        );

        manager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

    public static long unixTime() {

        return System.currentTimeMillis()/1000;
    }

    public static String getDay(long unix) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM", new Locale("ru", "RU"));
        Date d = new Date();
        String resDate;

        d.setTime(unix * 1000);
        dateFormat.applyPattern("d MMMM");

        resDate = dateFormat.format(d);

        Log.d("TimeUtil", "getDay - resDate: "
                + resDate);

        return resDate;
    }

    public static long getStartOfTheDay(long time) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time*1000);

        int year    = calendar.get(Calendar.YEAR);
        int month   = calendar.get(Calendar.MONTH);
        int day     = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.set(year, month, day, 0, 0, 0);

        long result = calendar.getTimeInMillis()/1000;

        Log.d("TimeUtil", "Today in millis: " + result);

        return result;
    }

    public static long getToday() {
        return getStartOfTheDay(unixTime());
    }
}
