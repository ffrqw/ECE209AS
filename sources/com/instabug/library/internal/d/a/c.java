package com.instabug.library.internal.d.a;

import java.util.ArrayList;
import java.util.List;

public abstract class c<K, V> {
    private final List<d<V>> a;
    private String b;
    private int c;

    public abstract V a(K k);

    public abstract V a(K k, V v);

    public abstract void a();

    public abstract List<V> b();

    public abstract long c();

    public c(String str) {
        this(str, 1);
    }

    protected c(String str, int i) {
        this.c = -1;
        this.b = str;
        this.c = i;
        this.a = new ArrayList();
    }

    public final void b(V v) {
        for (d a : this.a) {
            a.a(v);
        }
    }

    public final void c(V v) {
        for (d b : this.a) {
            b.b(v);
        }
    }

    public final void b(V v, V v2) {
        for (d a : this.a) {
            a.a(v, v2);
        }
    }

    public final void d() {
        for (d a_ : this.a) {
            a_.a_();
        }
    }

    public final String e() {
        return this.b;
    }

    public final boolean a(d<V> dVar) {
        return this.a.add(dVar);
    }

    public final boolean b(d<V> dVar) {
        return this.a.remove(dVar);
    }
}
