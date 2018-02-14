package com.shinobicontrols.charts;

import android.graphics.PointF;
import java.text.DecimalFormat;

public class NumberAxis extends Axis<Double, Double> {
    private final PointF gC = new PointF(1.2f, 1.2f);
    private int lB;
    private int lC = -1;
    private final dd lD = new dd();
    private DecimalFormat lE;

    public NumberAxis(NumberRange defaultRange) {
        setDefaultRange(defaultRange);
    }

    boolean isDataValid(Object point) {
        return point instanceof Number;
    }

    double convertPoint(Object userData) {
        return translatePoint(userData);
    }

    double translatePoint(Object userData) {
        validateUserData(userData);
        return transformUserValueToInternal(Double.valueOf(convertUserValueTypeToInternalDataType(userData)));
    }

    double transformExternalValueToInternal(Double externalValue) {
        return externalValue.doubleValue();
    }

    double convertUserValueTypeToInternalDataType(Object rawUserValue) {
        return Double.valueOf(((Number) rawUserValue).doubleValue()).doubleValue();
    }

    Double transformInternalValueToExternal(double internalValue) {
        return Double.valueOf(internalValue);
    }

    protected Double transformUserValueToChartValue(Double userValue) {
        return userValue;
    }

    protected Double transformChartValueToUserValue(Double chartValue) {
        return chartValue;
    }

    Range<Double> createRange(Double min, Double max) {
        return new NumberRange(min, max);
    }

    public DecimalFormat getLabelFormat() {
        return this.lE;
    }

    public void setLabelFormat(DecimalFormat labelFormat) {
        this.lE = labelFormat;
    }

    String x() {
        if (B()) {
            dj();
        }
        if (z()) {
            return this.aI;
        }
        if (ca()) {
            String format = this.lE.format(this.ai.nv);
            String format2 = this.lE.format(this.ai.nw);
            if (format2.length() <= format.length()) {
                format2 = format;
            }
            this.aH = format2;
        } else {
            this.lD.A(this.lB);
            if (this.lD.B(this.lC)) {
                this.aA.eW();
            }
            this.aH = this.lD.dk();
        }
        return this.aH;
    }

    private void dj() {
        int j = j(((Double) this.av).doubleValue());
        int j2 = j(E());
        this.lB = i(Math.max(Math.abs(this.ai.nv), Math.abs(this.ai.nw)));
        this.lC = Math.max(j2, j);
    }

    private static int i(double d) {
        double log10 = Math.log10(d);
        if (log10 >= 1.0d) {
            return ((int) Math.floor(log10)) + 1;
        }
        return 1;
    }

    private static int j(double d) {
        if (d == 0.0d) {
            return 0;
        }
        double log10 = Math.log10(d);
        if (log10 < 0.0d) {
            return (int) Math.ceil(-log10);
        }
        return 0;
    }

    public String getFormattedString(Double value) {
        return ca() ? this.lE.format(value) : this.lD.format(value);
    }

    private boolean ca() {
        return this.lE != null;
    }

    void d(int i) {
        setCurrentMajorTickFrequency(Double.valueOf(this.ai.dF() / 20.0d));
        double pow = Math.pow(10.0d, Math.floor(Math.log10(((Double) this.av).doubleValue())));
        do {
            if (pow > ((Double) this.av).doubleValue()) {
                this.av = Double.valueOf(pow);
                this.aw = Double.valueOf(pow / 2.0d);
            } else if (pow * 5.0d > ((Double) this.av).doubleValue()) {
                this.av = Double.valueOf(pow * 5.0d);
                this.aw = Double.valueOf(pow);
            } else if (pow * 10.0d > ((Double) this.av).doubleValue()) {
                this.av = Double.valueOf(pow * 10.0d);
                this.aw = Double.valueOf(pow * 5.0d);
            } else {
                pow *= 10.0d;
            }
            H();
        } while (!a((int) Math.floor(this.ai.dF() / ((Double) this.av).doubleValue()), i, this.gC));
    }

    double b(int i) {
        double d = this.ai.nv;
        double E = E();
        E += ((Double) this.av).doubleValue() * Math.floor((d - E) / ((Double) this.av).doubleValue());
        while (E < d) {
            E = a(E, true);
        }
        while (!a(E, (double) i, this.ai.dF())) {
            E += ((Double) this.av).doubleValue();
        }
        return E;
    }

    double c(int i) {
        if (!D()) {
            return Double.NaN;
        }
        double b = b(i);
        double b2 = b(b, false);
        while (b2 >= this.ai.nv) {
            b = b2;
            b2 = b(b2, false);
        }
        return b;
    }

    boolean b(double d) {
        double IEEEremainder = Math.IEEEremainder((d - E()) / ((Double) this.av).doubleValue(), 2.0d);
        return (IEEEremainder < -0.5d && IEEEremainder > -1.5d) || (IEEEremainder > 0.5d && IEEEremainder < 1.5d);
    }

    double a(double d, boolean z) {
        double doubleValue = (z ? (Double) this.av : (Double) getCurrentMinorTickFrequency()).doubleValue() + d;
        if (doubleValue >= 0.0d || (0.9998999834060669d * Math.pow(10.0d, (double) (-(this.lC + 1)))) + doubleValue <= 0.0d) {
            return doubleValue;
        }
        return 0.0d;
    }

    double b(double d, boolean z) {
        double doubleValue = d - (z ? (Double) this.av : (Double) getCurrentMinorTickFrequency()).doubleValue();
        if (doubleValue >= 0.0d || (0.9998999834060669d * Math.pow(10.0d, (double) (-(this.lC + 1)))) + doubleValue <= 0.0d) {
            return doubleValue;
        }
        return 0.0d;
    }

    double transformExternalFrequencyToInternal(Double externalValue) {
        return externalValue.doubleValue();
    }

    double N() {
        return 1.0d;
    }

    void setMajorTickFrequencyInternal(Double frequency) {
        if (frequency == null) {
            this.at = null;
        } else if (frequency.doubleValue() > 0.0d) {
            this.at = frequency;
        } else {
            ev.g(this.J != null ? this.J.getContext().getString(R.string.NumberAxisInvalidFrequency) : "The frequency is invalid and will be ignored");
            this.at = null;
        }
    }

    void setMinorTickFrequencyInternal(Double frequency) {
        if (frequency == null) {
            this.au = null;
        } else if (frequency.doubleValue() > 0.0d) {
            this.au = frequency;
        } else {
            ev.g(this.J != null ? this.J.getContext().getString(R.string.NumberAxisInvalidFrequency) : "The frequency is invalid and will be ignored");
            this.au = null;
        }
    }

    void F() {
        if (this.at != null) {
            this.av = Double.valueOf(((Double) this.at).doubleValue());
        }
    }

    void G() {
        if (A() && this.au != null) {
            this.aw = Double.valueOf(((Double) this.au).doubleValue());
        }
    }

    Double getDefaultBaseline() {
        return Double.valueOf(0.0d);
    }

    Double applyMappingForSkipRangesToUserValue(Double userValue) {
        return Double.valueOf(this.aK.g(userValue.doubleValue()));
    }

    Double removeMappingForSkipRangesFromChartValue(Double chartValue) {
        return Double.valueOf(this.aL.g(chartValue.doubleValue()));
    }

    void s() {
    }

    double E() {
        return 0.0d;
    }

    public final double getZoomLevel() {
        return super.getZoomLevel();
    }
}
