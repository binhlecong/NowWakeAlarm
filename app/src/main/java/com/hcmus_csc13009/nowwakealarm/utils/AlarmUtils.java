package com.hcmus_csc13009.nowwakealarm.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.hcmus_csc13009.nowwakealarm.R;
import com.hcmus_csc13009.nowwakealarm.models.Alarm;
import com.hcmus_csc13009.nowwakealarm.receiver.AlarmBroadcastReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmUtils {
    private static final Calendar calendar = Calendar.getInstance();
    private static SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("hh:mm", Locale.getDefault());

    public static String getHourMinute(long time) {
        return TIME_FORMAT.format(time);
    }

    public static long getTimeMillis(int hour, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTimeInMillis();
    }

    public static String getDaysInWeek(int mask) {
        StringBuilder result = new StringBuilder();
        for (WeekDays w : WeekDays.values()) {
            int x = w.ordinal();
            if ((mask >> x & 1) == 1) {
                result.append(w.toString());
                result.append(" ");
            }
        }
        return result.toString();
    }

    public static byte getBitFormat(WeekDays... days) {
        byte result = 0;
        for (WeekDays w : days) {
            int x = w.ordinal();
            result |= 1 << x;
        }
        return result;
    }

    public static byte getBitFormat(boolean... days) {
        byte result = 0;
        for (int i = 0; i < 7; ++i) {
            result |= (days[i] ? 1 : 0) << i;
        }
        return result;
    }

    public static String toWeekDay(int day) throws Exception {
        // Get week day name in String
        switch (day) {
            case Calendar.SUNDAY:
                return "Sunday";
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
        }
        throw new Exception("Could not locate day");
    }

    public static void scheduleAlarm(Context context, Alarm alarm) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(context.getString(R.string.arg_alarm_obj), alarm);
        intent.putExtra(context.getString(R.string.bundle_alarm_obj), bundle);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context,
                alarm.getRequestCode(),
                intent, 0);

        Calendar alarmCalendar = Calendar.getInstance();
        alarmCalendar.setTimeInMillis(alarm.getTime());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, alarmCalendar.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, alarmCalendar.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // if alarm time has already passed, increment day by 1
        if (alarm.getTime() <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        if (!alarm.isRepeatMode()) {
            String toastText = null;
            try {
                toastText = String.format("One-time Alarm %s scheduled for %s at %02d:%02d",
                        alarm.getTitle(),
                        toWeekDay(calendar.get(Calendar.DAY_OF_WEEK)),
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();

            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    alarmPendingIntent);
        } else {
            String toastText = String.format("Repeating Alarm %s scheduled for %s at %02d:%02d",
                    alarm.getTitle(),
                    getRecurringDaysText(alarm),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE));
            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();

            final long RUN_DAILY = 24 * 60 * 60 * 1000;
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    RUN_DAILY,
                    alarmPendingIntent);
        }

        alarm.setEnable(true);
    }

    public static String getRecurringDaysText(Alarm alarm) {
        if (!alarm.isRepeatMode()) {
            return null;
        }

        String days = "";
        if (alarm.isRepeatAt(WeekDays.MON)) {
            days += "Mo ";
        }
        if (alarm.isRepeatAt(WeekDays.TUE)) {
            days += "Tu ";
        }
        if (alarm.isRepeatAt(WeekDays.WED)) {
            days += "We ";
        }
        if (alarm.isRepeatAt(WeekDays.THU)) {
            days += "Th ";
        }
        if (alarm.isRepeatAt(WeekDays.FRI)) {
            days += "Fr ";
        }
        if (alarm.isRepeatAt(WeekDays.SAT)) {
            days += "Sa ";
        }
        if (alarm.isRepeatAt(WeekDays.SUN)) {
            days += "Su ";
        }

        return days;
    }

    public static void cancelAlarm(Context context, Alarm alarm) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context,
                alarm.getRequestCode(),
                intent, 0);
        alarmManager.cancel(alarmPendingIntent);
        alarm.setEnable(false);
        String toastText = String.format("Alarm %s cancelled", alarm.getTitle());
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
    }

}
