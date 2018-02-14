package com.shinobicontrols.charts;

import android.graphics.Typeface;

public class AnnotationStyle {
    final fh<Integer> C = new fh(Integer.valueOf(-16777216));
    final fh<Float> D = new fh(Float.valueOf(12.0f));
    final fh<Typeface> E = new fh(null);
    final fh<Integer> F = new fh(Integer.valueOf(0));

    public int getTextColor() {
        return ((Integer) this.C.sU).intValue();
    }

    public void setTextColor(int textColor) {
        this.C.b(Integer.valueOf(textColor));
    }

    public float getTextSize() {
        return ((Float) this.D.sU).floatValue();
    }

    public void setTextSize(float textSize) {
        this.D.b(Float.valueOf(textSize));
    }

    public Typeface getTypeface() {
        return (Typeface) this.E.sU;
    }

    public void setTypeface(Typeface typeface) {
        this.E.b(typeface);
    }

    public int getBackgroundColor() {
        return ((Integer) this.F.sU).intValue();
    }

    public void setBackgroundColor(int backgroundColor) {
        this.F.b(Integer.valueOf(backgroundColor));
    }

    void a(AnnotationStyle annotationStyle) {
        if (annotationStyle != null) {
            this.F.c(Integer.valueOf(annotationStyle.getBackgroundColor()));
            this.C.c(Integer.valueOf(annotationStyle.getTextColor()));
            this.D.c(Float.valueOf(annotationStyle.getTextSize()));
            this.E.c(annotationStyle.getTypeface());
        }
    }
}
