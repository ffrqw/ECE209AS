package com.shinobicontrols.charts;

import android.util.Log;

class ev {
    static void a(Class<?> cls, String str, Throwable th) {
        if (cls != null) {
            str = String.format("%s: %s", new Object[]{cls.getName(), str});
        }
        if (th == null) {
            Log.d("shinobicharts", str);
        } else {
            Log.d("shinobicharts", str, th);
        }
    }

    static void f(String str) {
        a(null, str, null);
    }

    static void b(Class<?> cls, String str, Throwable th) {
        if (cls != null) {
            str = String.format("%s: %s", new Object[]{cls.getName(), str});
        }
        if (th == null) {
            Log.w("shinobicharts", str);
        } else {
            Log.w("shinobicharts", str, th);
        }
    }

    static void g(String str) {
        b(null, str, null);
    }

    static void c(Class<?> cls, String str, Throwable th) {
        if (cls != null) {
            str = String.format("%s: %s", new Object[]{cls.getName(), str});
        }
        if (th == null) {
            Log.e("shinobicharts", str);
        } else {
            Log.e("shinobicharts", str, th);
        }
    }

    static void h(String str) {
        c(null, str, null);
    }
}
