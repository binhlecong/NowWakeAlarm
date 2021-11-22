package com.hcmus_csc13009.nowwakealarm.challenge;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.hcmus_csc13009.nowwakealarm.R;
import com.hcmus_csc13009.nowwakealarm.customview.SpriteAnimation;
import com.hcmus_csc13009.nowwakealarm.ui.HandleAlarmActivity;

import java.util.Random;

public class CatchIt implements Challenge {
    private final HandleAlarmActivity activity;
    private final LinearLayoutCompat mainLayout;
    private int cnt = 3;

    public CatchIt(HandleAlarmActivity activity) {
        this.activity = activity;
        this.mainLayout = activity.findViewById(R.id.mainConstraintLayout);
    }

    private void prepareChallenge() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int scrHeight = displayMetrics.heightPixels - 400;
        int scrWidth = displayMetrics.widthPixels - 400;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.bird_sprite
                , options);
        bmp = Bitmap.createScaledBitmap(bmp, 640, 96, false);
        Random random = new Random();

        for (int i = 0; i < cnt; ++i) {
            SpriteAnimation x = new SpriteAnimation(activity, bmp, 0, 0, 80, 96, 8);
            x.setX(0);
            x.setY(0);
            x.setOnClickListener(view -> x.setY(0));
            ObjectAnimator translateX = new ObjectAnimator();
            translateX.setTarget(x);
            translateX.setPropertyName("x");
            translateX.setFloatValues((float) scrWidth);
            translateX.setDuration((i + 1) * 1000L);
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
                    float prev = Math.round((Float) translateX.getAnimatedValue());
                    int nextValue = random.nextInt(scrWidth);
                    translateX.setFloatValues(prev, nextValue);
                }
            });
            ObjectAnimator translateY = ObjectAnimator.ofFloat(x, "y", scrHeight);
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
                    float prev = Math.round((Float) translateY.getAnimatedValue());
                    int nextValue = random.nextInt(scrHeight);
                    translateY.setFloatValues(prev, nextValue);
                }
            });
            x.getAnimationSet().playTogether(translateX, translateY);
            x.startAnimation();
            // Add listener to sprite
            x.setOnClickListener(v -> {
                cnt--;
                if (cnt == 0) activity.dismissAlarm();
                mainLayout.removeView(v);
            });

            mainLayout.addView(x);
            mainLayout.setBackgroundResource(R.drawable.bg_handle_alarm_lowres);
        }
    }

    @Override
    public void play() {
        prepareChallenge();
    }
}
