package com.rachio.iro.ui.view.remote;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import com.rachio.iro.R;

public class WateringDialView extends BaseDialView {
    private static final String TAG = WateringDialView.class.getSimpleName();
    private static Paint debugPaint;
    private Runnable animateKnob;
    private Runnable animateResizeHour1Ring;
    private Runnable animateResizeHour2Ring;
    private boolean animating;
    private long animationDuration;
    private long animationNormalDuration;
    private float animationResetDuration;
    private long animationStarted;
    private int brightBlue;
    private int brightGreen;
    private boolean canAnimate;
    private float constantHourRing1Radius;
    private float constantHourRing2Radius;
    private final DecelerateInterpolator decelerateInterpolator;
    private String durationString;
    private float hourRing1Radius;
    private boolean hourRing1Shrinking;
    private float hourRing2Radius;
    private boolean hourRing2Shrinking;
    private int hourRingBlue;
    private int hourRingGreen;
    private Paint hourRingPaint;
    private float hourRingRadiusPct;
    private float hourRingWidthPct;
    private int innerKnobBlue;
    private int innerKnobGreen;
    private final float knobBoundsRadius;
    private float knobDownFuzz;
    private Paint knobInnerPaint;
    private float knobInnerRadiusPct;
    private Paint knobOuterPaint;
    private float knobRadius;
    private float lastX;
    private int mainRingBlue;
    private final OvershootInterpolator overshootInterpolator;
    private String remainingString;
    private int resetBottom;
    private Drawable resetEnabledDrawable;
    private int resetLeft;
    private float resetPct;
    private int resetRight;
    private float resetSize;
    private int resetTop;
    private String setTimerString;
    private float smallKnobRadius;
    private float smallKnobRadiusPct;
    int snap;
    private long startWatering;
    private float startingKnobRotation;
    private float startingRingRadius;
    private float targetKnobRotation;
    private float targetRingRadius;
    private boolean thumbDown;
    Matrix transform;
    private boolean watering;

    class GestureListener extends SimpleOnGestureListener {
        GestureListener() {
        }

        public boolean onSingleTapUp(MotionEvent event) {
            Matrix transform = new Matrix();
            transform.postTranslate(-(((float) WateringDialView.this.getWidth()) / 2.0f), -(((float) WateringDialView.this.getHeight()) / 2.0f));
            event.transform(transform);
            if (event.getX() <= ((float) WateringDialView.this.resetLeft) || event.getX() >= ((float) WateringDialView.this.resetRight) || event.getY() <= ((float) WateringDialView.this.resetTop) || event.getY() >= ((float) WateringDialView.this.resetBottom) || !WateringDialView.this.isResetEnabled()) {
                return false;
            }
            WateringDialView.this.reset();
            return true;
        }
    }

    static {
        Paint paint = new Paint();
        debugPaint = paint;
        paint.setColor(-65536);
        debugPaint.setStyle(Style.STROKE);
        debugPaint.setStrokeWidth(2.0f);
        debugPaint.setAntiAlias(true);
    }

