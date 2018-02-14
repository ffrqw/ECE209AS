package com.rachio.iro.ui.view.remote;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.View;

public abstract class BaseDialView extends View {
    int countdownFreq;
    Handler countdownHandler;
    Runnable countdownRunnable;
    protected int currentValue;
    long endWateringTime;
    GestureDetector gestureDetector;
    String hourNumText;
    float hourNumTextWidth;
    String hourUnitText;
    float hourUnitTextWidth;
    int hours;
    boolean isRunning;
    protected float knobRotation;
    protected float limit;
    protected Paint mainRingActivePaint;
    protected Paint mainRingPaint;
    float mainRingRadius;
    float mainRingRadiusPct;
    float mainRingWidthPct;
    String minNumText;
    float minNumTextWidth;
    String minUnitText;
    float minUnitTextWidth;
    int minutes;
    float numTextPct;
    RectF oval;
    protected float radius;
    protected int resetValue;
    String statusText;
    float statusTextPct;
    float statusTextY;
    float sweepAngle;
    float textCursor;
    float textNumSize;
    protected Paint textNumberPaint;
    protected Paint textStatusPaint;
    float textStatusSize;
    protected Paint textUnitPaint;
    float textUnitSize;
    float timeTextY;
    float totalTextWidth;
    float unitTextPct;
    protected float unitsPerDegree;
    ValueListener valueListener;

    public interface ValueListener {
        void onValueChanged(int i);
    }

    public final void setValueListener(ValueListener valueListener) {
        this.valueListener = valueListener;
    }

