package com.hcmus_csc13009.nowwakealarm.ui;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

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
import com.hcmus_csc13009.nowwakealarm.utils.SettingConstant;
import com.hcmus_csc13009.nowwakealarm.utils.TimePickerUtil;
import com.hcmus_csc13009.nowwakealarm.utils.WeekDays;
import com.hcmus_csc13009.nowwakealarm.viewmodel.AlarmViewModel;

import java.sql.Timestamp;

public class AddAlarmActivity extends AppCompatActivity {
    public static final String EXTRA_POSITION = "RETRIEVE_POSITION";
    final static private int REQUEST_FOR_RINGTONE = 5;
    final static private int REQUEST_FOR_POSITION = 55;
    private AlarmViewModel alarmViewModel;
    private ActivityAddAlarmBinding activityAddAlarmBinding;
    private String tone;
    private Alarm alarm = null;
    private Ringtone ringtone;
    private boolean isVibrate = false;
    private boolean isHard = false;
    private boolean isRepeat = false;

    private String position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        alarm = (Alarm) getIntent().getSerializableExtra(AlarmAdapter.ALARM_OBJECT_DATA);

        alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);

        activityAddAlarmBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_alarm);

        tone = SettingConstant.getDefaultRingtone(getApplicationContext());
        if (tone == null) {
            tone = RingtoneManager.getActualDefaultRingtoneUri(this,
                    RingtoneManager.TYPE_ALARM).toString();
        }
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
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(tone));
            startActivityForResult(intent, REQUEST_FOR_RINGTONE);
        });

        activityAddAlarmBinding.checkBoxTryHard.setOnCheckedChangeListener((compoundButton, b) -> isHard = b);

        activityAddAlarmBinding.checkBoxVibrate.setOnCheckedChangeListener((compoundButton, b) -> isVibrate = b);


        activityAddAlarmBinding.timePicker.setOnTimeChangedListener((timePicker, i, i1) -> {
            activityAddAlarmBinding.scheduleAlarmHeading
                    .setText(DayUtil.getDay(TimePickerUtil.getTimePickerHour(timePicker),
                            TimePickerUtil.getTimePickerMinute(timePicker)));
        });

        activityAddAlarmBinding.optionalCard.setOnClickListener(v -> {
            activityAddAlarmBinding.optionalOptions.setVisibility(View.VISIBLE);
        });

        activityAddAlarmBinding.setAddressCard.setOnClickListener(v -> {
            Intent intent = new Intent(AddAlarmActivity.this, PlacePickerActivity.class);
            if (alarm != null && alarm.getPosition() != null && alarm.getPosition().length() != 0) {
                intent.putExtra(EXTRA_POSITION, alarm.getPosition());
            }
            startActivityForResult(intent, REQUEST_FOR_POSITION);
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
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
            int hour = Integer.parseInt(time[0]);
            int minute = Integer.parseInt(time[1]);
            activityAddAlarmBinding.timePicker.setHour(hour);
            activityAddAlarmBinding.timePicker.setMinute(minute);
        }
        activityAddAlarmBinding.checkBoxTryHard.setChecked(alarm.isHardMode());
        activityAddAlarmBinding.checkBoxVibrate.setChecked(alarm.isVibrateMode());
        activityAddAlarmBinding.recurringCheck.setChecked(alarm.isRepeatMode());
        activityAddAlarmBinding.locationCheck.setChecked(alarm.isEnablePosition());
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
        // optional field
        activityAddAlarmBinding.urlAlarm.setText(alarm.getTagUri());
        if (alarm.getAddress() != null) {
            activityAddAlarmBinding.nameAddress.setText(alarm.getAddress());
        } else {
            activityAddAlarmBinding.nameAddress.setText("Select a location");
        }


        // assign to local value
        this.isRepeat = alarm.isRepeatMode();
        this.isVibrate = alarm.isVibrateMode();
        this.isHard = alarm.isHardMode();
        this.position = alarm.getPosition();


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.save) {
            if (alarm != null) {
                AlarmUtils.cancelAlarm(this, alarm);
                updateAlarm();
            } else {
                scheduleAlarm();
            }
            finish();
//            startActivity(new Intent(AddAlarmActivity.this, MainActivity.class));
            return true;
        } else if (itemId == R.id.delete) {
            if (alarm != null) {
                AlarmUtils.cancelAlarm(this, alarm);
                alarmViewModel.delete(alarm);
            }
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateCurrentAlarmInfo() {
        String alarmTitle = activityAddAlarmBinding.alarmTitle.getText().toString();
        String description = activityAddAlarmBinding.alarmNote.getText().toString();
        String uri = activityAddAlarmBinding.urlAlarm.getText().toString();
        String address = activityAddAlarmBinding.nameAddress.getText().toString();

        if (alarmTitle.length() == 0)
            alarmTitle = getString(R.string.default_title);
        byte daysInWeek =
                AlarmUtils.getBitFormat(activityAddAlarmBinding.monRecurringCheck.isChecked(),
                activityAddAlarmBinding.tueRecurringCheck.isChecked(),
                activityAddAlarmBinding.wedRecurringCheck.isChecked(),
                activityAddAlarmBinding.thuRecurringCheck.isChecked(),
                activityAddAlarmBinding.friRecurringCheck.isChecked(),
                activityAddAlarmBinding.satRecurringCheck.isChecked(),
                activityAddAlarmBinding.sunRecurringCheck.isChecked());

        if (!activityAddAlarmBinding.recurringCheck.isChecked()) {
            daysInWeek = 0;
            isRepeat = false;
        }
        if (daysInWeek == 0)
            isRepeat = false;

        long time =
                AlarmUtils.getTimeMillis(TimePickerUtil.getTimePickerHour(activityAddAlarmBinding.timePicker),
                TimePickerUtil.getTimePickerMinute(activityAddAlarmBinding.timePicker));

        boolean locationCheck = activityAddAlarmBinding.locationCheck.isChecked();
        if (position != null && locationCheck) {
            daysInWeek |= 1 << 7; // set on bit enable position alarm feature
        }

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        int id = timestamp.hashCode();

        if (alarm == null) {
            alarm = new Alarm(id, time, alarmTitle, description, tone, true,
                    isHard, isVibrate, isRepeat, daysInWeek, uri, position, address);
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
            alarm.setPosition(position);
            alarm.setTagUri(uri);
            alarm.setAddress(address);
        }
    }

    private void scheduleAlarm() {
        updateCurrentAlarmInfo();
        // write into database
        alarmViewModel.insert(alarm);
        AlarmUtils.scheduleAlarm(this, alarm);
    }

    private void updateAlarm() {
        updateCurrentAlarmInfo();
        // update database
        alarmViewModel.update(alarm);
        AlarmUtils.scheduleAlarm(this, alarm);
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
        if (resultCode == RESULT_OK && requestCode == REQUEST_FOR_POSITION) {
            if (data == null) return;
            position = data.getStringExtra(PlacePickerActivity.EXTRA_REPLY_POSITION);
            if (position != null && position.startsWith("("))
                position = position.substring(1, position.length() - 1);
            activityAddAlarmBinding.nameAddress.setText(data.getStringExtra(PlacePickerActivity.EXTRA_REPLY_ADDRESS));
        }
    }
}
