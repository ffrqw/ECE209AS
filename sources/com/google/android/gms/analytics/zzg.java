package com.google.android.gms.analytics;

import android.net.Uri;
import android.net.Uri.Builder;
import android.text.TextUtils;
import android.util.LogPrinter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class zzg implements zzo {
    private static final Uri zzadO;
    private final LogPrinter zzadP = new LogPrinter(4, "GA/LogCatTransport");

    static {
        Builder builder = new Builder();
        builder.scheme("uri");
        builder.authority("local");
        zzadO = builder.build();
    }

    public final void zzb(zzi zzi) {
        List arrayList = new ArrayList(zzi.zzjq());
        Collections.sort(arrayList, new zzh());
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList arrayList2 = (ArrayList) arrayList;
        int size = arrayList2.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList2.get(i);
            i++;
            obj = ((zzj) obj).toString();
            if (!TextUtils.isEmpty(obj)) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(obj);
            }
        }
        this.zzadP.println(stringBuilder.toString());
    }

    public final Uri zzjl() {
        return zzadO;
    }
}
