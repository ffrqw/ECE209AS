package com.rachio.iro.ui.newschedulerulepath.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.rachio.iro.R;

public class ScheduleTypeSavingsBarView extends LinearLayout {
    private ImageView indicator;
    private float indicatorPos;

    public ScheduleTypeSavingsBarView(Context context) {
        this(context, null);
    }

    public ScheduleTypeSavingsBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScheduleTypeSavingsBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(1);
        inflate(context, R.layout.view_scheduletypesavingsbar, this);
        this.indicator = (ImageView) findViewById(R.id.indicator);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScheduleTypeSavingsBarView);
        this.indicatorPos = a.getFloat(0, 0.0f);
        a.recycle();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ((LayoutParams) this.indicator.getLayoutParams()).setMargins((int) (((float) (getMeasuredWidth() - this.indicator.getMeasuredWidth())) * this.indicatorPos), 0, 0, 0);
    }
}
