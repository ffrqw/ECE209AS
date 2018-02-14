package com.rachio.iro.ui.newschedulerulepath.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.Checkable;
import com.rachio.iro.R;

public class Indicator extends BaseCanvasView implements Checkable {
    private Drawable check;
    private Paint checkPaint;
    private boolean checked;
    private Paint dotPaint;
    private Paint ringPaint;

    public Indicator(Context context) {
        this(context, null);
    }

    private Indicator(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    private Indicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);
        this.ringPaint = new Paint();
        this.dotPaint = new Paint();
        this.checkPaint = new Paint();
        this.checked = false;
        Resources resources = getResources();
        int ringColour = resources.getColor(R.color.newdarkgrey);
        int checkColour = resources.getColor(R.color.newdarkgrey);
        int dotColour = resources.getColor(R.color.rachio_blue);
        this.ringPaint.setColor(ringColour);
        this.ringPaint.setAntiAlias(true);
        this.ringPaint.setStrokeWidth(TypedValue.applyDimension(1, 1.0f, resources.getDisplayMetrics()));
        this.ringPaint.setStyle(Style.STROKE);
        this.dotPaint.setColor(dotColour);
        this.dotPaint.setAntiAlias(true);
        this.checkPaint.setColor(checkColour);
        this.checkPaint.setAntiAlias(true);
        this.check = resources.getDrawable(R.drawable.tinycheck);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float centerX = ((float) getWidth()) / 2.0f;
        float centerY = ((float) getWidth()) / 2.0f;
        float radius = Math.min(centerX, centerY) * 0.95f;
        float dotRadius = radius * 0.75f;
        if (this.checked) {
            canvas.drawCircle(centerX, centerY, radius, this.checkPaint);
            this.check.draw(canvas);
            return;
        }
        canvas.drawCircle(centerX, centerY, radius, this.ringPaint);
        if (isSelected()) {
            canvas.drawCircle(centerX, centerY, dotRadius, this.dotPaint);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int checkSize = (int) (((float) Math.min(width, height)) * 0.9f);
        int leftPad = (width - checkSize) / 2;
        int topPad = (height - checkSize) / 2;
        this.check.setBounds(leftPad, topPad, checkSize + leftPad, checkSize + topPad);
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        invalidate();
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void toggle() {
        this.checked = !this.checked;
        invalidate();
    }

    public void setSelected(boolean selected) {
        super.setSelected(selected);
        invalidate();
    }

    protected final int getDefaultSize() {
        return 18;
    }
}
