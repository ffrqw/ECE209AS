package com.shinobicontrols.charts;

import android.graphics.Canvas;
import android.graphics.PointF;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

final class fe {
    private final Axis<?, ?> aS;
    private final List<TickMark> sj = new ArrayList();
    private final List<TickMark> sk = new ArrayList();
    private final Stack<TickMark> sl = new Stack();
    private final fd sm;
    private final a sn = new a();
    private TickMark[] so = new TickMark[0];
    boolean sp = false;

    private static class a {
        public int count;

        private a() {
        }
    }

    public fe(Axis<?, ?> axis) {
        this.aS = axis;
        this.sm = axis.U();
    }

    public final void b(c cVar) {
        if (this.sp) {
            eU();
            this.sp = false;
        } else {
            eV();
        }
        if (!Range.h(this.aS.ai)) {
            if (cVar.aY || cVar.aZ || cVar.bb || cVar.bc || cVar.ba) {
                e(cVar);
            }
        }
    }

    public final void eT() {
        this.sj.clear();
    }

    final void eU() {
        this.sj.clear();
        this.sk.clear();
        this.sl.clear();
    }

    private void eV() {
        int i;
        int size = this.sk.size();
        for (i = 0; i < size; i++) {
            this.sl.push((TickMark) this.sk.get(i));
        }
        this.sk.clear();
        i = this.sj.size();
        this.so = (TickMark[]) this.sj.toArray(this.so);
        for (int i2 = 0; i2 < i; i2++) {
            TickMark tickMark = this.so[i2];
            if (tickMark.rR) {
                this.sk.add(tickMark);
            } else {
                this.sl.push(tickMark);
            }
        }
        this.sj.clear();
    }

    public final void b(Canvas canvas, c cVar) {
        int size = this.sj.size();
        this.so = (TickMark[]) this.sj.toArray(this.so);
        for (int i = 0; i < size; i++) {
            this.so[i].a(canvas, this, i, cVar);
        }
    }

    private TickMark a(double d, c cVar) {
        TickMark tickMark;
        int size = this.sk.size();
        for (int i = 0; i < size; i++) {
            tickMark = (TickMark) this.sk.get(i);
            if (tickMark.value == d) {
                break;
            }
        }
        tickMark = null;
        if (tickMark == null) {
            tickMark = d(cVar);
        } else {
            this.sk.remove(tickMark);
        }
        tickMark.rR = true;
        tickMark.rU = cVar.aZ;
        return tickMark;
    }

    private TickMark c(c cVar) {
        TickMark d = d(cVar);
        d.rR = false;
        d.rU = cVar.ba;
        return d;
    }

    private TickMark d(c cVar) {
        if (this.sl.isEmpty()) {
            return new TickMark(this.aS);
        }
        return (TickMark) this.sl.pop();
    }

    private void e(c cVar) {
        if (this.aS.az != null) {
            a(this.aS.az, cVar);
        } else {
            f(cVar);
        }
    }

    private void a(double[] dArr, c cVar) {
        this.sn.count = 0;
        int a = a(dArr);
        if (a != -1) {
            a(cVar, this.sn, a, dArr, false);
        }
    }

    private void a(c cVar, a aVar, int i, double[] dArr, boolean z) {
        boolean z2 = z;
        while (aVar.count < 2 && i < dArr.length) {
            a(dArr[i], cVar, z2, cVar.aY, aVar);
            z2 = !z2;
            i++;
        }
    }

    private int a(double[] dArr) {
        for (int i = 0; i < dArr.length; i++) {
            if (dArr[i] >= this.aS.ai.nv) {
                return i;
            }
        }
        return -1;
    }

    private void f(c cVar) {
        this.sn.count = 0;
        double b = this.aS.b(cVar.bw);
        boolean b2 = this.aS.b(b);
        a(cVar, this.sn, b, b2);
        b(cVar, this.sn, b, b2);
    }

