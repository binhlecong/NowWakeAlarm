package com.hcmus_csc13009.nowwakealarm.ui;

import static com.hcmus_csc13009.nowwakealarm.utils.AlarmUtils.cancelAlarm;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.hcmus_csc13009.nowwakealarm.R;
import com.hcmus_csc13009.nowwakealarm.challenge.Challenge;
import com.hcmus_csc13009.nowwakealarm.challenge.SakeIt;
import com.hcmus_csc13009.nowwakealarm.models.Alarm;
import com.hcmus_csc13009.nowwakealarm.service.AlarmService;
import com.hcmus_csc13009.nowwakealarm.viewmodel.AlarmViewModel;

import java.lang.reflect.Constructor;
import java.util.Objects;

public class HandleAlarmActivity extends AppCompatActivity {
    public int shakeCount = 0;
    private Alarm alarm;
    private AlarmViewModel alarmsListViewModel;
    private Challenge challenge;
    private SensorManager mSensorManager = null;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private SensorEventListener mSensorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_alarm);
        // Lock screen, use full screen for displaying challenges
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            );
        }
        // Get ViewModel
        alarmsListViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);
        // Get alarm from bundle
        Bundle bundle = getIntent().getBundleExtra(getString(R.string.bundle_alarm_obj));
        if (bundle != null) {
            alarm = (Alarm) bundle.getSerializable(getString(R.string.arg_alarm_obj));
        }
        // Get random challenges from alarm
        Class<?> challengeClass = (Class<?>) getIntent().getSerializableExtra("challenge_obj");
        if (challengeClass != null) {
            doSomething(challengeClass);
        }
        // Used for testing
//        challenge = new SakeIt(this);
//        challenge.play();
    }

    public void dismissAlarm() {
        if (alarm != null) {
            alarm.setEnable(false);
            cancelAlarm(getBaseContext(), alarm);
            alarmsListViewModel.update(alarm);
        }
        // Exit activity and end alarm service
        Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
        getApplicationContext().stopService(intentService);
        finish();
    }

    void doSomething(Class<?> challengeClass) {
        try {
            Constructor<?> ctor = challengeClass.getConstructor(HandleAlarmActivity.class);
            Challenge challenge = (Challenge) ctor.newInstance(new Object[]{this});
            challenge.play();
        } catch (Exception e) {
            Toast.makeText(this, "Can't not play Challenge", Toast.LENGTH_SHORT).show();
        }
    }

    public void prepareSensor() {
        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        mSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                mAccelLast = mAccelCurrent;
                mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
                float delta = mAccelCurrent - mAccelLast;
                mAccel = mAccel * 0.9f + delta;
                if (mAccel > 12) {
                    shakeCount += 1;
                    ((SakeIt) challenge).update(shakeCount);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

}