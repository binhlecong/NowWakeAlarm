package com.hcmus_csc13009.nowwakealarm.utils;

import android.content.Context;
import android.content.SharedPreferences;

final public class SettingConstant {
    final static private String REQUEST_FOR_NEARBY_RANGE = "NEARY_RANGE_NOW_WAKE_ALARM";
    final static private String REQUEST_FOR_DEFAULT_RINGSTONE = "REQUEST_FOR_DEFAULT_RINGSTONE";

    private static SharedPreferences pref = null;

    public static float getNearbyRange(Context context) {
        if (pref == null) {
            pref = context.getSharedPreferences(REQUEST_FOR_NEARBY_RANGE, 0);
        }
//        SharedPreferences.Editor editor = pref.edit();
        return pref.getFloat(REQUEST_FOR_NEARBY_RANGE, 1000);
    }

    public static void updateNearbyRange(Context context, float value) {
        if (pref == null) {
            pref = context.getSharedPreferences(REQUEST_FOR_NEARBY_RANGE, 0);
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat(REQUEST_FOR_NEARBY_RANGE, value);
        editor.apply();
    }

    public static String getDefaultRingtone(Context context) {
        if (pref == null) {
            pref = context.getSharedPreferences(REQUEST_FOR_NEARBY_RANGE, 0);
        }
        return pref.getString(REQUEST_FOR_DEFAULT_RINGSTONE, null);
    }

    public static void updateDefaultRingtone(Context context, String value) {
        if (pref == null) {
            pref = context.getSharedPreferences(REQUEST_FOR_NEARBY_RANGE, 0);
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(REQUEST_FOR_DEFAULT_RINGSTONE, value);
        editor.apply();
    }
}
