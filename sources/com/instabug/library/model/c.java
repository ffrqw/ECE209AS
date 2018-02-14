package com.instabug.library.model;

import com.instabug.library.util.g;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public final class c implements Serializable {
    private String a;
    private ArrayList<g> b = new ArrayList();

    public static class a implements Comparator<c> {
        public final /* synthetic */ int compare(Object obj, Object obj2) {
            return g.b(((c) obj).h()).compareTo(g.b(((c) obj2).h()));
        }
    }

    public c(String str) {
        this.a = str;
    }

    public final String a() {
        return this.a;
    }

    public final List<g> b() {
        return this.b;
    }

    public final int c() {
        l();
        Iterator it = this.b.iterator();
        int i = 0;
        while (it.hasNext()) {
            int i2;
            if (((g) it.next()).d()) {
                i2 = i;
            } else {
                i2 = i + 1;
            }
            i = i2;
        }
        return i;
    }

    public final void d() {
        for (int size = this.b.size() - 1; size >= 0; size--) {
            ((g) this.b.get(size)).a(true);
        }
    }

    public final IssueType e() {
        return j().b();
    }

    public final String f() {
        g k = k();
        if (k != null) {
            return k.h();
        }
        return null;
    }

    public final String g() {
        g k = k();
        if (k != null) {
            return k.f();
        }
        return null;
    }

    private g k() {
        g j = j();
        if (!j.k()) {
            return j;
        }
        Iterator it = this.b.iterator();
        while (it.hasNext()) {
            j = (g) it.next();
            if (j.a() != null && !j.k()) {
                return j;
            }
        }
        return null;
    }

    public final String h() {
        return i().e();
    }

    public final g i() {
        l();
        return (g) this.b.get(this.b.size() - 1);
    }

    private g j() {
        l();
        for (int size = this.b.size() - 1; size >= 0; size--) {
            if (((g) this.b.get(size)).a() != null) {
                return (g) this.b.get(size);
            }
        }
        return (g) this.b.get(this.b.size() - 1);
    }

    private void l() {
        if (this.b != null && this.b.size() == 0) {
            throw new IllegalStateException("Conversation object without messages!");
        }
    }

    public final String toString() {
        return "Conversation:[" + this.a + ", " + this.b + "]";
    }
}
