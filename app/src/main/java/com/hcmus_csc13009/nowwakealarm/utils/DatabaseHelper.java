package com.hcmus_csc13009.nowwakealarm.utils;

import com.hcmus_csc13009.nowwakealarm.models.Alarm;

public interface DatabaseHelper {
    void onToggle(Alarm alarm);
    void onDelete(Alarm alarm);
    void onUpdate(Alarm alarm);
}
