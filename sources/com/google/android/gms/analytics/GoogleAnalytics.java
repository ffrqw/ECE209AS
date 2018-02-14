package com.google.android.gms.analytics;

import android.content.Context;
import com.google.android.gms.internal.zzamj;
import com.google.android.gms.internal.zzaot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class GoogleAnalytics extends zza {
    private static List<Runnable> zzadC = new ArrayList();
    private Set<Object> zzadD = new HashSet();
    private boolean zzadF;
    private volatile boolean zzadG;
    private boolean zzuH;

    public GoogleAnalytics(zzamj zzamj) {
        super(zzamj);
    }

    public static GoogleAnalytics getInstance(Context context) {
        return zzamj.zzaf(context).zzkG();
    }

    public static void zzjo() {
        synchronized (GoogleAnalytics.class) {
            if (zzadC != null) {
                for (Runnable run : zzadC) {
                    run.run();
                }
                zzadC = null;
            }
        }
    }

    public final boolean getAppOptOut() {
        return this.zzadG;
    }

    public final void initialize() {
        zzaot zzkx = zzji().zzkx();
        zzkx.zzmg();
        if (zzkx.zzmh()) {
            this.zzadF = zzkx.zzmi();
        }
        zzkx.zzmg();
        this.zzuH = true;
    }

    public final boolean isDryRunEnabled() {
        return this.zzadF;
    }

    public final boolean isInitialized() {
        return this.zzuH;
    }

    public final Tracker newTracker(String str) {
        Tracker tracker;
        synchronized (this) {
            tracker = new Tracker(zzji(), str);
            tracker.initialize();
        }
        return tracker;
    }
}
