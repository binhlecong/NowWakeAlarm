package com.hcmus_csc13009.nowwakealarm.challenge;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.hcmus_csc13009.nowwakealarm.R;
import com.hcmus_csc13009.nowwakealarm.customview.SpriteAnimation;
import com.hcmus_csc13009.nowwakealarm.ui.HandleAlarmActivity;

import java.util.ArrayList;
import java.util.Random;


public class HelpIt implements Challenge {
    private final HandleAlarmActivity activity;
    private final ConstraintLayout mainLayout;
    private ArrayList<ObjectAnimator> bossList;
    private TextView textView;

    public HelpIt(HandleAlarmActivity activity) {
        this.activity = activity;
        activity.setContentView(R.layout.fragment_help_it);
        this.mainLayout = activity.findViewById(R.id.helpit_fragment);
        textView = activity.findViewById(R.id.helpItTextView);

    }

    private boolean isIntersect(SpriteAnimation x, SpriteAnimation y) {
        Rect rect1 = new Rect();
        Rect rect2 = new Rect();
        x.getHitRect(rect1);
        y.getHitRect(rect2);
        return Rect.intersects(rect1, rect2);
    }

    private SpriteAnimation setupPlayer(Bitmap bmp, int factor, Random random, int width, int height) {
        int playerIndex = random.nextInt(2) * 9 * 16 * factor + 16 * 32 * factor;
        SpriteAnimation player = new SpriteAnimation(activity, bmp, playerIndex, 0, 16 * factor, 28 * factor, 9);
        player.startAnimation();
        player.setOnTouchListener(new View.OnTouchListener() {
            float prevX = 0, prevY = 0;
            long oldTime = System.currentTimeMillis();
            long totalTime = 10;
            long prevCatch = 0;
            long delta = 2;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int maskedAction = motionEvent.getActionMasked();
                float x = motionEvent.getRawX();
                float y = motionEvent.getRawY();
                switch (maskedAction) {
                    case MotionEvent.ACTION_DOWN: {
                        prevX = player.getX() - x;
                        prevY = player.getY() - x;
                        break;
                    }
                    case MotionEvent.ACTION_UP: {

                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        if (true) {
                            player.animate().x(x - 42  * 2).y(y - 24 * 2).setDuration(0).start();

                        }
                        break;
                    }
                }
                long sec = (System.currentTimeMillis() - oldTime) / 1000;

                for (ObjectAnimator boss : bossList) {
                    if (sec - prevCatch <= 5) break;
                    if (isIntersect((SpriteAnimation) boss.getTarget(), player)) {
                        totalTime += delta;
                        prevCatch = sec;
                        break;
                    }
                }
                if (totalTime > 60) {
                    totalTime = 60;
                    delta = 0;
                }
                textView.setText(String.format("Keep You Safe for %d seconds", totalTime - sec));
                if (totalTime - sec <= 0)
                    activity.dismissAlarm();
                return true;
            }
        });
        mainLayout.addView(player);
        return player;
    }

    private void prepareChallenge() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int scrHeight = displayMetrics.heightPixels - 400;
        int scrWidth = displayMetrics.widthPixels - 400;


        int factor = 3;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.dungoen_sprite, options);
        bmp = Bitmap.createScaledBitmap(bmp, 800 * factor, 36 * factor, false);
        Random random = new Random();
        // create Player
        SpriteAnimation player = setupPlayer(bmp, factor, random, scrWidth, scrHeight);
        // create Monster
        bossList = new ArrayList<>();
        for (int i = 0; i < 1; ++i) {
            int bossIndex = random.nextInt(2) * 32 * factor;
            SpriteAnimation boss = new SpriteAnimation(activity, bmp, bossIndex, 0, 32 * factor, 36 * factor, 8);
            boss.setX(scrWidth);
            boss.setY(scrHeight);
            ObjectAnimator translateX = ObjectAnimator.ofFloat(boss, "x", 0L);
            translateX.setDuration(2000);
//            translateX.setRepeatMode(ValueAnimator.RESTART);
            translateX.setRepeatCount(ValueAnimator.INFINITE);
            ObjectAnimator translateY = ObjectAnimator.ofFloat(boss, "y", 0L);
            translateY.setDuration(2000);
            translateY.setRepeatCount(ValueAnimator.INFINITE);

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
                    float nextValue = player.getX();
                    translateX.setFloatValues(prev, nextValue);
                }
            });

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
                    float nextValue = player.getY();
                    translateY.setFloatValues(prev, nextValue);
                }
            });

            boss.getAnimationSet().playTogether(translateX, translateY);
            boss.startAnimation();
            mainLayout.addView(boss);
            bossList.add(translateX);
            bossList.add(translateY);
        }

    }

    @Override
    public void play() {
        prepareChallenge();
    }


}
