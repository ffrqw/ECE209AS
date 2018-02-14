package com.rachio.iro.ui.newschedulerulepath.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;

public abstract class BaseCanvasView extends View {
    protected abstract int getDefaultSize();

    public BaseCanvasView(Context context) {
        super(context);
    }

    public BaseCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseCanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int defaultSize = (int) TypedValue.applyDimension(1, (float) getDefaultSize(), getResources().getDisplayMetrics());
        switch (widthMode) {
            case Integer.MIN_VALUE:
                width = Math.min(defaultSize, widthSize);
                break;
            case 1073741824:
                width = widthSize;
                break;
            default:
                width = defaultSize;
                break;
        }
        switch (heightMode) {
            case Integer.MIN_VALUE:
                height = Math.min(defaultSize, heightSize);
                break;
            case 1073741824:
                height = heightSize;
                break;
            default:
                height = defaultSize;
                break;
        }
        setMeasuredDimension(width, height);
    }
}
