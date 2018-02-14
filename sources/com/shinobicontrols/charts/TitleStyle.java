package com.shinobicontrols.charts;

import android.graphics.Typeface;
import com.shinobicontrols.charts.Title.Position;

public abstract class TitleStyle {
    final fh<Integer> C = new fh(Integer.valueOf(-16777216));
    final fh<Float> D = new fh(Float.valueOf(12.0f));
    final fh<Typeface> E = new fh(null);
    final fh<Integer> F = new fh(Integer.valueOf(-1));
    final fh<Float> jO = new fh(Float.valueOf(6.0f));
    final fh<Float> sF = new fh(Float.valueOf(12.0f));
    final fh<Position> sG = new fh(Position.CENTER);
    final fh<Float> sH = new fh(Float.valueOf(10.0f));

    void b(TitleStyle titleStyle) {
        if (titleStyle != null) {
            this.F.c(Integer.valueOf(titleStyle.getBackgroundColor()));
            this.E.c(titleStyle.getTypeface());
            this.D.c(Float.valueOf(titleStyle.getTextSize()));
            this.sF.c(Float.valueOf(titleStyle.eZ()));
            this.sG.c(titleStyle.getPosition());
            this.C.c(Integer.valueOf(titleStyle.getTextColor()));
        }
    }

    public float getPadding() {
        return ((Float) this.jO.sU).floatValue();
    }

    public void setPadding(float padding) {
        this.jO.b(Float.valueOf(padding));
    }

    public float getMargin() {
        return ((Float) this.sH.sU).floatValue();
    }

    public void setMargin(float margin) {
        this.sH.sU = Float.valueOf(margin);
    }

    public int getBackgroundColor() {
        return ((Integer) this.F.sU).intValue();
    }

    public void setBackgroundColor(int backgroundColor) {
        this.F.b(Integer.valueOf(backgroundColor));
    }

    public Typeface getTypeface() {
        return (Typeface) this.E.sU;
    }

    public void setTypeface(Typeface typeface) {
        this.E.b(typeface);
    }

    public float getTextSize() {
        return ((Float) this.D.sU).floatValue();
    }

    public void setTextSize(float textSize) {
        this.D.b(Float.valueOf(textSize));
    }

    float eZ() {
        return ((Float) this.sF.sU).floatValue();
    }

    public Position getPosition() {
        return (Position) this.sG.sU;
    }

    public void setPosition(Position position) {
        this.sG.b(position);
    }

    public int getTextColor() {
        return ((Integer) this.C.sU).intValue();
    }

    public void setTextColor(int textColor) {
        this.C.b(Integer.valueOf(textColor));
    }
}
