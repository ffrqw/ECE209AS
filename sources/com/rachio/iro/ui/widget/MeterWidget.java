package com.rachio.iro.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.View;
import com.rachio.iro.R;

public class MeterWidget extends View {
    private Bitmap backingBitmap;
    private Path clipPath;
    private float cornerRadius;
    private float mCurrentValue;
    private float mMaxValue;
    private int mMeterEndColor;
    private int mMeterStartColor;
    private Paint progressPaint;
    private Rect progressRect;
    private Canvas temp;

    public MeterWidget(Context context) {
        this(context, null);
    }

    public MeterWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.clipPath = new Path();
        this.temp = new Canvas();
        this.progressPaint = new Paint();
        this.progressRect = new Rect();
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MeterWidget);
        this.mMeterStartColor = a.getColor(0, getResources().getColor(R.color.rachio_blue));
        this.mMeterEndColor = a.getColor(1, getResources().getColor(R.color.rachio_grey));
        this.mCurrentValue = a.getFloat(3, 0.0f);
        this.mMaxValue = a.getFloat(2, 1.0f);
        a.recycle();
        this.cornerRadius = (float) getResources().getDimensionPixelSize(R.dimen.card_corner_radius);
        this.progressPaint.setColor(this.mMeterStartColor);
    }

    public final void setCurrentValue(float value) {
        if (this.mCurrentValue != value) {
            this.mCurrentValue = Math.min(this.mMaxValue, value);
            updateMeter();
        }
    }

    public final void setMaxValue(float value) {
        if (this.mMaxValue != value) {
            this.mMaxValue = Math.max(0.0f, value);
            updateMeter();
        }
    }

    private void updateMeter() {
        invalidate();
        requestLayout();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.backingBitmap != null) {
            this.temp.drawARGB(0, 0, 0, 0);
            this.temp.clipPath(this.clipPath);
            this.temp.drawColor(this.mMeterEndColor);
            float p = this.mCurrentValue / this.mMaxValue;
            this.progressRect.right = (int) (((float) getWidth()) * p);
            this.temp.drawRect(this.progressRect, this.progressPaint);
            canvas.drawBitmap(this.backingBitmap, 0.0f, 0.0f, null);
        }
    }

    @SuppressLint({"NewApi"})
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.clipPath.reset();
        this.clipPath.addRoundRect(new RectF(0.0f, 0.0f, (float) w, (float) h), this.cornerRadius, this.cornerRadius, Direction.CW);
        if (this.backingBitmap == null || this.backingBitmap.getWidth() * this.backingBitmap.getHeight() != w * h || VERSION.SDK_INT < 19) {
            this.backingBitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        } else {
            this.backingBitmap.reconfigure(w, h, Config.ARGB_8888);
        }
        this.temp.setBitmap(this.backingBitmap);
        this.progressRect.left = 0;
        this.progressRect.top = 0;
        this.progressRect.bottom = h;
    }
}
