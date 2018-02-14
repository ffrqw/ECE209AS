package com.google.android.gms.common.util;

import android.content.Context;

public final class zzj {
    private static Boolean zzaJL;
    private static Boolean zzaJM;
    private static Boolean zzaJN;

    public static boolean zzaJ(Context context) {
        if (zzaJN == null) {
            boolean z = context.getPackageManager().hasSystemFeature("android.hardware.type.iot") || context.getPackageManager().hasSystemFeature("android.hardware.type.embedded");
            zzaJN = Boolean.valueOf(z);
        }
        return zzaJN.booleanValue();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.annotation.TargetApi(24)
    public static boolean zzaH(android.content.Context r4) {
        /*
        r1 = 1;
        r2 = 0;
        r0 = android.os.Build.VERSION.SDK_INT;
        r3 = 24;
        if (r0 < r3) goto L_0x0056;
    L_0x0008:
        r0 = r1;
    L_0x0009:
        if (r0 == 0) goto L_0x0030;
    L_0x000b:
        r0 = zzaJM;
        if (r0 != 0) goto L_0x0028;
    L_0x000f:
        r0 = com.google.android.gms.common.util.zzq.zzse();
        if (r0 == 0) goto L_0x0058;
    L_0x0015:
        r0 = r4.getPackageManager();
        r3 = "cn.google";
        r0 = r0.hasSystemFeature(r3);
        if (r0 == 0) goto L_0x0058;
    L_0x0021:
        r0 = r1;
    L_0x0022:
        r0 = java.lang.Boolean.valueOf(r0);
        zzaJM = r0;
    L_0x0028:
        r0 = zzaJM;
        r0 = r0.booleanValue();
        if (r0 == 0) goto L_0x005c;
    L_0x0030:
        r0 = zzaJL;
        if (r0 != 0) goto L_0x004d;
    L_0x0034:
        r0 = com.google.android.gms.common.util.zzq.zzsd();
        if (r0 == 0) goto L_0x005a;
    L_0x003a:
        r0 = r4.getPackageManager();
        r3 = "android.hardware.type.watch";
        r0 = r0.hasSystemFeature(r3);
        if (r0 == 0) goto L_0x005a;
    L_0x0046:
        r0 = r1;
    L_0x0047:
        r0 = java.lang.Boolean.valueOf(r0);
        zzaJL = r0;
    L_0x004d:
        r0 = zzaJL;
        r0 = r0.booleanValue();
        if (r0 == 0) goto L_0x005c;
    L_0x0055:
        return r1;
    L_0x0056:
        r0 = r2;
        goto L_0x0009;
    L_0x0058:
        r0 = r2;
        goto L_0x0022;
    L_0x005a:
        r0 = r2;
        goto L_0x0047;
    L_0x005c:
        r1 = r2;
        goto L_0x0055;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.common.util.zzj.zzaH(android.content.Context):boolean");
    }
}
