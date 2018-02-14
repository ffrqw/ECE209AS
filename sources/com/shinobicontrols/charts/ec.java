package com.shinobicontrols.charts;

import android.graphics.PointF;
import com.shinobicontrols.charts.Axis.DoubleTapBehavior;
import com.shinobicontrols.charts.Axis.MotionState;
import com.shinobicontrols.charts.Axis.Orientation;
import com.shinobicontrols.charts.ShinobiChart.OnGestureListener;
import java.util.Locale;

class ec implements OnGestureListener {
    private final Axis<?, ?> aS;
    private final b mh = new b();
    private final b nA = new b(this);
    boolean nB = true;
    boolean nC = false;
    boolean nD = true;
    boolean nE = true;
    private double nF = 1.0d;
    boolean nG = false;
    boolean nH = false;
    boolean nI = false;
    boolean nJ = false;
    float nK = 1.2f;
    float nL = 0.75f;
    boolean nM = true;
    MotionState nN = MotionState.STOPPED;
    private final NumberRange nO = new NumberRange();
    private final NumberRange nP = new NumberRange();
    private final EaseOutAnimationCurve nQ = new EaseOutAnimationCurve();
    private final cx nR = new cx();
    private final c nx = new c(this);
    private final e ny = new e(this);
    private final a nz = new a(this);

    private static abstract class d implements a {
        protected final ec aF;
        protected double nS;
        protected double nT;
        protected double nU;
        protected double nV;
        protected double nW;
        protected double nX;
        protected boolean nY;

        protected abstract void r(double d);

        d(ec ecVar) {
            this.aF = ecVar;
        }

        public void b(Animation animation) {
            if (animation instanceof h) {
                r((double) ((h) animation).ad());
            }
        }

        protected void o(double d, double d2) {
            this.nS = d;
            this.nT = d2;
            this.nW = this.nS;
            this.nX = this.nT;
        }

        protected void a(double d, double d2, boolean z) {
            this.nU = d;
            this.nV = d2;
            this.nY = z;
        }

        protected void s(double d) {
            this.nW = this.nS + ((this.nU - this.nS) * d);
            this.nX = this.nT + ((this.nV - this.nT) * d);
            if (Double.isNaN(this.nW) || Double.isInfinite(this.nW)) {
                this.nW = this.aF.aS.ai.nv;
            }
            if (Double.isNaN(this.nX) || Double.isInfinite(this.nX)) {
                this.nX = this.aF.aS.ai.nw;
            }
            if (this.nX < this.nW) {
                this.nW = this.nU;
                this.nX = this.nV;
            }
        }
    }

    private static class a extends d implements a {
        a(ec ecVar) {
            super(ecVar);
        }

        protected void r(double d) {
            s(d);
            this.aF.j(this.nW, this.nX);
        }

        public void onAnimationStart() {
            this.aF.dO();
        }

        public void onAnimationEnd() {
            this.aF.dP();
        }

        public void c() {
            this.aF.dQ();
        }
    }

    private static class b extends d {
        b(ec ecVar) {
            super(ecVar);
        }

        protected void r(double d) {
            s(d);
            this.aF.k(this.nW, this.nX);
        }

        public void onAnimationStart() {
            this.aF.dR();
        }

        public void onAnimationEnd() {
            this.aF.dS();
        }

        public void c() {
            this.aF.dT();
        }
    }

    private static class c extends d {
        c(ec ecVar) {
            super(ecVar);
        }

        protected void r(double d) {
            s(d);
            this.aF.h(this.nW, this.nX);
        }

        public void onAnimationStart() {
        }

        public void onAnimationEnd() {
            this.aF.dK();
        }

        public void c() {
            this.aF.dL();
        }
    }

    private static class e extends d {
        private double nZ;
        private double oa;
        private double ob;

        e(ec ecVar) {
            super(ecVar);
        }

        protected void r(double d) {
            this.aF.i(this.oa - (this.ob * d), this.nZ);
        }

        public void onAnimationStart() {
        }

        public void onAnimationEnd() {
            this.aF.dM();
        }

        public void c() {
            this.aF.dN();
        }

        void p(double d, double d2) {
            this.nZ = d;
            this.oa = d2;
            this.ob = this.oa - 1.0d;
        }
    }

    public ec(Axis<?, ?> axis) {
        this.aS = axis;
    }

    public void onDoubleTapDown(ShinobiChart sender, PointF position) {
    }

