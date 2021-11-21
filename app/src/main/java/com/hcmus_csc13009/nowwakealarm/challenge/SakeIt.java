package com.hcmus_csc13009.nowwakealarm.challenge;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.hcmus_csc13009.nowwakealarm.R;
import com.hcmus_csc13009.nowwakealarm.ui.HandleAlarmActivity;

public class SakeIt implements Challenge {
    TextView shakeCntTextView;
    private HandleAlarmActivity activity;
    private LinearLayoutCompat mainLayout;

    public SakeIt(HandleAlarmActivity activity) {
        this.activity = activity;
        this.mainLayout = activity.findViewById(R.id.mainConstraintLayout);
    }

    public void prepareChallenge() {
        shakeCntTextView = new TextView(activity);
        // Animation

        // Instrcution
        shakeCntTextView.setText("Shake your device 10 times");
        shakeCntTextView.setId(View.generateViewId());
        mainLayout.addView(shakeCntTextView);
    }

    public void update(int cnt) {
        shakeCntTextView.setText(String.valueOf(cnt));
    }

    @Override
    public void play() {
        activity.prepareSensor();
        prepareChallenge();
    }
}
