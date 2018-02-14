package com.shinobicontrols.charts;

import android.view.VelocityTracker;
import java.util.ArrayList;

class dw {
    private final int id;
    private int nm;
    private final fi nn;
    private final ArrayList<dx> no = new ArrayList();
    private boolean np;
    private dx nq;

    dw(int i, fi fiVar) {
        this.id = i;
        this.nn = fiVar;
    }

    void clear() {
        this.nm = 0;
        this.no.clear();
        if (this.nn.fg() != null) {
            this.nn.fg().clear();
        }
    }

    void reset() {
        this.nq = null;
        clear();
    }

    dx dy() {
        return D(this.nm);
    }

    VectorF dz() {
        return h(this.nm, this.no.size() - 1);
    }

    VectorF h(int i, int i2) {
        int size = this.no.size();
        if (i >= i2 || i < 0 || size <= i2) {
            return new VectorF(0.0f, 0.0f);
        }
        return VectorF.f(((dx) this.no.get(i)).ls, ((dx) this.no.get(i2)).ls);
    }

    dx dA() {
        return D(0);
    }

    long dB() {
        if (this.nq != null) {
            return this.nq.b(dA());
        }
        return 0;
    }

    dx D(int i) {
        return (dx) this.no.get(i);
    }

    VectorF dC() {
        float xVelocity;
        float f = 0.0f;
        VelocityTracker fg = this.nn.fg();
        if (fg != null) {
            fg.computeCurrentVelocity(1000);
            xVelocity = fg.getXVelocity(this.id);
            f = fg.getYVelocity(this.id);
        } else {
            xVelocity = 0.0f;
        }
        return new VectorF(xVelocity, f);
    }

    void E(int i) {
        switch (i) {
            case 0:
            case 5:
                this.np = true;
                return;
            case 1:
            case 3:
            case 6:
                this.nq = dD();
                this.np = false;
                return;
            default:
                return;
        }
    }

    boolean a(dx dxVar) {
        if (!this.no.isEmpty() && dxVar.equals(dD())) {
            return false;
        }
        this.no.add(dxVar);
        return true;
    }

    boolean n(float f) {
        float f2 = 0.0f;
        float f3 = 0.0f;
        for (int i = 0; i < this.no.size() - 2; i++) {
            VectorF fe = h(i, i + 1).fe();
            f3 += fe.x;
            f2 += fe.y;
            if (f3 > f || f2 > f) {
                return true;
            }
        }
        return false;
    }

    dx dD() {
        if (this.no.isEmpty()) {
            return null;
        }
        return D(this.no.size() - 1);
    }

    void dE() {
        this.nm = this.no.size() - 1;
    }

    boolean isActive() {
        return this.np;
    }
}
