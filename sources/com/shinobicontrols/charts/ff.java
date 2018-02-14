package com.shinobicontrols.charts;

import android.graphics.PointF;
import android.view.View;
import android.widget.TextView;
import com.shinobicontrols.charts.Series.Orientation;

abstract class ff {
    static ff sQ = new ff() {
        final void a(Tooltip tooltip, DataPoint<?, ?> dataPoint) {
            DefaultTooltipView defaultTooltipView = (DefaultTooltipView) tooltip.getView();
            this.sN = defaultTooltipView.getChildCount();
            Series trackedSeries = tooltip.getTrackedSeries();
            Axis xAxis = trackedSeries.getXAxis();
            Axis yAxis = trackedSeries.getYAxis();
            this.sL.setLength(0);
            this.sL.append(xAxis.a(xAxis.translatePoint(dataPoint.getX()))).append(", ").append(yAxis.a(yAxis.translatePoint(dataPoint.getY())));
            defaultTooltipView.gV.setText(this.sL.toString());
            defaultTooltipView.gV.setVisibility(0);
            defaultTooltipView.gW.setVisibility(8);
            defaultTooltipView.gX.setVisibility(8);
            defaultTooltipView.gY.setVisibility(8);
            defaultTooltipView.gZ.setVisibility(8);
            defaultTooltipView.ha.setVisibility(8);
            defaultTooltipView.hb.setVisibility(8);
            defaultTooltipView.hc.setVisibility(8);
            defaultTooltipView.hd.setVisibility(8);
        }
    };
    static ff sR = new ff() {
        final void a(Tooltip tooltip, DataPoint<?, ?> dataPoint) {
            DefaultTooltipView defaultTooltipView = (DefaultTooltipView) tooltip.getView();
            this.sN = defaultTooltipView.getChildCount();
            a(new TextView[]{defaultTooltipView.gV, defaultTooltipView.gW, defaultTooltipView.ha, defaultTooltipView.gX, defaultTooltipView.hb}, (DataPoint) dataPoint, tooltip);
            defaultTooltipView.gY.setVisibility(8);
            defaultTooltipView.gZ.setVisibility(8);
            defaultTooltipView.hc.setVisibility(8);
            defaultTooltipView.hd.setVisibility(8);
            a(defaultTooltipView, tooltip);
        }
    };
    static ff sS = new ff() {
        final void a(Tooltip tooltip, DataPoint<?, ?> dataPoint) {
            DefaultTooltipView defaultTooltipView = (DefaultTooltipView) tooltip.getView();
            this.sN = defaultTooltipView.getChildCount();
            Axis m = ff.c(tooltip.getTrackedSeries());
            a(new TextView[]{defaultTooltipView.gV, defaultTooltipView.gX, defaultTooltipView.hb, defaultTooltipView.gY, defaultTooltipView.hc}, (DataPoint) dataPoint, tooltip);
            MultiValueData multiValueData = (MultiValueData) dataPoint;
            defaultTooltipView.gW.setText("open : ");
            defaultTooltipView.ha.setText(m.a(m.translatePoint(multiValueData.getOpen())));
            defaultTooltipView.gW.setVisibility(0);
            defaultTooltipView.ha.setVisibility(0);
            defaultTooltipView.gZ.setText("  close : ");
            defaultTooltipView.hd.setText(m.a(m.translatePoint(multiValueData.getClose())));
            defaultTooltipView.gZ.setVisibility(0);
            defaultTooltipView.hd.setVisibility(0);
            a(defaultTooltipView, tooltip);
        }
    };
    static ff sT = new ff() {
        final void a(Tooltip tooltip, DataPoint<?, ?> dataPoint) {
        }
    };
    final StringBuilder sL = new StringBuilder();
    by sM = new by();
    int sN;
    int sO;
    int sP;

    abstract void a(Tooltip tooltip, DataPoint<?, ?> dataPoint);

    ff() {
    }

