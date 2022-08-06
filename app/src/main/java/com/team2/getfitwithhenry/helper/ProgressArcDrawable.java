package com.team2.getfitwithhenry.helper;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ProgressArcDrawable extends Drawable {

    Paint mPaint;
    Paint BgPaint;
    RectF oval;
    float angleTo;
//    RectF mInnerBoundsF;
//    final float START_ANGLE = 0.f;
//    float mDrawTo;

    public ProgressArcDrawable(float angleTo, String color) {
        BgPaint = new Paint();
        mPaint = new Paint();
        this.angleTo = angleTo;
        oval = new RectF();
        setPaintColors(color);

    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        BgPaint.setStyle(Paint.Style.STROKE);
        BgPaint.setStrokeWidth(30);
        BgPaint.setStrokeCap(Paint.Cap.ROUND);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(30);
        mPaint.setStrokeCap(Paint.Cap.ROUND);


        int width = getBounds().width();
        int height = getBounds().height();
        float radius = Math.min(width, height) / 2.5f;
        oval.set(width/2 - radius, height/1.8f - radius, width/2 + radius, height/1.8f + radius);
        canvas.drawArc(oval, 225, -270, false, BgPaint);
        canvas.drawArc(oval, 225, angleTo*-1, false, mPaint);

    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    public void setPaintColors(String color) {
        switch(color) {
            case("green"):
                BgPaint.setARGB(60, 0, 185, 0);
                mPaint.setARGB(255, 0, 185, 0);
                break;
            case("red"):
                BgPaint.setARGB(60, 245, 0, 0);
                mPaint.setARGB(255, 245, 0, 0);
                break;
            case("blue"):
                BgPaint.setARGB(95, 28, 0, 255);
                mPaint.setARGB(192, 0, 0, 255);
        }
    }
}
