package com.shinobicontrols.charts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

@SuppressLint({"ViewConstructor"})
public class Tooltip extends FrameLayout {
    final du fF = new du();
    private CartesianSeries<?> fM;
    private Data<?, ?> sI;
    ff sJ;
    private final GradientDrawable sK = new GradientDrawable();
    private final bg u = new bg();
    private View view;

    Tooltip(Context context) {
        super(context);
        this.view = new DefaultTooltipView(context);
        addView(this.view, new LayoutParams(-2, -2, 17));
        setVisibility(8);
        this.sJ = ff.c(this);
    }

    public CartesianSeries<?> getTrackedSeries() {
        return this.fM;
    }

    void l(CartesianSeries<?> cartesianSeries) {
        this.fM = cartesianSeries;
        this.sJ = ff.c(this);
    }

    public Data<?, ?> getCenter() {
        return this.sI;
    }

    public void setCenter(Data<?, ?> center) {
        this.sI = center;
        if (center == null || center.getX() == null || center.getY() == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.TooltipNullXOrYInCenterPoint));
        }
        bP();
    }

    void c(CrosshairStyle crosshairStyle) {
        if (crosshairStyle != null && (this.view instanceof DefaultTooltipView)) {
            a((View) this, crosshairStyle);
            a(this.view, crosshairStyle, 0, 9);
        }
    }

    public void forceLayout() {
        super.forceLayout();
        this.view.forceLayout();
    }

    void c(CartesianSeries<?> cartesianSeries, DataPoint<?, ?> dataPoint, DataPoint<?, ?> dataPoint2, DataPoint<?, ?> dataPoint3) {
        setCenter(cartesianSeries.dX.a(dataPoint, dataPoint2, dataPoint3, cartesianSeries.getChart().getCrosshair().fI));
        ChartUtils.updateTooltipContent(this, cartesianSeries.dX.b(dataPoint, dataPoint2, dataPoint3, cartesianSeries.getChart().getCrosshair().fI));
    }

    private void bP() {
        if (this.fM != null) {
            if (this.sI == null) {
                throw new IllegalStateException(getContext().getString(R.string.TooltipNullCenter));
            }
            this.fF.x = Crosshair.a(this.sI.getX(), this.fM.getXAxis(), this.fM);
            this.fF.y = Crosshair.a(this.sI.getY(), this.fM.getYAxis(), this.fM);
        }
    }

    public View getView() {
        return this.view;
    }

    void bX() {
        bP();
    }

    private void a(View view, CrosshairStyle crosshairStyle, int i, int i2) {
        while (i < i2) {
            a((TextView) ((DefaultTooltipView) view).getChildAt(i), crosshairStyle);
            i++;
        }
    }

    private void a(TextView textView, CrosshairStyle crosshairStyle) {
        textView.setTextColor(crosshairStyle.getTooltipTextColor());
        textView.setTypeface(crosshairStyle.getTooltipTypeface());
        textView.setTextSize(2, crosshairStyle.getTooltipTextSize());
        textView.setBackgroundColor(crosshairStyle.getTooltipLabelBackgroundColor());
    }

    private void a(View view, CrosshairStyle crosshairStyle) {
        float f = view.getResources().getDisplayMetrics().density;
        int c = ca.c(f, crosshairStyle.getTooltipPadding());
        view.setPadding(c, c, c, c);
        this.sK.setColor(crosshairStyle.getTooltipBackgroundColor());
        this.sK.setStroke(ca.c(f, crosshairStyle.getTooltipBorderWidth()), crosshairStyle.getTooltipBorderColor());
        this.sK.setCornerRadius((float) ca.c(f, crosshairStyle.getTooltipCornerRadius()));
        a.a(view, this.sK);
    }

    public void setView(View view) {
        if (view == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.TooltipNullView));
        }
        removeView(this.view);
        this.view = view;
        addView(view);
        this.u.a(new ea());
    }

    bh a(a aVar) {
        return this.u.a(ea.A, (a) aVar);
    }

    void fa() {
        setVisibility(8);
    }

    void fb() {
        setVisibility(0);
    }

    void fc() {
        setVisibility(8);
    }
}
