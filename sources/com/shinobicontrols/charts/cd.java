package com.shinobicontrols.charts;

import android.content.Context;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.shinobicontrols.charts.Legend.SymbolAlignment;

class cd extends LinearLayout {
    private final float density = getResources().getDisplayMetrics().density;
    private final LinearLayout jJ;
    private final LinearLayout jK;

    public cd(Context context) {
        super(context);
        setOrientation(0);
        this.jJ = new LinearLayout(context);
        this.jJ.setLayoutParams(new LayoutParams(-2, -2));
        this.jJ.setOrientation(1);
        this.jK = new LinearLayout(context);
        this.jK.setLayoutParams(new LayoutParams(-2, -2));
        this.jK.setOrientation(1);
    }

    void a(cc ccVar) {
        View dc = ccVar.dc();
        dc.setLayoutParams(new LayoutParams(0, 0));
        this.jJ.addView(dc);
        dc = ccVar.db();
        dc.setLayoutParams(new LayoutParams(-2, -2));
        this.jK.addView(dc);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i = 0;
        this.jK.measure(widthMeasureSpec, heightMeasureSpec);
        if (this.jK.getChildAt(0) != null) {
            FontMetrics fontMetrics = ((TextView) this.jK.getChildAt(0)).getPaint().getFontMetrics();
            int ceil = (((int) Math.ceil((double) (fontMetrics.bottom - fontMetrics.top))) + ((TextView) this.jK.getChildAt(0)).getPaddingTop()) + ((TextView) this.jK.getChildAt(0)).getPaddingBottom();
            if (this.jJ.getVisibility() == 0) {
                for (int i2 = 0; i2 < this.jJ.getChildCount(); i2++) {
                    MarginLayoutParams marginLayoutParams = (MarginLayoutParams) this.jJ.getChildAt(i2).getLayoutParams();
                    marginLayoutParams.height = ceil;
                    MarginLayoutParams marginLayoutParams2 = (MarginLayoutParams) this.jK.getChildAt(i2).getLayoutParams();
                    double measuredHeight = (double) this.jK.getChildAt(i2).getMeasuredHeight();
                    int floor = (int) Math.floor((measuredHeight - ((double) ceil)) / 2.0d);
                    floor += marginLayoutParams2.topMargin;
                    int ceil2 = marginLayoutParams2.bottomMargin + ((int) Math.ceil((measuredHeight - ((double) ceil)) / 2.0d));
                    if (floor < 0) {
                        floor = 0;
                    }
                    if (ceil2 < 0) {
                        ceil2 = 0;
                    }
                    marginLayoutParams.topMargin = floor;
                    marginLayoutParams.bottomMargin = ceil2;
                }
                this.jJ.measure(widthMeasureSpec, heightMeasureSpec);
                i = ca.b(this.jJ);
            }
            setMeasuredDimension(View.resolveSize(ca.b(this.jK) + i, widthMeasureSpec), View.resolveSize(ca.a(this.jK), heightMeasureSpec));
        }
    }

    void d(LegendStyle legendStyle) {
        d(legendStyle.areSymbolsShown());
        int c = ca.c(this.density, legendStyle.getRowVerticalMargin() / 2.0f);
        a(legendStyle.getSymbolAlignment(), legendStyle.getSymbolLabelGap());
        int c2 = ca.c(this.density, legendStyle.getSymbolWidth());
        int childCount = this.jJ.getChildCount();
        for (int i = 0; i < childCount; i++) {
            int i2;
            View childAt = this.jJ.getChildAt(i);
            a(childAt, legendStyle.getSymbolCornerRadius());
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            layoutParams.width = c2;
            if (i > 0) {
                i2 = c;
            } else {
                i2 = 0;
            }
            layoutParams.topMargin = i2;
            if (i < childCount - 1) {
                i2 = c;
            } else {
                i2 = 0;
            }
            layoutParams.bottomMargin = i2;
        }
        int childCount2 = this.jK.getChildCount();
        int i3 = 0;
        while (i3 < childCount2) {
            View childAt2 = this.jK.getChildAt(i3);
            a(childAt2, legendStyle.getTypeface(), legendStyle.getTextSize(), legendStyle.getTextColor(), legendStyle.getTextAlignment());
            layoutParams = (LayoutParams) childAt2.getLayoutParams();
            layoutParams.gravity = legendStyle.getTextAlignment();
            layoutParams.topMargin = i3 > 0 ? c : 0;
            if (i3 < childCount2 - 1) {
                i2 = c;
            } else {
                i2 = 0;
            }
            layoutParams.bottomMargin = i2;
            i3++;
        }
        a(legendStyle.getSymbolAlignment());
    }

    private void d(boolean z) {
        this.jJ.setVisibility(z ? 0 : 8);
    }

    private void a(SymbolAlignment symbolAlignment, float f) {
        int c = ca.c(this.density, f / 2.0f);
        if (symbolAlignment == SymbolAlignment.LEFT) {
            ((LayoutParams) this.jJ.getLayoutParams()).setMargins(0, 0, c, 0);
            ((LayoutParams) this.jK.getLayoutParams()).setMargins(c, 0, 0, 0);
            return;
        }
        ((LayoutParams) this.jK.getLayoutParams()).setMargins(0, 0, c, 0);
        ((LayoutParams) this.jJ.getLayoutParams()).setMargins(c, 0, 0, 0);
    }

    private void a(View view, Typeface typeface, float f, int i, int i2) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            textView.setTypeface(typeface);
            textView.setTextSize(2, f);
            textView.setTextColor(i);
            textView.setGravity(i2);
        }
    }

    private void a(View view, float f) {
        if (view instanceof cf) {
            Drawable de = ((cf) view).de();
            if (de instanceof GradientDrawable) {
                ((GradientDrawable) de).setCornerRadius(f);
            }
        }
    }

    private void a(SymbolAlignment symbolAlignment) {
        if (this.jJ.getVisibility() != 0) {
            addView(this.jK);
        } else if (symbolAlignment == SymbolAlignment.LEFT) {
            addView(this.jJ);
            addView(this.jK);
        } else {
            addView(this.jK);
            addView(this.jJ);
        }
    }
}