    public void onDoubleTapUp(ShinobiChart sender, PointF position) {
        if (!this.aS.J.bn() && this.nM) {
            double d;
            if (this.aS.getDoubleTapBehavior() == DoubleTapBehavior.RESET_TO_DEFAULT_RANGE && (this.aS.isGesturePanningEnabled() || this.aS.isGestureZoomingEnabled())) {
                double d2;
                if (this.aS.al != null) {
                    d2 = this.aS.al.nv;
                    d = this.aS.al.nw;
                } else {
                    d2 = this.aS.n();
                    d = this.aS.l();
                }
                this.mh.cancel();
                this.nz.a(d2, d, this.nE);
                a(this.nz);
            } else if (this.aS.isGestureZoomingEnabled()) {
                double dF = this.aS.ai.dF() / 4.0d;
                if (this.aS.P.equals(Orientation.HORIZONTAL)) {
                    d = this.aS.e((double) position.x);
                } else {
                    d = this.aS.e((double) position.y);
                }
                this.mh.cancel();
                this.nz.a(d - dF, d + dF, this.nE);
                a(this.nz);
            }
        }
    }

    public void onLongTouchDown(ShinobiChart sender, PointF position) {
    }

    public void onLongTouchUp(ShinobiChart sender, PointF position) {
    }

    public void onSingleTouchDown(ShinobiChart sender, PointF position) {
        this.mh.cancel();
    }

    public void onSingleTouchUp(ShinobiChart sender, PointF position) {
    }

    public void onSecondTouchDown(ShinobiChart sender, PointF position, PointF position2) {
    }

    public void onSecondTouchUp(ShinobiChart sender, PointF position, PointF position2) {
    }

    public void onSwipe(ShinobiChart sender, PointF startPosition, PointF endPosition) {
        if (this.nG && !Range.h(this.aS.ai)) {
            this.mh.cancel();
            VectorF f = VectorF.f(startPosition, endPosition);
            double d = this.aS.d((double) f(f.x, f.y));
            if (this.aS.h()) {
                d = -d;
            }
            m(d);
        }
    }

    public void onSwipeEnd(ShinobiChart sender, PointF position, boolean flinging, PointF velocity) {
        if (this.nG && !Range.h(this.aS.ai)) {
            if (flinging) {
                i(velocity);
            } else {
                dJ();
            }
        }
    }

    public void onPinch(ShinobiChart sender, PointF startFocus, PointF endFocus, PointF scaleFactor) {
        if (this.nH && !Range.h(this.aS.ai)) {
            this.mh.cancel();
            a(this.aS.e((double) f(startFocus.x, startFocus.y)), this.aS.e((double) f(endFocus.x, endFocus.y)), f(scaleFactor.x, scaleFactor.y));
        }
    }

    public void onPinchEnd(ShinobiChart sender, PointF focus, boolean flinging, PointF scaleFactor) {
        if (this.nH && !Range.h(this.aS.ai)) {
            if (flinging) {
                e(scaleFactor, focus);
            } else {
                g(flinging);
            }
        }
    }

    public void dH() {
        if (!Range.h(this.aS.ai) && !dU()) {
            if (Range.h(this.aS.aj) && Range.h(this.aS.al)) {
                ev.f(this.aS.J != null ? this.aS.J.getContext().getString(R.string.RangeManagerUnableToSetRange) : "Unable to set axis range as axis has no associated data and no default range set");
                return;
            }
            this.nO.c(this.aS.ai.nv, this.aS.ai.nw);
            this.nO.f(dV(), dX());
            this.nz.a(this.nO.nv, this.nO.nw, this.nE);
            if (a(this.nz)) {
                a(MotionState.STOPPED);
            }
        }
    }

    public boolean g(double d, double d2) {
        return b(d, d2, this.nE, this.nD);
    }

    public boolean b(double d, double d2, boolean z, boolean z2) {
        if (l(d, d2)) {
            boolean z3;
            this.mh.cancel();
            this.nO.c(d, d2);
            this.nP.c(d, d2);
            if (dU()) {
                z3 = true;
            } else if (Range.h(this.aS.aj) && Range.h(this.aS.al)) {
                ev.f(this.aS.J != null ? this.aS.J.getContext().getString(R.string.RangeManagerUnableToSetRange) : "Unable to set axis range as axis has no associated data and no default range set");
                return false;
            } else {
                if (d < dV() || d2 > dX()) {
                    z3 = false;
                } else {
                    z3 = true;
                }
                this.nO.f(dV(), dX());
                this.nP.f(h(z2), i(z2));
            }
            this.nz.a(this.nP.nv, this.nP.nw, z);
            this.nA.a(this.nO.nv, this.nO.nw, true);
            if (a(this.nz) && a(this.nA)) {
                a(MotionState.STOPPED);
            }
            return z3;
        }
        String string;
        if (this.aS.J != null) {
            string = this.aS.J.getContext().getString(R.string.RangeManagerInvalidRangeRequested);
        } else {
            string = "Invalid axis range requested";
        }
        ev.f(string);
        return false;
    }

