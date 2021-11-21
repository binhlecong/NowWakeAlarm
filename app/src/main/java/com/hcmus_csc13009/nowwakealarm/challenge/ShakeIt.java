package com.hcmus_csc13009.nowwakealarm.challenge;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.hcmus_csc13009.nowwakealarm.R;
import com.hcmus_csc13009.nowwakealarm.ui.HandleAlarmActivity;

public class ShakeIt implements Challenge {
    TextView shakeCntTextView;
    private HandleAlarmActivity activity;
    private LinearLayoutCompat mainLayout;

    public ShakeIt(HandleAlarmActivity activity) {
        this.activity = activity;
        this.mainLayout = activity.findViewById(R.id.mainConstraintLayout);
    }

    public void prepareChallenge() {
        activity.setContentView(R.layout.fragment_shake_it);
        shakeCntTextView = activity.findViewById(R.id.sake_left);
        shakeCntTextView.setText("0");
        // Rotate clock
        animateClock();
    }

    private void animateClock() {
        Animation rotation = AnimationUtils.loadAnimation(activity, R.anim.rotate);
        rotation.setFillAfter(true);
        activity.findViewById(R.id.sake_clock).startAnimation(rotation);
    }


    public void update(int cnt) {
        if (cnt < 5)
            shakeCntTextView.setTextColor(
                    activity.getResources().getColor(R.color.range_1_4, activity.getTheme()));
        else if (cnt < 10)
            shakeCntTextView.setTextColor(
                    activity.getResources().getColor(R.color.range_5_9, activity.getTheme()));
        else if (cnt < 14)
            shakeCntTextView.setTextColor(
                    activity.getResources().getColor(R.color.range_10_13, activity.getTheme()));
        else if (cnt < 19)
            shakeCntTextView.setTextColor(
                    activity.getResources().getColor(R.color.range_14_18, activity.getTheme()));
        else
            shakeCntTextView.setTextColor(
                    activity.getResources().getColor(R.color.range_19_20, activity.getTheme()));

        shakeCntTextView.setText(String.valueOf(cnt));
        if (cnt == 20) {
            activity.dismissAlarm();
        }
    }

    @Override
    public void play() {
        activity.prepareSensor();
        prepareChallenge();
    }
}
