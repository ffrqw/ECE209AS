package com.shinobicontrols.charts;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class g<T extends Comparable<T>> implements bw<T> {
    g() {
    }

    public Set<bv> b(List<Range<T>> list) {
        Set<bv> hashSet = new HashSet();
        hashSet.add(new bv(Double.NEGATIVE_INFINITY, new cu(1.0d, 0.0d)));
        if (list.isEmpty()) {
            return hashSet;
        }
        double d = 0.0d;
        for (Range range : list) {
            bv a = a(range, d);
            bv b = b(range, d);
            hashSet.add(a);
            hashSet.add(b);
            d = range.dF() + d;
        }
        return hashSet;
    }

    private bv a(Range<T> range, double d) {
        return new bv(range.nv, new cu(0.0d, range.nv - d));
    }

    private bv b(Range<T> range, double d) {
        return new bv(range.nw, new cu(1.0d, -(range.dF() + d)));
    }
}
