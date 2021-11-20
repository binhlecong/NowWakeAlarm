package com.hcmus_csc13009.nowwakealarm.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hcmus_csc13009.nowwakealarm.data.AlarmRepository;
import com.hcmus_csc13009.nowwakealarm.models.Alarm;

import java.util.List;

public class AlarmViewModel extends AndroidViewModel {
    final private AlarmRepository repository;
    private LiveData<List<Alarm>> allAlarms;

    public AlarmViewModel(@NonNull Application application) {
        super(application);
        repository = new AlarmRepository(application);
        allAlarms = repository.getAllAlarms();
    }

    LiveData<List<Alarm>> getAllAlarms() {
        return this.allAlarms;
    }

    public void insert(Alarm alarm) {
        repository.insert(alarm);
    }

    public void delete(Alarm alarm) {
        repository.delete(alarm);
    }

    public void update(Alarm alarm) {
        repository.update(alarm);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    LiveData<List<Alarm>> searchByTitle(String title) {
        return repository.searchByTitle(title);
    }

}