    public boolean b(double d, boolean z, boolean z2) {
        this.mh.cancel();
        double n = n(d);
        this.nP.c(this.aS.ai.nv + n, n + this.aS.ai.nw);
        this.nP.f(h(z2), i(z2));
        this.nx.a(this.nP.nv, this.nP.nw, z);
        return a(this.nx);
    }

    public boolean c(double d, double d2, boolean z, boolean z2) {
        if (this.aS.aj == null) {
            this.nF = d;
            return true;
        }
        this.mh.cancel();
        n(d, d2);
        this.nP.c(this.nO.nv, this.nO.nw);
        this.nP.f(h(z2), i(z2));
        this.ny.a(this.nP.nv, this.nP.nw, z);
        return a(this.ny);
    }

    public double getZoomLevel() {
        if (Range.h(this.aS.ai)) {
            return this.nF;
        }
        if (Range.h(this.aS.al) && Range.h(this.aS.aj)) {
            return this.nF;
        }
        return dI() / this.aS.ai.dF();
    }

    private double dI() {
        if (this.aS.al != null) {
            return this.aS.al.dF();
        }
        return this.aS.l() - this.aS.n();
    }

    private void m(double d) {
        a(MotionState.GESTURE);
        this.aS.a(d, false, this.nD);
    }

    private void dJ() {
        ec();
    }

    private void i(PointF pointF) {
        if (!this.aS.isMomentumPanningEnabled() || ea() || eb()) {
            ec();
        } else {
            p(this.aS.d(this.aS.P == Orientation.HORIZONTAL ? (double) (-pointF.x) : (double) pointF.y));
        }
    }

    private void h(double d, double d2) {
        this.nO.c(d, d2);
        boolean f = this.nO.f(dV(), dX());
        this.aS.a(this.nO.nv, this.nO.nw);
        if (f) {
            this.mh.cancel();
        } else {
            a(MotionState.MOMENTUM);
        }
    }

    private void dK() {
        a(MotionState.STOPPED);
    }

    private void dL() {
        a(MotionState.STOPPED);
    }

    private boolean a(double d, double d2, float f) {
        boolean a;
        boolean a2;
        if (f > 0.0f) {
            a(MotionState.GESTURE);
            a2 = this.aS.a(this.aS.getZoomLevel() * ((double) f), d, false, this.nD);
        } else {
            a2 = false;
        }
        if (this.nG) {
            a = this.aS.a(d - d2, false, this.nD);
        } else {
            a = false;
        }
        if (a || r0) {
            return true;
        }
        return false;
    }

    private void g(boolean z) {
        ec();
    }

    private void e(PointF pointF, PointF pointF2) {
        if (!this.nJ || ea() || eb()) {
            ec();
            return;
        }
        m(this.aS.e((double) f(pointF2.x, pointF2.y)), (double) f(pointF.x, pointF.y));
    }

    private void i(double d, double d2) {
        n(getZoomLevel() * d, d2);
        boolean f = this.nO.f(dV(), dX());
        this.aS.a(this.nO.nv, this.nO.nw);
        if (f) {
            this.mh.cancel();
        } else {
            a(MotionState.MOMENTUM);
        }
    }

    private void dM() {
        a(MotionState.STOPPED);
    }

    private void dN() {
        a(MotionState.STOPPED);
    }

    private void j(double d, double d2) {
        this.aS.a(d, d2);
    }

    private void dO() {
        a(MotionState.ANIMATING);
    }

    private void dP() {
        ec();
    }

    private void dQ() {
        a(MotionState.STOPPED);
    }

    private void k(double d, double d2) {
        this.aS.a(d, d2);
    }

    private void dR() {
        a(MotionState.BOUNCING);
    }

    private void dS() {
        a(MotionState.STOPPED);
    }

    private void dT() {
        a(MotionState.STOPPED);
    }

    private double n(double d) {
        if ((!ea() || d >= 0.0d) && (!eb() || d <= 0.0d)) {
            return d;
        }
        return d / 3.0d;
    }

    private float f(float f, float f2) {
        return this.aS.P == Orientation.HORIZONTAL ? f : f2;
    }

