package com.instabug.library.internal.d.a;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class g<K, V> extends c<K, V> {
    private final LinkedHashMap<K, V> a;

    public g(String str) {
        this(str, 1);
    }

    private g(String str, int i) {
        super(str, 1);
        this.a = new LinkedHashMap();
    }

    public final V d(K k) {
        return this.a.get(k);
    }

    public final V a(K k, V v) {
        V put = this.a.put(k, v);
        if (put == null) {
            c(v);
        } else {
            b(put, v);
        }
        return put;
    }

    public final V a(K k) {
        V remove = this.a.remove(k);
        if (remove != null) {
            b((Object) remove);
        }
        return remove;
    }

    public final long c() {
        return (long) this.a.size();
    }

    public void a() {
        this.a.clear();
        d();
    }

    public final List<V> b() {
        List<V> arrayList = new ArrayList();
        for (Object d : this.a.keySet()) {
            arrayList.add(d(d));
        }
        return arrayList;
    }
}
