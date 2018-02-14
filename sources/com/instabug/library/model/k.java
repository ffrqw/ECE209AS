package com.instabug.library.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class k {
    private String a;
    private String b;
    private String c;
    private String d;
    private int e;

    public final void a(StringBuilder stringBuilder) {
        stringBuilder.append(this.a);
        stringBuilder.append("     ");
        if (this.e == 2561) {
            stringBuilder.append("In activity ");
            stringBuilder.append(this.d);
            stringBuilder.append(": ");
            if (this.b != null) {
                stringBuilder.append("View(");
                stringBuilder.append(this.b);
                stringBuilder.append(")");
            } else {
                stringBuilder.append("View");
            }
            stringBuilder.append(" of type ");
            stringBuilder.append(this.c);
            stringBuilder.append(" received a click event");
        } else if (this.e == 2562) {
            stringBuilder.append("In activity ");
            stringBuilder.append(this.d);
            stringBuilder.append(": the user shook the phone");
        } else if (this.e == 2565 || this.e == 2576) {
            stringBuilder.append(this.d);
            stringBuilder.append(" was resumed.");
        } else if (this.e == 2564) {
            stringBuilder.append(this.d);
            stringBuilder.append(" was displayed.");
        } else if (this.e == 2566 || this.e == 2568) {
            stringBuilder.append(this.d);
            stringBuilder.append(" was paused ");
        } else if (this.e == 2563) {
            stringBuilder.append("In activity ");
            stringBuilder.append(this.d);
            stringBuilder.append(": started feedback process. Feedback Options dialog displayed.");
        } else if (this.e == 2567) {
            stringBuilder.append("Fragment: ");
            stringBuilder.append(this.d);
            stringBuilder.append(" is now the top backstack entry.");
        } else if (this.e == 2577) {
            stringBuilder.append("Fragment back stack is now empty.");
        }
    }

    public final void a(int i) {
        this.e = i;
    }

    public final void a(long j) {
        this.a = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.US).format(new Date(j));
    }

    public final void a(String str) {
        this.b = str;
    }

    public final void b(String str) {
        this.c = str;
    }

    public final void c(String str) {
        this.d = str;
    }
}