    private boolean a(d dVar) {
        if (!l(dVar.nU, dVar.nV)) {
            return true;
        }
        if (this.aS.ai.nv == dVar.nU && this.aS.ai.nw == dVar.nV) {
            return true;
        }
        if (dVar.nY) {
            a(0.95f, this.nQ, dVar);
            return false;
        }
        this.aS.a(dVar.nU, dVar.nV);
        return true;
    }

    private void a(float f, AnimationCurve animationCurve, d dVar) {
        dVar.o(this.aS.ai.nv, this.aS.ai.nw);
        Animation hVar = new h();
        hVar.setDuration(f);
        hVar.a(animationCurve);
        this.mh.a(hVar);
        this.mh.a((a) dVar);
        this.mh.start();
    }

    private boolean l(double d, double d2) {
        if (o(d) || o(d2)) {
            String string;
            if (this.aS.J != null) {
                string = this.aS.J.getContext().getString(R.string.RangeManagerLimitsCannotBeNaN);
            } else {
                string = "Range minimum and maximum cannot be infinite or NaNs";
            }
            ev.f(string);
            return false;
        } else if (d2 > d) {
            return true;
        } else {
            ev.f(String.format(Locale.getDefault(), this.aS.J != null ? this.aS.J.getContext().getString(R.string.RangeManagerNonPositiveSpan) : "Ignoring range with %f span", new Object[]{Double.valueOf(d2 - d)}));
            return false;
        }
    }

    private boolean dU() {
        return this.nB && this.nC;
    }

    private double h(boolean z) {
        double dV = dV();
        if (o(dV) || !z) {
            return dV;
        }
        return dV - dZ();
    }

    double dV() {
        if (dU()) {
            return Double.NEGATIVE_INFINITY;
        }
        if (this.nB || this.aS.al == null) {
            return dW();
        }
        return this.aS.al.nv;
    }

    double dW() {
        if (this.aS.al != null) {
            return Math.min(this.aS.aj.nv, this.aS.al.nv);
        }
        return this.aS.n();
    }

    double dX() {
        if (dU()) {
            return Double.POSITIVE_INFINITY;
        }
        if (this.nB || this.aS.al == null) {
            return dY();
        }
        return this.aS.al.nw;
    }

    double dY() {
        if (this.aS.al != null) {
            return Math.max(this.aS.aj.nw, this.aS.al.nw);
        }
        return this.aS.l();
    }

    private double i(boolean z) {
        double dX = dX();
        if (o(dX) || !z) {
            return dX;
        }
        return dX + dZ();
    }

    private boolean o(double d) {
        return Double.isInfinite(d) || Double.isNaN(d);
    }

    private double dZ() {
        if (Range.h(this.aS.ai)) {
            return 0.0d;
        }
        return this.aS.ai.dF() / 4.0d;
    }

    private boolean m(double d, double d2) {
        if (!this.nJ) {
            return false;
        }
        this.ny.p(d, d2);
        a(this.nL, this.nR, this.ny);
        return true;
    }

    private boolean p(double d) {
        if (!this.nI) {
            return false;
        }
        double d2 = d(d, q((double) this.nK), (double) this.nK);
        this.nx.a(this.aS.ai.nv + d2, d2 + this.aS.ai.nw, true);
        a(this.nK, this.nR, this.nx);
        return true;
    }

    private double q(double d) {
        return (-Math.log(0.012000000104308128d)) / d;
    }

    private double d(double d, double d2, double d3) {
        return ((1.0d - Math.pow(2.718281828459045d, (-d3) * d2)) / d2) * d;
    }

    private boolean ea() {
        return this.aS.ai.nv < dV();
    }

    private boolean eb() {
        return this.aS.ai.nw > dX();
    }

    private void a(MotionState motionState) {
        Object obj = this.nN != motionState ? 1 : null;
        this.nN = motionState;
        if (obj != null && this.aS.J != null) {
            this.aS.J.onAxisMotionStateChange(this.aS);
        }
    }

    private void ec() {
        if (dU()) {
            a(MotionState.STOPPED);
            return;
        }
        this.nP.c(this.aS.ai.nv, this.aS.ai.nw);
        this.nP.f(dV(), dX());
        this.nA.a(this.nP.nv, this.nP.nw, true);
        if (a(this.nA)) {
            a(MotionState.STOPPED);
        }
    }

    private void n(double d, double d2) {
        double dF = (d2 - this.aS.ai.nv) / this.aS.ai.dF();
        double dI = dI() / d;
        this.nO.c(d2 - (dF * dI), ((1.0d - dF) * dI) + d2);
    }
}
