package com.shinobicontrols.charts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import com.shinobicontrols.charts.Axis.Orientation;
import com.shinobicontrols.charts.Axis.Position;
import com.shinobicontrols.charts.Legend.Placement;
import com.shinobicontrols.charts.ShinobiChart.OnAxisMotionStateChangeListener;
import com.shinobicontrols.charts.ShinobiChart.OnAxisRangeChangeListener;
import com.shinobicontrols.charts.ShinobiChart.OnCrosshairActivationStateChangedListener;
import com.shinobicontrols.charts.ShinobiChart.OnCrosshairDrawListener;
import com.shinobicontrols.charts.ShinobiChart.OnGestureListener;
import com.shinobicontrols.charts.ShinobiChart.OnInternalLayoutListener;
import com.shinobicontrols.charts.ShinobiChart.OnPieDonutSliceLabelDrawListener;
import com.shinobicontrols.charts.ShinobiChart.OnPieDonutSliceUpdateListener;
import com.shinobicontrols.charts.ShinobiChart.OnSeriesAnimationListener;
import com.shinobicontrols.charts.ShinobiChart.OnSeriesSelectionListener;
import com.shinobicontrols.charts.ShinobiChart.OnSnapshotDoneListener;
import com.shinobicontrols.charts.ShinobiChart.OnTickMarkDrawListener;
import com.shinobicontrols.charts.ShinobiChart.OnTickMarkUpdateListener;
import com.shinobicontrols.charts.ShinobiChart.OnTrackingInfoChangedForCrosshairListener;
import com.shinobicontrols.charts.ShinobiChart.OnTrackingInfoChangedForTooltipListener;
import com.shinobicontrols.charts.Title.CentersOn;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

final class af extends ViewGroup implements ShinobiChart {
    private static final boolean ed = (VERSION.SDK_INT >= 14);
    private Title ap;
    private final float density;
    private OnAxisRangeChangeListener eA;
    private OnInternalLayoutListener eB;
    private OnSeriesSelectionListener eC;
    OnSnapshotDoneListener eD;
    private OnSeriesAnimationListener eE;
    private OnCrosshairActivationStateChangedListener eF;
    private OnPieDonutSliceLabelDrawListener eG;
    private OnPieDonutSliceUpdateListener eH;
    private OnTickMarkUpdateListener eI;
    private OnTickMarkDrawListener eJ;
    private OnCrosshairDrawListener eK;
    private OnTrackingInfoChangedForTooltipListener eL;
    private OnTrackingInfoChangedForCrosshairListener eM;
    final AnnotationsManager eN;
    final ai eO;
    private float eP;
    private float eQ;
    private boolean eR;
    private final bz ee;
    private final cw ef;
    final Rect eg;
    private fb eh;
    private ChartStyle ei;
    private MainTitleStyle ej;
    private Legend ek;
    private final cb el;
    ag em;
    final List<Series<?>> en;
    final et eo;
    private final eo ep;
    final i eq;
    final i er;
    private String es;
    private boolean et;
    ei eu;
    Crosshair ev;
    final eu ew;
    final el ex;
    private OnGestureListener ey;
    private OnAxisMotionStateChangeListener ez;

    protected final /* synthetic */ LayoutParams generateDefaultLayoutParams() {
        return bg();
    }

    public final /* synthetic */ LayoutParams generateLayoutParams(AttributeSet x0) {
        return a(x0);
    }

    protected final /* synthetic */ LayoutParams generateLayoutParams(LayoutParams x0) {
        return a(x0);
    }

    final List<en> aR() {
        return this.eo.aR();
    }

    af(Context context) {
        this(context, null);
    }

