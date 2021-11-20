package com.hcmus_csc13009.nowwakealarm.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AlarmUtils {
    private static SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("hh:mm", Locale.getDefault());

    public static String getHourMinute(long time) {
        return TIME_FORMAT.format(time);
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
}
