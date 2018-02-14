package com.shinobicontrols.charts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class dq {
    private final List<bv> mQ = new ArrayList();

    dq() {
    }

    static dq du() {
        dq dqVar = new dq();
        bv bvVar = new bv(Double.NEGATIVE_INFINITY, new cu(1.0d, 0.0d));
        Set hashSet = new HashSet();
        hashSet.add(bvVar);
        dqVar.b(hashSet);
        return dqVar;
    }

    double g(double d) {
        bv k = k(d);
        if (k == null) {
            return Double.NaN;
        }
        return k.jj.g(d);
    }

    bv k(double d) {
        if (this.mQ.isEmpty() || d < ((bv) this.mQ.get(0)).ji) {
            return null;
        }
        for (int i = 0; i < this.mQ.size() - 1; i++) {
            bv bvVar = (bv) this.mQ.get(i);
            bv bvVar2 = (bv) this.mQ.get(i + 1);
            if (d >= bvVar.ji && d < bvVar2.ji) {
                return bvVar;
            }
        }
        return (bv) this.mQ.get(this.mQ.size() - 1);
    }

    void b(Set<bv> set) {
        this.mQ.clear();
        this.mQ.addAll(set);
        Collections.sort(this.mQ, bv.jh);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < this.mQ.size(); i++) {
            stringBuilder.append(((bv) this.mQ.get(i)).toString());
            if (i != this.mQ.size() - 1) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