    af(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    af(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.ee = new bz();
        this.ef = new cw();
        this.eg = new Rect();
        this.ei = new ChartStyle();
        this.ej = new MainTitleStyle();
        this.el = new cb(this);
        this.en = new ArrayList();
        this.eo = new et();
        this.ep = new eo();
        this.eq = new i("x");
        this.er = new i("y");
        this.et = true;
        this.eu = new ei(this);
        this.ew = new eu(this);
        this.ex = new el(this);
        this.eP = -1.0f;
        this.eQ = -1.0f;
        this.eR = false;
        this.eO = new ai(this);
        this.density = getResources().getDisplayMetrics().density;
        this.eh = fc.a(context, attributeSet);
        this.ei = this.eh.eK();
        this.ej = this.eh.eL();
        aS();
        a(context);
        b(context);
        c(context);
        aT();
        this.em.d(this.eu);
        this.eN = new AnnotationsManager(this);
    }

    public final void setLicenseKey(String trialLicenseKey) {
        this.et = true;
        this.es = trialLicenseKey;
    }

    private void aS() {
        if (this.ei == null) {
            return;
        }
        if (this.ei.getBackgroundColor() == 0) {
            a.a((View) this, null);
        } else {
            setBackgroundColor(this.ei.getBackgroundColor());
        }
    }

    private void a(Context context) {
        this.ap = new Title(context);
        this.ap.setLayoutParams(new MarginLayoutParams(-2, -2));
        this.ap.a(this.ej);
        this.ap.setVisibility(8);
        addView(this.ap);
    }

    private void b(Context context) {
        this.em = new ag(context, this);
        this.em.setLayoutParams(new MarginLayoutParams(-2, -2));
        this.em.bx();
        this.em.a(this.ei);
        addView(this.em);
    }

    private void c(Context context) {
        this.ek = new Legend(context);
        this.ek.a(this.el);
        this.ek.setVisibility(8);
        LayoutParams marginLayoutParams = new MarginLayoutParams(-2, -2);
        int c = ca.c(this.density, 10.0f);
        marginLayoutParams.setMargins(c, c, c, c);
        this.ek.setLayoutParams(marginLayoutParams);
        this.ek.setStyle(this.eh.eN());
        addView(this.ek);
    }

    private void aT() {
        this.ev = new Crosshair();
        this.ev.a(this);
        this.ev.setStyle(this.eh.eM());
        this.em.c(this.ew);
    }

    final void onCreate(Bundle savedInstanceState) {
    }

    final void onResume() {
        this.em.onResume();
    }

    final void onPause() {
        this.em.onPause();
    }

    final void onDestroy() {
    }

    protected final void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.ef.reset();
        b(widthMeasureSpec, heightMeasureSpec);
        c(widthMeasureSpec, heightMeasureSpec);
        d(widthMeasureSpec, heightMeasureSpec);
        this.ef.z(getPaddingLeft() + getPaddingRight());
        this.ef.y(getPaddingTop() + getPaddingBottom());
        setMeasuredDimension(View.resolveSize(this.ef.li, widthMeasureSpec), View.resolveSize(this.ef.lh, heightMeasureSpec));
    }

    private void b(int i, int i2) {
        if (!aU()) {
            measureChildWithMargins(this.ap, i, this.ef.li, i2, this.ef.lh);
            this.ef.y(ca.a(this.ap));
        }
    }

    private boolean aU() {
        return this.ap == null || this.ap.getVisibility() == 8;
    }

    private void c(int i, int i2) {
        if (!aV()) {
            measureChildWithMargins(this.ek, i, this.ef.li, i2, this.ef.lh);
            this.ef.z(aX());
            this.ef.y(aY());
        }
    }

    private boolean aV() {
        return this.ek == null || this.ek.getVisibility() == 8;
    }

    public final boolean isSeriesSelectionSingle() {
        return this.eu.ol;
    }

    public final void setSeriesSelectionSingle(boolean seriesSelectionIsSingle) {
        this.eu.ol = seriesSelectionIsSingle;
    }

    final boolean aW() {
        return this.eq.af() && this.er.af();
    }

    private int aX() {
        return this.ek.jt.b(ca.b(this.ek), this.ei.bK(), a(bl(), null), a(bl(), 0), (MarginLayoutParams) this.em.getLayoutParams());
    }

    private Position a(Axis<?, ?> axis, Position position) {
        return axis != null ? axis.Q : position;
    }

    private int a(Axis<?, ?> axis, int i) {
        return axis != null ? axis.as : i;
    }

    private int aY() {
        return this.ek.jt.a(ca.a(this.ek), this.ei.bK(), a(bk(), null), a(bk(), 0), (MarginLayoutParams) this.em.getLayoutParams());
    }

    private void d(int i, int i2) {
        measureChildWithMargins(this.em, i, this.ef.li, i2, this.ef.lh);
        this.ef.z(ca.b(this.em));
        this.ef.y(ca.a(this.em));
    }

    protected final void onLayout(boolean changed, int left, int top, int right, int bottom) {
        aZ();
        int paddingRight = (right - getPaddingRight()) - (getPaddingLeft() + left);
        int paddingBottom = (bottom - getPaddingBottom()) - (getPaddingTop() + top);
        this.ee.b(0, 0, paddingRight, paddingBottom);
        if (this.ej.getOverlapsChart()) {
            this.ap.bringToFront();
        } else {
            this.ee.q(ca.a(this.ap));
        }
        bb();
        bf();
        this.ee.b(0, 0, paddingRight, paddingBottom);
        ba();
        bp();
    }

    private void aZ() {
        if (this.et) {
            co c = ba.c(this.es);
            if (c.kn) {
                this.et = false;
                return;
            }
            throw new InvalidLicenseException(c.km);
        }
    }

