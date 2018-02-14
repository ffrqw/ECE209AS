package com.shinobicontrols.charts;

import android.graphics.Typeface;
import com.shinobicontrols.charts.Legend.SymbolAlignment;

public final class LegendStyle {
    final fh<Integer> C = new fh(Integer.valueOf(-16777216));
    final fh<Float> D = new fh(Float.valueOf(10.0f));
    final fh<Integer> F = new fh(Integer.valueOf(0));
    final fh<Integer> fn = new fh(Integer.valueOf(0));
    final fh<Float> fo = new fh(Float.valueOf(0.0f));
    final fh<Float> jL = new fh(Float.valueOf(0.0f));
    final fh<Typeface> jM = new fh(null);
    final fh<Float> jN = new fh(Float.valueOf(4.0f));
    final fh<Float> jO = new fh(Float.valueOf(4.0f));
    final fh<Boolean> jP = new fh(Boolean.valueOf(true));
    final fh<SymbolAlignment> jQ = new fh(SymbolAlignment.LEFT);
    final fh<Float> jR = new fh(Float.valueOf(0.0f));
    final fh<Float> jS = new fh(Float.valueOf(32.0f));
    final fh<Integer> jT = new fh(Integer.valueOf(0));
    final fh<Typeface> jU = new fh(null);
    final fh<Integer> jV = new fh(Integer.valueOf(-16777216));
    final fh<Float> jW = new fh(Float.valueOf(12.0f));
    final fh<Float> jX = new fh(Float.valueOf(4.0f));
    final cm jY = new cm();

    final void e(LegendStyle legendStyle) {
        if (legendStyle != null) {
            this.F.c(Integer.valueOf(legendStyle.getBackgroundColor()));
            this.fn.c(Integer.valueOf(legendStyle.getBorderColor()));
            this.fo.c(Float.valueOf(legendStyle.getBorderWidth()));
            this.jL.c(Float.valueOf(legendStyle.getCornerRadius()));
            this.jM.c(legendStyle.getTypeface());
            this.C.c(Integer.valueOf(legendStyle.getTextColor()));
            this.D.c(Float.valueOf(legendStyle.getTextSize()));
            this.jN.c(Float.valueOf(legendStyle.getSymbolLabelGap()));
            this.jO.c(Float.valueOf(legendStyle.getPadding()));
            this.jP.c(Boolean.valueOf(legendStyle.areSymbolsShown()));
            this.jQ.c(legendStyle.getSymbolAlignment());
            this.jR.c(Float.valueOf(legendStyle.getSymbolCornerRadius()));
            this.jS.c(Float.valueOf(legendStyle.getSymbolWidth()));
            this.jT.c(Integer.valueOf(legendStyle.getTextAlignment()));
            this.jU.c(legendStyle.getTitleTypeface());
            this.jV.c(Integer.valueOf(legendStyle.getTitleTextColor()));
            this.jW.c(Float.valueOf(legendStyle.getTitleTextSize()));
            this.jX.c(Float.valueOf(legendStyle.getRowVerticalMargin()));
        }
    }

    public final float getTitlePadding() {
        return this.jY.getPadding();
    }

    public final void setTitlePadding(float padding) {
        this.jY.setPadding(padding);
    }

    public final float getTitleMargin() {
        return this.jY.getMargin();
    }

    public final void setTitleMargin(float margin) {
        this.jY.setMargin(margin);
    }

    public final int getBackgroundColor() {
        return ((Integer) this.F.sU).intValue();
    }

    public final int getBorderColor() {
        return ((Integer) this.fn.sU).intValue();
    }

    public final float getBorderWidth() {
        return ((Float) this.fo.sU).floatValue();
    }

    public final float getCornerRadius() {
        return ((Float) this.jL.sU).floatValue();
    }

    public final Typeface getTypeface() {
        return (Typeface) this.jM.sU;
    }

    public final int getTextColor() {
        return ((Integer) this.C.sU).intValue();
    }

    public final float getTextSize() {
        return ((Float) this.D.sU).floatValue();
    }

    public final float getSymbolLabelGap() {
        return ((Float) this.jN.sU).floatValue();
    }

    public final float getPadding() {
        return ((Float) this.jO.sU).floatValue();
    }

    public final SymbolAlignment getSymbolAlignment() {
        return (SymbolAlignment) this.jQ.sU;
    }

    public final float getSymbolCornerRadius() {
        return ((Float) this.jR.sU).floatValue();
    }

    public final float getSymbolWidth() {
        return ((Float) this.jS.sU).floatValue();
    }

    public final int getTextAlignment() {
        return ((Integer) this.jT.sU).intValue();
    }

    public final Typeface getTitleTypeface() {
        return this.jY.getTypeface();
    }

    public final int getTitleTextColor() {
        return this.jY.getTextColor();
    }

    public final float getTitleTextSize() {
        return this.jY.getTextSize();
    }

    public final float getRowVerticalMargin() {
        return ((Float) this.jX.sU).floatValue();
    }

    public final boolean areSymbolsShown() {
        return ((Boolean) this.jP.sU).booleanValue();
    }

    public final void setBackgroundColor(int backgroundColor) {
        this.F.b(Integer.valueOf(backgroundColor));
    }

    public final void setBorderColor(int borderColor) {
        this.fn.b(Integer.valueOf(borderColor));
    }

    public final void setBorderWidth(float borderWidth) {
        this.fo.b(Float.valueOf(borderWidth));
    }

    public final void setCornerRadius(float cornerRadius) {
        this.jL.b(Float.valueOf(cornerRadius));
    }

    public final void setTypeface(Typeface typeface) {
        this.jM.b(typeface);
    }

    public final void setTextColor(int textColor) {
        this.C.b(Integer.valueOf(textColor));
    }

    public final void setTextSize(float textSize) {
        this.D.b(Float.valueOf(textSize));
    }

    public final void setSymbolLabelGap(float symbolLabelGap) {
        this.jN.b(Float.valueOf(symbolLabelGap));
    }

    public final void setPadding(float padding) {
        this.jO.b(Float.valueOf(padding));
    }

    public final void setSymbolsShown(boolean showSymbols) {
        this.jP.b(Boolean.valueOf(showSymbols));
    }

    public final void setSymbolAlignment(SymbolAlignment symbolAlignment) {
        this.jQ.b(symbolAlignment);
    }

    public final void setSymbolCornerRadius(float symbolCornerRadius) {
        this.jR.b(Float.valueOf(symbolCornerRadius));
    }

    public final void setSymbolWidth(float symbolWidth) {
        this.jS.b(Float.valueOf(symbolWidth));
    }

    public final void setTextAlignment(int textAlignment) {
        this.jT.b(Integer.valueOf(textAlignment));
    }

    public final void setTitleTypeface(Typeface titleTypeface) {
        this.jY.E.b(titleTypeface);
    }

    public final void setTitleTextColor(int titleTextColor) {
        this.jY.C.b(Integer.valueOf(titleTextColor));
    }

    public final void setTitleTextSize(float titleTextSize) {
        this.jY.D.b(Float.valueOf(titleTextSize));
    }

    public final void setRowVerticalMargin(float rowVerticalMargin) {
        this.jX.b(Float.valueOf(rowVerticalMargin));
    }

    final cm dd() {
        return this.jY;
    }
}