    static ff c(Tooltip tooltip) {
        View view = tooltip.getView();
        CartesianSeries trackedSeries = tooltip.getTrackedSeries();
        if (trackedSeries == null || !(view instanceof DefaultTooltipView)) {
            return sT;
        }
        if ((trackedSeries instanceof CandlestickSeries) || (trackedSeries instanceof OHLCSeries)) {
            return sS;
        }
        if (trackedSeries instanceof BandSeries) {
            return sR;
        }
        return sQ;
    }

    private static Axis<?, ?> b(CartesianSeries<?> cartesianSeries) {
        return d(cartesianSeries) ? cartesianSeries.getYAxis() : cartesianSeries.getXAxis();
    }

    private static Axis<?, ?> c(CartesianSeries<?> cartesianSeries) {
        return d(cartesianSeries) ? cartesianSeries.getXAxis() : cartesianSeries.getYAxis();
    }

    private static boolean d(CartesianSeries<?> cartesianSeries) {
        return cartesianSeries.dR == Orientation.VERTICAL;
    }

    void a(TextView[] textViewArr, DataPoint<?, ?> dataPoint, Tooltip tooltip) {
        CartesianSeries trackedSeries = tooltip.getTrackedSeries();
        Axis c = c(trackedSeries);
        Axis b = b(trackedSeries);
        this.sL.setLength(0);
        this.sL.append("x : ").append(b.a(b.translatePoint(dataPoint.getX())));
        textViewArr[0].setText(this.sL.toString());
        textViewArr[0].setVisibility(0);
        MultiValueData multiValueData = (MultiValueData) dataPoint;
        textViewArr[1].setText("high : ");
        textViewArr[2].setText(c.a(c.translatePoint(multiValueData.getHigh())));
        textViewArr[1].setVisibility(0);
        textViewArr[2].setVisibility(0);
        textViewArr[3].setText("low : ");
        textViewArr[4].setText(c.a(c.translatePoint(multiValueData.getLow())));
        textViewArr[3].setVisibility(0);
        textViewArr[4].setVisibility(0);
    }

    void a(DefaultTooltipView defaultTooltipView, Tooltip tooltip) {
        ShinobiChart chart = tooltip.getTrackedSeries().getChart();
        b(defaultTooltipView, a(chart.getCrosshair().getStyle(), chart));
        a(defaultTooltipView, a(defaultTooltipView, chart.getCrosshair().getStyle(), chart));
    }

    int a(CrosshairStyle crosshairStyle, ShinobiChart shinobiChart) {
        PointF pointF = new PointF(0.0f, 0.0f);
        this.sM.a(pointF, "  close : ", crosshairStyle.getTooltipTextSize(), crosshairStyle.getTooltipTypeface(), (af) shinobiChart);
        this.sO = (int) pointF.x;
        return this.sO;
    }

    int a(DefaultTooltipView defaultTooltipView, CrosshairStyle crosshairStyle, ShinobiChart shinobiChart) {
        PointF pointF = new PointF(0.0f, 0.0f);
        int i = 5;
        float f = 0.0f;
        while (i < this.sN) {
            float f2;
            this.sM.a(pointF, (String) ((TextView) defaultTooltipView.getChildAt(i)).getText(), crosshairStyle.getTooltipTextSize(), crosshairStyle.getTooltipTypeface(), (af) shinobiChart);
            if (pointF.x > f) {
                f2 = pointF.x;
            } else {
                f2 = f;
            }
            i++;
            f = f2;
        }
        this.sP = (int) f;
        return this.sP;
    }

    void a(DefaultTooltipView defaultTooltipView, int i) {
        for (int i2 = 5; i2 < this.sN; i2++) {
            ((TextView) defaultTooltipView.getChildAt(i2)).setWidth(i);
        }
    }

    void b(DefaultTooltipView defaultTooltipView, int i) {
        for (int i2 = 1; i2 < 5; i2++) {
            ((TextView) defaultTooltipView.getChildAt(i2)).setWidth(i);
        }
    }
}
