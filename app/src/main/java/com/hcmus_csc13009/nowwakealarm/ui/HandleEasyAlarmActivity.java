package com.hcmus_csc13009.nowwakealarm.ui;

import static com.hcmus_csc13009.nowwakealarm.utils.AlarmUtils.cancelAlarm;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.hcmus_csc13009.nowwakealarm.R;
import com.hcmus_csc13009.nowwakealarm.databinding.ActivityHandleEasyAlarmBinding;
import com.hcmus_csc13009.nowwakealarm.models.Alarm;
import com.hcmus_csc13009.nowwakealarm.service.AlarmService;
import com.hcmus_csc13009.nowwakealarm.viewmodel.AlarmViewModel;

public class HandleEasyAlarmActivity extends AppCompatActivity {
    Alarm alarm;
    private AlertDialog dialog;
    private AlarmViewModel alarmsListViewModel;
    private ActivityHandleEasyAlarmBinding handleEasyAlarmBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handleEasyAlarmBinding = ActivityHandleEasyAlarmBinding.inflate(getLayoutInflater());
        setContentView(handleEasyAlarmBinding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            );
        }

        alarmsListViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);
        Bundle bundle = getIntent().getBundleExtra(getString(R.string.bundle_alarm_obj));
        if (bundle != null)
            alarm = (Alarm) bundle.getSerializable(getString(R.string.arg_alarm_obj));

        handleEasyAlarmBinding.btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAlarm();
            }
        });
        animateClock();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Open this url: " + alarm.getTagUri());
        builder.setCancelable(true);
        builder.setPositiveButton(
                "Open link",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(alarm.getTagUri()));
                        startActivity(intent);
                        finish();
                    }
                });
        builder.setNegativeButton(
                "Exit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        dialog = builder.create();
    }

    public void dismissAlarm() {
        // Exit activity and end alarm service
        Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
        getApplicationContext().stopService(intentService);
        if (alarm != null) {
            alarm.setEnable(false);
            cancelAlarm(getBaseContext(), alarm);
            alarmsListViewModel.update(alarm);

            if (alarm.getTagUri() != null && alarm.getTagUri().length() != 0) {
                dialog.show();
                return;
            }
        }
        finish();
    }

    private void animateClock() {
        ObjectAnimator rotateAnimation =
                ObjectAnimator.ofFloat(
                        handleEasyAlarmBinding.sakeClock,
                        "rotation",
                        0f, 30f, 0f, -30f, 0f);
        rotateAnimation.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnimation.setDuration(800);
        rotateAnimation.start();
    }

}