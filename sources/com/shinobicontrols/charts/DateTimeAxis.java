package com.shinobicontrols.charts;

import android.graphics.PointF;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DateTimeAxis extends Axis<Date, DateFrequency> {
    private final SimpleDateFormat gA = ((SimpleDateFormat) DateFormat.getDateInstance());
    private DateFormat gB;
    private final PointF gC = new PointF(1.0f, 1.0f);
    private boolean gD;
    private long gE = 0;
    private String gF = "";
    private final Map<RepeatedTimePeriod, List<Range<Date>>> gG = new LinkedHashMap();
    private final au gH = new au(this);
    private final at gI = new at();
    private Date gJ;

    public DateTimeAxis(DateRange defaultRange) {
        setDefaultRange(defaultRange);
        a(defaultRange.getMinimum());
    }

    boolean isDataValid(Object point) {
        return point instanceof Date;
    }

    double convertPoint(Object userData) {
        return translatePoint(userData);
    }

    private void a(Date date) {
        if (this.gE == 0) {
            this.gE = date.getTime();
            this.gJ = new Date(this.gE);
        }
    }

    double translatePoint(Object userData) {
        validateUserData(userData);
        return transformUserValueToInternal((Date) userData);
    }

    double transformExternalValueToInternal(Date externalValue) {
        long time = externalValue.getTime();
        a(externalValue);
        return (double) (time - this.gE);
    }

    Date transformInternalValueToExternal(double internalValue) {
        if (internalValue == Double.POSITIVE_INFINITY) {
            return new Date(Long.MAX_VALUE);
        }
        if (internalValue == Double.NEGATIVE_INFINITY) {
            return new Date(Long.MIN_VALUE);
        }
        return new Date(((long) internalValue) + this.gE);
    }

    protected Date transformUserValueToChartValue(Date userValue) {
        return userValue;
    }

    protected Date transformChartValueToUserValue(Date chartValue) {
        return chartValue;
    }

    Range<Date> createRange(Date min, Date max) {
        return new DateRange(min, max);
    }

    String x() {
        cb();
        if (z()) {
            return this.aI;
        }
        if (!ca()) {
            switch (((DateFrequency) this.av).gv) {
                case SECONDS:
                case MINUTES:
                    this.aH = " 00:00:00 ";
                    break;
                case HOURS:
                    this.aH = " Mmm 00:00 ";
                    break;
                case MONTHS:
                    this.aH = " Mmm 00 ";
                    break;
                case YEARS:
                    this.aH = " 2000 ";
                    break;
                default:
                    this.aH = " 00 Mmm ";
                    break;
            }
        }
        String format = this.gB.format(Double.valueOf(this.ai.nv));
        String format2 = this.gB.format(Double.valueOf(this.ai.nw));
        if (format2.length() <= format.length()) {
            format2 = format;
        }
        this.aH = format2;
        return this.aH;
    }

    public DateFormat getLabelFormat() {
        return this.gB;
    }

    public void setLabelFormat(DateFormat dateFormat) {
        this.gB = dateFormat;
    }

    public String getFormattedString(Date value) {
        return ca() ? this.gB.format(value) : this.gA.format(value);
    }

    private boolean ca() {
        return this.gB != null;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void d(int r14) {
        /*
        r13 = this;
        r3 = 5;
        r12 = 3;
        r11 = 30;
        r10 = 15;
        r2 = 1;
        r0 = 0;
        r13.gD = r0;
        r0 = r13.av;
        if (r0 != 0) goto L_0x0017;
    L_0x000e:
        r0 = new com.shinobicontrols.charts.DateFrequency;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.SECONDS;
        r0.<init>(r2, r1);
        r13.av = r0;
    L_0x0017:
        r0 = r13.aw;
        if (r0 != 0) goto L_0x0024;
    L_0x001b:
        r0 = new com.shinobicontrols.charts.DateFrequency;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.SECONDS;
        r0.<init>(r2, r1);
        r13.aw = r0;
    L_0x0024:
        r0 = r13.ai;
        r0 = r0.dF();
        r4 = 4666723172467343360; // 0x40c3880000000000 float:0.0 double:10000.0;
        r0 = r0 / r4;
        r4 = 4607632778762754458; // 0x3ff199999999999a float:-1.5881868E-23 double:1.1;
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 >= 0) goto L_0x007e;
    L_0x0039:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.SECONDS;
        r0.a(r2, r1);
        r1 = r2;
    L_0x0043:
        r13.H();
        r0 = r13.ai;
        r4 = r0.dF();
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r6 = r0.bY();
        r6 = (double) r6;
        r4 = r4 / r6;
        r4 = java.lang.Math.floor(r4);
        r0 = (int) r4;
        r4 = r13.gC;
        r0 = r13.a(r0, r14, r4);
        if (r0 == 0) goto L_0x027b;
    L_0x0063:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.SECONDS;
        r0 = r0.b(r2, r1);
        if (r0 != 0) goto L_0x007b;
    L_0x006f:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.MONTHS;
        r0 = r0.b(r2, r1);
        if (r0 == 0) goto L_0x0276;
    L_0x007b:
        r13.gD = r2;
    L_0x007d:
        return;
    L_0x007e:
        r4 = 4625337554797854720; // 0x4030800000000000 float:0.0 double:16.5;
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 >= 0) goto L_0x0092;
    L_0x0087:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.SECONDS;
        r0.a(r10, r1);
        r1 = r2;
        goto L_0x0043;
    L_0x0092:
        r4 = 4629841154425225216; // 0x4040800000000000 float:0.0 double:33.0;
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 >= 0) goto L_0x00af;
    L_0x009b:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.SECONDS;
        r0.a(r11, r1);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.SECONDS;
        r0.a(r10, r1);
        r1 = r2;
        goto L_0x0043;
    L_0x00af:
        r4 = 4634344754052595712; // 0x4050800000000000 float:0.0 double:66.0;
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 >= 0) goto L_0x00cd;
    L_0x00b8:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.MINUTES;
        r0.a(r2, r1);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.SECONDS;
        r0.a(r11, r1);
        r1 = r2;
        goto L_0x0043;
    L_0x00cd:
        r4 = 4651919347910967296; // 0x408ef00000000000 float:0.0 double:990.0;
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 >= 0) goto L_0x00eb;
    L_0x00d6:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.MINUTES;
        r0.a(r10, r1);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.MINUTES;
        r0.a(r3, r1);
        r1 = r2;
        goto L_0x0043;
    L_0x00eb:
        r4 = 4656422947538337792; // 0x409ef00000000000 float:0.0 double:1980.0;
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 >= 0) goto L_0x0109;
    L_0x00f4:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.MINUTES;
        r0.a(r11, r1);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.MINUTES;
        r0.a(r10, r1);
        r1 = r2;
        goto L_0x0043;
    L_0x0109:
        r4 = 4660926547165708288; // 0x40aef00000000000 float:0.0 double:3960.0;
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 >= 0) goto L_0x0127;
    L_0x0112:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.HOURS;
        r0.a(r2, r1);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.MINUTES;
        r0.a(r11, r1);
        r1 = r2;
        goto L_0x0043;
    L_0x0127:
        r4 = 4672260313024823296; // 0x40d7340000000000 float:0.0 double:23760.0;
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 >= 0) goto L_0x0146;
    L_0x0130:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = 6;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.HOURS;
        r0.a(r1, r4);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.HOURS;
        r0.a(r2, r1);
        r1 = r2;
        goto L_0x0043;
    L_0x0146:
        r4 = 4676763912652193792; // 0x40e7340000000000 float:0.0 double:47520.0;
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 >= 0) goto L_0x0167;
    L_0x014f:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = 12;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.HOURS;
        r0.a(r1, r4);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = 6;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.HOURS;
        r0.a(r1, r4);
        r1 = r2;
        goto L_0x0043;
    L_0x0167:
        r4 = 4681267512279564289; // 0x40f7340000000001 float:1.4E-45 double:95040.00000000001;
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 >= 0) goto L_0x0187;
    L_0x0170:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.DAYS;
        r0.a(r2, r1);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = 12;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.HOURS;
        r0.a(r1, r4);
        r1 = r2;
        goto L_0x0043;
    L_0x0187:
        r4 = 4693961923778052096; // 0x41244d8000000000 float:0.0 double:665280.0;
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 >= 0) goto L_0x01a6;
    L_0x0190:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = 7;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.DAYS;
        r0.a(r1, r4);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.DAYS;
        r0.a(r2, r1);
        r1 = r2;
        goto L_0x0043;
    L_0x01a6:
        r4 = 4698465523405422592; // 0x41344d8000000000 float:0.0 double:1330560.0;
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 >= 0) goto L_0x01c7;
    L_0x01af:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = 14;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.DAYS;
        r0.a(r1, r4);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = 7;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.DAYS;
        r0.a(r1, r4);
        r1 = r2;
        goto L_0x0043;
    L_0x01c7:
        r4 = 4703581413570510849; // 0x41467a6000000001 float:1.4E-45 double:2946240.0000000005;
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 >= 0) goto L_0x01e6;
    L_0x01d0:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.MONTHS;
        r0.a(r2, r1);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = 7;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.DAYS;
        r0.a(r1, r4);
        r1 = r2;
        goto L_0x0043;
    L_0x01e6:
        r4 = 4711006862269480960; // 0x4160dbc800000000 float:0.0 double:8838720.0;
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 >= 0) goto L_0x0204;
    L_0x01ef:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.MONTHS;
        r0.a(r12, r1);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.MONTHS;
        r0.a(r2, r1);
        r1 = r2;
        goto L_0x0043;
    L_0x0204:
        r4 = 4694574214315769856; // 0x41267a6000000000 float:0.0 double:736560.0;
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 >= 0) goto L_0x0223;
    L_0x020d:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = 6;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.MONTHS;
        r0.a(r1, r4);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.MONTHS;
        r0.a(r12, r1);
        r1 = r2;
        goto L_0x0043;
    L_0x0223:
        r4 = 4719927958167355392; // 0x41808d7880000000 float:-0.0 double:3.471336E7;
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 >= 0) goto L_0x0241;
    L_0x022c:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.YEARS;
        r0.a(r2, r1);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.MONTHS;
        r0.a(r12, r1);
        r1 = r2;
        goto L_0x0043;
    L_0x0241:
        r4 = 4724431557794725888; // 0x41908d7880000000 float:-0.0 double:6.942672E7;
        r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r0 >= 0) goto L_0x0260;
    L_0x024a:
        r1 = 2;
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = 2;
        r5 = com.shinobicontrols.charts.DateFrequency.Denomination.YEARS;
        r0.a(r4, r5);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.YEARS;
        r0.a(r2, r4);
        goto L_0x0043;
    L_0x0260:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.YEARS;
        r0.a(r3, r1);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = 2;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.YEARS;
        r0.a(r1, r4);
        r1 = r3;
        goto L_0x0043;
    L_0x0276:
        r0 = 0;
        r13.gD = r0;
        goto L_0x007d;
    L_0x027b:
        r4 = com.shinobicontrols.charts.DateTimeAxis.AnonymousClass1.gw;
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r0 = r0.gv;
        r0 = r0.ordinal();
        r0 = r4[r0];
        switch(r0) {
            case 1: goto L_0x0290;
            case 2: goto L_0x02d7;
            case 3: goto L_0x0333;
            case 4: goto L_0x03d0;
            case 5: goto L_0x041a;
            case 6: goto L_0x0382;
            default: goto L_0x028c;
        };
    L_0x028c:
        r0 = r1;
    L_0x028d:
        r1 = r0;
        goto L_0x0043;
    L_0x0290:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r0 = r0.gu;
        switch(r0) {
            case 1: goto L_0x029b;
            case 15: goto L_0x02af;
            case 30: goto L_0x02c3;
            default: goto L_0x0299;
        };
    L_0x0299:
        goto L_0x0043;
    L_0x029b:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.SECONDS;
        r0.a(r10, r4);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.SECONDS;
        r0.a(r2, r4);
        r0 = r1;
        goto L_0x028d;
    L_0x02af:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.SECONDS;
        r0.a(r11, r4);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.SECONDS;
        r0.a(r10, r4);
        r0 = r1;
        goto L_0x028d;
    L_0x02c3:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.MINUTES;
        r0.a(r2, r4);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.SECONDS;
        r0.a(r11, r4);
        goto L_0x0043;
    L_0x02d7:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r0 = r0.gu;
        switch(r0) {
            case 1: goto L_0x02e2;
            case 5: goto L_0x02f6;
            case 15: goto L_0x030a;
            case 30: goto L_0x031f;
            default: goto L_0x02e0;
        };
    L_0x02e0:
        goto L_0x0043;
    L_0x02e2:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.MINUTES;
        r0.a(r3, r4);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.MINUTES;
        r0.a(r2, r4);
        r0 = r1;
        goto L_0x028d;
    L_0x02f6:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.MINUTES;
        r0.a(r10, r4);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.MINUTES;
        r0.a(r3, r4);
        r0 = r1;
        goto L_0x028d;
    L_0x030a:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.MINUTES;
        r0.a(r11, r4);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.MINUTES;
        r0.a(r10, r4);
        r0 = r1;
        goto L_0x028d;
    L_0x031f:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.HOURS;
        r0.a(r2, r4);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.MINUTES;
        r0.a(r11, r4);
        goto L_0x0043;
    L_0x0333:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r0 = r0.gu;
        switch(r0) {
            case 1: goto L_0x033e;
            case 6: goto L_0x0354;
            case 12: goto L_0x036c;
            default: goto L_0x033c;
        };
    L_0x033c:
        goto L_0x0043;
    L_0x033e:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = 6;
        r5 = com.shinobicontrols.charts.DateFrequency.Denomination.HOURS;
        r0.a(r4, r5);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.HOURS;
        r0.a(r2, r4);
        r0 = r1;
        goto L_0x028d;
    L_0x0354:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = 12;
        r5 = com.shinobicontrols.charts.DateFrequency.Denomination.HOURS;
        r0.a(r4, r5);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = 6;
        r5 = com.shinobicontrols.charts.DateFrequency.Denomination.HOURS;
        r0.a(r4, r5);
        r0 = r1;
        goto L_0x028d;
    L_0x036c:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.DAYS;
        r0.a(r2, r4);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = 12;
        r5 = com.shinobicontrols.charts.DateFrequency.Denomination.HOURS;
        r0.a(r4, r5);
        goto L_0x0043;
    L_0x0382:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r0 = r0.gu;
        switch(r0) {
            case 1: goto L_0x038d;
            case 7: goto L_0x03a3;
            case 14: goto L_0x03bb;
            default: goto L_0x038b;
        };
    L_0x038b:
        goto L_0x0043;
    L_0x038d:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = 7;
        r5 = com.shinobicontrols.charts.DateFrequency.Denomination.DAYS;
        r0.a(r4, r5);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.DAYS;
        r0.a(r2, r4);
        r0 = r1;
        goto L_0x028d;
    L_0x03a3:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = 14;
        r5 = com.shinobicontrols.charts.DateFrequency.Denomination.DAYS;
        r0.a(r4, r5);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = 7;
        r5 = com.shinobicontrols.charts.DateFrequency.Denomination.DAYS;
        r0.a(r4, r5);
        r0 = r1;
        goto L_0x028d;
    L_0x03bb:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.MONTHS;
        r0.a(r2, r4);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = 7;
        r5 = com.shinobicontrols.charts.DateFrequency.Denomination.DAYS;
        r0.a(r4, r5);
        goto L_0x0043;
    L_0x03d0:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r0 = r0.gu;
        switch(r0) {
            case 1: goto L_0x03db;
            case 2: goto L_0x03d9;
            case 3: goto L_0x03f0;
            case 4: goto L_0x03d9;
            case 5: goto L_0x03d9;
            case 6: goto L_0x0406;
            default: goto L_0x03d9;
        };
    L_0x03d9:
        goto L_0x0043;
    L_0x03db:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.MONTHS;
        r0.a(r12, r4);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.MONTHS;
        r0.a(r2, r4);
        r0 = r1;
        goto L_0x028d;
    L_0x03f0:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = 6;
        r5 = com.shinobicontrols.charts.DateFrequency.Denomination.MONTHS;
        r0.a(r4, r5);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.MONTHS;
        r0.a(r12, r4);
        r0 = r1;
        goto L_0x028d;
    L_0x0406:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.YEARS;
        r0.a(r2, r4);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.MONTHS;
        r0.a(r12, r4);
        goto L_0x0043;
    L_0x041a:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r4 = r0.gu;
        switch(r1) {
            case 1: goto L_0x0425;
            case 2: goto L_0x043c;
            case 3: goto L_0x0423;
            case 4: goto L_0x0423;
            case 5: goto L_0x0458;
            default: goto L_0x0423;
        };
    L_0x0423:
        goto L_0x028c;
    L_0x0425:
        r1 = 2;
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r5 = r4 << 1;
        r6 = com.shinobicontrols.charts.DateFrequency.Denomination.YEARS;
        r0.a(r5, r6);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r5 = com.shinobicontrols.charts.DateFrequency.Denomination.YEARS;
        r0.a(r4, r5);
        goto L_0x0043;
    L_0x043c:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r6 = (double) r4;
        r8 = 4612811918334230528; // 0x4004000000000000 float:0.0 double:2.5;
        r6 = r6 * r8;
        r1 = (int) r6;
        r5 = com.shinobicontrols.charts.DateFrequency.Denomination.YEARS;
        r0.a(r1, r5);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = r4 / 2;
        r4 = com.shinobicontrols.charts.DateFrequency.Denomination.YEARS;
        r0.a(r1, r4);
        r1 = r3;
        goto L_0x0043;
    L_0x0458:
        r0 = r13.av;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = r4 << 1;
        r5 = com.shinobicontrols.charts.DateFrequency.Denomination.YEARS;
        r0.a(r1, r5);
        r0 = r13.aw;
        r0 = (com.shinobicontrols.charts.DateFrequency) r0;
        r1 = com.shinobicontrols.charts.DateFrequency.Denomination.YEARS;
        r0.a(r4, r1);
        r0 = r2;
        goto L_0x028d;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.shinobicontrols.charts.DateTimeAxis.d(int):void");
    }

    double b(int i) {
        return this.gH.a((DateFrequency) this.av);
    }

    double c(int i) {
        return this.gH.b((DateFrequency) this.aw);
    }

    boolean b(double d) {
        return this.gH.c(d, (DateFrequency) this.av);
    }

    double a(double d, boolean z) {
        return (double) this.gH.a(d, z ? (DateFrequency) this.av : (DateFrequency) this.aw);
    }

    double b(double d, boolean z) {
        return (double) this.gH.b(d, z ? (DateFrequency) this.av : (DateFrequency) this.aw);
    }

    double transformExternalFrequencyToInternal(DateFrequency externalValue) {
        return (double) externalValue.bY();
    }

    private void cb() {
        if (ca()) {
            this.aA.eW();
            return;
        }
        String cc = cc();
        this.gA.applyPattern(cc);
        if (cc != this.gF) {
            this.aA.eW();
            this.gF = cc;
        }
    }

    double N() {
        return 1.728E8d;
    }

    private String cc() {
        switch (((DateFrequency) this.av).gv) {
            case SECONDS:
            case MINUTES:
                return "HH:mm:ss";
            case HOURS:
                return "EEE HH:mm";
            case MONTHS:
                return "MMM yy";
            case YEARS:
                return "yyyy";
            default:
                return "dd MMM";
        }
    }

    void setMajorTickFrequencyInternal(DateFrequency frequency) {
        if (frequency == null) {
            this.at = null;
        } else if (frequency.gu > 0) {
            this.at = frequency;
        } else {
            ev.g(this.J != null ? this.J.getContext().getString(R.string.DateTimeAxisInvalidDateFrequency) : "The DateFrequency is invalid and will be ignored");
            this.at = null;
        }
    }

    void setMinorTickFrequencyInternal(DateFrequency frequency) {
        if (frequency == null) {
            this.au = null;
        } else if (frequency.gu > 0) {
            this.au = frequency;
        } else {
            ev.g(this.J != null ? this.J.getContext().getString(R.string.DateTimeAxisInvalidDateFrequency) : "The DateFrequency is invalid and will be ignored");
            this.au = null;
        }
    }

    void F() {
        if (this.at != null) {
            this.av = ((DateFrequency) this.at).clone();
        }
    }

    void G() {
        if (A() && this.au != null) {
            this.aw = ((DateFrequency) this.au).clone();
        }
    }

    Date getDefaultBaseline() {
        return new Date(this.gE);
    }

    Date applyMappingForSkipRangesToUserValue(Date userValue) {
        return new Date((long) this.aK.g((double) userValue.getTime()));
    }

    double convertUserValueTypeToInternalDataType(Object rawUserValue) {
        return (double) ((Date) rawUserValue).getTime();
    }

    Date removeMappingForSkipRangesFromChartValue(Date userValue) {
        return new Date((long) this.aL.g((double) userValue.getTime()));
    }

    public void addRepeatedSkipRange(RepeatedTimePeriod repeatedTimePeriod) {
        if (this.J != null) {
            this.J.bv();
        }
        if (repeatedTimePeriod == null) {
            ev.g(this.J != null ? this.J.getContext().getString(R.string.CannotAddNullRepeatedSkipRange) : "Cannot add a null repeated skip range.");
            return;
        }
        List a = this.gI.a(repeatedTimePeriod, cd());
        j(a);
        this.gG.put(repeatedTimePeriod, a);
        if (!a.isEmpty()) {
            Y();
        }
    }

    private void j(List<Range<Date>> list) {
        Collection arrayList = new ArrayList();
        for (Range range : list) {
            if (!e(range)) {
                arrayList.add(range);
                f(range);
            }
        }
        list.removeAll(arrayList);
    }

    private void f(Range<Date> range) {
        String date = ((Date) range.getMinimum()).toString();
        String date2 = ((Date) range.getMaximum()).toString();
        if (this.J != null) {
            date2 = this.J.getContext().getString(R.string.CannotAddCalculatedUndefinedOrEmptySkip, new Object[]{date, date2});
        } else {
            date2 = String.format("Calculated skip range with min: %1$s and max: %2$s is invalid: cannot be added as it has zero or negative span", new Object[]{date, date2});
        }
        ev.g(date2);
    }

    private DateRange cd() {
        return new DateRange((Date) transformInternalValueToUser(this.aF.dW()), (Date) transformInternalValueToUser(this.aF.dY()));
    }

    public void removeRepeatedSkipRange(RepeatedTimePeriod repeatedTimePeriod) {
        if (((List) this.gG.remove(repeatedTimePeriod)) != null) {
            Y();
        }
    }

    public void removeAllRepeatedSkipRanges() {
        List<RepeatedTimePeriod> arrayList = new ArrayList(this.gG.keySet());
        if (!arrayList.isEmpty()) {
            for (RepeatedTimePeriod remove : arrayList) {
                this.gG.remove(remove);
            }
            Y();
        }
    }

    public List<RepeatedTimePeriod> getRepeatedSkipRanges() {
        return Collections.unmodifiableList(new ArrayList(this.gG.keySet()));
    }

    List<Range<Date>> aa() {
        List<Range<Date>> arrayList = new ArrayList(super.aa());
        for (List addAll : this.gG.values()) {
            arrayList.addAll(addAll);
        }
        return arrayList;
    }

    void s() {
        if (!this.gG.isEmpty()) {
            Range cd = cd();
            if (this.gI.g(cd)) {
                for (RepeatedTimePeriod repeatedTimePeriod : this.gG.keySet()) {
                    List a = this.gI.a(repeatedTimePeriod, cd);
                    j(a);
                    this.gG.put(repeatedTimePeriod, a);
                }
                Y();
            }
        }
    }

    void a(af afVar) {
        super.a(afVar);
        if (afVar != null && !this.gG.isEmpty()) {
            afVar.bv();
        }
    }

    void Y() {
        this.gH.invalidate();
        super.Y();
    }

    double E() {
        if (this.gJ == null) {
            return 0.0d;
        }
        return transformUserValueToInternal(this.gJ);
    }

    public void disableTickMarkCaching(boolean disableTickMarkCaching) {
        this.gH.gP = disableTickMarkCaching;
    }

    public boolean isTickMarkCachingDisabled() {
        return this.gH.gP;
    }

    public final double getZoomLevel() {
        return super.getZoomLevel();
    }
}
