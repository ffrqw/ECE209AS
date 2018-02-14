package com.shinobicontrols.charts;

import android.graphics.Paint;
import android.graphics.Point;

public final class PieDonutSlice extends InternalDataPoint {
    String mE;
    float mF;
    float mG;
    float mH;
    float mI;
    Point mJ;
    Point mK;
    Paint mL = new Paint();
    Paint mM = new Paint();

    public final /* bridge */ /* synthetic */ String toString() {
        return super.toString();
    }

    PieDonutSlice(double x, double y) {
        super(x, y);
    }

    public final float getCenterAngle() {
        return (this.mF + this.mG) / 2.0f;
    }

    public final Point getLabelCenter() {
        return this.mK;
    }

    public final String getLabelText() {
        return this.mE;
    }

    public final void setLabelText(String labelText) {
        this.mE = labelText;
    }

    public final double getY() {
        return this.y;
    }

    public final int getCenterX() {
        return this.mJ.x;
    }

    public final int getCenterY() {
        return this.mJ.y;
    }

    public final Paint getLabelPaint() {
        return this.mL;
    }

    public final Paint getLabelBackgroundPaint() {
        return this.mM;
    }
}
