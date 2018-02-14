package com.shinobicontrols.charts;

import android.graphics.Typeface;
import com.shinobicontrols.charts.PieDonutSeries.RadialEffect;

abstract class PieDonutSeriesStyle extends SeriesStyle {
    final fh<Typeface> mA = new fh(null);
    final fh<Float> mB = new fh(Float.valueOf(10.0f));
    final fh<Integer> mC = new fh(Integer.valueOf(-16777216));
    final fh<Integer> mD = new fh(Integer.valueOf(0));
    final fh<Boolean> mr = new fh(Boolean.valueOf(true));
    final fh<Boolean> ms = new fh(Boolean.valueOf(true));
    final fh<Boolean> mt = new fh(Boolean.valueOf(true));
    final fh<RadialEffect> mu = new fh(RadialEffect.DEFAULT);
    final fh<Float> mv = new fh(Float.valueOf(0.0f));
    final fh<Integer[]> mw = new fh(new Integer[]{Integer.valueOf(-16777216), Integer.valueOf(-1)});
    final fh<Float> mx = new fh(Float.valueOf(0.0f));
    final fh<Integer[]> my = new fh(new Integer[]{Integer.valueOf(-16777216), Integer.valueOf(-1)});
    final fh<Float> mz = new fh(Float.valueOf(0.0f));

    PieDonutSeriesStyle() {
    }

    void a(SeriesStyle seriesStyle) {
        synchronized (ah.lock) {
            super.a(seriesStyle);
            PieDonutSeriesStyle pieDonutSeriesStyle = (PieDonutSeriesStyle) seriesStyle;
            this.mr.c(Boolean.valueOf(pieDonutSeriesStyle.isCrustShown()));
            this.ms.c(Boolean.valueOf(pieDonutSeriesStyle.isFlavorShown()));
            this.mt.c(Boolean.valueOf(pieDonutSeriesStyle.areLabelsShown()));
            this.mu.c(pieDonutSeriesStyle.getRadialEffect());
            this.mv.c(Float.valueOf(pieDonutSeriesStyle.getInitialRotation()));
            this.mw.c(pieDonutSeriesStyle.mw.sU);
            this.mx.c(Float.valueOf(pieDonutSeriesStyle.getCrustThickness()));
            this.my.c(pieDonutSeriesStyle.my.sU);
            this.mz.c(Float.valueOf(pieDonutSeriesStyle.getProtrusion()));
            this.mA.c(pieDonutSeriesStyle.getLabelTypeface());
            this.mB.c(Float.valueOf(pieDonutSeriesStyle.getLabelTextSize()));
            this.mC.c(Integer.valueOf(pieDonutSeriesStyle.getLabelTextColor()));
            this.mD.c(Integer.valueOf(pieDonutSeriesStyle.getLabelBackgroundColor()));
        }
    }

    public boolean isCrustShown() {
        return ((Boolean) this.mr.sU).booleanValue();
    }

    public void setCrustShown(boolean crustShown) {
        synchronized (ah.lock) {
            this.mr.b(Boolean.valueOf(crustShown));
            ag();
        }
    }

    public boolean isFlavorShown() {
        return ((Boolean) this.ms.sU).booleanValue();
    }

    public void setFlavorShown(boolean flavorShown) {
        synchronized (ah.lock) {
            this.ms.b(Boolean.valueOf(flavorShown));
            ag();
        }
    }

    public boolean areLabelsShown() {
        return ((Boolean) this.mt.sU).booleanValue();
    }

    public void setLabelsShown(boolean labelsShown) {
        synchronized (ah.lock) {
            this.mt.b(Boolean.valueOf(labelsShown));
            ag();
        }
    }

    public RadialEffect getRadialEffect() {
        return (RadialEffect) this.mu.sU;
    }

    public void setRadialEffect(RadialEffect radialEffect) {
        synchronized (ah.lock) {
            this.mu.b(radialEffect);
            ag();
        }
    }

    public float getInitialRotation() {
        return ((Float) this.mv.sU).floatValue();
    }

    public void setInitialRotation(float initialRotation) {
        synchronized (ah.lock) {
            this.mv.b(Float.valueOf(initialRotation));
            ag();
        }
    }

    public int crustColorAtIndex(int index) {
        Integer[] numArr = (Integer[]) this.mw.sU;
        return numArr[index % numArr.length].intValue();
    }

    Integer[] do() {
        return (Integer[]) this.mw.sU;
    }

    public void setCrustColors(int[] crustColors) {
        synchronized (ah.lock) {
            if (crustColors == null) {
                return;
            }
            Object obj = new Integer[crustColors.length];
            for (int i = 0; i < crustColors.length; i++) {
                obj[i] = Integer.valueOf(crustColors[i]);
            }
            this.mw.b(obj);
            ag();
        }
    }

    public float getCrustThickness() {
        return ((Float) this.mx.sU).floatValue();
    }

    public void setCrustThickness(float crustThickness) {
        synchronized (ah.lock) {
            if (crustThickness < 1.0f) {
                ev.g("Ignoring setting of crustThickness: cannot have a crustThickness of less than 1");
            } else {
                this.mx.b(Float.valueOf(crustThickness));
                ag();
            }
        }
    }

    public int flavorColorAtIndex(int index) {
        Integer[] numArr = (Integer[]) this.my.sU;
        return numArr[index % numArr.length].intValue();
    }

    Integer[] dp() {
        return (Integer[]) this.my.sU;
    }

    public void setFlavorColors(int[] flavorColors) {
        synchronized (ah.lock) {
            if (flavorColors == null) {
                return;
            }
            Object obj = new Integer[flavorColors.length];
            for (int i = 0; i < flavorColors.length; i++) {
                obj[i] = Integer.valueOf(flavorColors[i]);
            }
            this.my.b(obj);
            ag();
        }
    }

    public float getProtrusion() {
        return ((Float) this.mz.sU).floatValue();
    }

    public void setProtrusion(float protrusion) {
        synchronized (ah.lock) {
            if (protrusion < 0.0f) {
                throw new IllegalArgumentException("Protrusion must be positive");
            }
            this.mz.b(Float.valueOf(protrusion));
            ag();
        }
    }

    public Typeface getLabelTypeface() {
        return (Typeface) this.mA.sU;
    }

    public void setLabelTypeface(Typeface labelTypeface) {
        this.mA.b(labelTypeface);
        ag();
    }

    public int getLabelTextColor() {
        return ((Integer) this.mC.sU).intValue();
    }

    public void setLabelTextColor(int labelTextColor) {
        this.mC.b(Integer.valueOf(labelTextColor));
        ag();
    }

    public int getLabelBackgroundColor() {
        return ((Integer) this.mD.sU).intValue();
    }

    public void setLabelBackgroundColor(int labelBackgroundColor) {
        this.mD.b(Integer.valueOf(labelBackgroundColor));
        ag();
    }

    public float getLabelTextSize() {
        return ((Float) this.mB.sU).floatValue();
    }

    public void setLabelTextSize(float labelTextSize) {
        this.mB.b(Float.valueOf(labelTextSize));
        ag();
    }
}
