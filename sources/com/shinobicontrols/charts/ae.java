package com.shinobicontrols.charts;

class ae extends fd {
    ae() {
    }

    boolean a(TickMark tickMark, c cVar, Axis<?, ?> axis, double d) {
        return c(tickMark.value, axis.aj.nw, 0.0d) || super.a(tickMark, cVar, axis, d);
    }

    boolean b(TickMark tickMark, c cVar, Axis<?, ?> axis, double d) {
        return c(tickMark.value, axis.aj.nw, 0.0d) || super.b(tickMark, cVar, axis, d);
    }

    boolean a(TickMark tickMark, c cVar, Axis<?, ?> axis, boolean z, boolean z2) {
        return c(tickMark.value, axis.aj.nw, 0.0d) || super.a(tickMark, cVar, axis, z, z2);
    }

    private boolean c(double d, double d2, double d3) {
        return d > d2 + d3;
    }
}
