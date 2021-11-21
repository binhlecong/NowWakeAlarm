package com.hcmus_csc13009.nowwakealarm.utils;

import java.util.Calendar;

public final class DayUtil {
    public static final String toDay(int day) throws Exception {
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

    public static String getDay(int hour, int minute) {
        // Get "Today" or "Tomorrow" based on hour and minute
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // if alarm time has already passed
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            return "Tomorrow";
        } else {
            return "Today";
        }
    }
}