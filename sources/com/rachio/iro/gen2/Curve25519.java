package com.rachio.iro.gen2;

public class Curve25519 {
    private static final long10 BASE_2Y = new long10(39999547, 18689728, 59995525, 1648697, 57546132, 24010086, 19059592, 5425144, 63499247, 16420658);
    private static final long10 BASE_R2Y = new long10(5744, 8160848, 4790893, 13779497, 35730846, 12541209, 49101323, 30047407, 40071253, 6226132);
    public static final byte[] ORDER = new byte[]{(byte) -19, (byte) -45, (byte) -11, (byte) 92, (byte) 26, (byte) 99, (byte) 18, (byte) 88, (byte) -42, (byte) -100, (byte) -9, (byte) -94, (byte) -34, (byte) -7, (byte) -34, (byte) 20, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 16};
    private static final byte[] ORDER_TIMES_8 = new byte[]{(byte) 104, (byte) -97, (byte) -82, (byte) -25, (byte) -46, (byte) 24, (byte) -109, (byte) -64, (byte) -78, (byte) -26, (byte) -68, (byte) 23, (byte) -11, (byte) -50, (byte) -9, (byte) -90, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, Byte.MIN_VALUE};
    public static final byte[] PRIME = new byte[]{(byte) -19, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, Byte.MAX_VALUE};
    public static final byte[] ZERO = new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};

    private static final class long10 {
        public long _0;
        public long _1;
        public long _2;
        public long _3;
        public long _4;
        public long _5;
        public long _6;
        public long _7;
        public long _8;
        public long _9;

        public long10(long _0, long _1, long _2, long _3, long _4, long _5, long _6, long _7, long _8, long _9) {
            this._0 = _0;
            this._1 = _1;
            this._2 = _2;
            this._3 = _3;
            this._4 = _4;
            this._5 = _5;
            this._6 = _6;
            this._7 = _7;
            this._8 = _8;
            this._9 = _9;
        }
    }

    public static final void keygen(byte[] P, byte[] s, byte[] k) {
        k[31] = (byte) (k[31] & 127);
        k[31] = (byte) (k[31] | 64);
        k[0] = (byte) (k[0] & 248);
        core(P, null, k, null);
    }

    public static final void curve(byte[] Z, byte[] k, byte[] P) {
        core(Z, null, k, P);
    }

    private static final void cpy32(byte[] d, byte[] s) {
        for (int i = 0; i < 32; i++) {
            d[i] = s[i];
        }
    }

    private static final int mula_small(byte[] p, byte[] q, int m, byte[] x, int n, int z) {
        int v = 0;
        for (int i = 0; i < n; i++) {
            v += (q[i + m] & 255) + ((x[i] & 255) * z);
            p[i + m] = (byte) v;
            v >>= 8;
        }
        return v;
    }

    private static final int mula32(byte[] p, byte[] x, byte[] y, int t, int z) {
        int w = 0;
        int i = 0;
        while (i < t) {
            int zy = (y[i] & 255) * -1;
            w += (mula_small(p, p, i, x, 31, zy) + (p[i + 31] & 255)) + ((x[31] & 255) * zy);
            p[i + 31] = (byte) w;
            w >>= 8;
            i++;
        }
        p[i + 31] = (byte) ((p[i + 31] & 255) + w);
        return w >> 8;
    }

    private static final void divmod(byte[] q, byte[] r, int n, byte[] d, int t) {
        int n2;
        int rn = 0;
        int dt = (d[t - 1] & 255) << 8;
        if (t > 1) {
            dt |= d[t - 2] & 255;
            n2 = n;
        } else {
            n2 = n;
        }
        while (true) {
            n = n2 - 1;
            if (n2 >= t) {
                int z = (rn << 16) | ((r[n] & 255) << 8);
                if (n > 0) {
                    z |= r[n - 1] & 255;
                }
                z /= dt;
                rn += mula_small(r, r, (n - t) + 1, d, t, -z);
                q[(n - t) + 1] = (byte) (z + rn);
                mula_small(r, r, (n - t) + 1, d, t, -rn);
                rn = r[n] & 255;
                r[n] = (byte) 0;
                n2 = n;
            } else {
                r[t - 1] = (byte) rn;
                return;
            }
        }
    }

    private static final int numsize(byte[] x, int n) {
        int n2 = n;
        while (true) {
            n = n2 - 1;
            if (n2 != 0 && x[n] == (byte) 0) {
                n2 = n;
            }
        }
        return n + 1;
    }

    private static final boolean is_overflow(long10 x) {
        return (x._0 > 67108844 && ((((x._1 & x._3) & x._5) & x._7) & x._9) == 33554431 && (((x._2 & x._4) & x._6) & x._8) == 67108863) || x._9 > 33554431;
    }

    private static final void set(long10 out, int in) {
        out._0 = (long) in;
        out._1 = 0;
        out._2 = 0;
        out._3 = 0;
        out._4 = 0;
        out._5 = 0;
        out._6 = 0;
        out._7 = 0;
        out._8 = 0;
        out._9 = 0;
    }

    private static final void add(long10 xy, long10 x, long10 y) {
        xy._0 = x._0 + y._0;
        xy._1 = x._1 + y._1;
        xy._2 = x._2 + y._2;
        xy._3 = x._3 + y._3;
        xy._4 = x._4 + y._4;
        xy._5 = x._5 + y._5;
        xy._6 = x._6 + y._6;
        xy._7 = x._7 + y._7;
        xy._8 = x._8 + y._8;
        xy._9 = x._9 + y._9;
    }

    private static final void sub(long10 xy, long10 x, long10 y) {
        xy._0 = x._0 - y._0;
        xy._1 = x._1 - y._1;
        xy._2 = x._2 - y._2;
        xy._3 = x._3 - y._3;
        xy._4 = x._4 - y._4;
        xy._5 = x._5 - y._5;
        xy._6 = x._6 - y._6;
        xy._7 = x._7 - y._7;
        xy._8 = x._8 - y._8;
        xy._9 = x._9 - y._9;
    }

    private static final long10 mul_small(long10 xy, long10 x, long y) {
        long t = x._8 * y;
        xy._8 = t & 67108863;
        t = (t >> 26) + (x._9 * y);
        xy._9 = t & 33554431;
        t = (19 * (t >> 25)) + (x._0 * y);
        xy._0 = t & 67108863;
        t = (t >> 26) + (x._1 * y);
        xy._1 = t & 33554431;
        t = (t >> 25) + (x._2 * y);
        xy._2 = t & 67108863;
        t = (t >> 26) + (x._3 * y);
        xy._3 = t & 33554431;
        t = (t >> 25) + (x._4 * y);
        xy._4 = t & 67108863;
        t = (t >> 26) + (x._5 * y);
        xy._5 = t & 33554431;
        t = (t >> 25) + (x._6 * y);
        xy._6 = t & 67108863;
        t = (t >> 26) + (x._7 * y);
        xy._7 = t & 33554431;
        t = (t >> 25) + xy._8;
        xy._8 = t & 67108863;
        xy._9 += t >> 26;
        return xy;
    }

    private static final long10 mul(long10 xy, long10 x, long10 y) {
        long x_0 = x._0;
        long x_1 = x._1;
        long x_2 = x._2;
        long x_3 = x._3;
        long x_4 = x._4;
        long x_5 = x._5;
        long x_6 = x._6;
        long x_7 = x._7;
        long x_8 = x._8;
        long x_9 = x._9;
        long y_0 = y._0;
        long y_1 = y._1;
        long y_2 = y._2;
        long y_3 = y._3;
        long y_4 = y._4;
        long y_5 = y._5;
        long y_6 = y._6;
        long y_7 = y._7;
        long y_8 = y._8;
        long y_9 = y._9;
        long t = ((((((x_0 * y_8) + (x_2 * y_6)) + (x_4 * y_4)) + (x_6 * y_2)) + (x_8 * y_0)) + (2 * ((((x_1 * y_7) + (x_3 * y_5)) + (x_5 * y_3)) + (x_7 * y_1)))) + (38 * (x_9 * y_9));
        xy._8 = 67108863 & t;
        t = ((((((((((t >> 26) + (x_0 * y_9)) + (x_1 * y_8)) + (x_2 * y_7)) + (x_3 * y_6)) + (x_4 * y_5)) + (x_5 * y_4)) + (x_6 * y_3)) + (x_7 * y_2)) + (x_8 * y_1)) + (x_9 * y_0);
        xy._9 = 33554431 & t;
        t = ((x_0 * y_0) + (19 * (((((t >> 25) + (x_2 * y_8)) + (x_4 * y_6)) + (x_6 * y_4)) + (x_8 * y_2)))) + (38 * (((((x_1 * y_9) + (x_3 * y_7)) + (x_5 * y_5)) + (x_7 * y_3)) + (x_9 * y_1)));
        xy._0 = 67108863 & t;
        t = (((t >> 26) + (x_0 * y_1)) + (x_1 * y_0)) + (19 * ((((((((x_2 * y_9) + (x_3 * y_8)) + (x_4 * y_7)) + (x_5 * y_6)) + (x_6 * y_5)) + (x_7 * y_4)) + (x_8 * y_3)) + (x_9 * y_2)));
        xy._1 = 33554431 & t;
        t = (((((t >> 25) + (x_0 * y_2)) + (x_2 * y_0)) + (19 * (((x_4 * y_8) + (x_6 * y_6)) + (x_8 * y_4)))) + (2 * (x_1 * y_1))) + (38 * ((((x_3 * y_9) + (x_5 * y_7)) + (x_7 * y_5)) + (x_9 * y_3)));
        xy._2 = 67108863 & t;
        t = (((((t >> 26) + (x_0 * y_3)) + (x_1 * y_2)) + (x_2 * y_1)) + (x_3 * y_0)) + (19 * ((((((x_4 * y_9) + (x_5 * y_8)) + (x_6 * y_7)) + (x_7 * y_6)) + (x_8 * y_5)) + (x_9 * y_4)));
        xy._3 = 33554431 & t;
        t = ((((((t >> 25) + (x_0 * y_4)) + (x_2 * y_2)) + (x_4 * y_0)) + (19 * ((x_6 * y_8) + (x_8 * y_6)))) + (2 * ((x_1 * y_3) + (x_3 * y_1)))) + (38 * (((x_5 * y_9) + (x_7 * y_7)) + (x_9 * y_5)));
        xy._4 = 67108863 & t;
        t = (((((((t >> 26) + (x_0 * y_5)) + (x_1 * y_4)) + (x_2 * y_3)) + (x_3 * y_2)) + (x_4 * y_1)) + (x_5 * y_0)) + (19 * ((((x_6 * y_9) + (x_7 * y_8)) + (x_8 * y_7)) + (x_9 * y_6)));
        xy._5 = 33554431 & t;
        t = (((((((t >> 25) + (x_0 * y_6)) + (x_2 * y_4)) + (x_4 * y_2)) + (x_6 * y_0)) + (19 * (x_8 * y_8))) + (2 * (((x_1 * y_5) + (x_3 * y_3)) + (x_5 * y_1)))) + (38 * ((x_7 * y_9) + (x_9 * y_7)));
        xy._6 = 67108863 & t;
        t = (((((((((t >> 26) + (x_0 * y_7)) + (x_1 * y_6)) + (x_2 * y_5)) + (x_3 * y_4)) + (x_4 * y_3)) + (x_5 * y_2)) + (x_6 * y_1)) + (x_7 * y_0)) + (19 * ((x_8 * y_9) + (x_9 * y_8)));
        xy._7 = 33554431 & t;
        t = (t >> 25) + xy._8;
        xy._8 = 67108863 & t;
        xy._9 += t >> 26;
        return xy;
    }

    private static final long10 sqr(long10 x2, long10 x) {
        long x_0 = x._0;
        long x_1 = x._1;
        long x_2 = x._2;
        long x_3 = x._3;
        long x_4 = x._4;
        long x_5 = x._5;
        long x_6 = x._6;
        long x_7 = x._7;
        long x_8 = x._8;
        long x_9 = x._9;
        long t = (((x_4 * x_4) + (2 * ((x_0 * x_8) + (x_2 * x_6)))) + (38 * (x_9 * x_9))) + (4 * ((x_1 * x_7) + (x_3 * x_5)));
        x2._8 = 67108863 & t;
        t = (t >> 26) + (2 * (((((x_0 * x_9) + (x_1 * x_8)) + (x_2 * x_7)) + (x_3 * x_6)) + (x_4 * x_5)));
        x2._9 = 33554431 & t;
        t = (((19 * (t >> 25)) + (x_0 * x_0)) + (38 * (((x_2 * x_8) + (x_4 * x_6)) + (x_5 * x_5)))) + (76 * ((x_1 * x_9) + (x_3 * x_7)));
        x2._0 = 67108863 & t;
        t = ((t >> 26) + (2 * (x_0 * x_1))) + (38 * ((((x_2 * x_9) + (x_3 * x_8)) + (x_4 * x_7)) + (x_5 * x_6)));
        x2._1 = 33554431 & t;
        t = ((((t >> 25) + (19 * (x_6 * x_6))) + (2 * ((x_0 * x_2) + (x_1 * x_1)))) + (38 * (x_4 * x_8))) + (76 * ((x_3 * x_9) + (x_5 * x_7)));
        x2._2 = 67108863 & t;
        t = ((t >> 26) + (2 * ((x_0 * x_3) + (x_1 * x_2)))) + (38 * (((x_4 * x_9) + (x_5 * x_8)) + (x_6 * x_7)));
        x2._3 = 33554431 & t;
        t = (((((t >> 25) + (x_2 * x_2)) + (2 * (x_0 * x_4))) + (38 * ((x_6 * x_8) + (x_7 * x_7)))) + (4 * (x_1 * x_3))) + (76 * (x_5 * x_9));
        x2._4 = 67108863 & t;
        t = ((t >> 26) + (2 * (((x_0 * x_5) + (x_1 * x_4)) + (x_2 * x_3)))) + (38 * ((x_6 * x_9) + (x_7 * x_8)));
        x2._5 = 33554431 & t;
        t = ((((t >> 25) + (19 * (x_8 * x_8))) + (2 * (((x_0 * x_6) + (x_2 * x_4)) + (x_3 * x_3)))) + (4 * (x_1 * x_5))) + (76 * (x_7 * x_9));
        x2._6 = 67108863 & t;
        t = ((t >> 26) + (2 * ((((x_0 * x_7) + (x_1 * x_6)) + (x_2 * x_5)) + (x_3 * x_4)))) + (38 * (x_8 * x_9));
        x2._7 = 33554431 & t;
        t = (t >> 25) + x2._8;
        x2._8 = 67108863 & t;
        x2._9 += t >> 26;
        return x2;
    }

    private static final void recip(long10 y, long10 x, int sqrtassist) {
        int i;
        long10 t0 = new long10();
        long10 t1 = new long10();
        long10 t2 = new long10();
        long10 t3 = new long10();
        long10 t4 = new long10();
        sqr(t1, x);
        sqr(t2, t1);
        sqr(t0, t2);
        mul(t2, t0, x);
        mul(t0, t2, t1);
        sqr(t1, t0);
        mul(t3, t1, t2);
        sqr(t1, t3);
        sqr(t2, t1);
        sqr(t1, t2);
        sqr(t2, t1);
        sqr(t1, t2);
        mul(t2, t1, t3);
        sqr(t1, t2);
        sqr(t3, t1);
        for (i = 1; i < 5; i++) {
            sqr(t1, t3);
            sqr(t3, t1);
        }
        mul(t1, t3, t2);
        sqr(t3, t1);
        sqr(t4, t3);
        for (i = 1; i < 10; i++) {
            sqr(t3, t4);
            sqr(t4, t3);
        }
        mul(t3, t4, t1);
        for (i = 0; i < 5; i++) {
            sqr(t1, t3);
            sqr(t3, t1);
        }
        mul(t1, t3, t2);
        sqr(t2, t1);
        sqr(t3, t2);
        for (i = 1; i < 25; i++) {
            sqr(t2, t3);
            sqr(t3, t2);
        }
        mul(t2, t3, t1);
        sqr(t3, t2);
        sqr(t4, t3);
        for (i = 1; i < 50; i++) {
            sqr(t3, t4);
            sqr(t4, t3);
        }
        mul(t3, t4, t2);
        for (i = 0; i < 25; i++) {
            sqr(t4, t3);
            sqr(t3, t4);
        }
        mul(t2, t3, t1);
        sqr(t1, t2);
        sqr(t2, t1);
        sqr(t1, t2);
        sqr(t2, t1);
        sqr(t1, t2);
        mul(y, t1, t0);
    }

    private static final void mont_prep(long10 t1, long10 t2, long10 ax, long10 az) {
        add(t1, ax, az);
        sub(t2, ax, az);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static final void core(byte[] r30, byte[] r31, byte[] r32, byte[] r33) {
        /*
        r16 = new com.rachio.iro.gen2.Curve25519$long10;
        r16.<init>();
        r21 = new com.rachio.iro.gen2.Curve25519$long10;
        r21.<init>();
        r22 = new com.rachio.iro.gen2.Curve25519$long10;
        r22.<init>();
        r23 = new com.rachio.iro.gen2.Curve25519$long10;
        r23.<init>();
        r24 = new com.rachio.iro.gen2.Curve25519$long10;
        r24.<init>();
        r4 = 2;
        r0 = new com.rachio.iro.gen2.Curve25519.long10[r4];
        r28 = r0;
        r4 = 0;
        r5 = new com.rachio.iro.gen2.Curve25519$long10;
        r5.<init>();
        r28[r4] = r5;
        r4 = 1;
        r5 = new com.rachio.iro.gen2.Curve25519$long10;
        r5.<init>();
        r28[r4] = r5;
        r4 = 2;
        r0 = new com.rachio.iro.gen2.Curve25519.long10[r4];
        r29 = r0;
        r4 = 0;
        r5 = new com.rachio.iro.gen2.Curve25519$long10;
        r5.<init>();
        r29[r4] = r5;
        r4 = 1;
        r5 = new com.rachio.iro.gen2.Curve25519$long10;
        r5.<init>();
        r29[r4] = r5;
        if (r33 == 0) goto L_0x02d6;
    L_0x0045:
        r4 = 0;
        r4 = r33[r4];
        r4 = r4 & 255;
        r5 = 1;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 << 8;
        r4 = r4 | r5;
        r5 = 2;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 << 16;
        r4 = r4 | r5;
        r5 = 3;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 & 3;
        r5 = r5 << 24;
        r4 = r4 | r5;
        r4 = (long) r4;
        r0 = r16;
        r0._0 = r4;
        r4 = 3;
        r4 = r33[r4];
        r4 = r4 & 255;
        r4 = r4 & -4;
        r4 = r4 >> 2;
        r5 = 4;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 << 6;
        r4 = r4 | r5;
        r5 = 5;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 << 14;
        r4 = r4 | r5;
        r5 = 6;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 & 7;
        r5 = r5 << 22;
        r4 = r4 | r5;
        r4 = (long) r4;
        r0 = r16;
        r0._1 = r4;
        r4 = 6;
        r4 = r33[r4];
        r4 = r4 & 255;
        r4 = r4 & -8;
        r4 = r4 >> 3;
        r5 = 7;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 << 5;
        r4 = r4 | r5;
        r5 = 8;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 << 13;
        r4 = r4 | r5;
        r5 = 9;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 & 31;
        r5 = r5 << 21;
        r4 = r4 | r5;
        r4 = (long) r4;
        r0 = r16;
        r0._2 = r4;
        r4 = 9;
        r4 = r33[r4];
        r4 = r4 & 255;
        r4 = r4 & -32;
        r4 = r4 >> 5;
        r5 = 10;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 << 3;
        r4 = r4 | r5;
        r5 = 11;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 << 11;
        r4 = r4 | r5;
        r5 = 12;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 & 63;
        r5 = r5 << 19;
        r4 = r4 | r5;
        r4 = (long) r4;
        r0 = r16;
        r0._3 = r4;
        r4 = 12;
        r4 = r33[r4];
        r4 = r4 & 255;
        r4 = r4 & -64;
        r4 = r4 >> 6;
        r5 = 13;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 << 2;
        r4 = r4 | r5;
        r5 = 14;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 << 10;
        r4 = r4 | r5;
        r5 = 15;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 << 18;
        r4 = r4 | r5;
        r4 = (long) r4;
        r0 = r16;
        r0._4 = r4;
        r4 = 16;
        r4 = r33[r4];
        r4 = r4 & 255;
        r5 = 17;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 << 8;
        r4 = r4 | r5;
        r5 = 18;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 << 16;
        r4 = r4 | r5;
        r5 = 19;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 & 1;
        r5 = r5 << 24;
        r4 = r4 | r5;
        r4 = (long) r4;
        r0 = r16;
        r0._5 = r4;
        r4 = 19;
        r4 = r33[r4];
        r4 = r4 & 255;
        r4 = r4 & -2;
        r4 = r4 >> 1;
        r5 = 20;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 << 7;
        r4 = r4 | r5;
        r5 = 21;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 << 15;
        r4 = r4 | r5;
        r5 = 22;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 & 7;
        r5 = r5 << 23;
        r4 = r4 | r5;
        r4 = (long) r4;
        r0 = r16;
        r0._6 = r4;
        r4 = 22;
        r4 = r33[r4];
        r4 = r4 & 255;
        r4 = r4 & -8;
        r4 = r4 >> 3;
        r5 = 23;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 << 5;
        r4 = r4 | r5;
        r5 = 24;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 << 13;
        r4 = r4 | r5;
        r5 = 25;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 & 15;
        r5 = r5 << 21;
        r4 = r4 | r5;
        r4 = (long) r4;
        r0 = r16;
        r0._7 = r4;
        r4 = 25;
        r4 = r33[r4];
        r4 = r4 & 255;
        r4 = r4 & -16;
        r4 = r4 >> 4;
        r5 = 26;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 << 4;
        r4 = r4 | r5;
        r5 = 27;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 << 12;
        r4 = r4 | r5;
        r5 = 28;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 & 63;
        r5 = r5 << 20;
        r4 = r4 | r5;
        r4 = (long) r4;
        r0 = r16;
        r0._8 = r4;
        r4 = 28;
        r4 = r33[r4];
        r4 = r4 & 255;
        r4 = r4 & -64;
        r4 = r4 >> 6;
        r5 = 29;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 << 2;
        r4 = r4 | r5;
        r5 = 30;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 << 10;
        r4 = r4 | r5;
        r5 = 31;
        r5 = r33[r5];
        r5 = r5 & 255;
        r5 = r5 << 18;
        r4 = r4 | r5;
        r4 = (long) r4;
        r0 = r16;
        r0._9 = r4;
    L_0x01e7:
        r4 = 0;
        r4 = r28[r4];
        r5 = 1;
        set(r4, r5);
        r4 = 0;
        r4 = r29[r4];
        r5 = 0;
        set(r4, r5);
        r4 = 1;
        r4 = r28[r4];
        r0 = r16;
        r6 = r0._0;
        r4._0 = r6;
        r0 = r16;
        r6 = r0._1;
        r4._1 = r6;
        r0 = r16;
        r6 = r0._2;
        r4._2 = r6;
        r0 = r16;
        r6 = r0._3;
        r4._3 = r6;
        r0 = r16;
        r6 = r0._4;
        r4._4 = r6;
        r0 = r16;
        r6 = r0._5;
        r4._5 = r6;
        r0 = r16;
        r6 = r0._6;
        r4._6 = r6;
        r0 = r16;
        r6 = r0._7;
        r4._7 = r6;
        r0 = r16;
        r6 = r0._8;
        r4._8 = r6;
        r0 = r16;
        r6 = r0._9;
        r4._9 = r6;
        r4 = 1;
        r4 = r29[r4];
        r5 = 1;
        set(r4, r5);
        r17 = 32;
        r18 = r17;
    L_0x023f:
        r17 = r18 + -1;
        if (r18 == 0) goto L_0x02df;
    L_0x0243:
        if (r17 != 0) goto L_0x0247;
    L_0x0245:
        r17 = 0;
    L_0x0247:
        r19 = 8;
        r20 = r19;
    L_0x024b:
        r19 = r20 + -1;
        if (r20 == 0) goto L_0x05d9;
    L_0x024f:
        r4 = r32[r17];
        r4 = r4 & 255;
        r4 = r4 >> r19;
        r13 = r4 & 1;
        r4 = r32[r17];
        r4 = r4 & 255;
        r4 = r4 ^ -1;
        r4 = r4 >> r19;
        r12 = r4 & 1;
        r10 = r28[r12];
        r11 = r29[r12];
        r14 = r28[r13];
        r15 = r29[r13];
        r0 = r21;
        r1 = r22;
        mont_prep(r0, r1, r10, r11);
        r0 = r23;
        r1 = r24;
        mont_prep(r0, r1, r14, r15);
        r0 = r22;
        r1 = r23;
        mul(r10, r0, r1);
        r0 = r21;
        r1 = r24;
        mul(r11, r0, r1);
        r0 = r21;
        add(r0, r10, r11);
        r0 = r22;
        sub(r0, r10, r11);
        r0 = r21;
        sqr(r10, r0);
        sqr(r21, r22);
        r0 = r21;
        r1 = r16;
        mul(r11, r0, r1);
        r0 = r21;
        r1 = r23;
        sqr(r0, r1);
        r0 = r22;
        r1 = r24;
        sqr(r0, r1);
        r0 = r21;
        r1 = r22;
        mul(r14, r0, r1);
        r0 = r22;
        r1 = r21;
        r2 = r22;
        sub(r0, r1, r2);
        r4 = 121665; // 0x1db41 float:1.70489E-40 double:6.01105E-319;
        r0 = r22;
        mul_small(r15, r0, r4);
        r0 = r21;
        r1 = r21;
        add(r0, r1, r15);
        r0 = r21;
        r1 = r22;
        mul(r15, r0, r1);
        r20 = r19;
        goto L_0x024b;
    L_0x02d6:
        r4 = 9;
        r0 = r16;
        set(r0, r4);
        goto L_0x01e7;
    L_0x02df:
        r4 = 0;
        r4 = r29[r4];
        r5 = 0;
        r0 = r21;
        recip(r0, r4, r5);
        r4 = 0;
        r4 = r28[r4];
        r0 = r16;
        r1 = r21;
        mul(r0, r4, r1);
        r4 = is_overflow(r16);
        if (r4 == 0) goto L_0x055a;
    L_0x02f8:
        r4 = 1;
    L_0x02f9:
        r0 = r16;
        r6 = r0._9;
        r8 = 0;
        r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r5 >= 0) goto L_0x055d;
    L_0x0303:
        r5 = 1;
    L_0x0304:
        r4 = r4 - r5;
        r5 = -33554432; // 0xfffffffffe000000 float:-4.2535296E37 double:NaN;
        r5 = r5 * r4;
        r4 = r4 * 19;
        r6 = (long) r4;
        r0 = r16;
        r8 = r0._0;
        r6 = r6 + r8;
        r0 = r16;
        r8 = r0._1;
        r4 = 26;
        r8 = r8 << r4;
        r6 = r6 + r8;
        r4 = 0;
        r8 = (int) r6;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 1;
        r8 = 8;
        r8 = r6 >> r8;
        r8 = (int) r8;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 2;
        r8 = 16;
        r8 = r6 >> r8;
        r8 = (int) r8;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 3;
        r8 = 24;
        r8 = r6 >> r8;
        r8 = (int) r8;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 32;
        r6 = r6 >> r4;
        r0 = r16;
        r8 = r0._2;
        r4 = 19;
        r8 = r8 << r4;
        r6 = r6 + r8;
        r4 = 4;
        r8 = (int) r6;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 5;
        r8 = 8;
        r8 = r6 >> r8;
        r8 = (int) r8;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 6;
        r8 = 16;
        r8 = r6 >> r8;
        r8 = (int) r8;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 7;
        r8 = 24;
        r8 = r6 >> r8;
        r8 = (int) r8;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 32;
        r6 = r6 >> r4;
        r0 = r16;
        r8 = r0._3;
        r4 = 13;
        r8 = r8 << r4;
        r6 = r6 + r8;
        r4 = 8;
        r8 = (int) r6;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 9;
        r8 = 8;
        r8 = r6 >> r8;
        r8 = (int) r8;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 10;
        r8 = 16;
        r8 = r6 >> r8;
        r8 = (int) r8;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 11;
        r8 = 24;
        r8 = r6 >> r8;
        r8 = (int) r8;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 32;
        r6 = r6 >> r4;
        r0 = r16;
        r8 = r0._4;
        r4 = 6;
        r8 = r8 << r4;
        r6 = r6 + r8;
        r4 = 12;
        r8 = (int) r6;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 13;
        r8 = 8;
        r8 = r6 >> r8;
        r8 = (int) r8;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 14;
        r8 = 16;
        r8 = r6 >> r8;
        r8 = (int) r8;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 15;
        r8 = 24;
        r8 = r6 >> r8;
        r8 = (int) r8;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 32;
        r6 = r6 >> r4;
        r0 = r16;
        r8 = r0._5;
        r6 = r6 + r8;
        r0 = r16;
        r8 = r0._6;
        r4 = 25;
        r8 = r8 << r4;
        r6 = r6 + r8;
        r4 = 16;
        r8 = (int) r6;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 17;
        r8 = 8;
        r8 = r6 >> r8;
        r8 = (int) r8;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 18;
        r8 = 16;
        r8 = r6 >> r8;
        r8 = (int) r8;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 19;
        r8 = 24;
        r8 = r6 >> r8;
        r8 = (int) r8;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 32;
        r6 = r6 >> r4;
        r0 = r16;
        r8 = r0._7;
        r4 = 19;
        r8 = r8 << r4;
        r6 = r6 + r8;
        r4 = 20;
        r8 = (int) r6;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 21;
        r8 = 8;
        r8 = r6 >> r8;
        r8 = (int) r8;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 22;
        r8 = 16;
        r8 = r6 >> r8;
        r8 = (int) r8;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 23;
        r8 = 24;
        r8 = r6 >> r8;
        r8 = (int) r8;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 32;
        r6 = r6 >> r4;
        r0 = r16;
        r8 = r0._8;
        r4 = 12;
        r8 = r8 << r4;
        r6 = r6 + r8;
        r4 = 24;
        r8 = (int) r6;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 25;
        r8 = 8;
        r8 = r6 >> r8;
        r8 = (int) r8;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 26;
        r8 = 16;
        r8 = r6 >> r8;
        r8 = (int) r8;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 27;
        r8 = 24;
        r8 = r6 >> r8;
        r8 = (int) r8;
        r8 = (byte) r8;
        r30[r4] = r8;
        r4 = 32;
        r6 = r6 >> r4;
        r0 = r16;
        r8 = r0._9;
        r4 = (long) r5;
        r4 = r4 + r8;
        r8 = 6;
        r4 = r4 << r8;
        r4 = r4 + r6;
        r6 = 28;
        r7 = (int) r4;
        r7 = (byte) r7;
        r30[r6] = r7;
        r6 = 29;
        r7 = 8;
        r8 = r4 >> r7;
        r7 = (int) r8;
        r7 = (byte) r7;
        r30[r6] = r7;
        r6 = 30;
        r7 = 16;
        r8 = r4 >> r7;
        r7 = (int) r8;
        r7 = (byte) r7;
        r30[r6] = r7;
        r6 = 31;
        r7 = 24;
        r4 = r4 >> r7;
        r4 = (int) r4;
        r4 = (byte) r4;
        r30[r6] = r4;
        if (r31 == 0) goto L_0x059a;
    L_0x0483:
        r0 = r22;
        r1 = r16;
        sqr(r0, r1);
        r4 = 486662; // 0x76d06 float:6.81959E-40 double:2.40443E-318;
        r0 = r21;
        r1 = r16;
        mul_small(r0, r1, r4);
        r0 = r22;
        r1 = r22;
        r2 = r21;
        add(r0, r1, r2);
        r0 = r22;
        r4 = r0._0;
        r6 = 1;
        r4 = r4 + r6;
        r0 = r22;
        r0._0 = r4;
        r0 = r21;
        r1 = r22;
        r2 = r16;
        mul(r0, r1, r2);
        r4 = 1;
        r4 = r29[r4];
        r5 = 0;
        r0 = r23;
        recip(r0, r4, r5);
        r4 = 1;
        r4 = r28[r4];
        r0 = r22;
        r1 = r23;
        mul(r0, r4, r1);
        r0 = r22;
        r1 = r22;
        r2 = r16;
        add(r0, r1, r2);
        r0 = r22;
        r4 = r0._0;
        r6 = 486671; // 0x76d0f float:6.81971E-40 double:2.404474E-318;
        r4 = r4 + r6;
        r0 = r22;
        r0._0 = r4;
        r0 = r16;
        r4 = r0._0;
        r6 = 9;
        r4 = r4 - r6;
        r0 = r16;
        r0._0 = r4;
        r0 = r23;
        r1 = r16;
        sqr(r0, r1);
        r0 = r16;
        r1 = r22;
        r2 = r23;
        mul(r0, r1, r2);
        r0 = r16;
        r1 = r16;
        r2 = r21;
        sub(r0, r1, r2);
        r0 = r16;
        r4 = r0._0;
        r6 = 39420360; // 0x25981c8 float:1.5979888E-37 double:1.94762456E-316;
        r4 = r4 - r6;
        r0 = r16;
        r0._0 = r4;
        r4 = BASE_R2Y;
        r0 = r21;
        r1 = r16;
        mul(r0, r1, r4);
        r4 = is_overflow(r21);
        if (r4 != 0) goto L_0x0522;
    L_0x0518:
        r0 = r21;
        r4 = r0._9;
        r6 = 0;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 >= 0) goto L_0x0560;
    L_0x0522:
        r4 = 1;
    L_0x0523:
        r4 = (long) r4;
        r0 = r21;
        r6 = r0._0;
        r8 = 1;
        r6 = r6 & r8;
        r4 = r4 ^ r6;
        r4 = (int) r4;
        if (r4 == 0) goto L_0x0562;
    L_0x052f:
        cpy32(r31, r32);
    L_0x0532:
        r4 = 32;
        r0 = new byte[r4];
        r25 = r0;
        r4 = 64;
        r0 = new byte[r4];
        r26 = r0;
        r4 = 64;
        r0 = new byte[r4];
        r27 = r0;
        r4 = ORDER;
        r0 = r25;
        cpy32(r0, r4);
        r4 = 32;
        r5 = 0;
    L_0x054e:
        r6 = 32;
        if (r5 >= r6) goto L_0x0570;
    L_0x0552:
        r6 = 0;
        r27[r5] = r6;
        r26[r5] = r6;
        r5 = r5 + 1;
        goto L_0x054e;
    L_0x055a:
        r4 = 0;
        goto L_0x02f9;
    L_0x055d:
        r5 = 0;
        goto L_0x0304;
    L_0x0560:
        r4 = 0;
        goto L_0x0523;
    L_0x0562:
        r5 = ORDER_TIMES_8;
        r6 = 0;
        r8 = 32;
        r9 = -1;
        r4 = r31;
        r7 = r32;
        mula_small(r4, r5, r6, r7, r8, r9);
        goto L_0x0532;
    L_0x0570:
        r5 = 0;
        r6 = 1;
        r26[r5] = r6;
        r5 = 32;
        r0 = r31;
        r5 = numsize(r0, r5);
        if (r5 != 0) goto L_0x059b;
    L_0x057e:
        r0 = r31;
        r1 = r27;
        cpy32(r0, r1);
        r4 = 31;
        r4 = r31[r4];
        r4 = r4 & 128;
        if (r4 == 0) goto L_0x059a;
    L_0x058d:
        r6 = 0;
        r7 = ORDER;
        r8 = 32;
        r9 = 1;
        r4 = r31;
        r5 = r31;
        mula_small(r4, r5, r6, r7, r8, r9);
    L_0x059a:
        return;
    L_0x059b:
        r6 = 32;
        r6 = new byte[r6];
    L_0x059f:
        r7 = r4 - r5;
        r7 = r7 + 1;
        r0 = r25;
        r1 = r31;
        divmod(r6, r0, r4, r1, r5);
        r0 = r25;
        r4 = numsize(r0, r4);
        if (r4 != 0) goto L_0x05b5;
    L_0x05b2:
        r27 = r26;
        goto L_0x057e;
    L_0x05b5:
        r8 = -1;
        r0 = r27;
        r1 = r26;
        mula32(r0, r1, r6, r7, r8);
        r7 = r5 - r4;
        r7 = r7 + 1;
        r0 = r31;
        r1 = r25;
        divmod(r6, r0, r5, r1, r4);
        r0 = r31;
        r5 = numsize(r0, r5);
        if (r5 == 0) goto L_0x057e;
    L_0x05d0:
        r8 = -1;
        r0 = r26;
        r1 = r27;
        mula32(r0, r1, r6, r7, r8);
        goto L_0x059f;
    L_0x05d9:
        r18 = r17;
        goto L_0x023f;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.rachio.iro.gen2.Curve25519.core(byte[], byte[], byte[], byte[]):void");
    }
}
