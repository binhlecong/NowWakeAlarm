package com.hcmus_csc13009.nowwakealarm.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;
import com.hcmus_csc13009.nowwakealarm.utils.WeekDays;

import java.io.Serializable;

@Entity(tableName = "alarm_table")
public class Alarm implements Serializable {
    @PrimaryKey
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
    @ColumnInfo(name = "address")
    private String address;


    @Ignore
    public Alarm(int id, long time, String title, String description, String ringtoneUri,
                 boolean isEnable, boolean hardMode, boolean vibrateMode, boolean repeatMode,
                 byte daysInWeek) {
        this(id, time, title, description, ringtoneUri, isEnable, hardMode, vibrateMode,
                repeatMode, daysInWeek, null, null, null);
    }

    public Alarm(int id, long time, String title, String description, String ringtoneUri,
                 boolean isEnable, boolean hardMode, boolean vibrateMode, boolean repeatMode,
                 byte daysInWeek, String tagUri, String position, String address) {
        this.ID = id;
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
        this.address = address;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRingtoneUri() {
        return ringtoneUri;
    }

    public void setRingtoneUri(String ringtoneUri) {
        this.ringtoneUri = ringtoneUri;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public boolean isHardMode() {
        return hardMode;
    }

    public void setHardMode(boolean hardMode) {
        this.hardMode = hardMode;
    }
    // ------------ setter --------------------

    public boolean isVibrateMode() {
        return vibrateMode;
    }

    public void setVibrateMode(boolean vibrateMode) {
        this.vibrateMode = vibrateMode;
    }

    public boolean isRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(boolean repeatMode) {
        this.repeatMode = repeatMode;
    }

    public byte getDaysInWeek() {
        return daysInWeek;
    }

    public void setDaysInWeek(byte daysInWeek) {
        this.daysInWeek = daysInWeek;
    }

    public boolean isRepeatAt(WeekDays day) {
        int x = day.ordinal();
        return (daysInWeek >> x & 1) == 1; //if bit x on then it repeated
    }

    public String getTagUri() {
        return tagUri;
    }

    public void setTagUri(String tagUri) {
        this.tagUri = tagUri;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setPosition(LatLng position) {
        this.position = position.latitude + "," + position.longitude;
    }

    public LatLng getLatLngPosition() {
        if (position == null)
            return null;
        String[] pos = position.split(",");
        return new LatLng(Double.parseDouble(pos[0]), Double.parseDouble(pos[1]));
    }

    public void setRepeatAt(WeekDays day, boolean isRepeat) {
        int x = day.ordinal();
        if (isRepeat)
            this.daysInWeek |= 1 << x;
        else
            this.daysInWeek &= ~(1 << x);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
