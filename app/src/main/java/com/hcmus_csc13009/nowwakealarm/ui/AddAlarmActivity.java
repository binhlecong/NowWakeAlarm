package com.hcmus_csc13009.nowwakealarm.ui;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.hcmus_csc13009.nowwakealarm.R;
import com.hcmus_csc13009.nowwakealarm.adapter.AlarmAdapter;
import com.hcmus_csc13009.nowwakealarm.databinding.ActivityAddAlarmBinding;
import com.hcmus_csc13009.nowwakealarm.models.Alarm;
import com.hcmus_csc13009.nowwakealarm.utils.AlarmUtils;
import com.hcmus_csc13009.nowwakealarm.utils.DayUtil;
import com.hcmus_csc13009.nowwakealarm.utils.TimePickerUtil;
import com.hcmus_csc13009.nowwakealarm.utils.WeekDays;
import com.hcmus_csc13009.nowwakealarm.viewmodel.AlarmViewModel;

public class AddAlarmActivity extends AppCompatActivity {
    final static private int REQUEST_FOR_RINGTONE = 5;

    private AlarmViewModel alarmViewModel;
    private ActivityAddAlarmBinding activityAddAlarmBinding;
    private String tone;
    private Alarm alarm;
    private Ringtone ringtone;
    private boolean isVibrate = false;
    private boolean isHard = false;
    private boolean isRepeat = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        alarm = (Alarm) getIntent().getSerializableExtra(AlarmAdapter.ALARM_OBJECT_DATA);

        alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);

        activityAddAlarmBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_alarm);

        tone = RingtoneManager.getActualDefaultRingtoneUri(this,
                RingtoneManager.TYPE_ALARM).toString();
        ringtone = RingtoneManager.getRingtone(this, Uri.parse(tone));

        activityAddAlarmBinding.setToneNameAlarm.setText(ringtone.getTitle(this));

        if (alarm != null) {
            loadAlarmInfo(alarm);
        }

        activityAddAlarmBinding.recurringCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                activityAddAlarmBinding.optionsRecurring.setVisibility(View.VISIBLE);
            } else {
                activityAddAlarmBinding.optionsRecurring.setVisibility(View.GONE);
            }
            isRepeat = isChecked;
        });


        activityAddAlarmBinding.alarmSoundCard.setOnClickListener(view -> {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Sound");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) Uri.parse(tone));
            startActivityForResult(intent, REQUEST_FOR_RINGTONE);
        });

        activityAddAlarmBinding.checkBoxTryHard.setOnCheckedChangeListener((compoundButton, b) -> isHard = b);

        activityAddAlarmBinding.checkBoxVibrate.setOnCheckedChangeListener((compoundButton, b) -> isVibrate = b);


        activityAddAlarmBinding.timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                activityAddAlarmBinding.scheduleAlarmHeading
                        .setText(DayUtil.getDay(TimePickerUtil.getTimePickerHour(timePicker),
                                TimePickerUtil.getTimePickerMinute(timePicker)));
            }
        });

        activityAddAlarmBinding.optionalCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityAddAlarmBinding.optionalOptions.setVisibility(View.VISIBLE);
            }
        });


        activityAddAlarmBinding.setAddressCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadAlarmInfo(Alarm alarm) {
        activityAddAlarmBinding.alarmTitle.setText(alarm.getTitle());
        activityAddAlarmBinding.alarmNote.setText(alarm.getDescription());
        String[] time = AlarmUtils.getHourMinute(alarm.getTime()).split(":");
        if (time.length != 2) {
            activityAddAlarmBinding.timePicker.setHour(0);
            activityAddAlarmBinding.timePicker.setMinute(0);
        } else {
            activityAddAlarmBinding.timePicker.setHour(Integer.parseInt(time[0]));
            activityAddAlarmBinding.timePicker.setMinute(Integer.parseInt(time[1]));
        }
        activityAddAlarmBinding.checkBoxTryHard.setChecked(alarm.isHardMode());
        activityAddAlarmBinding.checkBoxVibrate.setChecked(alarm.isVibrateMode());
        activityAddAlarmBinding.recurringCheck.setChecked(alarm.isRepeatMode());
        if (alarm.isRepeatMode()) {
            activityAddAlarmBinding.optionsRecurring.setVisibility(View.VISIBLE);
            for (WeekDays day : WeekDays.values()) {
                switch (day) {
                    case MON:
                        activityAddAlarmBinding.monRecurringCheck.setChecked(alarm.isRepeatAt(day));
                        break;
                    case TUE:
                        activityAddAlarmBinding.tueRecurringCheck.setChecked(alarm.isRepeatAt(day));
                        break;
                    case WED:
                        activityAddAlarmBinding.wedRecurringCheck.setChecked(alarm.isRepeatAt(day));
                        break;
                    case THU:
                        activityAddAlarmBinding.thuRecurringCheck.setChecked(alarm.isRepeatAt(day));
                        break;
                    case FRI:
                        activityAddAlarmBinding.friRecurringCheck.setChecked(alarm.isRepeatAt(day));
                        break;
                    case SAT:
                        activityAddAlarmBinding.satRecurringCheck.setChecked(alarm.isRepeatAt(day));
                        break;
                    case SUN:
                        activityAddAlarmBinding.sunRecurringCheck.setChecked(alarm.isRepeatAt(day));
                        break;
                }
            }
        }
        if (alarm.getRingtoneUri().length() != 0) {
            tone = alarm.getRingtoneUri();
            ringtone = RingtoneManager.getRingtone(this, Uri.parse(tone));
            activityAddAlarmBinding.setToneNameAlarm.setText(ringtone.getTitle(this));
        }
        // assign to local value
        this.isRepeat = alarm.isRepeatMode();
        this.isVibrate = alarm.isVibrateMode();
        this.isHard = alarm.isHardMode();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.save) {
            if (alarm != null) {
                updateAlarm();
            } else {
                scheduleAlarm();
            }
            finish();
//            startActivity(new Intent(AddAlarmActivity.this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateCurrentAlarmInfo() {
        String alarmTitle = activityAddAlarmBinding.alarmTitle.getText().toString();
        String description = activityAddAlarmBinding.alarmNote.getText().toString();
        if (alarmTitle.length() == 0)
            alarmTitle = getString(R.string.default_title);
        byte daysInWeek = AlarmUtils.getBitFormat(activityAddAlarmBinding.monRecurringCheck.isChecked(),
                activityAddAlarmBinding.tueRecurringCheck.isChecked(),
                activityAddAlarmBinding.wedRecurringCheck.isChecked(),
                activityAddAlarmBinding.thuRecurringCheck.isChecked(),
                activityAddAlarmBinding.friRecurringCheck.isChecked(),
                activityAddAlarmBinding.satRecurringCheck.isChecked(),
                activityAddAlarmBinding.sunRecurringCheck.isChecked());
        if (daysInWeek == 0)
            isRepeat = false;
        long time = AlarmUtils.getTimeMillis(TimePickerUtil.getTimePickerHour(activityAddAlarmBinding.timePicker),
                TimePickerUtil.getTimePickerMinute(activityAddAlarmBinding.timePicker));

        if (alarm == null) {
            alarm = new Alarm(time, alarmTitle, description, tone, true,
                    isHard, isVibrate, isRepeat, daysInWeek);
        } else {
            alarm.setTime(time);
            alarm.setTitle(alarmTitle);
            alarm.setDescription(description);
            alarm.setRingtoneUri(tone);
            alarm.setEnable(true);
            alarm.setHardMode(isHard);
            alarm.setVibrateMode(isVibrate);
            alarm.setRepeatMode(isRepeat);
            alarm.setDaysInWeek(daysInWeek);
        }
    }

    private void scheduleAlarm() {
        updateCurrentAlarmInfo();
        // TODO: DB
        // write into database
        alarmViewModel.insert(alarm);
    }

    private void updateAlarm() {
        updateCurrentAlarmInfo();
        // TODO: DB
        // update database
        alarmViewModel.update(alarm);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //startActivity(new Intent(AddAlarmActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_add_menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityAddAlarmBinding = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_FOR_RINGTONE) {
            if (data == null) return;
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                ringtone = RingtoneManager.getRingtone(this, uri);
                tone = uri.toString();
                activityAddAlarmBinding.setToneNameAlarm.setText(ringtone.getTitle(this));
            }
        }
    }
}
