package com.hcmus_csc13009.nowwakealarm.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.hcmus_csc13009.nowwakealarm.models.Alarm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Alarm.class}, version = 2, exportSchema = false)
public abstract class AlarmRoomDatabase extends RoomDatabase {
    static public AlarmRoomDatabase INSTANCE = null;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(2);

    public abstract AlarmDao alarmDao();

    public static AlarmRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AlarmRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AlarmRoomDatabase.class, "alarm_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
