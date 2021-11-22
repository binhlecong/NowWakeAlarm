package com.hcmus_csc13009.nowwakealarm.customview;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.appcompat.widget.AppCompatImageView;

public class SpriteAnimation extends AppCompatImageView {
    /*
     * bitmap: whole sprite sheet
     * posX: X position of sprite in sprite sheet
     * posY: Y position of sprite in sprite sheet
     * width: width of sprite
     * height: height of sprite
     * columns: number of sprite frame
     * duration: time to play sprite animation
     */
    final private Bitmap bitmap;
    final private int width, height;
    ValueAnimator sprite;
    AnimatorSet animationSet;
    private int posX, posY;
    private int columns; // number of sprite frame in one row
    private int totalFrames; // total number of sprite frame
    private int frameIndex; // current frame in sprite animation

    SpriteAnimation(Context context, Bitmap bitmap, int posX, int posY, int width, int height,
                    int columns, int totalFrames, long spriteDuration) {
        super(context, null);
        this.bitmap = bitmap;
        this.columns = columns;
        this.width = width;
        this.height = height;
        this.frameIndex = 0;
        this.posX = posX;
        this.posY = posY;
        this.totalFrames = totalFrames;
        animationSet = new AnimatorSet();
        this.setImageBitmap(getSubImage(posX, posY, width, height));
        // setup sprite animation
        sprite = ValueAnimator.ofInt(0, totalFrames - 1);
        sprite.setDuration(spriteDuration);
        sprite.setRepeatCount(ValueAnimator.INFINITE);
        sprite.addUpdateListener(valueAnimator -> {
            int id = (Integer) valueAnimator.getAnimatedValue();
            if (id == this.frameIndex) return;
            this.frameIndex = id;
            int x = this.posX + (id % this.columns) * this.width;
            int y = this.posY + (id / this.columns) * this.height;
            SpriteAnimation.this.setImageBitmap(
                    SpriteAnimation.this.getSubImage(x, y, this.width, this.height));
            invalidate();
        });
        animationSet.play(sprite);
    }

    SpriteAnimation(Context context, Bitmap bitmap, int posX, int posY, int width, int height,
                    int columns, long spriteDuration) {
        this(context, bitmap, posX, posY, width, height, columns, columns, spriteDuration);
    }

    // default duration = 40*frames (millis)
    public SpriteAnimation(Context context, Bitmap bitmap, int posX, int posY, int width,
                           int height,
                           int columns) {
        this(context, bitmap, posX, posY, width, height, columns, 40L * columns);
    }
    
    

    private Bitmap getSubImage(int x, int y, int width, int height) {
        return Bitmap.createBitmap(this.bitmap, x, y, width, height);
    }

    // use this to add another animation
    public AnimatorSet getAnimationSet() {
        return animationSet;
    }

    public void startAnimation() {
        animationSet.start();
    }

    public void cancelAnimation() {
        animationSet.cancel();
    }

    public void setPosX(int x) {
        this.posX = x;
    }

    public void setPosY(int y) {
        this.posY = y;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public void setTotalFrames(int totalFrames) {
        this.totalFrames = totalFrames;
        sprite.setIntValues(0, this.totalFrames - 1);
    }
}
