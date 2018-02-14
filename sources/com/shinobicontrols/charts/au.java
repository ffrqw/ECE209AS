package com.shinobicontrols.charts;

import java.util.Date;
import java.util.GregorianCalendar;

class au {
    private final DateTimeAxis gL;
    private final GregorianCalendar gM;
    private final a gN = new a();
    private final a gO = new a();
    boolean gP = false;

    private static class a {
        boolean gQ;
        int gR;
        int gS;
        long value;

        a() {
            reset();
        }

        void reset() {
            this.value = Long.MAX_VALUE;
            this.gQ = false;
        }
    }

    au(DateTimeAxis dateTimeAxis) {
        this.gL = dateTimeAxis;
        this.gM = new GregorianCalendar();
        this.gM.clear();
    }

    long a(double d, DateFrequency dateFrequency) {
        return a((long) d, dateFrequency, false);
    }

    long b(double d, DateFrequency dateFrequency) {
        return a((long) d, dateFrequency, true);
    }

    double a(DateFrequency dateFrequency) {
        if (!c(dateFrequency, this.gN)) {
            this.gN.reset();
        }
        return (double) a(dateFrequency, this.gN);
    }

    double b(DateFrequency dateFrequency) {
        if (!c(dateFrequency, this.gO)) {
            this.gO.reset();
        }
        return (double) a(dateFrequency, this.gO);
    }

    boolean c(double d, DateFrequency dateFrequency) {
        long b = b(dateFrequency, this.gN);
        boolean z = this.gN.gQ;
        if (((double) b) < d) {
            while (((double) b) < d) {
                b = a((double) b, dateFrequency);
                z = !z;
            }
        } else if (((double) b) > d) {
            while (((double) b) > d) {
                b = b((double) b, dateFrequency);
                z = !z;
            }
        }
        return z;
    }

    private long a(long j, DateFrequency dateFrequency, boolean z) {
        this.gM.setTime((Date) this.gL.transformInternalValueToUser((double) j));
        int i = dateFrequency.gu;
        if (z) {
            i = -i;
        }
        if (this.gM.get(0) == 0) {
            i = -i;
        }
        this.gM.add(dateFrequency.gv.value, i);
        Date time = this.gM.getTime();
        long transformUserValueToInternal = (long) this.gL.transformUserValueToInternal(time);
        if (transformUserValueToInternal == j) {
            return a(time, dateFrequency.gv.value, i);
        }
        return transformUserValueToInternal;
    }

    private long a(Date date, int i, int i2) {
        this.gM.setTimeInMillis((long) b(date).ji);
        this.gM.add(i, i2);
        return (long) this.gL.transformUserValueToInternal(this.gM.getTime());
    }

    private bv b(Date date) {
        return this.gL.aK.k((double) date.getTime());
    }

    private long a(DateFrequency dateFrequency, a aVar) {
        long b = b(dateFrequency, aVar);
        boolean z = aVar.gQ;
        long j = (long) this.gL.ai.nv;
        long a = a((double) j, dateFrequency);
        while (b < j) {
            b = a((double) b, dateFrequency);
            z = !z;
        }
        while (b > a) {
            b = b((double) b, dateFrequency);
            z = !z;
        }
        aVar.value = b;
        aVar.gQ = z;
        aVar.gR = dateFrequency.gu;
        aVar.gS = dateFrequency.gv.value;
        return b;
    }

    private long b(DateFrequency dateFrequency, a aVar) {
        if (aVar.value != Long.MAX_VALUE) {
            return aVar.value;
        }
        return (long) this.gL.E();
    }

    private boolean c(DateFrequency dateFrequency, a aVar) {
        if (this.gP && d(dateFrequency, aVar)) {
            return false;
        }
        return true;
    }

    private boolean d(DateFrequency dateFrequency, a aVar) {
        return (dateFrequency.gu == aVar.gR && dateFrequency.gv.value == aVar.gS) ? false : true;
    }

    void invalidate() {
        this.gN.reset();
        this.gO.reset();
    }
}