    public BaseDialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mainRingPaint = new Paint();
        this.mainRingActivePaint = new Paint();
        this.textNumberPaint = new Paint();
        this.textUnitPaint = new Paint();
        this.textStatusPaint = new Paint();
        this.mainRingRadiusPct = 0.813f;
        this.mainRingWidthPct = 0.034f;
        this.oval = new RectF();
        this.knobRotation = 0.0f;
        this.resetValue = 0;
        this.limit = 10800.0f;
        this.unitsPerDegree = (this.limit / 360.0f) / 3.0f;
        this.numTextPct = 0.469f;
        this.unitTextPct = 0.175f;
        this.statusTextPct = 0.12f;
        this.hourUnitText = "H ";
        this.minUnitText = "M";
        this.countdownHandler = new Handler();
        this.countdownFreq = 3000;
        this.countdownRunnable = new Runnable() {
            public void run() {
                if (BaseDialView.this.showRemainingTime() && BaseDialView.this.isRunning) {
                    BaseDialView.this.countdownHandler.postDelayed(this, (long) BaseDialView.this.countdownFreq);
                }
            }
        };
    }

    public BaseDialView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public BaseDialView(Context context) {
        this(context, null);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.radius = ((float) Math.min(w, h)) / 2.0f;
        this.mainRingRadius = this.mainRingRadiusPct * this.radius;
        this.oval.set(-this.mainRingRadius, -this.mainRingRadius, this.mainRingRadius, this.mainRingRadius);
        float mainRingWidth = this.radius * this.mainRingWidthPct;
        this.mainRingPaint.setStrokeWidth(mainRingWidth);
        this.mainRingActivePaint.setStrokeWidth(mainRingWidth);
        this.textNumSize = this.radius * this.numTextPct;
        this.textUnitSize = this.radius * this.unitTextPct;
        this.textStatusSize = this.radius * this.statusTextPct;
        this.textNumberPaint.setTextSize(this.textNumSize);
        this.textUnitPaint.setTextSize(this.textUnitSize);
        this.textStatusPaint.setTextSize(this.textStatusSize);
        this.hourUnitTextWidth = this.textUnitPaint.measureText(this.hourUnitText);
        this.minUnitTextWidth = this.textUnitPaint.measureText(this.minUnitText);
    }

    protected void onDraw(Canvas canvas) {
        int i = 0;
        float f = 0.0f;
        super.onDraw(canvas);
        canvas.translate(((float) getWidth()) / 2.0f, ((float) getHeight()) / 2.0f);
        canvas.drawCircle(0.0f, 0.0f, this.mainRingRadius, this.mainRingPaint);
        determineTextHeights$262b7b90();
        if (this.currentValue < 0) {
            this.minutes = 0;
        } else if (this.isRunning || ((float) this.currentValue) <= this.limit) {
            this.minutes = (int) Math.ceil((double) (((float) this.currentValue) / 60.0f));
        } else {
            this.minutes = (int) Math.ceil((double) (this.limit / 60.0f));
        }
        this.hours = (int) Math.floor((double) (((float) this.minutes) / 60.0f));
        this.minutes -= this.hours * 60;
        this.minNumText = this.minutes;
        this.hourNumText = this.hours;
        if (this.minutes != 0 || (this.minutes == 0 && this.hours == 0)) {
            i = 1;
        }
        this.minNumTextWidth = this.textNumberPaint.measureText(this.minNumText);
        float f2 = i != 0 ? this.minNumTextWidth : 0.0f;
        if (this.minutes > 0) {
            f = this.minUnitTextWidth;
        }
        this.totalTextWidth = f2 + f;
        this.textCursor = -(this.totalTextWidth / 2.0f);
        if (this.hours > 0) {
            this.hourNumTextWidth = this.textNumberPaint.measureText(this.hourNumText);
            this.totalTextWidth += this.hourNumTextWidth + this.hourUnitTextWidth;
            this.textCursor = -(this.totalTextWidth / 2.0f);
            canvas.drawText(this.hourNumText, this.textCursor, this.timeTextY, this.textNumberPaint);
            this.textCursor += this.hourNumTextWidth;
            canvas.drawText(this.hourUnitText, this.textCursor, this.timeTextY, this.textUnitPaint);
            this.textCursor += this.hourUnitTextWidth;
        }
        if (i != 0) {
            canvas.drawText(this.minNumText, this.textCursor, this.timeTextY, this.textNumberPaint);
            this.textCursor += this.minNumTextWidth;
        }
        if (this.minutes > 0) {
            canvas.drawText(this.minUnitText, this.textCursor, this.timeTextY, this.textUnitPaint);
        }
        String str = this.statusText != null ? this.statusText : "";
        canvas.drawText(str, (-this.textStatusPaint.measureText(str)) / 2.0f, this.statusTextY, this.textStatusPaint);
    }

    public void determineTextHeights$262b7b90() {
        this.timeTextY = ((-((1.5f * this.textNumSize) + this.textStatusSize)) / 2.0f) + this.textNumSize;
        this.statusTextY = this.timeTextY + this.textStatusSize;
    }

    public int getValue() {
        return this.currentValue;
    }

    final int updateCurrentValue() {
        this.currentValue = (int) Math.ceil((double) (this.knobRotation * this.unitsPerDegree));
        this.currentValue = (int) (Math.ceil((double) (((float) this.currentValue) / 60.0f)) * 60.0d);
        if (this.valueListener != null) {
            this.valueListener.onValueChanged(this.currentValue);
        }
        return this.currentValue;
    }

    public final boolean showRemainingTime() {
        float remainingTime = ((float) (this.endWateringTime - System.currentTimeMillis())) / 1000.0f;
        this.minutes = (int) Math.ceil((double) (remainingTime / 60.0f));
        if (this.minutes <= 0) {
            this.minutes = 1;
        }
        this.knobRotation = remainingTime / this.unitsPerDegree;
        updateCurrentValue();
        invalidate();
        if (remainingTime > 0.0f) {
            return true;
        }
        return false;
    }

    void onWateringStopped() {
        this.endWateringTime = -1;
        this.countdownHandler.removeCallbacksAndMessages(null);
    }

    public void setIsRunning(boolean value) {
        this.isRunning = value;
    }

    final float valueToDegrees(int value) {
        return (float) Math.round(((float) value) / this.unitsPerDegree);
    }
}
