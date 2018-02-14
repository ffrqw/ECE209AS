package com.shinobicontrols.charts;

import android.util.SparseArray;
import android.view.MotionEvent;
import java.util.ArrayList;

class dy {
    private final fi nn;
    private final SparseArray<dw> nr;
    private final ArrayList<dw> ns;

    dy(int i, fi fiVar) {
        this.nr = new SparseArray(i);
        this.ns = new ArrayList(i);
        this.nn = fiVar;
    }

    dw F(int i) {
        if (i < 0 || i >= this.ns.size()) {
            return null;
        }
        return (dw) this.ns.get(i);
    }

    void b(MotionEvent motionEvent) {
        c(motionEvent);
        d(motionEvent);
    }

    private void c(MotionEvent motionEvent) {
        int pointerCount = motionEvent.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            dw a = a(i, motionEvent);
            a.a(new dx(motionEvent.getX(i), motionEvent.getY(i)));
            if (!this.ns.contains(a)) {
                this.ns.add(a);
            }
        }
    }

    private dw a(int i, MotionEvent motionEvent) {
        int pointerId = motionEvent.getPointerId(i);
        dw dwVar = (dw) this.nr.get(pointerId);
        if (dwVar != null) {
            return dwVar;
        }
        dwVar = new dw(pointerId, this.nn);
        this.nr.put(pointerId, dwVar);
        return dwVar;
    }

    private void d(MotionEvent motionEvent) {
        dw dwVar = (dw) this.nr.get(motionEvent.getPointerId(motionEvent.getActionIndex()));
        dwVar.E(motionEvent.getActionMasked());
        if (!dwVar.isActive()) {
            this.ns.remove(dwVar);
        }
    }

    void reset() {
        int size = this.nr.size();
        for (int i = 0; i < size; i++) {
            dw dwVar = (dw) this.nr.valueAt(i);
            if (dwVar != null) {
                dwVar.reset();
            }
        }
        this.ns.clear();
    }

    int size() {
        return this.ns.size();
    }
}
