package com.shinobicontrols.charts;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

public class Crosshair {
    private af J;
    private float density = 0.0f;
    private CrosshairStyle fD;
    private final Paint fE = new Paint();
    private final du fF = new du();
    private float fG;
    a fH;
    boolean fI = true;
    Mode fJ = Mode.SINGLE_SERIES;
    OutOfRangeBehavior fK = OutOfRangeBehavior.HIDE;
    DrawLinesBehavior fL = DrawLinesBehavior.SERIES_DEFAULT;
    CartesianSeries<?> fM;
    private Data<?, ?> fN;
    Tooltip fO;
    private final ed fP = new ed();
    private final Rect fQ = new Rect();
    private final b fR = new b(this);
    private boolean fS = true;

    public enum DrawLinesBehavior {
        ALWAYS,
        NEVER,
        SERIES_DEFAULT
    }

    public enum Mode {
        SINGLE_SERIES,
        FLOATING
    }

    public enum OutOfRangeBehavior {
        KEEP_AT_EDGE,
        HIDE,
        REMOVE
    }

    enum a {
        SHOWN,
        HIDDEN,
        REMOVED
    }

    private static class b implements a {
        private final Crosshair ev;

        public b(Crosshair crosshair) {
            this.ev = crosshair;
        }

        public void ac() {
            this.ev.e();
        }
    }

    public Mode getMode() {
        return this.fJ;
    }

    public void setMode(Mode mode) {
        this.fJ = mode;
    }

    public OutOfRangeBehavior getOutOfRangeBehavior() {
        return this.fK;
    }

    public void setOutOfRangeBehavior(OutOfRangeBehavior outOfRangeBehavior) {
        this.fK = outOfRangeBehavior;
    }

    public DrawLinesBehavior getDrawLinesBehavior() {
        return this.fL;
    }

    public void setDrawLinesBehavior(DrawLinesBehavior drawLinesBehavior) {
        this.fL = drawLinesBehavior;
    }

    Crosshair() {
        this.fE.setStyle(Style.STROKE);
    }

    void a(af afVar) {
        bT();
        this.J = afVar;
        if (afVar != null) {
            this.density = afVar.getResources().getDisplayMetrics().density;
            this.fG = (float) ca.c(this.density, 2.5f);
            this.fO = new Tooltip(afVar.getContext());
            this.fO.a(this.fR);
            this.fO.c(this.fD);
            bS();
        }
    }

    public CrosshairStyle getStyle() {
        return this.fD;
    }

    public void setStyle(CrosshairStyle style) {
        this.fD = style;
    }

    public float getPixelXValue() {
        if (this.J == null) {
            return 0.0f;
        }
        return ((float) this.fF.x) + ((float) this.J.eg.left);
    }

    public float getPixelYValue() {
        if (this.J == null) {
            return 0.0f;
        }
        return ((float) this.fF.y) + ((float) this.J.eg.top);
    }

    public Tooltip getTooltip() {
        return this.fO;
    }

    public CartesianSeries<?> getTrackedSeries() {
        return this.fM;
    }

    public Data<?, ?> getFocus() {
        return this.fN;
    }

    public void setFocus(Data<?, ?> focus) {
        this.fN = focus;
        if (focus == null || focus.getX() == null || focus.getY() == null) {
            throw new IllegalArgumentException(this.fO.getContext().getString(R.string.CrosshairNullXOrYInFocusPoint));
        }
        bP();
    }

    public boolean isShown() {
        return this.fH == a.SHOWN;
    }

    public void setLineSeriesInterpolationEnabled(boolean enabled) {
        this.fI = enabled;
    }

    public boolean isLineSeriesInterpolationEnabled() {
        return this.fI;
    }

    public boolean isActive() {
        return this.fH == a.SHOWN || this.fH == a.HIDDEN;
    }

    void invalidate() {
        this.J.em.bB();
    }

    void requestLayout() {
        if (this.fO != null) {
            this.fO.requestLayout();
        }
    }

    void forceLayout() {
        if (this.J != null) {
            this.J.em.bD();
        }
        this.fO.forceLayout();
    }

    boolean bN() {
        switch (this.fL) {
            case ALWAYS:
                return true;
            case NEVER:
                return false;
            case SERIES_DEFAULT:
                return this.fM.dX.gd;
            default:
                throw new UnsupportedOperationException("drawLinesBehavior set incorrectly");
        }
    }

    void a(Canvas canvas, Rect rect) {
        this.fQ.set(rect);
        if (!this.J.a(canvas, this.fQ, (float) this.fF.x, (float) this.fF.y, this.fG, this.fE)) {
            ChartUtils.drawCrosshair(this.J, canvas, this.fQ, (float) this.fF.x, (float) this.fF.y, this.fG, this.fE);
        }
    }

    void measure(int crosshairWidthMeasureSpec, int crosshairHeightMeasureSpec) {
        if (this.fO != null) {
            this.fO.measure(crosshairWidthMeasureSpec, crosshairWidthMeasureSpec);
        }
    }

    void layout(int l, int t, int r, int b) {
        if (this.fO != null) {
            int measuredWidth = this.fO.getMeasuredWidth();
            int measuredHeight = this.fO.getMeasuredHeight();
            this.fP.b(this.fO.fF.x, this.fO.fF.y, this.fO.fF.x + ((double) measuredWidth), this.fO.fF.y + ((double) measuredHeight));
            this.fP.q(((double) (-measuredWidth)) / 2.0d, ((double) (-measuredHeight)) / 2.0d);
            a(this.fP, l, r);
            c(this.fP, t, b);
            ca.a(this.fO, this.fP);
        }
    }

