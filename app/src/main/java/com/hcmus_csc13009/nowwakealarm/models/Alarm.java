package com.hcmus_csc13009.nowwakealarm.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.hcmus_csc13009.nowwakealarm.utils.WeekDays;

import java.io.Serializable;

@Entity(tableName = "alarm_table")
public class Alarm implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int ID;

    @ColumnInfo(name = "time")
    private long time; // hour:min in millis
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "ringtone")
    private String ringtoneUri;
    @ColumnInfo(name = "is_enable")
    private boolean isEnable; // if alarm is set
    @ColumnInfo(name = "hard_mode")
    private boolean hardMode; // true if user really want to wake up
    @ColumnInfo(name = "vibrate_mode")
    private boolean vibrateMode; // true if alarm vibrate
    @ColumnInfo(name = "repeat_mode")
    private boolean repeatMode; // false if only one time
    @ColumnInfo(name = "days_in_week")
    private byte daysInWeek; // bit i set 1 if day i repeated
    // optional field
    @ColumnInfo(name = "tag_uri")
    private String tagUri; // use for open a website such as zoom/meet/....
    @ColumnInfo(name = "position")
    private String position; // string store LatLng


    @Ignore
    public Alarm(long time, String title, String description, String ringtoneUri,
                 boolean isEnable, boolean hardMode, boolean vibrateMode, boolean repeatMode, byte daysInWeek) {
        this.time = time;
        this.title = title;
        this.description = description;
        this.ringtoneUri = ringtoneUri;
        this.isEnable = isEnable;
        this.hardMode = hardMode;
        this.vibrateMode = vibrateMode;
        this.repeatMode = repeatMode;
        this.daysInWeek = daysInWeek;
    }

    public Alarm(long time, String title, String description, String ringtoneUri,
                 boolean isEnable, boolean hardMode, boolean vibrateMode, boolean repeatMode, byte daysInWeek,
                 String tagUri, String position) {
        this.time = time;
        this.title = title;
        this.description = description;
        this.ringtoneUri = ringtoneUri;
        this.isEnable = isEnable;
        this.hardMode = hardMode;
        this.vibrateMode = vibrateMode;
        this.repeatMode = repeatMode;
        this.daysInWeek = daysInWeek;
        this.tagUri = tagUri;
        this.position = position;
    }

    public int getID() {
        return ID;
    }

    public long getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getRingtoneUri() {
        return ringtoneUri;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public boolean isHardMode() {
        return hardMode;
    }

    public boolean isVibrateMode() {
        return vibrateMode;
    }

    public boolean isRepeatMode() {
        return repeatMode;
    }

    public byte getDaysInWeek() {
        return daysInWeek;
    }

    public boolean isRepeatAt(WeekDays day) {
        int x = day.ordinal();
        return (daysInWeek >> x & 1) == 1; //if bit x on then it repeated
    }

    public String getTagUri() {
        return tagUri;
    }

    public String getPosition() {
        return position;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRingtoneUri(String ringtoneUri) {
        this.ringtoneUri = ringtoneUri;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public void setHardMode(boolean hardMode) {
        this.hardMode = hardMode;
    }

    public void setVibrateMode(boolean vibrateMode) {
        this.vibrateMode = vibrateMode;
    }

    public void setRepeatMode(boolean repeatMode) {
        this.repeatMode = repeatMode;
    }

    public void setDaysInWeek(byte daysInWeek) {
        this.daysInWeek = daysInWeek;
    }

    public void setRepeatAt(WeekDays day, boolean isRepeat) {
        int x = day.ordinal();
        if (isRepeat)
            this.daysInWeek |= 1 << x;
        else
            this.daysInWeek &= ~(1 << x);
    }

    public void setTagUri(String tagUri) {
        this.tagUri = tagUri;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
