package com.instabug.library;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

final class m {
    private static m a;
    private final SimpleDateFormat b = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US);
    private StringBuilder c = new StringBuilder("");

    private m() {
    }

    private static synchronized m d() {
        m mVar;
        synchronized (m.class) {
            if (a == null) {
                a = new m();
            }
            mVar = a;
        }
        return mVar;
    }

    public static String a() {
        return d().c.toString();
    }

    public static void a(String str) {
        d().b(str);
    }

    public static void b() {
        d().c = new StringBuilder();
    }

    private synchronized void b(String str) {
        this.c.append(this.b.format(new Date(System.currentTimeMillis())));
        this.c.append(": ");
        this.c.append(str);
        this.c.append("\n");
    }
}
