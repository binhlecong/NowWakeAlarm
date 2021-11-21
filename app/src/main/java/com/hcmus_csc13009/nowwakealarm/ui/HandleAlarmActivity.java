package com.hcmus_csc13009.nowwakealarm.ui;

import static com.hcmus_csc13009.nowwakealarm.utils.AlarmUtils.cancelAlarm;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.hcmus_csc13009.nowwakealarm.R;
import com.hcmus_csc13009.nowwakealarm.challenge.CatchIt;
import com.hcmus_csc13009.nowwakealarm.challenge.Challenge;
import com.hcmus_csc13009.nowwakealarm.customview.SpriteAnimation;
import com.hcmus_csc13009.nowwakealarm.models.Alarm;
import com.hcmus_csc13009.nowwakealarm.service.AlarmService;
import com.hcmus_csc13009.nowwakealarm.viewmodel.AlarmViewModel;

import java.lang.reflect.Constructor;
import java.util.Random;

public class HandleAlarmActivity extends AppCompatActivity {
    int cnt = 3;
    private Alarm alarm;
    private AlarmViewModel alarmsListViewModel;

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
        Class<?> challengeClass = (Class<?>) getIntent().getSerializableExtra("challenge_obj");
        if (challengeClass != null) {
            doSomething(challengeClass);
        }
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
}