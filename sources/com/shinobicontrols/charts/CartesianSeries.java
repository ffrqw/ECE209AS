package com.shinobicontrols.charts;

import com.shinobicontrols.charts.Series.Orientation;

public abstract class CartesianSeries<T extends SeriesStyle> extends Series<T> {
    Object dK = null;
    Integer dL = null;
    boolean dM;
    boolean dN = false;
    boolean dO = false;
    boolean dP = false;
    boolean dQ = false;
    final Orientation dR;
    private int dS;
    private CartesianSeries<?> dT;
    em dU;
    private NumberRange dV = new NumberRange();
    private NumberRange dW = new NumberRange();
    ak dX;
    boolean da = false;

    enum a {
        CROW_FLIES,
        HORIZONTAL,
        VERTICAL
    }

    abstract ak am();

    CartesianSeries(Orientation orientation, ao dataLoadHelperFactory) {
        super(dataLoadHelperFactory);
        this.dR = orientation;
        this.dX = am();
    }

    void a(int i, int i2, CartesianSeries<?> cartesianSeries) {
        this.dS = i;
        this.dT = cartesianSeries;
    }

    int aI() {
        return this.dS;
    }

    CartesianSeries<?> aJ() {
        return this.dT;
    }

    public Object getBaseline() {
        return this.dK;
    }

    public void setBaseline(Object baseline) {
        this.dK = baseline;
        this.ot.av();
        Axis yAxis = this.dR == Orientation.HORIZONTAL ? getYAxis() : getXAxis();
        if (yAxis != null) {
            yAxis.aF.dH();
        }
    }

    void aK() {
        super.aK();
        this.ot.av();
    }

    void a(af afVar) {
        super.a(afVar);
        this.ot.av();
    }

    public Integer getStackId() {
        return this.dL;
    }

    public void setStackId(Integer stackId) {
        synchronized (ah.lock) {
            this.dL = stackId;
            this.ot.av();
            if (this.oy != null) {
                this.oy.ew();
            }
            fireUpdateHandler();
        }
    }

    boolean aL() {
        return this.dT != null;
    }

    final NumberRange aM() {
        return this.dV;
    }

    final NumberRange aN() {
        return this.dW;
    }

    NumberRange g(Axis<?, ?> axis) {
        return (NumberRange) (axis.h() ? this.dV.bZ() : this.dW.bZ());
    }

    final NumberRange h(Axis<?, ?> axis) {
        return (NumberRange) (axis.h() ? this.dV.bZ() : this.dW.bZ());
    }

    public boolean isSelected() {
        return this.dM;
    }

    public void setSelected(boolean selected) {
        if (this.dM != selected) {
            synchronized (ah.lock) {
                this.dM = selected;
                if (this.db != null) {
                    for (InternalDataPoint internalDataPoint : this.db.je) {
                        internalDataPoint.iU = selected;
                    }
                }
            }
            if (this.J != null) {
                this.ot.av();
                this.J.a(this);
            }
        }
    }

    void a(a aVar, du duVar, boolean z, b bVar) {
        ej.b(this, aVar, duVar, z, a(bVar));
    }

    InternalDataPoint[] a(b bVar) {
        return this.db.je;
    }

    a aq() {
        return a.CROW_FLIES;
    }

    a ar() {
        return a.HORIZONTAL;
    }

    void a(a aVar, du duVar) {
        aVar.a(null);
    }

    public boolean isCrosshairEnabled() {
        return this.dX.gc;
    }

    public void setCrosshairEnabled(boolean crosshairEnabled) {
        this.dX.gc = crosshairEnabled;
    }

    public Orientation getOrientation() {
        return this.dR;
    }

    double as() {
        if (this.oz.pa != null) {
            return (getXAxis().ai.nv * (1.0d - ((double) this.oz.pa.floatValue()))) + (getXAxis().ai.nw * ((double) this.oz.pa.floatValue()));
        }
        return 0.5d * (getXAxis().ai.nv + getXAxis().ai.nw);
    }

    double al() {
        if (this.oz.pb != null) {
            return (getYAxis().ai.nv * (1.0d - ((double) this.oz.pb.floatValue()))) + (getYAxis().ai.nw * ((double) this.oz.pb.floatValue()));
        }
        return 0.5d * (getYAxis().ai.nv + getYAxis().ai.nw);
    }

    boolean aO() {
        return false;
    }

    boolean aP() {
        return this.oy.j(this);
    }
}
