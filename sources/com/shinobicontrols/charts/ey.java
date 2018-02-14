package com.shinobicontrols.charts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class ey<T extends Comparable<T>, U> {
    private final Axis<T, U> aS;
    private final Comparator<Range<T>> pS = new Comparator<Range<T>>(this) {
        final /* synthetic */ ey pT;

        {
            this.pT = r1;
        }

        public /* synthetic */ int compare(Object x0, Object x1) {
            return b((Range) x0, (Range) x1);
        }

        public int b(Range<T> range, Range<T> range2) {
            if (range.nv < range2.nv) {
                return -1;
            }
            if (range.nv > range2.nv) {
                return 1;
            }
            if (range.nw < range2.nw) {
                return -1;
            }
            if (range.nw > range2.nw) {
                return 1;
            }
            return 0;
        }
    };

    ey(Axis<T, U> axis) {
        this.aS = axis;
    }

    List<Range<T>> q(List<Range<T>> list) {
        List<Range<T>> arrayList = new ArrayList(list);
        Collections.sort(arrayList, this.pS);
        return arrayList;
    }

    List<Range<T>> r(List<Range<T>> list) {
        List<Range<T>> arrayList = new ArrayList();
        int i;
        for (int i2 = 0; i2 < list.size(); i2 = i) {
            Range range = (Range) list.get(i2);
            Comparable minimum = range.getMinimum();
            i = i2 + 1;
            Comparable maximum = range.getMaximum();
            Object obj = range;
            while (i < list.size()) {
                range = (Range) list.get(i);
                if (!obj.a(range, true)) {
                    break;
                }
                range = a(obj, range);
                i++;
                maximum = range.getMaximum();
                Range range2 = range;
            }
            if (i - i2 != 1) {
                obj = this.aS.createRange(minimum, maximum);
            }
            arrayList.add(obj);
        }
        return arrayList;
    }

    private Range<T> a(Range<T> range, Range<T> range2) {
        return range.getMaximum().compareTo(range2.getMaximum()) >= 0 ? range : range2;
    }
}
