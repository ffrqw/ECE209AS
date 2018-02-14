package com.instabug.library.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public final class f {
    private Context a;
    private PackageManager b;
    private ApplicationInfo c;
    private String d;

    public f(Context context) {
        this.a = context;
        this.d = context.getPackageName();
        this.b = context.getPackageManager();
        try {
            this.c = this.b.getApplicationInfo(this.d, 128);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public final String a() {
        return (String) (this.c != null ? this.b.getApplicationLabel(this.c) : "(unknown)");
    }

    public final int b() {
        return this.c.icon;
    }
}
