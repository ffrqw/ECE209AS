package com.shinobicontrols.charts;

import java.util.ArrayList;
import java.util.List;

class i {
    Axis<?, ?>[] bK = new Axis[0];
    private final String bL;

    i(String str) {
        this.bL = str;
    }

    Axis<?, ?> ae() {
        if (this.bK.length == 0) {
            return null;
        }
        return this.bK[0];
    }

    boolean af() {
        return this.bK.length > 0;
    }

    void b(Axis<?, ?> axis) {
        List arrayList = new ArrayList();
        arrayList.add(axis);
        a(arrayList, axis);
        this.bK = new Axis[0];
        this.bK = (Axis[]) arrayList.toArray(this.bK);
    }

    void c(Axis<?, ?> axis) {
        List arrayList = new ArrayList();
        a(arrayList, axis);
        arrayList.add(axis);
        this.bK = new Axis[0];
        this.bK = (Axis[]) arrayList.toArray(this.bK);
    }

    void d(Axis<?, ?> axis) {
        List arrayList = new ArrayList();
        a(arrayList, axis);
        this.bK = new Axis[0];
        this.bK = (Axis[]) arrayList.toArray(this.bK);
    }

    private void a(List<Axis<?, ?>> list, Axis<?, ?> axis) {
        for (int i = 0; i < this.bK.length; i++) {
            if (axis != this.bK[i]) {
                list.add(this.bK[i]);
            }
        }
    }
}
