package com.rachio.iro.ui.newschedulerulepath.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.model.WateringScheduleType;

public class WateringScheduleCell extends TextView {
    private static final int[] STATE_SCHEDULE_DIFFERENTMONTH = new int[]{R.attr.state_schedule_differentmonth};
    private static final int[] STATE_SCHEDULE_FIXED = new int[]{R.attr.state_schedule_fixed, R.attr.state_schedule_watering_day};
    private static final int[] STATE_SCHEDULE_FLEX = new int[]{R.attr.state_schedule_flex, R.attr.state_schedule_watering_day};
    private static final int[] STATE_SCHEDULE_RAINDELAY = new int[]{R.attr.state_schedule_raindelay, R.attr.state_schedule_watering_day};
    private static final int[] STATE_SCHEDULE_TODAY = new int[]{R.attr.state_schedule_today};
    public boolean isDifferentMonth;
    public boolean isToday;
    private Drawable mForeground;
    public long timeMillis;
    public WateringScheduleType wateringScheduleType;

    public WateringScheduleCell(Context context) {
        this(context, null);
    }

    public WateringScheduleCell(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.wateringScheduleCellStyle);
        setFocusable(false);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.WateringScheduleCell, R.attr.wateringScheduleCellStyle, 0);
        this.mForeground = a.getDrawable(0);
        a.recycle();
    }

    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 7);
        if (this.isToday) {
            mergeDrawableStates(drawableState, STATE_SCHEDULE_TODAY);
        }
        if (this.isDifferentMonth) {
            mergeDrawableStates(drawableState, STATE_SCHEDULE_DIFFERENTMONTH);
        }
        if (this.wateringScheduleType != null) {
            if (this.wateringScheduleType == WateringScheduleType.FLEX || this.wateringScheduleType == WateringScheduleType.FLEX_TODAY) {
                mergeDrawableStates(drawableState, STATE_SCHEDULE_FLEX);
            } else if (this.wateringScheduleType == WateringScheduleType.FIXED || this.wateringScheduleType == WateringScheduleType.FIXED_TODAY) {
                mergeDrawableStates(drawableState, STATE_SCHEDULE_FIXED);
            } else if (this.wateringScheduleType == WateringScheduleType.BOTH || this.wateringScheduleType == WateringScheduleType.BOTH_TODAY) {
                mergeDrawableStates(drawableState, STATE_SCHEDULE_FLEX);
                mergeDrawableStates(drawableState, STATE_SCHEDULE_FIXED);
            } else if (this.wateringScheduleType == WateringScheduleType.RAINDELAYED_TODAY) {
                mergeDrawableStates(drawableState, STATE_SCHEDULE_RAINDELAY);
                mergeDrawableStates(drawableState, STATE_SCHEDULE_TODAY);
            } else if (this.wateringScheduleType == WateringScheduleType.RAINDELAYED) {
                mergeDrawableStates(drawableState, STATE_SCHEDULE_RAINDELAY);
            }
        }
        return drawableState;
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mForeground != null) {
            this.mForeground.setState(getDrawableState());
            invalidate();
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (this.mForeground != null) {
            this.mForeground.setBounds(0, 0, w, h);
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mForeground != null) {
            this.mForeground.draw(canvas);
        }
    }

    public final void set(WateringScheduleType type) {
        this.wateringScheduleType = type;
        refreshDrawableState();
    }
}