    private void a(c cVar, a aVar, double d, boolean z) {
        boolean y = this.aS.y();
        int a = a(y, this.aS.C(), cVar);
        int a2 = y ? 0 : this.aS.a(cVar.bw, a);
        int i = 0;
        boolean z2 = z;
        double d2 = d;
        while (aVar.count < 2) {
            boolean z3 = cVar.aY && i == a2;
            a(d2, cVar, z2, z3, aVar);
            int i2 = i + 1;
            if (i2 == a) {
                i2 = 0;
            }
            d2 = this.aS.a(d2, true);
            z2 = !z2;
            i = i2;
        }
    }

    private int a(boolean z, double d, c cVar) {
        if (z) {
            return 1;
        }
        int ceil = (int) Math.ceil(this.aS.ai.dF() / d);
        if (this.aS.h()) {
            return (int) Math.ceil((double) (1.0f / ((float) ((cVar.bw / cVar.ay.x) / ceil))));
        }
        return (int) Math.ceil((double) (1.0f / ((float) ((cVar.bw / cVar.ay.y) / ceil))));
    }

    private void b(c cVar, a aVar, double d, boolean z) {
        boolean y = this.aS.y();
        this.aS.G();
        if (this.aS.D() && cVar.ba && this.aS.c(cVar.bw) != Double.NaN) {
            double c = this.aS.c(cVar.bw);
            while (c <= this.aS.ai.nw) {
                a(c, cVar, z, y);
                c = this.aS.a(c, false);
            }
        }
    }

    private void a(double d, c cVar, boolean z, boolean z2, a aVar) {
        TickMark a = a(d, cVar);
        this.sj.add(a);
        a.value = d;
        a.rS = z;
        a.rT = z2;
        double b = this.aS.b(d, (double) cVar.bw, cVar.bx);
        boolean a2 = this.sm.a(a, cVar, this.aS, b);
        if (this.aS.h() && a2) {
            aVar.count++;
        }
        boolean b2 = this.sm.b(a, cVar, this.aS, b);
        if (a2 || b2) {
            if (!this.aS.h()) {
                aVar.count++;
            }
            a.rT = false;
            a.rU = !this.sm.a(a, cVar, this.aS, a2, b2);
        }
    }

    private void a(double d, c cVar, boolean z, boolean z2) {
        TickMark c = c(cVar);
        this.sj.add(c);
        c.value = d;
        c.rS = z;
        c.rT = false;
    }

    final void a(int i, PointF pointF, TickMark tickMark, PointF pointF2, c cVar) {
        int i2 = i - 1;
        while (i2 > 0 && !((TickMark) this.sj.get(i2)).rR) {
            i2--;
        }
        if (i2 < 0 || !((TickMark) this.sj.get(i2)).rR) {
            pointF.x = (float) cVar.aX.left;
            pointF.y = (float) cVar.aX.bottom;
        } else {
            pointF.x = ((TickMark) this.sj.get(i2)).sb.exactCenterX();
            pointF.y = ((TickMark) this.sj.get(i2)).sb.exactCenterY();
        }
        pointF2.x = tickMark.sb.exactCenterX();
        pointF2.y = tickMark.sb.exactCenterY();
        if (pointF2.x > ((float) cVar.aX.right)) {
            pointF2.x = (float) cVar.aX.right;
        }
        if (pointF2.y < ((float) cVar.aX.top)) {
            pointF2.y = (float) cVar.aX.top;
        }
        if (pointF.x > ((float) cVar.aX.right)) {
            pointF.x = (float) cVar.aX.right;
        }
        if (pointF.y < ((float) cVar.aX.top)) {
            pointF.y = (float) cVar.aX.top;
        }
    }

    final void eW() {
        int size = this.sj.size();
        this.so = (TickMark[]) this.sj.toArray(this.so);
        for (int i = 0; i < size; i++) {
            this.so[i].eS();
        }
    }
}