    public WateringDialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.hourRingPaint = new Paint();
        this.knobOuterPaint = new Paint();
        this.knobInnerPaint = new Paint();
        this.hourRingRadiusPct = 0.9f;
        this.hourRingWidthPct = 0.025f;
        this.smallKnobRadiusPct = 0.15f;
        this.knobInnerRadiusPct = 0.78f;
        this.snap = 90;
        this.knobDownFuzz = 1.5f;
        this.knobBoundsRadius = 4.0f;
        this.resetPct = 0.25f;
        this.decelerateInterpolator = new DecelerateInterpolator();
        this.overshootInterpolator = new OvershootInterpolator();
        this.startingKnobRotation = 0.0f;
        this.targetKnobRotation = 0.0f;
        this.animationStarted = 0;
        this.animationResetDuration = 0.25f;
        this.animationNormalDuration = 300;
        this.animating = false;
        this.canAnimate = true;
        this.thumbDown = false;
        this.lastX = 0.0f;
        this.transform = new Matrix();
        this.animateKnob = new Runnable() {
            public void run() {
                if (WateringDialView.this.canAnimate) {
                    float interpolatedProgress = WateringDialView.this.decelerateInterpolator.getInterpolation(Math.min(((float) (System.currentTimeMillis() - WateringDialView.this.animationStarted)) / ((float) WateringDialView.this.animationDuration), 1.0f));
                    float diff = Math.abs(WateringDialView.this.startingKnobRotation - WateringDialView.this.targetKnobRotation);
                    if (WateringDialView.this.startingKnobRotation > WateringDialView.this.targetKnobRotation) {
                        WateringDialView.this.knobRotation = WateringDialView.this.startingKnobRotation - (diff * interpolatedProgress);
                    } else {
                        WateringDialView.this.knobRotation = WateringDialView.this.startingKnobRotation + (diff * interpolatedProgress);
                    }
                    WateringDialView.this.updateCurrentValue();
                    WateringDialView.this.invalidate();
                    if (interpolatedProgress == 1.0f) {
                        WateringDialView.this.animating = false;
                        WateringDialView.this.invalidate();
                        return;
                    }
                    WateringDialView.this.post(WateringDialView.this.animateKnob);
                    return;
                }
                WateringDialView.this.animating = false;
                WateringDialView.this.knobRotation = WateringDialView.this.targetKnobRotation;
                WateringDialView.this.updateCurrentValue();
                WateringDialView.this.invalidate();
            }
        };
        this.animateResizeHour1Ring = new Runnable() {
            public void run() {
                float interpolatedProgress = WateringDialView.this.decelerateInterpolator.getInterpolation(Math.min(((float) (System.currentTimeMillis() - WateringDialView.this.animationStarted)) / ((float) WateringDialView.this.animationDuration), 1.0f));
                float diff = Math.abs(WateringDialView.this.startingRingRadius - WateringDialView.this.targetRingRadius);
                if (WateringDialView.this.startingRingRadius > WateringDialView.this.targetRingRadius) {
                    WateringDialView.this.hourRing1Radius = WateringDialView.this.startingRingRadius - (diff * interpolatedProgress);
                } else {
                    WateringDialView.this.hourRing1Radius = WateringDialView.this.startingRingRadius + (diff * interpolatedProgress);
                }
                WateringDialView.this.invalidate();
                if (interpolatedProgress < 1.0f) {
                    WateringDialView.this.post(WateringDialView.this.animateResizeHour1Ring);
                    return;
                }
                WateringDialView.this.hourRing1Radius = WateringDialView.this.constantHourRing1Radius;
                WateringDialView.this.hourRing1Shrinking = false;
            }
        };
        this.animateResizeHour2Ring = new Runnable() {
            public void run() {
                float interpolatedProgress = WateringDialView.this.decelerateInterpolator.getInterpolation(Math.min(((float) (System.currentTimeMillis() - WateringDialView.this.animationStarted)) / ((float) WateringDialView.this.animationDuration), 1.0f));
                float diff = Math.abs(WateringDialView.this.startingRingRadius - WateringDialView.this.targetRingRadius);
                if (WateringDialView.this.startingRingRadius > WateringDialView.this.targetRingRadius) {
                    WateringDialView.this.hourRing2Radius = WateringDialView.this.startingRingRadius - (diff * interpolatedProgress);
                } else {
                    WateringDialView.this.hourRing2Radius = WateringDialView.this.startingRingRadius + (diff * interpolatedProgress);
                }
                WateringDialView.this.invalidate();
                if (interpolatedProgress < 1.0f) {
                    WateringDialView.this.post(WateringDialView.this.animateResizeHour2Ring);
                    return;
                }
                WateringDialView.this.hourRing2Radius = WateringDialView.this.constantHourRing2Radius;
                WateringDialView.this.hourRing2Shrinking = false;
            }
        };
        this.watering = false;
        this.brightBlue = getResources().getColor(R.color.rachio_remote_bright_blue);
        this.innerKnobBlue = getResources().getColor(R.color.remote_inner_knob_blue);
        this.hourRingBlue = getResources().getColor(R.color.remote_hour_ring_blue);
        this.mainRingBlue = getResources().getColor(R.color.remote_main_ring_blue);
        this.brightGreen = getResources().getColor(R.color.rachio_remote_bright_green);
        this.innerKnobGreen = getResources().getColor(R.color.remote_inner_knob_green);
        this.hourRingGreen = getResources().getColor(R.color.remote_hour_ring_green);
        this.resetEnabledDrawable = getResources().getDrawable(R.drawable.timer_clear_active);
        this.setTimerString = getResources().getString(R.string.remote_set_timer);
        this.durationString = getResources().getString(R.string.remote_duration);
        this.remainingString = getResources().getString(R.string.remote_watering_remaining);
        this.mainRingPaint.setColor(this.mainRingBlue);
        this.mainRingPaint.setAntiAlias(true);
        this.mainRingPaint.setStyle(Style.STROKE);
        this.mainRingActivePaint.setColor(this.brightGreen);
        this.mainRingActivePaint.setAntiAlias(true);
        this.mainRingActivePaint.setStyle(Style.STROKE);
        this.hourRingPaint.setColor(this.hourRingGreen);
        this.hourRingPaint.setAntiAlias(true);
        this.hourRingPaint.setStyle(Style.STROKE);
        this.knobOuterPaint.setAntiAlias(true);
        this.knobOuterPaint.setStyle(Style.FILL);
        this.knobInnerPaint.setAntiAlias(true);
        this.knobInnerPaint.setStyle(Style.FILL);
        Typeface typeface = Typeface.create("sans-serif-light", 0);
        this.textNumberPaint.setColor(-1);
        this.textNumberPaint.setTypeface(typeface);
        this.textNumberPaint.setAntiAlias(true);
        this.textUnitPaint.setColor(-1);
        this.textUnitPaint.setTypeface(typeface);
        this.textUnitPaint.setAntiAlias(true);
        this.textStatusPaint.setTypeface(typeface);
        this.textStatusPaint.setAntiAlias(true);
        this.gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public WateringDialView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
        updateKnob();
    }

    public WateringDialView(Context context) {
        this(context, null);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.smallKnobRadius = this.radius * this.smallKnobRadiusPct;
        if (this.knobRadius == 0.0f) {
            this.knobRadius = this.smallKnobRadius;
        }
        this.hourRing1Radius = this.mainRingRadius * this.hourRingRadiusPct;
        this.hourRing2Radius = this.hourRing1Radius * this.hourRingRadiusPct;
        this.constantHourRing1Radius = this.hourRing1Radius;
        this.constantHourRing2Radius = this.hourRing2Radius;
        this.oval.set(-this.mainRingRadius, -this.mainRingRadius, this.mainRingRadius, this.mainRingRadius);
        this.hourRingPaint.setStrokeWidth(this.radius * this.hourRingWidthPct);
        this.resetSize = this.radius * this.resetPct;
        this.resetTop = (int) ((-this.mainRingRadius) - (this.resetSize / 4.0f));
        this.resetLeft = (int) (this.mainRingRadius - (this.resetSize / 4.0f));
        this.resetBottom = (int) ((-this.mainRingRadius) + ((this.resetSize * 3.0f) / 4.0f));
        this.resetRight = (int) (this.mainRingRadius + ((this.resetSize * 3.0f) / 4.0f));
        this.resetEnabledDrawable.setBounds(this.resetLeft, this.resetTop, this.resetRight, this.resetBottom);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.watering) {
            Canvas canvas2 = canvas;
            canvas2.drawArc(this.oval, 270.0f, 360.0f * (((float) (this.endWateringTime - System.currentTimeMillis())) / ((float) (this.endWateringTime - this.startWatering))), false, this.mainRingActivePaint);
            return;
        }
        if (this.knobRotation >= 720.0f || this.hourRing2Shrinking) {
            canvas.drawCircle(0.0f, 0.0f, this.hourRing2Radius, this.hourRingPaint);
        }
        if (this.knobRotation >= 360.0f || this.hourRing1Shrinking) {
            canvas.drawCircle(0.0f, 0.0f, this.hourRing1Radius, this.hourRingPaint);
        }
        this.sweepAngle = this.knobRotation % 360.0f;
        canvas.drawArc(this.oval, 270.0f, this.sweepAngle, false, this.mainRingActivePaint);
        if (isResetEnabled()) {
            this.resetEnabledDrawable.draw(canvas);
        }
        if (!this.isRunning) {
            canvas.rotate(this.knobRotation);
            canvas.translate(0.0f, -this.mainRingRadius);
            canvas.drawCircle(0.0f, 0.0f, this.knobRadius, this.knobOuterPaint);
            canvas.drawCircle(0.0f, 0.0f, this.knobRadius * this.knobInnerRadiusPct, this.knobInnerPaint);
        }
    }

    public final void determineTextHeights$262b7b90() {
        this.timeTextY = ((-((1.5f * this.textNumSize) + this.textStatusSize)) / 2.0f) + this.textNumSize;
        this.statusTextY = this.timeTextY + this.textStatusSize;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.gestureDetector.onTouchEvent(event)) {
            return true;
        }
        if (this.animating) {
            return false;
        }
        this.transform.postTranslate(-(((float) getWidth()) / 2.0f), -(((float) getHeight()) / 2.0f));
        this.transform.postRotate(-this.knobRotation);
        event.transform(this.transform);
        float x = event.getX();
        double angle = 90.0d - Math.toDegrees(Math.atan2((double) (-event.getY()), (double) x));
        this.transform.reset();
        this.transform.postTranslate(0.0f, this.mainRingRadius);
        event.transform(this.transform);
        float knobY = -event.getY();
        this.transform.reset();
        switch (event.getActionMasked()) {
            case 0:
                if (!this.isRunning && Math.max(Math.abs(x), Math.abs(knobY)) < this.knobRadius * this.knobDownFuzz) {
                    this.thumbDown = true;
                    setGreenKnob();
                    break;
                }
            case 1:
            case 3:
                if (this.knobRotation < 0.0f) {
                    setValue(0, true);
                }
                if (this.knobRotation > this.limit / this.unitsPerDegree) {
                    setValue((int) this.limit, true);
                }
                this.thumbDown = false;
                break;
            case 2:
                if (this.thumbDown && Math.max(Math.abs(x), Math.abs(knobY)) < this.knobRadius * 4.0f) {
                    float prevKnobRotation = this.knobRotation;
                    this.knobRotation += (float) angle;
                    this.knobRotation = Math.min(this.knobRotation, (this.limit / this.unitsPerDegree) + ((float) this.snap));
                    this.knobRotation = Math.max(this.knobRotation, (float) (0 - this.snap));
                    if (prevKnobRotation < 360.0f && this.knobRotation >= 360.0f) {
                        resizeHourRing(1, true);
                    } else if (prevKnobRotation >= 360.0f && this.knobRotation < 360.0f) {
                        resizeHourRing(1, false);
                    }
                    if (prevKnobRotation < 720.0f && this.knobRotation >= 720.0f) {
                        resizeHourRing(2, true);
                    } else if (prevKnobRotation >= 720.0f && this.knobRotation < 720.0f) {
                        resizeHourRing(2, false);
                    }
                    invalidate();
                    updateCurrentValue();
                    updateStatusText();
                    break;
                }
                break;
        }
        return true;
    }

    private void resizeHourRing(int hours, boolean show) {
        this.animationDuration = this.animationNormalDuration;
        this.animationStarted = System.currentTimeMillis();
        if (hours == 1) {
            if (show) {
                this.startingRingRadius = this.mainRingRadius;
                this.targetRingRadius = this.hourRing1Radius;
            } else {
                this.startingRingRadius = this.hourRing1Radius;
                this.targetRingRadius = this.mainRingRadius;
                this.hourRing1Shrinking = true;
            }
            post(this.animateResizeHour1Ring);
        } else if (hours == 2) {
            if (show) {
                this.startingRingRadius = this.hourRing1Radius;
                this.targetRingRadius = this.hourRing2Radius;
            } else {
                this.startingRingRadius = this.hourRing2Radius;
                this.targetRingRadius = this.hourRing1Radius;
                this.hourRing2Shrinking = true;
            }
            post(this.animateResizeHour2Ring);
        }
    }

    public final boolean isResetEnabled() {
        return !this.isRunning && getValue() > 0;
    }

    public final void reset() {
        if (getValue() != 0) {
            setValue(0, true, (long) ((int) (this.animationResetDuration * ((float) getValue()))));
        }
    }

    private void setGreenKnob() {
        this.knobOuterPaint.setColor(this.brightGreen);
        this.knobInnerPaint.setColor(this.innerKnobGreen);
    }

    public final int getValue() {
        if (this.watering) {
            return 10;
        }
        if (this.animating) {
            return (int) (this.targetKnobRotation * this.unitsPerDegree);
        }
        return this.currentValue;
    }

    public final void setValue(int value, boolean animate) {
        setValue(value, animate, this.animationNormalDuration);
    }

    private void setValue(int value, boolean animate, long duration) {
        if (value >= 0 && ((float) value) <= this.limit) {
            if (animate) {
                this.animating = true;
                this.animationDuration = duration;
                this.startingKnobRotation = this.knobRotation;
                if (value == 0) {
                    this.targetKnobRotation = 0.0f;
                } else {
                    this.targetKnobRotation = valueToDegrees(value);
                }
                this.animationStarted = System.currentTimeMillis();
                post(this.animateKnob);
            } else {
                this.knobRotation = valueToDegrees(value);
                invalidate();
            }
            this.currentValue = value;
            updateStatusText();
            updateKnob();
        }
    }

    public final void onWateringStarted(long startedWhen, int duration) {
        this.startWatering = startedWhen;
        this.endWateringTime = ((long) duration) + startedWhen;
        this.watering = true;
        this.countdownHandler.post(this.countdownRunnable);
        invalidate();
    }

    public final void onWateringStopped() {
        super.onWateringStopped();
        this.watering = false;
        setValue(0, false);
        invalidate();
    }

    public final void setIsRunning(boolean value) {
        super.setIsRunning(value);
        if (this.isRunning) {
            this.mainRingActivePaint.setColor(this.brightBlue);
            this.hourRingPaint.setColor(this.hourRingBlue);
            this.textStatusPaint.setColor(this.brightBlue);
        } else {
            this.mainRingActivePaint.setColor(this.brightGreen);
            this.hourRingPaint.setColor(this.hourRingGreen);
            this.textStatusPaint.setColor(this.brightGreen);
        }
        updateStatusText();
    }

    private void updateStatusText() {
        if (this.isRunning) {
            this.textStatusPaint.setColor(this.brightBlue);
            this.statusText = this.remainingString;
        } else if (getValue() <= 0) {
            this.textStatusPaint.setColor(this.brightBlue);
            this.statusText = this.setTimerString;
        } else {
            this.textStatusPaint.setColor(this.brightGreen);
            this.statusText = this.durationString;
        }
    }

    private void updateKnob() {
        if (getValue() == 0) {
            this.knobOuterPaint.setColor(this.brightBlue);
            this.knobInnerPaint.setColor(this.innerKnobBlue);
            return;
        }
        setGreenKnob();
    }
}
