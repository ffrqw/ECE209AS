package com.shinobicontrols.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.TextView;

public class Title extends TextView {
    private final float density = getResources().getDisplayMetrics().density;
    private final boolean sy;
    private Orientation sz = Orientation.HORIZONTAL;

    public enum CentersOn {
        CANVAS,
        CHART,
        PLOTTING_AREA
    }

    public enum Orientation {
        HORIZONTAL(0),
        VERTICAL(1);
        
        private final int mp;

        private Orientation(int xmlValue) {
            this.mp = xmlValue;
        }

        public final int getXmlValue() {
            return this.mp;
        }
    }

    public enum Position {
        BOTTOM_OR_LEFT(0, 3, 80),
        CENTER(1, 1, 16),
        TOP_OR_RIGHT(2, 5, 48);
        
        private final int mp;
        private final int sC;
        private final int sD;

        private Position(int xmlValue, int horizontalGravity, int verticalGravity) {
            this.mp = xmlValue;
            this.sC = horizontalGravity;
            this.sD = verticalGravity;
        }

        public final int getXmlValue() {
            return this.mp;
        }

        final int eX() {
            return this.sC;
        }

        final int eY() {
            return this.sD;
        }
    }

    Title(Context context) {
        super(context);
        int gravity = getGravity();
        if (Gravity.isVertical(gravity) && (gravity & 112) == 80) {
            setGravity((gravity & 7) | 48);
            this.sy = false;
            return;
        }
        this.sy = true;
    }

    void setOrientation(Orientation orientation) {
        this.sz = orientation;
        invalidate();
        requestLayout();
    }

    void a(TitleStyle titleStyle) {
        setTextSize(2, ((Float) titleStyle.D.sU).floatValue());
        setTypeface((Typeface) titleStyle.E.sU);
        setTextColor(((Integer) titleStyle.C.sU).intValue());
        if (titleStyle.getBackgroundColor() == 0) {
            a.a((View) this, null);
        } else {
            setBackgroundColor(((Integer) titleStyle.F.sU).intValue());
        }
        int c = ca.c(this.density, ((Float) titleStyle.jO.sU).floatValue());
        setPadding(c, c, c, c);
        int c2 = ca.c(this.density, ((Float) titleStyle.sH.sU).floatValue());
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) getLayoutParams();
        marginLayoutParams.topMargin = c2;
        marginLayoutParams.bottomMargin = c2;
        marginLayoutParams.leftMargin = c2;
        marginLayoutParams.rightMargin = c2;
    }

    protected void onDraw(Canvas canvas) {
        if (this.sz == Orientation.HORIZONTAL) {
            super.onDraw(canvas);
            return;
        }
        TextPaint paint = getPaint();
        paint.setColor(getCurrentTextColor());
        paint.drawableState = getDrawableState();
        canvas.save();
        if (this.sy) {
            canvas.translate(0.0f, (float) getHeight());
            canvas.rotate(-90.0f);
        } else {
            canvas.translate((float) getWidth(), 0.0f);
            canvas.rotate(90.0f);
        }
        canvas.translate((float) getCompoundPaddingLeft(), (float) getExtendedPaddingTop());
        getLayout().draw(canvas);
        canvas.restore();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.sz == Orientation.HORIZONTAL) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
            return;
        }
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }
}
