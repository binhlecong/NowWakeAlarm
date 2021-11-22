package com.hcmus_csc13009.nowwakealarm.utils;

import android.location.Location;

final public class MapUtil {
    static public double getDistance(double lat1, double lng1, double lat2, double lng2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lng1, lat2, lng2, results);
        return results[0];
    }
}