    private void ba() {
        if (!aU()) {
            this.ee.cW();
            if (this.ej.getCentersOn() != CentersOn.CHART) {
                be();
            }
            if (this.ej.getCentersOn() == CentersOn.PLOTTING_AREA) {
                Rect rect = this.ee.jq;
                rect.left += this.em.eZ;
                rect = this.ee.jq;
                rect.right -= this.em.fa;
            }
            int b = ca.b(this.ap);
            int a = ca.a(this.ap);
            Gravity.apply(this.ej.getPosition().eX() | 48, b, a, this.ee.jq, this.ee.cX());
            this.ee.cY();
            ca.a(this.ap, this.ee.jq);
            ca.b(this.ap, this.ee.jq);
            this.ee.q(a);
        }
    }

    private void bb() {
        this.ee.cW();
        bd();
        this.eg.set(this.ee.jq);
        ca.b(this.em, this.ee.jq);
    }

    private void bc() {
        List arrayList = new ArrayList();
        int size = this.en.size();
        for (int i = 0; i < size; i++) {
            Series series = (Series) this.en.get(i);
            if (series instanceof PieDonutSeries) {
                arrayList.add((PieDonutSeries) series);
            }
        }
        int size2 = arrayList.size();
        if (size2 != 0) {
            float f = 0.4f / ((float) size2);
            size = 0;
            PieDonutSeries pieDonutSeries = null;
            while (size < size2) {
                Object obj;
                PieDonutSeries pieDonutSeries2 = (PieDonutSeries) arrayList.get(size);
                float dm = pieDonutSeries != null ? pieDonutSeries.dm() : 0.0f;
                if (pieDonutSeries2.lZ.sV) {
                    obj = null;
                } else {
                    pieDonutSeries2.lZ.c(Float.valueOf(pieDonutSeries != null ? dm + pieDonutSeries.getOuterRadius() : pieDonutSeries2.h(f)));
                    obj = 1;
                }
                float dm2 = pieDonutSeries2.dm();
                if (!pieDonutSeries2.ma.sV) {
                    pieDonutSeries2.ma.c(Float.valueOf(((((float) (size + 1)) * ((0.95f - f) / ((float) size2))) + f) - dm2));
                    obj = 1;
                }
                if (obj != null) {
                    pieDonutSeries2.ac();
                }
                size++;
                pieDonutSeries = pieDonutSeries2;
            }
        }
    }

    private void bd() {
        if (!aV()) {
            this.ek.jt.b(this.ee.jq, aX(), aY());
        }
        ca.a(this.em, this.ee.jq);
    }

    private void be() {
        if (!aV()) {
            this.ek.jt.b(this.ee.jq, aX(), 0);
        }
        ca.a(this.em, this.ee.jq);
    }

    private void bf() {
        if (!aV()) {
            this.ee.cW();
            if (this.ek.getPlacement() == Placement.INSIDE_PLOT_AREA || this.ek.getPlacement() == Placement.ON_PLOT_AREA_BORDER) {
                bd();
            }
            this.ek.jt.a(this.ee.jq, this.em.eZ, this.em.eY, this.em.fa, this.em.eX);
            Gravity.apply(this.ek.getPosition().getGravity(), ca.b(this.ek), ca.a(this.ek), this.ee.jq, this.ee.cX());
            this.ee.cY();
            ca.a(this.ek, this.ee.jq);
            this.ek.jt.a(this.ee.jq, (MarginLayoutParams) this.ek.getLayoutParams(), this.ek.getMeasuredWidth(), this.ek.getMeasuredHeight(), this.ei.bK());
            ca.b(this.ek, this.ee.jq);
        }
    }

    public final MarginLayoutParams a(AttributeSet attributeSet) {
        return new MarginLayoutParams(getContext(), attributeSet);
    }

    protected final MarginLayoutParams a(LayoutParams layoutParams) {
        return new MarginLayoutParams(layoutParams);
    }

    protected final boolean checkLayoutParams(LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }

    protected final MarginLayoutParams bg() {
        return new MarginLayoutParams(-2, -2);
    }

