package com.qqdd.lottery.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.qqdd.lottery.data.Constants;
import com.qqdd.lottery.data.Lottery;

import java.util.Calendar;

/**
 * Created by danliu on 2/14/16.
 */
public class AlarmHelper {

    public static void registerNextAlarm(final Context context, Lottery.Type type) {
        final String action = getAction(type);
        if (action == null) {
            return;
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        final long time = getTime(type);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);

    }

    private static long getTime(Lottery.Type type) {
        Calendar calendar = Calendar.getInstance();
        Calendar destCalendar = Calendar.getInstance();
        final int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        final int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (type == Lottery.Type.DLT) {
            if (dayOfWeek < Calendar.MONDAY) {
                destCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            } else if (dayOfWeek == Calendar.MONDAY && hourOfDay >= 21) {
                destCalendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
            } else if (dayOfWeek < Calendar.WEDNESDAY) {
                destCalendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
            } else if (dayOfWeek == Calendar.WEDNESDAY && hourOfDay >= 21) {
                destCalendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
            } else if (dayOfWeek < Calendar.SATURDAY) {
                destCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
            } else if (dayOfWeek == Calendar.SATURDAY && hourOfDay >= 21) {
                destCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                setHourAndMinute(destCalendar,type);
                destCalendar.setTimeInMillis(destCalendar.getTimeInMillis() + Constants.ONE_DAY * 2);
            } else {
                //DO NOTHING.
            }
            setHourAndMinute(destCalendar,type);
        } else if (type == Lottery.Type.SSQ) {
            if (dayOfWeek < Calendar.SUNDAY) {
                destCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            } else if (dayOfWeek == Calendar.SUNDAY && hourOfDay >= 21) {
                destCalendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
            } else if (dayOfWeek < Calendar.TUESDAY) {
                destCalendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
            } else if (dayOfWeek == Calendar.TUESDAY && hourOfDay >= 21) {
                destCalendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
            } else if (dayOfWeek < Calendar.THURSDAY) {
                destCalendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
            } else if (dayOfWeek == Calendar.THURSDAY && hourOfDay >= 21 || dayOfWeek > Calendar.THURSDAY) {
                destCalendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                setHourAndMinute(destCalendar,type);
                destCalendar.setTimeInMillis(destCalendar.getTimeInMillis() + Constants.ONE_DAY * 3);
            } else {
                //DO NOTHING.
            }
            setHourAndMinute(destCalendar,type);
        }
        return destCalendar.getTimeInMillis();
    }

    private static void setHourAndMinute(Calendar destCalendar, Lottery.Type type) {
        if (type == Lottery.Type.DLT) {
            destCalendar.set(Calendar.HOUR_OF_DAY, 21);
            destCalendar.set(Calendar.MINUTE, 0);
            destCalendar.set(Calendar.SECOND, 0);
            destCalendar.set(Calendar.MILLISECOND, 0);
        } else {
            destCalendar.set(Calendar.HOUR_OF_DAY, 22);
            destCalendar.set(Calendar.MINUTE, 15);
            destCalendar.set(Calendar.SECOND, 15);
            destCalendar.set(Calendar.MILLISECOND, 15);
        }

    }

    @Nullable
    private static String getAction(Lottery.Type type) {
        final String action;
        if (type == Lottery.Type.DLT) {
            action = AlarmBroadcastReceiver.ACTION_UPDATE_DLT;
        } else if (type == Lottery.Type.SSQ) {
            action = AlarmBroadcastReceiver.ACTION_UPDATE_SSQ;
        } else {
            return null;
        }
        return action;
    }

}
