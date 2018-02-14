package com.shinobicontrols.charts;

import java.util.Comparator;
import java.util.Locale;

class bv {
    static final Comparator<bv> jh = new Comparator<bv>() {
        public final /* synthetic */ int compare(Object x0, Object x1) {
            return a((bv) x0, (bv) x1);
        }

        public final int a(bv bvVar, bv bvVar2) {
            if (bvVar.ji < bvVar2.ji) {
                return -1;
            }
            if (bvVar.ji > bvVar2.ji) {
                return 1;
            }
            return 0;
        }
    };
    final double ji;
    final cu jj;

    bv(double d, cu cuVar) {
        this.ji = d;
        this.jj = cuVar;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof bv)) {
            return false;
        }
        if (((bv) o).ji != this.ji) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        long doubleToLongBits = Double.doubleToLongBits(this.ji);
        return ((int) (doubleToLongBits ^ (doubleToLongBits >>> 32))) + 527;
    }

    public String toString() {
        return String.format(Locale.US, "%s, from %f", new Object[]{this.jj.toString(), Double.valueOf(this.ji)});
    }
}