    protected final void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (bn()) {
            bc();
        }
    }

    public final void addSeries(Series<?> series) {
        addSeries(series, null, null);
    }

    public final void addSeries(Series<?> series, Axis<?, ?> xAxis, Axis<?, ?> yAxis) {
        if (h((Series) series)) {
            bq();
        }
        synchronized (ah.lock) {
            if (series == null) {
                throw new IllegalArgumentException(getContext().getString(R.string.ChartCannotAddNullSeries));
            }
            if (series instanceof PieDonutSeries) {
                e((Series) series);
            } else {
                a((Series) series, (Axis) xAxis, (Axis) yAxis);
            }
        }
    }

    private void e(Series<?> series) {
        if (bh()) {
            throw new IllegalStateException(getContext().getString(R.string.ChartPieDonutInCartesian));
        } else if (this.eq.af() || this.er.af()) {
            throw new IllegalStateException(getContext().getString(R.string.ChartPieDonutCannotHaveAxes));
        } else {
            synchronized (ah.lock) {
                if (series.nE) {
                    this.ex.a((Series) series, this);
                } else {
                    f((Series) series);
                }
            }
        }
    }

    private boolean bh() {
        return this.en.size() > 0 && !bn();
    }

    final void f(Series<?> series) {
        this.en.add(series);
        series.j(this.eh, this.ep.z(series), false);
        if (series instanceof PieDonutSeries) {
            bc();
        }
        series.a(this);
    }

    private void a(Series<?> series, Axis<?, ?> axis, Axis<?, ?> axis2) {
        if (bi()) {
            throw new IllegalStateException(getContext().getString(R.string.ChartCartesianInPieDonut));
        }
        Axis i = i((Axis) axis);
        Axis j = j(axis2);
        if (i == null) {
            ev.f(getContext().getString(R.string.ChartNoPrimaryX));
        }
        if (j == null) {
            ev.f(getContext().getString(R.string.ChartNoPrimaryY));
        }
        this.eo.a((CartesianSeries) series, i, j);
        synchronized (ah.lock) {
            if (series.nE) {
                this.ex.a((Series) series, this);
            } else {
                f((Series) series);
            }
        }
    }

    private boolean bi() {
        return this.en.size() > 0 && bn();
    }

    private Axis<?, ?> i(Axis<?, ?> axis) {
        if (axis == null) {
            return bk();
        }
        if (axis.J == this) {
            return axis;
        }
        addXAxis(axis);
        return axis;
    }

    private Axis<?, ?> j(Axis<?, ?> axis) {
        if (axis == null) {
            return bl();
        }
        if (axis.J == this) {
            return axis;
        }
        addYAxis(axis);
        return axis;
    }

    public final boolean removeSeries(Series<?> s) {
        boolean z;
        synchronized (ah.lock) {
            if (s.nE) {
                this.ex.b(s, this);
                z = false;
            } else {
                g((Series) s);
                z = true;
            }
        }
        return z;
    }

    final void g(Series<?> series) {
        Axis axis;
        Axis axis2;
        if (series instanceof PieDonutSeries) {
            axis = null;
            axis2 = null;
        } else {
            axis2 = series.getXAxis();
            axis = series.getYAxis();
        }
        this.en.remove(series);
        this.eo.x((Series) series);
        this.ep.A(series);
        if (series instanceof PieDonutSeries) {
            bc();
        } else {
            if (axis2 != null) {
                axis2.q();
            }
            if (axis != null) {
                axis.q();
            }
        }
        if (this.ev != null) {
            this.ev.j(series);
        }
        series.a(null);
        this.em.av();
        this.em.invalidate();
        redrawChart();
    }

    public final List<Series<?>> getSeries() {
        return Collections.unmodifiableList(this.en);
    }

    final List<CartesianSeries<?>> bj() {
        List<CartesianSeries<?>> arrayList = new ArrayList();
        for (Series series : this.en) {
            if (series instanceof CartesianSeries) {
                arrayList.add((CartesianSeries) series);
            }
        }
        return arrayList;
    }

    public final Axis<?, ?> getXAxisForSeries(Series<?> s) {
        return this.eo.getXAxisForSeries(s);
    }

    public final Axis<?, ?> getYAxisForSeries(Series<?> s) {
        return this.eo.getYAxisForSeries(s);
    }

    public final List<Series<?>> getSeriesForAxis(Axis<?, ?> a) {
        Set x = this.eo.x((Axis) a);
        if (x == null) {
            return new ArrayList();
        }
        return Arrays.asList(x.toArray(new Series[0]));
    }

    public final void setHidden(List<Series<?>> seriesList, boolean hidden) {
        if (seriesList == null) {
            ev.g(getResources().getString(R.string.ChartCannotPassInNullSeriesList));
            return;
        }
        synchronized (ah.lock) {
            List arrayList = new ArrayList();
            for (Series series : seriesList) {
                if (!series.nE || series.J == null) {
                    series.j(hidden);
                } else if (hidden) {
                    if (!(series.oC || series.oz == series.oB)) {
                        arrayList.add(series);
                    }
                } else if ((series.oC || series.oz != null) && series.oz != series.oA) {
                    arrayList.add(series);
                }
            }
            if (hidden) {
                this.ex.o(arrayList);
            } else {
                this.ex.p(arrayList);
            }
        }
    }

    public final List<Axis<?, ?>> getAllXAxes() {
        List arrayList = new ArrayList();
        for (Object add : this.eq.bK) {
            arrayList.add(add);
        }
        return Collections.unmodifiableList(arrayList);
    }

    public final List<Axis<?, ?>> getAllYAxes() {
        List arrayList = new ArrayList();
        for (Object add : this.er.bK) {
            arrayList.add(add);
        }
        return Collections.unmodifiableList(arrayList);
    }

    public final Rect getPlotAreaRect() {
        Rect rect = new Rect(this.em.aX);
        rect.offset(this.eg.left, this.eg.top);
        return rect;
    }

    public final Rect getCanvasRect() {
        return this.eg;
    }

    public final String getInfo() {
        return BuildConfig.INFO;
    }

    public final Legend getLegend() {
        return this.ek;
    }

    public final Crosshair getCrosshair() {
        return this.ev;
    }

    final Axis<?, ?> bk() {
        return this.eq.ae();
    }

    final Axis<?, ?> bl() {
        return this.er.ae();
    }

    public final Axis<?, ?> getXAxis() {
        return bk();
    }

    public final void setXAxis(Axis<?, ?> axis) {
        a((Axis) axis, this.eq);
        k(axis);
    }

    private void a(Axis<?, ?> axis, i iVar) {
        if (axis == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.ChartCannotAddNullAxis));
        }
        if (b(iVar)) {
            bq();
        }
        if (bn()) {
            throw new IllegalStateException(getContext().getString(R.string.ChartPieDonutCannotHaveAxes));
        } else if (axis.J == this) {
            throw new IllegalStateException(getContext().getString(R.string.ChartAlreadyHasThisAxis));
        } else if (axis.J != null) {
            throw new IllegalArgumentException(getContext().getString(R.string.ChartAxisBelongsToAnotherChart));
        }
    }

    private void k(Axis<?, ?> axis) {
        Axis bk = bk();
        if (bk != null) {
            removeXAxis(bk);
        }
        a((Axis) axis, Orientation.HORIZONTAL, this.eh.eO());
        this.eq.b(axis);
        this.eo.u(axis);
    }

    public final Axis<?, ?> getYAxis() {
        return bl();
    }

    public final void setYAxis(Axis<?, ?> axis) {
        a((Axis) axis, this.er);
        l(axis);
    }

    private void l(Axis<?, ?> axis) {
        Axis bl = bl();
        if (bl != null) {
            removeYAxis(bl);
        }
        a((Axis) axis, Orientation.VERTICAL, this.eh.eP());
        this.er.b(axis);
        this.eo.v(axis);
    }

    public final void applyTheme(int themeResID, boolean overwrite) {
        if (overwrite) {
            this.eh = fc.a(getContext(), themeResID);
        } else {
            this.eh = fc.a(getContext(), this.eh, themeResID);
        }
        c(overwrite);
    }

    public final String getTitle() {
        if (this.ap == null) {
            return null;
        }
        return this.ap.getText().toString();
    }

    public final void setTitle(String title) {
        if (this.ap == null) {
            a(getContext());
        }
        this.ap.setText(title);
        if (title != null) {
            this.ap.setVisibility(0);
        } else {
            this.ap.setVisibility(8);
        }
    }

    public final void addXAxis(Axis<?, ?> axis) {
        a((Axis) axis, this.eq);
        if (this.eq.af()) {
            m(axis);
        } else {
            k(axis);
        }
    }

    private void m(Axis<?, ?> axis) {
        this.eq.c(axis);
        a((Axis) axis, Orientation.HORIZONTAL, this.eh.eO());
    }

    public final void addYAxis(Axis<?, ?> axis) {
        a((Axis) axis, this.er);
        if (this.er.af()) {
            n(axis);
        } else {
            l(axis);
        }
    }

    private void n(Axis<?, ?> axis) {
        this.er.c(axis);
        a((Axis) axis, Orientation.VERTICAL, this.eh.eP());
    }

    private void a(Axis<?, ?> axis, Orientation orientation, AxisStyle axisStyle) {
        axis.a(orientation);
        AxisStyle axisStyle2 = axis.getStyle() == null ? new AxisStyle() : axis.getStyle();
        axisStyle2.a(axisStyle);
        axis.setStyle(axisStyle2);
        axis.a(this);
        this.em.a(axis.R());
        this.em.addView(axis.K());
    }

    public final ChartStyle getStyle() {
        return this.ei;
    }

    public final void setStyle(ChartStyle style) {
        this.ei = style;
    }

    public final MainTitleStyle getTitleStyle() {
        return this.ej;
    }

    public final void setTitleStyle(MainTitleStyle titleStyle) {
        this.ej = titleStyle;
    }

    final void c(boolean z) {
        int i = 0;
        if (this.eh != null) {
            if (z || this.ei == null) {
                this.ei = new ChartStyle();
            }
            this.ei.b(this.eh.eK());
            if (z || this.ej == null) {
                this.ej = new MainTitleStyle();
            }
            this.ej.a(this.eh.eL());
            for (Axis axis : this.eq.bK) {
                AxisStyle axisStyle = (z || axis.getStyle() == null) ? new AxisStyle() : axis.getStyle();
                axisStyle.a(this.eh.eO());
                axis.setStyle(axisStyle);
            }
            while (i < this.er.bK.length) {
                Axis axis2 = this.er.bK[i];
                AxisStyle axisStyle2 = (z || axis2.getStyle() == null) ? new AxisStyle() : axis2.getStyle();
                axisStyle2.a(this.eh.eP());
                axis2.setStyle(axisStyle2);
                i++;
            }
            if (this.ek != null) {
                if (z || this.ek.getStyle() == null) {
                    this.ek.setStyle(new LegendStyle());
                }
                this.ek.getStyle().e(this.eh.eN());
            }
            if (this.ev != null) {
                if (z || this.ev.getStyle() == null) {
                    this.ev.setStyle(new CrosshairStyle());
                }
                this.ev.getStyle().a(this.eh.eM());
            }
            for (Series series : this.en) {
                series.j(this.eh, this.ep.B(series), z);
            }
            for (Annotation annotation : this.eN.getAnnotations()) {
                if (z || annotation.getStyle() == null) {
                    annotation.setStyle(new AnnotationStyle());
                }
                annotation.getStyle().a(this.eh.eQ());
            }
        }
    }

    public final void redrawChart() {
        if (this.ek != null) {
            this.ek.reload();
        }
        aS();
        this.em.a(this.ei);
        this.ap.a(this.ej);
        if (this.ev != null) {
            this.ev.e();
        }
        this.eN.e();
        bm();
    }

    private void bm() {
        int i = 0;
        invalidate();
        requestLayout();
        this.em.invalidate();
        this.em.requestLayout();
        for (Axis axis : this.eq.bK) {
            axis.aA.eW();
        }
        while (i < this.er.bK.length) {
            this.er.bK[i].aA.eW();
            i++;
        }
    }

    public final void removeXAxis(Axis<?, ?> axis) {
        this.eo.removeXAxis(axis);
        b((Axis) axis, this.eq);
    }

    public final void removeYAxis(Axis<?, ?> axis) {
        this.eo.removeYAxis(axis);
        b((Axis) axis, this.er);
    }

    private void b(Axis<?, ?> axis, i iVar) {
        iVar.d(axis);
        this.em.b(axis.R());
        this.em.removeView(axis.K());
        axis.w();
        this.eN.removeAllAnnotations(axis);
    }

    final ag O() {
        return this.em;
    }

    final boolean bn() {
        Object[] toArray = this.en.toArray();
        for (Object obj : toArray) {
            if (((Series) obj) instanceof PieDonutSeries) {
                return true;
            }
        }
        return false;
    }

    public final boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (aW()) {
            switch (motionEvent.getActionMasked()) {
                case 0:
                    this.eP = motionEvent.getX();
                    this.eQ = motionEvent.getY();
                    break;
                case 1:
                    break;
                case 2:
                    if (this.eR || a(motionEvent)) {
                        this.eR = true;
                        getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    }
                case 3:
                    this.em.az();
                    break;
            }
            this.eP = -1.0f;
            this.eQ = -1.0f;
            this.eR = false;
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return false;
    }

    private boolean a(MotionEvent motionEvent) {
        if (this.ev != null && this.ev.isActive()) {
            return true;
        }
        if (a(this.eq) && a(this.er)) {
            return true;
        }
        if (a(this.eq) && a(this.eP, motionEvent.getX())) {
            return true;
        }
        if (a(this.er) && a(this.eQ, motionEvent.getY())) {
            return true;
        }
        return false;
    }

    private boolean a(float f, float f2) {
        return Math.abs(f2 - f) > ((float) this.eO.fx);
    }

    private boolean a(i iVar) {
        for (Axis axis : iVar.bK) {
            if (axis.isGesturePanningEnabled() || axis.isGestureZoomingEnabled()) {
                return true;
            }
        }
        return false;
    }

    public final void setOnGestureListener(OnGestureListener listener) {
        this.ey = listener;
    }

    public final void setOnAxisMotionStateChangeListener(OnAxisMotionStateChangeListener listener) {
        this.ez = listener;
    }

    public final void setOnAxisRangeChangeListener(OnAxisRangeChangeListener listener) {
        this.eA = listener;
    }

    public final void setOnInternalLayoutListener(OnInternalLayoutListener listener) {
        this.eB = listener;
    }

    public final void setOnSeriesSelectionListener(OnSeriesSelectionListener listener) {
        this.eC = listener;
    }

    public final void setOnSnapshotDoneListener(OnSnapshotDoneListener listener) {
        this.eD = listener;
    }

    public final void setOnSeriesAnimationListener(OnSeriesAnimationListener listener) {
        this.eE = listener;
    }

    final void onAxisMotionStateChange(Axis<?, ?> axis) {
        if (this.ez != null) {
            this.ez.onAxisMotionStateChange(axis);
        }
    }

    final void onAxisRangeChange(Axis<?, ?> axis) {
        if (this.eA != null) {
            this.eA.onAxisRangeChange(axis);
        }
    }

    final void bo() {
        if (this.eF != null) {
            this.eF.onCrosshairActivationStateChanged(this);
        }
    }

    final void bp() {
        if (this.eB != null) {
            this.eB.onInternalLayout(this);
        }
    }

    final void b(PointF pointF) {
        if (this.ey != null) {
            this.ey.onDoubleTapDown(this, h(pointF));
        }
    }

    final void c(PointF pointF) {
        if (this.ey != null) {
            this.ey.onDoubleTapUp(this, h(pointF));
        }
    }

    final void d(PointF pointF) {
        if (this.ey != null) {
            this.ey.onLongTouchDown(this, h(pointF));
        }
    }

    final void e(PointF pointF) {
        if (this.ey != null) {
            this.ey.onLongTouchUp(this, h(pointF));
        }
    }

    final void a(PointF pointF, PointF pointF2, PointF pointF3) {
        if (this.ey != null) {
            this.ey.onPinch(this, h(pointF), h(pointF2), pointF3);
        }
    }

    final void a(PointF pointF, boolean z, PointF pointF2) {
        if (this.ey != null) {
            this.ey.onPinchEnd(this, h(pointF), z, pointF2);
        }
    }

    final void a(PointF pointF, PointF pointF2) {
        if (this.ey != null) {
            this.ey.onSecondTouchDown(this, h(pointF), h(pointF2));
        }
    }

    final void b(PointF pointF, PointF pointF2) {
        if (this.ey != null) {
            this.ey.onSecondTouchUp(this, h(pointF), h(pointF2));
        }
    }

    final void f(PointF pointF) {
        if (this.ey != null) {
            this.ey.onSingleTouchDown(this, h(pointF));
        }
    }

    final void g(PointF pointF) {
        if (this.ey != null) {
            this.ey.onSingleTouchUp(this, h(pointF));
        }
    }

    final void c(PointF pointF, PointF pointF2) {
        if (this.ey != null) {
            this.ey.onSwipe(this, h(pointF), h(pointF2));
        }
    }

    final void b(PointF pointF, boolean z, PointF pointF2) {
        if (this.ey != null) {
            this.ey.onSwipeEnd(this, h(pointF), z, pointF2);
        }
    }

    final void a(CartesianSeries<?> cartesianSeries) {
        if (this.eC != null) {
            this.eC.onSeriesSelectionStateChanged(cartesianSeries);
        }
    }

    final void onPointSelectionStateChanged(Series<?> series, int indexInSeries) {
        if (this.eC != null) {
            this.eC.onPointSelectionStateChanged(series, indexInSeries);
        }
    }

    final void onSeriesAnimationFinished(Series<?> series) {
        if (this.eE != null) {
            this.eE.onSeriesAnimationFinished(series);
        }
    }

    final void a(PieDonutSlice pieDonutSlice, PieDonutSeries<?> pieDonutSeries) {
        if (this.eH != null) {
            this.eH.onUpdateSlice(pieDonutSlice, pieDonutSeries);
        }
    }

    final void a(TickMark tickMark, Axis<?, ?> axis) {
        if (this.eI != null) {
            this.eI.onUpdateTickMark(tickMark, axis);
        }
    }

    final boolean a(Canvas canvas, TickMark tickMark, Rect rect, Rect rect2, Axis<?, ?> axis) {
        boolean z = this.eJ != null;
        if (z) {
            this.eJ.onDrawTickMark(canvas, tickMark, rect, rect2, axis);
        }
        return z;
    }

    final boolean a(Canvas canvas, PieDonutSlice pieDonutSlice, Rect rect, PieDonutSeries<?> pieDonutSeries) {
        boolean z = this.eG != null;
        if (z) {
            this.eG.onDrawLabel(canvas, pieDonutSlice, rect, pieDonutSeries);
        }
        return z;
    }

    final boolean a(Canvas canvas, Rect rect, float f, float f2, float f3, Paint paint) {
        boolean z = this.eK != null;
        if (z) {
            this.eK.onDrawCrosshair(this, canvas, rect, f, f2, f3, paint);
        }
        return z;
    }

    private PointF h(PointF pointF) {
        PointF pointF2 = new PointF(pointF.x, pointF.y);
        pointF2.offset((float) (this.eg.left + this.em.aX.left), (float) (this.eg.top + this.em.aX.top));
        return pointF2;
    }

    private void bq() {
        if (!ba.co()) {
            throw new UnsupportedOperationException(getContext().getString(R.string.ChartPremiumOnly));
        }
    }

    private boolean h(Series<?> series) {
        if ((series instanceof BandSeries) || (series instanceof OHLCSeries) || (series instanceof CandlestickSeries)) {
            return true;
        }
        return false;
    }

    private boolean b(i iVar) {
        return iVar.af();
    }

    public final void setOnPieDonutSliceLabelDrawListener(OnPieDonutSliceLabelDrawListener pieDonutSliceLabelListener) {
        this.eG = pieDonutSliceLabelListener;
    }

    public final void setOnPieDonutSliceUpdateListener(OnPieDonutSliceUpdateListener onPieDonutSliceUpdateListener) {
        this.eH = onPieDonutSliceUpdateListener;
    }

    public final void setOnTickMarkUpdateListener(OnTickMarkUpdateListener onTickMarkUpdateListener) {
        int i = 0;
        for (Axis axis : this.eq.bK) {
            axis.aA.sp = true;
        }
        Axis[] axisArr = this.er.bK;
        int length = axisArr.length;
        while (i < length) {
            axisArr[i].aA.sp = true;
            i++;
        }
        this.eI = onTickMarkUpdateListener;
    }

    public final void setOnTickMarkDrawListener(OnTickMarkDrawListener onTickMarkDrawListener) {
        int i = 0;
        for (Axis axis : this.eq.bK) {
            axis.aA.sp = true;
        }
        Axis[] axisArr = this.er.bK;
        int length = axisArr.length;
        while (i < length) {
            axisArr[i].aA.sp = true;
            i++;
        }
        this.eJ = onTickMarkDrawListener;
    }

    public final AnnotationsManager getAnnotationsManager() {
        return this.eN;
    }

    final void br() {
        bq();
    }

    final void b(final Bitmap bitmap) {
        if (this.eD != null) {
            if (ed) {
                this.em.d(bitmap);
            }
            post(new Runnable(this) {
                final /* synthetic */ af eT;

                public void run() {
                    this.eT.eD.onSnapshotDone(this.eT.c(bitmap));
                }
            });
        }
    }

    private Bitmap c(Bitmap bitmap) {
        Bitmap createBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        draw(canvas);
        if (!ed) {
            BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
            bitmapDrawable.setBounds(getPlotAreaRect());
            bitmapDrawable.draw(canvas);
        }
        BitmapDrawable bitmapDrawable2 = new BitmapDrawable(getResources(), createBitmap);
        bitmapDrawable2.setAntiAlias(true);
        return bitmapDrawable2.getBitmap();
    }

    public final void requestSnapshot() {
        this.em.bG();
        redrawChart();
    }

    final fb bs() {
        return this.eh;
    }

    final boolean bt() {
        for (Axis isDoubleTapEnabled : this.eq.bK) {
            if (isDoubleTapEnabled.isDoubleTapEnabled()) {
                return true;
            }
        }
        for (Axis isDoubleTapEnabled2 : this.er.bK) {
            if (isDoubleTapEnabled2.isDoubleTapEnabled()) {
                return true;
            }
        }
        return false;
    }

    final boolean bu() {
        for (CartesianSeries cartesianSeries : bj()) {
            if (cartesianSeries.dX.gc) {
                return true;
            }
        }
        return false;
    }

    public final void setOnCrosshairDrawListener(OnCrosshairDrawListener onCrosshairDrawListener) {
        this.eK = onCrosshairDrawListener;
    }

    public final void setOnTrackingInfoChangedForTooltipListener(OnTrackingInfoChangedForTooltipListener listener) {
        this.eL = listener;
    }

    final boolean a(CartesianSeries<?> cartesianSeries, DataPoint<?, ?> dataPoint, DataPoint<?, ?> dataPoint2, DataPoint<?, ?> dataPoint3) {
        boolean z = this.eL != null;
        if (z) {
            this.eL.onTrackingInfoChanged(this.ev.fO, dataPoint, dataPoint2, dataPoint3);
        }
        return z;
    }

    public final void setOnTrackingInfoChangedForCrosshairListener(OnTrackingInfoChangedForCrosshairListener listener) {
        this.eM = listener;
    }

    final boolean b(CartesianSeries<?> cartesianSeries, DataPoint<?, ?> dataPoint, DataPoint<?, ?> dataPoint2, DataPoint<?, ?> dataPoint3) {
        boolean z = this.eM != null;
        if (z) {
            this.eM.onTrackingInfoChanged(this.ev, dataPoint, dataPoint2, dataPoint3);
        }
        return z;
    }

    public final void setOnCrosshairActivationStateChangedListener(OnCrosshairActivationStateChangedListener listener) {
        this.eF = listener;
    }

    final void bv() {
        bq();
    }

    public final int getLongTouchTimeout() {
        return this.eO.fA;
    }

    public final void setLongTouchTimeout(int timeout) {
        this.eO.fA = timeout;
    }

    final int i(Series<?> series) {
        return this.en.indexOf(series);
    }
}
