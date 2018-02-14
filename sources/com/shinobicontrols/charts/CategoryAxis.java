package com.shinobicontrols.charts;

import android.graphics.PointF;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryAxis extends NumberAxis {
    private final List<String> ec = new ArrayList();

    public CategoryAxis(NumberRange range) {
        setDefaultRange(range);
    }

    boolean isDataValid(Object point) {
        return point.toString() != null;
    }

    double convertPoint(Object userData) {
        validateUserData(userData);
        String obj = userData.toString();
        b(obj);
        return (double) this.ec.indexOf(obj);
    }

    private void b(String str) {
        if (!this.ec.contains(str)) {
            this.ec.add(str);
        }
    }

    public List<String> getCategories() {
        return Collections.unmodifiableList(this.ec);
    }

    public boolean requestCurrentDisplayedRange(int minimum, int maximum) {
        return requestCurrentDisplayedRange(Double.valueOf((double) minimum), Double.valueOf((double) maximum));
    }

    public boolean requestCurrentDisplayedRange(int minimum, int maximum, boolean animation, boolean bounceAtLimits) {
        return requestCurrentDisplayedRange(Double.valueOf((double) minimum), Double.valueOf((double) maximum), animation, bounceAtLimits);
    }

    String x() {
        if (z()) {
            return this.aI;
        }
        if (this.ec.size() == 0) {
            this.aH = null;
        } else {
            float f = 0.0f;
            int size = this.ec.size();
            int i = 0;
            int i2 = 0;
            while (i < size) {
                int indexOf;
                float f2;
                PointF pointF = new PointF();
                a(pointF, (String) this.ec.get(i));
                float f3 = pointF.x;
                if (f3 > f) {
                    indexOf = this.ec.indexOf(this.ec.get(i));
                    f2 = f3;
                } else {
                    indexOf = i2;
                    f2 = f;
                }
                i++;
                f = f2;
                i2 = indexOf;
            }
            this.aH = (String) this.ec.get(i2);
        }
        return this.aH;
    }

    public String getFormattedString(Double value) {
        if (value == null) {
            return null;
        }
        int round = (int) Math.round(value.doubleValue());
        if (l(round)) {
            return (String) this.ec.get(round);
        }
        return null;
    }

    private boolean l(int i) {
        return i >= 0 && i < this.ec.size();
    }

    void d(int i) {
        this.av = Double.valueOf(1.0d);
        H();
    }

    double b(int i) {
        double ceil = Math.ceil(this.ai.nv);
        int i2 = (int) ceil;
        int intValue = this.aj.getMinimum().intValue();
        if (i2 < intValue) {
            ceil = (double) intValue;
        }
        if (a(ceil, (double) i, this.ai.dF())) {
            return ceil;
        }
        return a(ceil, true);
    }

    double c(int i) {
        return Double.NaN;
    }

    boolean b(double d) {
        return Math.IEEEremainder(d, 2.0d) == 0.0d;
    }

    double a(double d, boolean z) {
        return 1.0d + d;
    }

    double b(double d, boolean z) {
        return d - 1.0d;
    }

    public void setMajorTickMarkValues(List<Double> list) {
        ev.g(this.J != null ? this.J.getContext().getString(R.string.CategoryAxisIgnoresCustomTickValues) : "Category axes ignore custom tick mark values");
    }

    void T() {
        this.aA.eW();
    }

    fd U() {
        return new ae();
    }

    boolean y() {
        return true;
    }

    public void addSkipRange(Range<Double> range) {
        aQ();
    }

    public void addSkipRanges(List<? extends Range<Double>> list) {
        aQ();
    }

    private void aQ() {
        ev.g(this.J != null ? this.J.getContext().getString(R.string.CannotAddSkipToCategoryAxis) : "Cannot add a skip range to a category axis.");
    }
}
