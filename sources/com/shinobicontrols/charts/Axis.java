package com.shinobicontrols.charts;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup.MarginLayoutParams;
import com.shinobicontrols.charts.ShinobiChart.OnGestureListener;
import com.shinobicontrols.charts.TickMark.ClippingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public abstract class Axis<T extends Comparable<T>, U> {
    af J;
    private final Map<Series<?>, bh> L = new HashMap();
    private final a O = new a(this, this);
    Orientation P;
    Position Q = Position.NORMAL;
    private double R;
    double S;
    double T = 0.0d;
    private boolean U = false;
    private boolean V;
    private U W;
    private U X;
    private double Y;
    private double Z;
    final fe aA = new fe(this);
    private final c aB = new c();
    private final Rect aC = new Rect();
    private final by aD = new by();
    private final PointF aE = new PointF();
    final ec aF = new ec(this);
    private DoubleTapBehavior aG = DoubleTapBehavior.ZOOM_IN;
    String aH = null;
    String aI = null;
    private final List<Range<T>> aJ = new ArrayList();
    final dq aK = dq.du();
    final dq aL = dq.du();
    private final ey<T, U> aM = new ey(this);
    private final bw<T> aN = new g();
    private final bw<T> aO = new ef();
    AxisStyle aa;
    private bh ab;
    private final b ac = new b();
    private ClippingMode ad = ClippingMode.TICKS_AND_LABELS_PERSIST;
    private ClippingMode ae = ClippingMode.TICKS_AND_LABELS_PERSIST;
    private String af;
    private Float ag = null;
    int ah;
    NumberRange ai = new NumberRange();
    NumberRange aj = new NumberRange();
    NumberRange ak = new NumberRange();
    NumberRange al = null;
    Range<T> am;
    private boolean an = false;
    j ao;
    private Title ap;
    private final bz aq = new bz();
    private final Paint ar = new Paint();
    int as;
    U at = null;
    U au = null;
    U av = null;
    U aw = null;
    private double ax;
    private final Point ay = new Point();
    double[] az;
    float density = 1.0f;
    private final bg u = new bg();

    public enum DoubleTapBehavior {
        RESET_TO_DEFAULT_RANGE,
        ZOOM_IN
    }

    public enum MotionState {
        STOPPED,
        ANIMATING,
        GESTURE,
        MOMENTUM,
        BOUNCING
    }

    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    public enum Position {
        NORMAL,
        REVERSE
    }

    private class a implements a {
        final /* synthetic */ Axis aQ;
        private final Axis<?, ?> aS;

        public a(Axis axis, Axis<?, ?> axis2) {
            this.aQ = axis;
            this.aS = axis2;
        }

        public final void q() {
            this.aS.q();
        }
    }

    private class b implements a {
        final /* synthetic */ Axis aQ;

        private b(Axis axis) {
            this.aQ = axis;
        }

        public void ac() {
            this.aQ.q();
            this.aQ.J();
        }
    }

    static class c {
        Rect aX;
        boolean aY;
        boolean aZ;
        Point ay;
        float bA;
        ClippingMode bB;
        ClippingMode bC;
        float bD;
        float bE;
        boolean bF;
        Rect bG = new Rect();
        Rect bH = new Rect();
        Point bI = new Point();
        boolean ba;
        boolean bb;
        boolean bc;
        float bd;
        float be;
        float bf;
        float bg;
        float bh;
        float bi;
        float bj;
        int bk;
        int bl;
        int bm;
        int bn;
        Typeface bo;
        float bp;
        com.shinobicontrols.charts.TickMark.Orientation bq;
        boolean br;
        DashPathEffect bs;
        int bt;
        int bu;
        float bv;
        int bw;
        double bx;
        float by;
        float bz;

        c() {
        }
    }

    abstract double E();

    abstract void F();

    abstract void G();

    abstract double N();

    abstract double a(double d, boolean z);

    abstract T applyMappingForSkipRangesToUserValue(T t);

    abstract double b(double d, boolean z);

    abstract double b(int i);

    abstract boolean b(double d);

    abstract double c(int i);

    abstract double convertPoint(Object obj);

    abstract double convertUserValueTypeToInternalDataType(Object obj);

    abstract Range<T> createRange(T t, T t2);

    abstract void d(int i);

    abstract T getDefaultBaseline();

    abstract String getFormattedString(T t);

    abstract boolean isDataValid(Object obj);

    abstract T removeMappingForSkipRangesFromChartValue(T t);

    abstract void s();

    abstract void setMajorTickFrequencyInternal(U u);

    abstract void setMinorTickFrequencyInternal(U u);

    abstract T transformChartValueToUserValue(T t);

    abstract double transformExternalFrequencyToInternal(U u);

    abstract double transformExternalValueToInternal(T t);

    abstract T transformInternalValueToExternal(double d);

    abstract T transformUserValueToChartValue(T t);

    abstract double translatePoint(Object obj);

    abstract String x();

    static boolean a(String str) {
        return TextUtils.isEmpty(str) || TextUtils.getTrimmedLength(str) == 0;
    }

    Axis() {
        setStyle(new AxisStyle());
        this.ao = j.e(this);
    }

    void b(Series<?> series) {
        this.L.put(series, series.a(this.O));
    }

    void c(Series<?> series) {
        bh bhVar = (bh) this.L.get(series);
        if (bhVar != null) {
            bhVar.cP();
            this.L.remove(series);
        }
    }

    public void specifyBarColumnSpacing(U spacing) {
        if (spacing != null) {
            this.T = transformExternalFrequencyToInternal(spacing);
            this.U = true;
        } else {
            this.U = false;
            P();
        }
        if (this.J != null) {
            Q();
            J();
        }
    }

    public final Orientation getOrientation() {
        return this.P;
    }

    final boolean h() {
        return this.P == Orientation.HORIZONTAL;
    }

    final void a(Orientation orientation) {
        this.P = orientation;
        this.ao = j.e(this);
    }

    public final Position getPosition() {
        return this.Q;
    }

    public final void setPosition(Position axisPosition) {
        this.Q = axisPosition;
        this.ao = j.e(this);
    }

    float i() {
        for (Axis axis : this.J.eq.bK) {
            if (axis.Q == Position.REVERSE) {
                return ((Float) axis.aa.bQ.sU).floatValue();
            }
        }
        return 0.0f;
    }

    final double j() {
        return this.T;
    }

    private boolean k() {
        return false;
    }

    double transformUserValueToInternal(T userValue) {
        return transformExternalValueToInternal(transformUserValueToChartValue(applyMappingForSkipRangesToUserValue(userValue)));
    }

    T transformInternalValueToUser(double internalDataValue) {
        return transformChartValueToUserValue(removeMappingForSkipRangesFromChartValue(transformInternalValueToExternal(internalDataValue)));
    }

    void validateUserData(Object userData) {
        if (userData == null) {
            throw new IllegalArgumentException(this.J != null ? this.J.getContext().getString(R.string.AxisDataPointsNotNull) : "You must supply all DataPoint parameter arguments, non-null");
        }
    }

    String a(double d) {
        return getFormattedString(transformInternalValueToUser(d));
    }

    public final Range<T> getCurrentDisplayedRange() {
        return b(this.ai);
    }

    void a(NumberRange numberRange) {
        Object obj = (this.ai.nv == numberRange.nv && this.ai.nw == numberRange.nw) ? null : 1;
        synchronized (ah.lock) {
            this.ai = numberRange;
        }
        if (obj != null && this.J != null) {
            this.J.onAxisRangeChange(this);
        }
    }

    public final Range<T> getDataRange() {
        return b(this.ak);
    }

    public final Range<T> getDefaultRange() {
        if (this.al != null) {
            return b(this.al);
        }
        return null;
    }

    double l() {
        double d = this.aj.nw + this.Y;
        Double m = m();
        if (m != null && m.doubleValue() > d) {
            return m.doubleValue();
        }
        if (p()) {
            return d + (N() / 2.0d);
        }
        return d;
    }

    private Double m() {
        Double d = null;
        if (this.J != null) {
            for (Series series : this.J.getSeriesForAxis(this)) {
                Double valueOf;
                CartesianSeries cartesianSeries = (CartesianSeries) series;
                if (!(a(cartesianSeries.dR) || cartesianSeries.dK == null)) {
                    double h = cartesianSeries.oy.h(cartesianSeries);
                    if (d == null || h > d.doubleValue()) {
                        valueOf = Double.valueOf(h);
                        d = valueOf;
                    }
                }
                valueOf = d;
                d = valueOf;
            }
        }
        return d;
    }

    double n() {
        double d = this.aj.nv - this.Z;
        Double o = o();
        if (o != null && o.doubleValue() < d) {
            return o.doubleValue();
        }
        if (p()) {
            return d - (N() / 2.0d);
        }
        return d;
    }

    private Double o() {
        Double d = null;
        if (this.J != null) {
            for (Series series : this.J.getSeriesForAxis(this)) {
                Double valueOf;
                CartesianSeries cartesianSeries = (CartesianSeries) series;
                if (!(a(cartesianSeries.dR) || cartesianSeries.dK == null)) {
                    double h = cartesianSeries.oy.h(cartesianSeries);
                    if (d == null || h < d.doubleValue()) {
                        valueOf = Double.valueOf(h);
                        d = valueOf;
                    }
                }
                valueOf = d;
                d = valueOf;
            }
        }
        return d;
    }

    private boolean p() {
        return this.aj.isEmpty() && this.Y == 0.0d && this.Z == 0.0d;
    }

    public final void setDefaultRange(Range<T> defaultRange) {
        a((Range) defaultRange);
        this.am = defaultRange;
        b((Range) defaultRange);
        s();
    }

    private void a(Range<T> range) {
        if (range != null) {
            if (!Range.i(range)) {
                throw new IllegalArgumentException("Cannot set an undefined range as the default range: infinite minimum or maximum values or negative span not allowed.");
            } else if (range.isEmpty()) {
                throw new IllegalArgumentException(this.J != null ? this.J.getContext().getString(R.string.AxisDefaultRangeIsEmpty) : "Cannot set a default range with equal minimum and maximum values.");
            }
        }
    }

    final void b(Range<T> range) {
        this.al = range == null ? null : c((Range) range);
        if (this.al == null || !this.al.isEmpty()) {
            if (range == null || !Range.h(this.ai)) {
                this.aF.dH();
            } else {
                a((NumberRange) this.al.bZ());
            }
            J();
            return;
        }
        throw new IllegalStateException(this.J != null ? this.J.getContext().getString(R.string.AxisDefaultRangeInternalIsEmpty) : "Cannot set default range due to transformations applied to this axis: have you set skip ranges that completely cover the data or default ranges?");
    }

    public final boolean isCurrentDisplayedRangePreservedOnUpdate() {
        return this.an;
    }

    public final void setCurrentDisplayedRangePreservedOnUpdate(boolean preserved) {
        this.an = preserved;
    }

    public boolean requestCurrentDisplayedRange(T minimum, T maximum) {
        return this.aF.g(transformUserValueToInternal(minimum), transformUserValueToInternal(maximum));
    }

    public boolean requestCurrentDisplayedRange(T minimum, T maximum, boolean animation, boolean bounceAtLimits) {
        return this.aF.b(transformUserValueToInternal(minimum), transformUserValueToInternal(maximum), animation, bounceAtLimits);
    }

    void a(double d, double d2) {
        Object obj = (this.ai.nv == d && this.ai.nw == d2) ? null : 1;
        synchronized (ah.lock) {
            this.ai.c(d, d2);
        }
        if (obj != null && this.J != null) {
            this.J.em.bz();
            this.J.onAxisRangeChange(this);
        }
    }

    public DoubleTapBehavior getDoubleTapBehavior() {
        return this.aG;
    }

    public void setDoubleTapBehavior(DoubleTapBehavior behavior) {
        this.aG = behavior;
    }

    public boolean isDoubleTapEnabled() {
        return this.aF.nM;
    }

    public void enableDoubleTap(boolean doubleTapEnabled) {
        this.aF.nM = doubleTapEnabled;
    }

    public final Range<T> getVisibleRange() {
        return b(this.aj);
    }

    final void q() {
        if (this.J != null) {
            P();
            this.aj = t();
            this.ak = u();
            v();
            Q();
            if (!r()) {
                s();
            }
        }
    }

    private boolean r() {
        if (this.J != null && Range.h(this.aj)) {
            Set<CartesianSeries> x = this.J.eo.x(this);
            if (x != null) {
                for (CartesianSeries eh : x) {
                    if (eh.eh()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private NumberRange t() {
        NumberRange numberRange = new NumberRange();
        for (en enVar : this.J.aR()) {
            if (enVar.q(this)) {
                numberRange.j(enVar.r(this));
            }
        }
        return numberRange;
    }

    private NumberRange u() {
        NumberRange numberRange = new NumberRange();
        for (en enVar : this.J.aR()) {
            if (enVar.q(this)) {
                numberRange.j(enVar.s(this));
            }
        }
        return numberRange;
    }

    private void v() {
        if (!this.an || Range.h(this.ai)) {
            NumberRange numberRange;
            if (Range.i(this.al)) {
                numberRange = (NumberRange) this.al.bZ();
            } else if (Range.i(this.aj)) {
                numberRange = new NumberRange(Double.valueOf(n()), Double.valueOf(l()));
            } else {
                numberRange = new NumberRange();
            }
            a(numberRange);
        }
    }

    final void w() {
        this.aA.eT();
        this.J = null;
    }

    private NumberRange c(Range<T> range) {
        if (range == null) {
            return null;
        }
        return new NumberRange(Double.valueOf(transformUserValueToInternal(range.getMinimum())), Double.valueOf(transformUserValueToInternal(range.getMaximum())));
    }

    private Range<T> b(NumberRange numberRange) {
        if (numberRange != null) {
            return createRange(transformInternalValueToUser(numberRange.nv), transformInternalValueToUser(numberRange.nw));
        }
        return null;
    }

    public final U getRangePaddingHigh() {
        return this.W;
    }

    public final void setRangePaddingHigh(U rangePaddingHigh) {
        this.W = rangePaddingHigh;
        if (rangePaddingHigh == null) {
            this.Y = 0.0d;
        } else {
            this.Y = transformExternalFrequencyToInternal(rangePaddingHigh);
        }
        q();
        if (this.J != null) {
            J();
            this.J.em.bz();
        }
    }

    public final U getRangePaddingLow() {
        return this.X;
    }

    public final void setRangePaddingLow(U rangePaddingLow) {
        this.X = rangePaddingLow;
        if (rangePaddingLow == null) {
            this.Z = 0.0d;
        } else {
            this.Z = transformExternalFrequencyToInternal(rangePaddingLow);
        }
        q();
        if (this.J != null) {
            J();
            this.J.em.bz();
        }
    }

    boolean y() {
        return !A();
    }

    public String getExpectedLongestLabel() {
        return this.aI;
    }

    public void setExpectedLongestLabel(String longestLabel) {
        this.aI = longestLabel;
    }

    boolean z() {
        return this.aI != null;
    }

    public void setMajorTickMarkValues(List<T> values) {
        if (values == null) {
            this.az = null;
        } else if (values.contains(null)) {
            throw new IllegalArgumentException(this.J != null ? this.J.getContext().getString(R.string.AxisNullCustomTickMarkValues) : "Custom tick mark values cannot contain null.");
        } else {
            Set<Comparable> treeSet = new TreeSet(values);
            this.az = new double[treeSet.size()];
            int i = 0;
            for (Comparable transformUserValueToInternal : treeSet) {
                this.az[i] = transformUserValueToInternal(transformUserValueToInternal);
                i++;
            }
        }
    }

    public final U getMajorTickFrequency() {
        return this.at;
    }

    public final void setMajorTickFrequency(U majorTickFrequency) {
        setMajorTickFrequencyInternal(majorTickFrequency);
    }

    boolean A() {
        return this.at != null;
    }

    public final U getMinorTickFrequency() {
        return this.au;
    }

    public final void setMinorTickFrequency(U minorTickFrequency) {
        setMinorTickFrequencyInternal(minorTickFrequency);
    }

    public final U getCurrentMajorTickFrequency() {
        return this.av;
    }

    boolean B() {
        return this.av != null;
    }

    double C() {
        if (this.av != null) {
            return transformExternalFrequencyToInternal(this.av);
        }
        throw new IllegalStateException(this.J != null ? this.J.getContext().getString(R.string.AxisNullMajorTickFrequency) : "Null currentMajorTickFrequency");
    }

    void setCurrentMajorTickFrequency(U frequency) {
        this.av = frequency;
    }

    public final U getCurrentMinorTickFrequency() {
        return this.aw;
    }

    boolean D() {
        return this.aw != null;
    }

    void setCurrentMinorTickFrequency(U frequency) {
        this.aw = frequency;
    }

    void e(int i) {
        if (Range.h(this.ai)) {
            ev.g(this.J != null ? this.J.getContext().getString(R.string.AxisUndefinedRange) : "The axis has an undefined data range and cannot be displayed");
        } else {
            a(y(), i);
        }
    }

    private void a(boolean z, int i) {
        if (z) {
            d(i);
            this.ax = C();
            return;
        }
        F();
        H();
    }

    void H() {
        String x = x();
        if (x != null) {
            a(this.aE, x);
        }
        a(this.aE);
    }

    void a(PointF pointF, String str) {
        this.aD.b(pointF, str, ((Float) this.aa.bT.mB.sU).floatValue(), (Typeface) this.aa.bT.mA.sU, this.J);
    }

    void a(PointF pointF) {
        double d;
        double d2;
        switch ((com.shinobicontrols.charts.TickMark.Orientation) this.aa.bT.sx.sU) {
            case HORIZONTAL:
                d = (double) pointF.x;
                d2 = (double) pointF.y;
                break;
            case DIAGONAL:
                d = (double) ((pointF.x + pointF.y) * 0.70710677f);
                d2 = (double) ((pointF.x + pointF.y) * 0.70710677f);
                break;
            case VERTICAL:
                d = (double) pointF.y;
                d2 = (double) pointF.x;
                break;
            default:
                String string;
                if (this.J != null) {
                    string = this.J.getContext().getString(R.string.AxisUnrecognisedOrientation);
                } else {
                    string = "tickLabel orientation not recognised";
                }
                throw new IllegalStateException(string);
        }
        this.ay.x = (int) (d + 0.5d);
        this.ay.y = (int) (d2 + 0.5d);
    }

    void I() {
        if (!this.V && !this.J.bn()) {
            ev.g(this.J != null ? this.J.getContext().getString(R.string.AxisInsufficientWidth) : "Axis width does not provide enough space to fit the tickmarks and ticklabels.");
        }
    }

    boolean a(double d, double d2, double d3) {
        if (h()) {
            if (((double) j.a(this.J.er, Position.NORMAL)) + b(d, d2, d3) > 0.0d) {
                return true;
            }
            return false;
        }
        if (b(d, d2, d3) > ((double) (this.J.getHeight() - j.a(this.J.eq, Position.REVERSE)))) {
            return false;
        }
        return true;
    }

    int a(int i, int i2) {
        return 0;
    }

    double b(double d, double d2, double d3) {
        if (this.P == Orientation.HORIZONTAL) {
            return ((d - this.ai.nv) * d2) / d3;
        }
        return ((this.ai.nw - d) * d2) / d3;
    }

    public AxisStyle getStyle() {
        return this.aa;
    }

    public final void setStyle(AxisStyle style) {
        if (this.aa != null) {
            this.ab.cP();
        }
        this.aa = style;
        if (this.aa != null) {
            this.ab = this.aa.a(this.ac);
            if (this.J != null) {
                P();
                Q();
            }
        }
    }

    private void J() {
        if (this.J != null) {
            for (Series series : this.J.getSeriesForAxis(this)) {
                series.ot.av();
            }
        }
    }

    public ClippingMode getTickMarkClippingModeHigh() {
        return this.ad;
    }

    public ClippingMode getTickMarkClippingModeLow() {
        return this.ae;
    }

    public void setTickMarkClippingModeHigh(ClippingMode tickLabelClippingModeHigh) {
        this.ad = tickLabelClippingModeHigh;
    }

    public void setTickMarkClippingModeLow(ClippingMode tickLabelClippingModeLow) {
        this.ae = tickLabelClippingModeLow;
    }

    public final String getTitle() {
        return this.af;
    }

    public final void setTitle(String title) {
        this.af = title;
        if (this.ap != null) {
            this.ap.setText(title);
            a(this.ap);
        }
    }

    private void a(Title title) {
        int i = 8;
        if (!(title == null || a(title.getText().toString()))) {
            i = 0;
        }
        title.setVisibility(i);
    }

    final Title K() {
        return L();
    }

    Title L() {
        if (this.ap == null && this.J != null) {
            this.ap = new Title(this.J.getContext());
            this.ap.setLayoutParams(new MarginLayoutParams(-2, -2));
            M();
            this.ap.setText(this.af);
            a(this.ap);
        }
        return this.ap;
    }

    void M() {
        if (this.ap != null && this.aa.bU != null) {
            TitleStyle titleStyle = this.aa.bU;
            this.ap.setOrientation(titleStyle.getOrientation());
            this.ap.a(titleStyle);
        }
    }

    public final Float getWidth() {
        return this.ag;
    }

    public final void setWidth(Float width) {
        this.ag = width;
    }

    public boolean isPanningOutOfDefaultRangeAllowed() {
        return this.aF.nB;
    }

    public boolean isPanningOutOfMaxRangeAllowed() {
        return this.aF.nC;
    }

    public boolean isBouncingAtLimitsEnabled() {
        return this.aF.nD;
    }

    public boolean isAnimationEnabled() {
        return this.aF.nE;
    }

    public boolean isGesturePanningEnabled() {
        return this.aF.nG;
    }

    public boolean isGestureZoomingEnabled() {
        return this.aF.nH;
    }

    public boolean isMomentumPanningEnabled() {
        return this.aF.nI;
    }

    public boolean isMomentumZoomingEnabled() {
        return this.aF.nJ;
    }

    boolean a(double d, boolean z, boolean z2) {
        if (d == 0.0d) {
            return false;
        }
        return this.aF.b(d, z, z2);
    }

    public void allowPanningOutOfDefaultRange(boolean allowPanningOutOfDefaultRange) {
        this.aF.nB = allowPanningOutOfDefaultRange;
        if (!allowPanningOutOfDefaultRange) {
            this.aF.dH();
        }
    }

    public void allowPanningOutOfMaxRange(boolean allowPanningOutOfMaxRange) {
        this.aF.nC = allowPanningOutOfMaxRange;
        if (!allowPanningOutOfMaxRange) {
            this.aF.dH();
        }
    }

    public void enableBouncingAtLimits(boolean bounceAtLimits) {
        this.aF.nD = bounceAtLimits;
    }

    public void enableAnimation(boolean animationEnabled) {
        this.aF.nE = animationEnabled;
    }

    public void enableGesturePanning(boolean enableGesturePanning) {
        this.aF.nG = enableGesturePanning;
    }

    public void enableGestureZooming(boolean enableGestureZooming) {
        this.aF.nH = enableGestureZooming;
    }

    public void enableMomentumPanning(boolean enableMomentumPanning) {
        this.aF.nI = enableMomentumPanning;
    }

    public void enableMomentumZooming(boolean enableMomentumZooming) {
        this.aF.nJ = enableMomentumZooming;
    }

    boolean a(double d, double d2, boolean z, boolean z2) {
        if (d > 0.0d && !Double.isInfinite(d) && !Double.isNaN(d)) {
            return this.aF.c(d, d2, z, z2);
        }
        ev.f("Zoom must be greater than 0 and a real number");
        return false;
    }

    public double getZoomLevel() {
        return this.aF.getZoomLevel();
    }

    boolean c(double d) {
        return d > C();
    }

    private void f(int i) {
        this.aB.bw = i;
        this.aB.bx = this.ai.dF();
        this.aB.aY = ((Boolean) this.aa.bT.st.sU).booleanValue();
        this.aB.aZ = ((Boolean) this.aa.bT.su.sU).booleanValue();
        this.aB.ba = ((Boolean) this.aa.bT.sv.sU).booleanValue();
        this.aB.bb = ((Boolean) this.aa.bS.iG.sU).booleanValue();
        this.aB.bc = ((Boolean) this.aa.bR.iF.sU).booleanValue();
        this.aB.bd = ((Float) this.aa.bT.ss.sU).floatValue();
        this.aB.be = ((Float) this.aa.bT.bQ.sU).floatValue();
        this.aB.bh = this.aB.be / 2.0f;
        this.aB.bj = ((Float) this.aa.bT.sw.sU).floatValue();
        this.aB.bk = ((Integer) this.aa.bT.bP.sU).intValue();
        this.aB.bl = ((Integer) this.aa.bS.bP.sU).intValue();
        this.aB.bq = (com.shinobicontrols.charts.TickMark.Orientation) this.aa.bT.sx.sU;
        this.aB.bm = ((Integer) this.aa.bT.sq.sU).intValue();
        this.aB.bn = ((Integer) this.aa.bT.sr.sU).intValue();
        this.aB.bo = (Typeface) this.aa.bT.mA.sU;
        this.aB.bp = ((Float) this.aa.bT.mB.sU).floatValue() * this.J.getResources().getDisplayMetrics().scaledDensity;
        this.aB.br = ((Boolean) this.aa.bS.iH.sU).booleanValue();
        this.aB.bs = V();
        this.aB.bt = ((Integer) this.aa.bR.iD.sU).intValue();
        this.aB.bu = ((Integer) this.aa.bR.iE.sU).intValue();
        this.aB.ay = this.ay;
        this.aB.by = (float) this.aB.bw;
        this.aB.bz = 0.0f;
        this.aB.bA = h() ? (float) this.aB.ay.x : (float) this.aB.ay.y;
        this.aB.bB = h() ? this.ad : this.ae;
        this.aB.bC = h() ? this.ae : this.ad;
        this.aB.bD = 0.0f;
        if (this.aB.aZ || this.aB.ba) {
            this.aB.bD = this.aB.bd + this.aB.bj;
        }
        this.aB.bE = (float) this.ah;
        this.aB.bF = k();
        this.aB.bi = getStyle().getLineWidth();
        this.aB.bf = getStyle().getGridlineStyle().getLineWidth();
    }

    void draw(Canvas canvas, Rect plotAreaRect) {
        this.ao.a(this.aC, plotAreaRect, this.aa.getLineWidth(), this.ah, i());
        this.ar.setColor(((Integer) this.aa.bP.sU).intValue());
        canvas.drawRect(this.aC, this.ar);
        f(this.P == Orientation.HORIZONTAL ? plotAreaRect.width() : plotAreaRect.height());
        this.aB.aX = plotAreaRect;
        this.ao.a(this.aB);
        this.aA.b(this.aB);
        I();
        this.aA.b(canvas, this.aB);
    }

    void b(boolean z) {
        float max = Math.max(((Float) this.aa.bQ.sU).floatValue(), 0.0f);
        if (Range.h(this.ai)) {
            this.as = this.ao.a(0, max);
            return;
        }
        float max2;
        if (((Boolean) this.aa.bT.su.sU).booleanValue() || ((Boolean) this.aa.bT.sv.sU).booleanValue()) {
            max2 = Math.max(((Float) this.aa.bT.ss.sU).floatValue(), 0.0f) + Math.max(((Float) this.aa.bT.sw.sU).floatValue(), 0.0f);
        } else {
            max2 = 0.0f;
        }
        this.as = this.ao.a(0, max2 + max);
        if (((Boolean) this.aa.bT.st.sU).booleanValue()) {
            this.as = (h() ? this.ay.y : this.ay.x) + this.as;
        }
        if (L().getVisibility() == 0 && z) {
            this.as = (h() ? ca.a(L()) : ca.b(L())) + this.as;
        }
        if (this.ag != null) {
            boolean z2;
            float max3 = Math.max(0.0f, this.ag.floatValue());
            if (max3 >= ((float) this.as)) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.V = z2;
            this.as = Math.round(max3);
            return;
        }
        this.V = true;
    }

    boolean a(int i, int i2, PointF pointF) {
        float f = pointF.x * ((float) this.ay.x);
        float f2 = ((float) this.ay.y) * pointF.y;
        if (c(this.ax)) {
            f *= 1.3f;
            f2 *= 1.3f;
        }
        if (i < 0 || ((h() && ((float) i2) - (r1 * ((float) i)) < 0.0f) || (!h() && ((float) i2) - (r0 * ((float) i)) < 0.0f))) {
            return false;
        }
        return true;
    }

    ag O() {
        return this.J.O();
    }

    private void P() {
        int i = 0;
        if (!this.U) {
            this.T = 0.0d;
            List seriesForAxis = this.J.getSeriesForAxis(this);
            if (!seriesForAxis.isEmpty()) {
                List c = c(seriesForAxis);
                if (c.size() != 0) {
                    this.S = a((InternalDataPoint) c.get(0));
                    this.R = a((InternalDataPoint) c.get(c.size() - 1));
                    if (this.R - this.S == 0.0d) {
                        this.T = N();
                        return;
                    }
                    int i2 = 0;
                    while (i < c.size() - 1) {
                        double a = a((InternalDataPoint) c.get(i));
                        double a2 = a((InternalDataPoint) c.get(i + 1));
                        double abs = Math.abs(a2 - a);
                        if (a != a2 && (r2 == 0 || abs < this.T)) {
                            this.T = abs;
                            i2 = 1;
                        }
                        i++;
                    }
                }
            }
        }
    }

    private List<InternalDataPoint> c(List<Series<?>> list) {
        List<InternalDataPoint> arrayList = new ArrayList();
        for (Series series : list) {
            if ((series instanceof BarColumnSeries) && a(((BarColumnSeries) series).dR)) {
                for (Object add : series.db.je) {
                    arrayList.add(add);
                }
            }
        }
        if (h()) {
            Collections.sort(arrayList, InternalDataPoint.iY);
        } else {
            Collections.sort(arrayList, InternalDataPoint.iZ);
        }
        return arrayList;
    }

    boolean a(com.shinobicontrols.charts.Series.Orientation orientation) {
        return (orientation == com.shinobicontrols.charts.Series.Orientation.HORIZONTAL && this.P == Orientation.HORIZONTAL) || (orientation == com.shinobicontrols.charts.Series.Orientation.VERTICAL && this.P == Orientation.VERTICAL);
    }

    private double a(InternalDataPoint internalDataPoint) {
        return this.P == Orientation.HORIZONTAL ? internalDataPoint.x : internalDataPoint.y;
    }

    private void Q() {
        List<Series> seriesForAxis = this.J.getSeriesForAxis(this);
        List<en> arrayList = new ArrayList();
        for (Series series : seriesForAxis) {
            en enVar = series.oy;
            if (!(enVar == null || arrayList.contains(enVar))) {
                arrayList.add(enVar);
            }
        }
        for (en enVar2 : arrayList) {
            enVar2.t(this);
        }
    }

    OnGestureListener R() {
        return this.aF;
    }

    double d(double d) {
        Rect rect = O().aX;
        return (d / ((double) (this.P == Orientation.HORIZONTAL ? rect.width() : rect.height()))) * this.ai.dF();
    }

    double e(double d) {
        Rect rect = O().aX;
        if (this.P != Orientation.HORIZONTAL) {
            d = ((double) rect.height()) - d;
        }
        return d(d) + this.ai.nv;
    }

    double a(double d, CartesianSeries<?> cartesianSeries) {
        double d2;
        if (this.P == Orientation.VERTICAL) {
            d2 = (double) this.J.em.aX.top;
        } else {
            d2 = (double) this.J.em.aX.left;
        }
        return d2 + this.ao.a(d, this.J.em.aX.width(), this.J.em.aX.height());
    }

    public float getPixelValueForUserValue(T userValue) {
        if (!Range.h(this.ai)) {
            return (float) f(transformUserValueToInternal(userValue));
        }
        ev.f(this.J != null ? this.J.getContext().getString(R.string.AxisRangeNotSetPixelCall) : "Calling getPixelValueForUserValue before an axisRange has been set.");
        return 0.0f;
    }

    double f(double d) {
        return this.ao.a(d, this.J.em.aX.width(), this.J.em.aX.height()) + ((double) S());
    }

    public T getUserValueForPixelValue(float pixelValue) {
        if (!Range.h(this.ai)) {
            return transformInternalValueToUser(this.ao.a((double) (pixelValue - ((float) S())), this.J.em.aX));
        }
        ev.f(this.J != null ? this.J.getContext().getString(R.string.AxisRangeNotSetUserCall) : "Calling getUserValueForPixelValue before an axisRange has been set.");
        return null;
    }

    private int S() {
        if (this.P == Orientation.HORIZONTAL) {
            return this.J.eg.left + this.J.em.aX.left;
        }
        return this.J.eg.top + this.J.em.aX.top;
    }

    void T() {
    }

    fd U() {
        return new fd();
    }

    public MotionState getMotionState() {
        return this.aF.nN;
    }

    public final ShinobiChart getChart() {
        return this.J;
    }

    void a(Rect rect, int i, int i2) {
        if (this.ap != null) {
            this.ao.a(rect, this.as, this.ah, this.ap, this.aq.jq);
            Gravity.apply(this.ao.a((com.shinobicontrols.charts.Title.Position) this.aa.bU.sG.sU), this.ap.getMeasuredWidth(), this.ap.getMeasuredHeight(), this.aq.jq, this.aq.cX());
            ca.b(this.ap, this.aq.jr);
        }
    }

    private DashPathEffect V() {
        if (this.aa.bS.iI.sU == null || ((float[]) this.aa.bS.iI.sU).length <= 0) {
            return null;
        }
        float[] fArr = new float[((float[]) this.aa.bS.iI.sU).length];
        for (int i = 0; i < fArr.length; i++) {
            fArr[i] = (float) ca.a(this.density, 0, ((float[]) this.aa.bS.iI.sU)[i]);
        }
        return new DashPathEffect(fArr, 0.0f);
    }

    void a(af afVar) {
        this.J = afVar;
        if (afVar != null) {
            this.density = afVar.getContext().getResources().getDisplayMetrics().density;
            if (this.aJ.size() > 0) {
                afVar.bv();
            }
        }
    }

    public void removeAllSkipRanges() {
        this.aJ.clear();
        Y();
    }

    public List<Range<T>> getSkipRanges() {
        return Collections.unmodifiableList(this.aJ);
    }

    bh a(a aVar) {
        return this.u.a(ez.A, (a) aVar);
    }

    final void W() {
        this.u.a(new ez());
    }

    public void addSkipRange(Range<T> skipRange) {
        if (this.J != null) {
            this.J.bv();
        }
        if (e((Range) skipRange)) {
            this.aJ.add(skipRange);
            Y();
            return;
        }
        X();
    }

    public void addSkipRanges(List<? extends Range<T>> skipRanges) {
        if (this.J != null) {
            this.J.bv();
        }
        Collection arrayList = new ArrayList();
        for (Range range : skipRanges) {
            if (e(range)) {
                arrayList.add(range);
            } else {
                X();
            }
        }
        if (!arrayList.isEmpty()) {
            this.aJ.addAll(arrayList);
            Y();
        }
    }

    private void X() {
        ev.g(this.J != null ? this.J.getContext().getString(R.string.CannotAddNullUndefinedOrEmptySkip) : "Cannot add a null skip range or one with a zero or negative span.");
    }

    public void removeSkipRange(Range<T> skipRange) {
        this.aJ.remove(skipRange);
        Y();
    }

    public void removeSkipRanges(List<? extends Range<T>> skipRanges) {
        this.aJ.removeAll(skipRanges);
        Y();
    }

    void Y() {
        Range currentDisplayedRange = getCurrentDisplayedRange();
        d(aa());
        if (Range.i(this.am) && !this.am.isEmpty()) {
            b(this.am);
        }
        W();
        Z();
        if (this.J != null) {
            this.J.redrawChart();
            d(currentDisplayedRange);
        }
    }

    private void Z() {
        Set<Axis> hashSet = new HashSet();
        hashSet.add(this);
        if (this.J != null) {
            Set<CartesianSeries> x = this.J.eo.x(this);
            if (x != null) {
                for (CartesianSeries cartesianSeries : x) {
                    hashSet.add(cartesianSeries.getXAxis());
                    hashSet.add(cartesianSeries.getYAxis());
                }
            }
        }
        for (Axis q : hashSet) {
            q.q();
        }
    }

    List<Range<T>> aa() {
        return this.aJ;
    }

    boolean ab() {
        return aa().size() > 0;
    }

    private void d(final Range<T> range) {
        if (Range.i(range)) {
            this.J.post(new Runnable(this) {
                final /* synthetic */ Axis aQ;

                public void run() {
                    this.aQ.requestCurrentDisplayedRange(range.getMinimum(), range.getMaximum(), false, false);
                }
            });
        }
    }

    void d(List<Range<T>> list) {
        List r = this.aM.r(this.aM.q(list));
        this.aK.b(this.aN.b(r));
        this.aL.b(this.aO.b(r));
    }

    boolean e(Range<T> range) {
        return Range.i(range) && !range.isEmpty();
    }

    boolean isUserDataPointWithinASkipRange(Object rawUserValue) {
        bv k = this.aK.k(convertUserValueTypeToInternalDataType(rawUserValue));
        return k != null && k.jj.ld == 0.0d;
    }
}
