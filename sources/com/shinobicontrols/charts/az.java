package com.shinobicontrols.charts;

import android.graphics.PointF;
import com.shinobicontrols.charts.ShinobiChart.OnGestureListener;

class az {
    private final af J;
    private final OnGestureListener he;
    private final ai hf;
    private VectorF hg = new VectorF(1.0f, 1.0f);
    private boolean hh = false;

    az(af afVar, OnGestureListener onGestureListener, ai aiVar) {
        this.J = afVar;
        this.he = onGestureListener;
        this.hf = aiVar;
    }

    boolean a(int i, int i2, dw dwVar, dw dwVar2) {
        if (i == 1 && i2 == 2) {
            a(dwVar, dwVar2);
            return true;
        } else if (i == 2 && i2 == 2) {
            b(dwVar, dwVar2);
            return true;
        } else if (i != 2 || i2 != 1) {
            return false;
        } else {
            c(dwVar, dwVar2);
            return true;
        }
    }

    private void a(dw dwVar, dw dwVar2) {
        this.J.a(dwVar.dA().ls, dwVar2.dA().ls);
        this.he.onSecondTouchDown(this.J, dwVar.dA().ls, dwVar2.dA().ls);
    }

    private void b(dw dwVar, dw dwVar2) {
        boolean z = this.hh;
        this.hh = b(dwVar.dz().fd(), dwVar2.dz().fd());
        if (this.hh) {
            PointF pointF = z ? dwVar.dy().ls : dwVar.dA().ls;
            PointF pointF2 = z ? dwVar2.dy().ls : dwVar2.dA().ls;
            PointF d = d(pointF, pointF2);
            dwVar.dE();
            dwVar2.dE();
            PointF d2 = d(dwVar.dD().ls, dwVar2.dD().ls);
            this.hg = a(VectorF.f(pointF, pointF2).fe(), VectorF.f(dwVar.dD().ls, dwVar2.dD().ls).fe());
            this.J.a(d, d2, this.hg);
            this.he.onPinch(this.J, d, d2, this.hg);
        }
    }

    private boolean b(float f, float f2) {
        return this.hh || f > ((float) this.hf.fx) || f2 > ((float) this.hf.fx);
    }

    private PointF d(PointF pointF, PointF pointF2) {
        return new PointF((pointF.x + pointF2.x) / 2.0f, (pointF.y + pointF2.y) / 2.0f);
    }

    private VectorF a(VectorF vectorF, VectorF vectorF2) {
        float f;
        float f2 = 1.0f;
        if (vectorF.x <= ((float) this.hf.fB) || vectorF2.x <= ((float) this.hf.fB)) {
            f = 1.0f;
        } else {
            f = vectorF2.x / vectorF.x;
        }
        if (vectorF.y > ((float) this.hf.fB) && vectorF2.y > ((float) this.hf.fB)) {
            f2 = vectorF2.y / vectorF.y;
        }
        return new VectorF(f, f2);
    }

    private void c(dw dwVar, dw dwVar2) {
        boolean z = false;
        this.hh = false;
        int i = this.hf.fx;
        if (dwVar.n((float) i) || dwVar2.n((float) i)) {
            VectorF dC = dwVar.dC();
            VectorF dC2 = dwVar2.dC();
            if (dC.fd() > ((float) this.hf.fy) || dC2.fd() > ((float) this.hf.fy)) {
                z = true;
            }
            PointF d = d(dwVar.dD().ls, dwVar2.dD().ls);
            this.J.a(d, z, this.hg);
            this.he.onPinchEnd(this.J, d, z, this.hg);
        } else {
            this.J.b(dwVar.dD().ls, dwVar2.dD().ls);
            this.he.onSecondTouchUp(this.J, dwVar.dD().ls, dwVar2.dD().ls);
        }
        dwVar.clear();
        dwVar2.clear();
    }
}