    @SuppressLint({"WrongCall"})
    void draw(Canvas canvas, Rect plotAreaRect) {
        a(canvas, plotAreaRect);
    }

    void bO() {
        boolean isActive = isActive();
        this.fH = a.REMOVED;
        this.fM = null;
        this.fN = null;
        this.fO.fa();
        invalidate();
        if (isActive && this.J != null) {
            this.J.bo();
        }
    }

    void c(CartesianSeries<?> cartesianSeries, DataPoint<?, ?> dataPoint, DataPoint<?, ?> dataPoint2, DataPoint<?, ?> dataPoint3) {
        this.fM = cartesianSeries;
        if (!this.J.b(cartesianSeries, dataPoint, dataPoint2, dataPoint3)) {
            setFocus(cartesianSeries.dX.a(dataPoint, dataPoint2, dataPoint3, this.fI));
        }
        d(cartesianSeries, dataPoint, dataPoint2, dataPoint3);
        bQ();
        invalidate();
        requestLayout();
    }

    private void bP() {
        if (this.fM != null) {
            if (this.fN == null) {
                throw new IllegalStateException(this.J != null ? this.J.getContext().getString(R.string.CrosshairNullFocus) : "Unable to determine Crosshair position: must have non-null focus. Have you called setFocus on the crosshair?");
            }
            this.fF.x = a(this.fN.getX(), this.fM.getXAxis(), this.fM);
            this.fF.y = a(this.fN.getY(), this.fM.getYAxis(), this.fM);
        }
    }

    static double a(Object obj, Axis<?, ?> axis, CartesianSeries<?> cartesianSeries) {
        return axis.a(axis.translatePoint(obj), (CartesianSeries) cartesianSeries);
    }

    private void d(CartesianSeries<?> cartesianSeries, DataPoint<?, ?> dataPoint, DataPoint<?, ?> dataPoint2, DataPoint<?, ?> dataPoint3) {
        if (this.fO != null) {
            this.fO.l(cartesianSeries);
            if (!this.J.a((CartesianSeries) cartesianSeries, (DataPoint) dataPoint, (DataPoint) dataPoint2, (DataPoint) dataPoint3)) {
                this.fO.c(cartesianSeries, dataPoint, dataPoint2, dataPoint3);
            }
        }
    }

    void e() {
        if (this.fD != null) {
            this.fE.setColor(this.fD.getLineColor());
            this.fE.setStrokeWidth((float) ca.c(this.density, this.fD.getLineWidth()));
            if (this.fO != null) {
                this.fO.c(this.fD);
            }
        }
    }

    private void bQ() {
        if (bR()) {
            bU();
            return;
        }
        switch (this.fK) {
            case HIDE:
                bW();
                return;
            case REMOVE:
                bO();
                return;
            default:
                return;
        }
    }

    private boolean bR() {
        Rect rect = this.J.em.aX;
        return !rect.isEmpty() && this.fF.x >= ((double) rect.left) && this.fF.x <= ((double) rect.right) && this.fF.y >= ((double) rect.top) && this.fF.y <= ((double) rect.bottom);
    }

    private void bS() {
        this.J.em.a(this.fO);
    }

    private void bT() {
        if (this.J != null) {
            this.J.em.b(this.fO);
        }
    }

    private void bU() {
        boolean isActive = isActive();
        this.fH = a.SHOWN;
        bV();
        if (!isActive && this.J != null) {
            this.J.bo();
        }
    }

    private void bV() {
        if (this.fS) {
            this.fO.fb();
        } else {
            this.fO.fc();
        }
    }

    private void bW() {
        boolean isActive = isActive();
        this.fH = a.HIDDEN;
        this.fO.fc();
        if (!isActive && this.J != null) {
            this.J.bo();
        }
    }

    private void a(ed edVar, int i, int i2) {
        if (edVar.ed() > ((double) (i2 - i))) {
            b(edVar, i, i2);
            return;
        }
        a(edVar, i);
        b(edVar, i2);
    }

    private void b(ed edVar, int i, int i2) {
        edVar.r(((double) i) - ((edVar.ed() - ((double) (i2 - i))) / 2.0d), edVar.od);
    }

    private void a(ed edVar, int i) {
        if (edVar.oc < ((double) i)) {
            edVar.r((double) i, edVar.od);
        }
    }

    private void b(ed edVar, int i) {
        if (edVar.oe > ((double) i)) {
            edVar.r(((double) i) - edVar.ed(), edVar.od);
        }
    }

    private void c(ed edVar, int i, int i2) {
        if (edVar.ee() > ((double) (i2 - i))) {
            d(edVar, i, i2);
            return;
        }
        c(edVar, i);
        d(edVar, i2);
    }

    private void d(ed edVar, int i, int i2) {
        edVar.r(edVar.oc, ((double) i) - ((edVar.ee() - ((double) (i2 - i))) / 2.0d));
    }

    private void c(ed edVar, int i) {
        if (edVar.od < ((double) i)) {
            edVar.r(edVar.oc, (double) i);
        }
    }

    private void d(ed edVar, int i) {
        if (edVar.of > ((double) i)) {
            edVar.q(0.0d, -(edVar.of - ((double) i)));
            edVar.r(edVar.oc, ((double) i) - edVar.ee());
        }
    }

    void bX() {
        bP();
        if (this.fO != null) {
            this.fO.bX();
        }
        bQ();
    }

    void j(Series<?> series) {
        if (this.fM == series) {
            bO();
        }
    }

    void k(Series<?> series) {
        if (this.fM == series) {
            bO();
        }
    }

    public boolean isTooltipEnabled() {
        return this.fS;
    }

    public void enableTooltip(boolean tooltipEnabled) {
        this.fS = tooltipEnabled;
    }
}
