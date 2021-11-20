package com.hcmus_csc13009.nowwakealarm.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.Room;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.hcmus_csc13009.nowwakealarm.models.Alarm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Alarm.class}, version = 1, exportSchema = false)
public abstract class AlarmRoomDatabase extends RoomDatabase {
    static public AlarmRoomDatabase INSTANCE = null;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    final private static Callback sOnOpenCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            // do something to test database
        }
    };

    public abstract AlarmDao alarmDao();

    public static AlarmRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AlarmRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AlarmRoomDatabase.class, "alarm_database")
                            .addCallback(sOnOpenCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
