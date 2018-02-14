package com.shinobicontrols.charts;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import com.shinobicontrols.charts.DataAdapter.OnDataChangedListener;
import com.shinobicontrols.charts.InternalDataSeriesUpdater.PostUpdateCallback;
import com.shinobicontrols.charts.InternalDataSeriesUpdater.PreUpdateCallback;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Series<T extends SeriesStyle> {
    af J;
    private bh ab;
    private String af;
    final bt db = new bt();
    private final bm iN;
    private final ar jk;
    boolean nE;
    SeriesAnimation oA;
    SeriesAnimation oB;
    boolean oC;
    private boolean oD = true;
    final ay oE;
    private final InternalDataSeriesUpdater oF;
    private final PreUpdateCallback oG;
    private final PostUpdateCallback oH;
    final am oI;
    final List<bu> oJ = new ArrayList();
    final eq oK;
    final bs oL;
    final aq oM;
    DataAdapter<?, ?> oo;
    private final Map<Axis<?, ?>, bh> op = new HashMap();
    private final OnDataChangedListener oq = new c(this);
    private final b or = new b();
    private final d os = new d(this, this);
    er ot;
    T ou;
    T ov;
    private bh ow;
    SelectionMode ox = SelectionMode.NONE;
    en oy;
    SeriesAnimation oz;
    private final bg u = new bg();

    public enum Orientation {
        HORIZONTAL(0),
        VERTICAL(1);
        
        private int oQ;

        private Orientation(int glOrientation) {
            this.oQ = glOrientation;
        }

        final int eo() {
            return this.oQ;
        }
    }

    public enum SelectionMode {
        NONE,
        SERIES,
        POINT_SINGLE,
        POINT_MULTIPLE
    }

    static class a {
        private final Series<?> cZ;
        private double oN = Double.MAX_VALUE;
        private InternalDataPoint oO = null;
        private du oP = null;

        public a(Series<?> series) {
            this.cZ = series;
        }

        public double ek() {
            return this.oN;
        }

        public void t(double d) {
            this.oN = d;
        }

        public InternalDataPoint el() {
            return this.oO;
        }

        public void c(InternalDataPoint internalDataPoint) {
            this.oO = internalDataPoint;
        }

        public Series<?> em() {
            return this.cZ;
        }

        public du en() {
            return this.oP;
        }

        public void a(du duVar) {
            this.oP = duVar;
        }

        public void invalidate() {
            this.oO = null;
        }

        public boolean a(a aVar) {
            return b(this) && (aVar == null || this.oN < aVar.ek());
        }

        public static boolean b(a aVar) {
            if (aVar == null || aVar.oO == null) {
                return false;
            }
            return true;
        }
    }

    private class b implements a {
        final /* synthetic */ Series oS;

        private b(Series series) {
            this.oS = series;
        }

        public void ac() {
            this.oS.ac();
        }
    }

    private static class c implements OnDataChangedListener {
        private final Series<?> cZ;

        c(Series<?> series) {
            this.cZ = series;
        }

        public void onDataChanged() {
            this.cZ.onDataChanged();
        }
    }

    private class d implements a {
        private final Series<?> cZ;
        final /* synthetic */ Series oS;

        public d(Series series, Series<?> series2) {
            this.oS = series;
            this.cZ = series2;
        }

        public final void ei() {
            this.cZ.ei();
        }
    }

    abstract double al();

    abstract T an();

    abstract double as();

    abstract T b(fb fbVar, int i, boolean z);

    abstract Drawable c(float f);

    Series(ao dataLoadHelperFactory) {
        this.iN = dataLoadHelperFactory.aA();
        this.jk = dataLoadHelperFactory.aB();
        this.oF = dataLoadHelperFactory.at();
        this.oG = dataLoadHelperFactory.aC();
        this.oH = dataLoadHelperFactory.aD();
        this.oI = dataLoadHelperFactory.aG();
        this.oJ.addAll(dataLoadHelperFactory.au());
        this.oK = dataLoadHelperFactory.aH();
        this.oL = dataLoadHelperFactory.aF();
        this.oM = dataLoadHelperFactory.aE();
        this.oE = ay.m(this);
    }

    public final DataAdapter<?, ?> getDataAdapter() {
        return this.oo;
    }

    public final void setDataAdapter(DataAdapter<?, ?> dataAdapter) {
        if (dataAdapter != null) {
            if (this.oo != null) {
                this.oo.removeOnDataChangedListener(this.oq);
            }
            this.oo = dataAdapter;
            this.oo.addOnDataChangedListener(this.oq);
            eg();
            fireUpdateHandler();
        } else if (this.J == null) {
            throw new IllegalArgumentException("Trying to set a null DataAdapter, DataAdapter cannot be null");
        } else {
            throw new IllegalArgumentException(this.J.getContext().getString(R.string.SeriesNullDataAdapter));
        }
    }

    public boolean isSelected() {
        return false;
    }

    public void setSelected(boolean selected) {
    }

    public boolean isPointSelected(int index) {
        if (index < this.db.je.length && index >= 0) {
            return this.db.je[index].iU;
        }
        ev.f(this.J != null ? this.J.getContext().getString(R.string.SeriesDataPointOutOfRange) : "Attempting to access data point out of range");
        return false;
    }

    public void setPointSelected(boolean selected, int index) {
        if (index >= this.db.je.length || index < 0) {
            ev.f(this.J != null ? this.J.getContext().getString(R.string.SeriesDataPointOutOfRange) : "Attempting to access data point out of range");
        } else {
            b(selected, index);
        }
    }

    void b(boolean z, int i) {
        a(this.db.je[i], z);
    }

    boolean a(InternalDataPoint internalDataPoint, boolean z) {
        boolean z2 = z != internalDataPoint.iU;
        if (z2) {
            internalDataPoint.iU = z;
            if (this.J != null) {
                this.J.onPointSelectionStateChanged(this, internalDataPoint.iV);
                this.ot.av();
            }
        }
        return z2;
    }

    public T getStyle() {
        return this.ou;
    }

    public final void setStyle(T style) {
        if (style != null) {
            b(style);
        } else if (this.J == null) {
            throw new IllegalArgumentException("Styles may not be null");
        } else {
            throw new IllegalArgumentException(this.J.getContext().getString(R.string.SeriesStyleIsNull));
        }
    }

    private void b(T t) {
        synchronized (ah.lock) {
            if (this.ou != null) {
                this.ab.cP();
            }
            this.ou = t;
            if (this.ou != null) {
                this.ab = this.ou.a(this.or);
                ac();
            }
        }
    }

    public T getSelectedStyle() {
        return this.ov;
    }

    public final void setSelectedStyle(T style) {
        if (style != null) {
            c((SeriesStyle) style);
        } else if (this.J == null) {
            throw new IllegalArgumentException("Styles may not be null");
        } else {
            throw new IllegalArgumentException(this.J.getContext().getString(R.string.SeriesStyleIsNull));
        }
    }

    private void c(T t) {
        synchronized (ah.lock) {
            if (this.ov != null) {
                this.ow.cP();
            }
            this.ov = t;
            if (this.ov != null) {
                this.ow = this.ov.a(this.or);
                ac();
            }
        }
    }

    void a(af afVar) {
        this.J = afVar;
        eg();
        fireUpdateHandler();
    }

    public final ShinobiChart getChart() {
        return this.J;
    }

    void j(fb fbVar, int i, boolean z) {
        if (fbVar != null) {
            SeriesStyle b = b(fbVar, i, false);
            SeriesStyle b2 = b(fbVar, i, true);
            if (z || this.ou == null) {
                this.ou = an();
            }
            if (z || this.ov == null) {
                this.ov = an();
            }
            this.ou.a(b);
            this.ov.a(b2);
            b(this.ou);
            c(this.ov);
        }
    }

    void aK() {
        eg();
        fireUpdateHandler();
    }

    void eg() {
        if (this.iN.n(this)) {
            this.jk.l(this);
            synchronized (ah.lock) {
                this.oF.a(this, this.oG, this.oH);
            }
            this.oK.update(this);
            this.ot.av();
            this.J.redrawChart();
        }
    }

    public final Axis<?, ?> getXAxis() {
        if (this.J != null) {
            return this.J.getXAxisForSeries(this);
        }
        return null;
    }

    public final Axis<?, ?> getYAxis() {
        if (this.J != null) {
            return this.J.getYAxisForSeries(this);
        }
        return null;
    }

    void ac() {
        this.ot.av();
    }

    void onDataChanged() {
        eg();
        fireUpdateHandler();
        if (this.J != null && this.J.ev != null) {
            this.J.ev.k(this);
        }
    }

    final void fireUpdateHandler() {
        this.u.a(new fg());
    }

    bh a(a aVar) {
        return this.u.a(fg.A, (a) aVar);
    }

    void f(Axis<?, ?> axis) {
    }

    void a(en enVar) {
        this.oy = enVar;
    }

    public SelectionMode getSelectionMode() {
        return this.ox;
    }

    public void setSelectionMode(SelectionMode selectionMode) {
        this.ox = selectionMode;
    }

    public boolean isShownInLegend() {
        return this.oD;
    }

    public void setShownInLegend(boolean shownInLegend) {
        this.oD = shownInLegend;
    }

    public String getTitle() {
        return this.af;
    }

    public void setTitle(String title) {
        this.af = title;
    }

    void b(Canvas canvas, Rect rect) {
    }

    boolean eh() {
        return (this.oo == null || this.oo.isEmpty()) ? false : true;
    }

    public boolean isHidden() {
        return this.oC;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setHidden(boolean r4) {
        /*
        r3 = this;
        r1 = com.shinobicontrols.charts.ah.lock;
        monitor-enter(r1);
        r0 = r3.nE;	 Catch:{ all -> 0x0022 }
        if (r0 == 0) goto L_0x003d;
    L_0x0007:
        r0 = r3.J;	 Catch:{ all -> 0x0022 }
        if (r0 == 0) goto L_0x003d;
    L_0x000b:
        if (r4 == 0) goto L_0x0025;
    L_0x000d:
        r0 = r3.oC;	 Catch:{ all -> 0x0022 }
        if (r0 != 0) goto L_0x0017;
    L_0x0011:
        r0 = r3.oz;	 Catch:{ all -> 0x0022 }
        r2 = r3.oB;	 Catch:{ all -> 0x0022 }
        if (r0 != r2) goto L_0x0019;
    L_0x0017:
        monitor-exit(r1);	 Catch:{ all -> 0x0022 }
    L_0x0018:
        return;
    L_0x0019:
        r0 = r3.J;	 Catch:{ all -> 0x0022 }
        r0 = r0.ex;	 Catch:{ all -> 0x0022 }
        r0.u(r3);	 Catch:{ all -> 0x0022 }
    L_0x0020:
        monitor-exit(r1);	 Catch:{ all -> 0x0022 }
        goto L_0x0018;
    L_0x0022:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0022 }
        throw r0;
    L_0x0025:
        r0 = r3.oC;	 Catch:{ all -> 0x0022 }
        if (r0 != 0) goto L_0x002d;
    L_0x0029:
        r0 = r3.oz;	 Catch:{ all -> 0x0022 }
        if (r0 == 0) goto L_0x0033;
    L_0x002d:
        r0 = r3.oz;	 Catch:{ all -> 0x0022 }
        r2 = r3.oA;	 Catch:{ all -> 0x0022 }
        if (r0 != r2) goto L_0x0035;
    L_0x0033:
        monitor-exit(r1);	 Catch:{ all -> 0x0022 }
        goto L_0x0018;
    L_0x0035:
        r0 = r3.J;	 Catch:{ all -> 0x0022 }
        r0 = r0.ex;	 Catch:{ all -> 0x0022 }
        r0.v(r3);	 Catch:{ all -> 0x0022 }
        goto L_0x0020;
    L_0x003d:
        r3.j(r4);	 Catch:{ all -> 0x0022 }
        goto L_0x0020;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.shinobicontrols.charts.Series.setHidden(boolean):void");
    }

    void j(boolean z) {
        this.oC = z;
        if (this.J != null) {
            this.J.em.invalidate();
            this.ot.av();
            this.J.em.bC();
        }
        fireUpdateHandler();
    }

    public boolean isAnimationEnabled() {
        return this.nE;
    }

    public void enableAnimation(boolean enabled) {
        this.nE = enabled;
    }

    public SeriesAnimation getEntryAnimation() {
        return this.oA;
    }

    public void setEntryAnimation(SeriesAnimation seriesAnimation) {
        if (seriesAnimation == null) {
            throw new IllegalArgumentException("Series entry animations may not be null");
        }
        this.oA = seriesAnimation;
    }

    public SeriesAnimation getExitAnimation() {
        return this.oB;
    }

    public void setExitAnimation(SeriesAnimation seriesAnimation) {
        if (seriesAnimation == null) {
            throw new IllegalArgumentException("Series exit animations may not be null");
        }
        this.oB = seriesAnimation;
    }

    public boolean isAnimating() {
        return this.oz != null;
    }

    void o(Axis<?, ?> axis) {
        this.op.put(axis, axis.a(this.os));
    }

    void p(Axis<?, ?> axis) {
        bh bhVar = (bh) this.op.get(axis);
        if (bhVar != null) {
            bhVar.cP();
            this.op.remove(axis);
        }
    }

    private void ei() {
        eg();
    }

    int ej() {
        if (this.J == null) {
            return -1;
        }
        return this.J.i(this);
    }
}
