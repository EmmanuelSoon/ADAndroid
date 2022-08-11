package com.team2.getfitwithhenry.helper;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MacrosDrawable extends Drawable {

    private RectF oval;
    private Paint proteinRed;
    private Paint carbsGreen;
    private Paint fatYellow;
    private float proteinAngle;
    private float carbAngle;
    private float fatAngle;

    public MacrosDrawable(float proteinAngle, float carbAngle, float fatAngle){
        oval = new RectF();
        proteinRed = new Paint();
        carbsGreen = new Paint();
        fatYellow = new Paint();
        this.proteinAngle = proteinAngle;
        this.carbAngle = carbAngle;
        this.fatAngle = fatAngle;

    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        int width = getBounds().width();
        int height = getBounds().height();
        float radius = Math.min(width, height) / 2.4f;
        oval.set(width/2 - radius, height/1.8f - radius, width/2 + radius, height/1.8f + radius);

        proteinRed.setARGB(255, 241, 117, 117);
        carbsGreen.setARGB(255, 129, 241, 123);
        fatYellow.setARGB(255,251, 238, 130);

        canvas.drawArc(oval, 0F, proteinAngle, true, proteinRed);
        canvas.drawArc(oval, proteinAngle, carbAngle, true, carbsGreen);
        canvas.drawArc(oval, proteinAngle+carbAngle, fatAngle, true, fatYellow);

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
