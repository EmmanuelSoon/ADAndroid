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
    RectF oval;
//    RectF mInnerBoundsF;
//    final float START_ANGLE = 0.f;
//    float mDrawTo;

    public ProgressArcDrawable() {
        mPaint = new Paint();
        mPaint.setARGB(255, 255, 0, 0);
        oval = new RectF();

    }

    @Override
    public void draw(@NonNull Canvas canvas) {
//        canvas.rotate(-90f, getBounds().centerX(), getBounds().centerY());
          mPaint.setStyle(Paint.Style.STROKE);
          mPaint.setStrokeWidth(30);
//        canvas.drawOval(mBoundsF, mPaint);
//        mPaint.setStyle(Paint.Style.FILL);
//        canvas.drawArc(mInnerBoundsF, START_ANGLE, mDrawTo, true, mPaint);

        int width = getBounds().width();
        int height = getBounds().height();
        float radius = Math.min(width, height) / 2;
        canvas.drawCircle(width/2, height/2, radius-30, mPaint);

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
}
