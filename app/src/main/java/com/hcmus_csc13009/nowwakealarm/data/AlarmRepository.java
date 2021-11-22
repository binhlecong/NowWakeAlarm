package com.hcmus_csc13009.nowwakealarm.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.hcmus_csc13009.nowwakealarm.models.Alarm;

import java.util.List;

public class AlarmRepository {
    final private AlarmDao alarmDao;
    private LiveData<List<Alarm>> allAlarms;

    public AlarmRepository(Application application) {
        AlarmRoomDatabase db = AlarmRoomDatabase.getDatabase(application);
        alarmDao = db.alarmDao();
        allAlarms = alarmDao.getAllAlarms();
    }

    public LiveData<List<Alarm>> getAllAlarms() {
        return allAlarms;
    }

    public void insert(Alarm alarm) {
        AlarmRoomDatabase.databaseWriteExecutor.execute(() -> {
            alarmDao.insert(alarm);
        });
    }

    public void delete(Alarm alarm) {
        AlarmRoomDatabase.databaseWriteExecutor.execute(() -> {
            alarmDao.delete(alarm);
        });
    }

    public void update(Alarm alarm) {
        AlarmRoomDatabase.databaseWriteExecutor.execute(() -> {
            alarmDao.update(alarm);
        });
    }

    public void deleteAll() {
        AlarmRoomDatabase.databaseWriteExecutor.execute(alarmDao::deleteAll);
    }

    public LiveData<List<Alarm>> searchByTitle(String title) {
        return alarmDao.searchByTitle(title);
    }

    public Alarm[] getLastAlarm() {
        return alarmDao.getLastAlarm();
    }

    public String getNearbyRange() {
        return alarmDao.getNearbyRange();
    }
}
