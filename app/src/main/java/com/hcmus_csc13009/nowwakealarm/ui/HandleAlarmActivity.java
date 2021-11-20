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
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.hcmus_csc13009.nowwakealarm.R;
import com.hcmus_csc13009.nowwakealarm.customview.SpriteAnimation;
import com.hcmus_csc13009.nowwakealarm.models.Alarm;
import com.hcmus_csc13009.nowwakealarm.service.AlarmService;
import com.hcmus_csc13009.nowwakealarm.viewmodel.AlarmViewModel;

import java.util.Random;

public class HandleAlarmActivity extends AppCompatActivity {
    int cnt = 3;
    private Alarm alarm;
    private ConstraintLayout mainLayout;
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
        if (bundle != null)
            alarm = (Alarm) bundle.getSerializable(getString(R.string.arg_alarm_obj));
        // Run animations
        mainLayout = findViewById(R.id.mainConstraintLayout);
        doSomething();
    }

    private void dismissAlarm() {
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

    void doSomething() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bird_sprite, options);
        bmp = Bitmap.createScaledBitmap(bmp, 640, 96, false);

        for (int i = 0; i < cnt; ++i) {
            SpriteAnimation x = new SpriteAnimation(this, bmp, 0, 0, 80, 96, 8);
            x.setX(200 * i);
            x.setY(50 + 100 * i);
            x.setOnClickListener(view -> x.setY(0));
            Random random = new Random();
            ObjectAnimator translateX = new ObjectAnimator();
            translateX.setTarget(x);
            translateX.setPropertyName("x");
            translateX.setFloatValues(400f);
            translateX.setDuration((i + 1) * 1000);
            translateX.setRepeatMode(ValueAnimator.RESTART);
            translateX.setRepeatCount(ValueAnimator.INFINITE);
            translateX.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                    int prev = Math.round((Float) translateX.getAnimatedValue());
                    int nextValue = random.nextInt(800);
                    translateX.setFloatValues(prev, nextValue);
                }
            });
            ObjectAnimator translateY = ObjectAnimator.ofFloat(x, "y", 800f);
            translateY.setDuration((i + 1) * 1000);
            translateY.setRepeatCount(ValueAnimator.INFINITE);
            translateY.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                    int prev = Math.round((Float) translateY.getAnimatedValue());
                    int nextValue = random.nextInt(1600);
                    translateY.setFloatValues(prev, nextValue);
                }
            });
            x.getAnimationSet().playTogether(translateX, translateY);
            x.getAnimationSet().start();
            // Add listener to sprite
            x.setOnClickListener(v -> {
                cnt--;
                if (cnt == 0) dismissAlarm();
                mainLayout.removeView(v);
            });

            mainLayout.addView(x);
        }
    }
}