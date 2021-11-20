package com.hcmus_csc13009.nowwakealarm.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hcmus_csc13009.nowwakealarm.models.Alarm;

import java.util.List;

@Dao
public interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Alarm alarm);

    @Delete
    void delete(Alarm alarm);

    @Update
    void update(Alarm alarm);

    @Query("DELETE FROM alarm_table")
    void deleteAll();

    @Query("SELECT * FROM alarm_table ORDER BY id DESC")
    LiveData<List<Alarm>> getAllAlarms();

    @Query("SELECT * FROM alarm_table LIMIT 1")
    Alarm[] getLastAlarm();

    @Query("SELECT * FROM alarm_table WHERE title LIKE '%' || :filter || '%'")
    LiveData<List<Alarm>> searchByTitle(String filter);

    @Query("SELECT * FROM alarm_table WHERE (days_in_week >> :day & 1) = 1")
    LiveData<List<Alarm>> getByDay(int day);
}
