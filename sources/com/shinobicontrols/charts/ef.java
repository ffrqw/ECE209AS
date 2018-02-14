package com.shinobicontrols.charts;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ef<T extends Comparable<T>> implements bw<T> {
    ef() {
    }

    public Set<bv> b(List<Range<T>> list) {
        Set<bv> hashSet = new HashSet();
        hashSet.add(new bv(Double.NEGATIVE_INFINITY, new cu(1.0d, 0.0d)));
        if (list.isEmpty()) {
            return hashSet;
        }
        double d = 0.0d;
        for (Range range : list) {
            hashSet.add(new bv(range.nv - d, new cu(1.0d, range.dF() + d)));
            d = range.dF() + d;
        }
        return hashSet;
    }
}
