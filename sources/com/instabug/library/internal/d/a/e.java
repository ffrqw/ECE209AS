package com.instabug.library.internal.d.a;

import com.instabug.library.util.InstabugSDKLogger;
import java.util.ArrayList;
import java.util.List;

public final class e {
    private static e b;
    private List<c> a = new ArrayList();

    public static abstract class a<K, V> {
        public abstract K a(V v);
    }

    private e() {
        this.a.add(new g("DEFAULT_IN_MEMORY_CACHE_KEY"));
    }

    public static e a() {
        if (b == null) {
            b = new e();
        }
        return b;
    }

    public final c a(String str) {
        for (c cVar : this.a) {
            if (cVar.e().equals(str)) {
                return cVar;
            }
        }
        InstabugSDKLogger.d(this, "No cache with this ID was found " + str + " returning null");
        return null;
    }

    public final c a(c cVar) {
        c a = a(cVar.e());
        if (a != null) {
            return a;
        }
        this.a.add(cVar);
        return cVar;
    }

    public final boolean b(String str) {
        return a(str) != null;
    }

    public final boolean a(String str, d dVar) {
        if (b(str)) {
            return a(str).a(dVar);
        }
        throw new IllegalArgumentException("No cache exists with this ID to subscribe to");
    }

    public final boolean b(String str, d dVar) {
        if (b(str)) {
            return a(str).b(dVar);
        }
        return false;
    }

    public final <K, V> void a(String str, String str2, a<K, V> aVar) throws IllegalArgumentException {
        c a = a(str);
        c a2 = a(str2);
        InstabugSDKLogger.v(this, "Caches to be migrated " + a + " - " + a2);
        if (a == null) {
            throw new IllegalArgumentException("No cache with these keys was found to migrate from, " + a);
        }
        if (a2 == null) {
            a2 = new g(str2);
            a(a2);
        }
        a(a, a2, (a) aVar);
    }

    public final <K, V> void a(c<K, V> cVar, c<K, V> cVar2, a<K, V> aVar) {
        InstabugSDKLogger.d(this, "Invalidated migratingTo cache");
        cVar2.a();
        List b = cVar.b();
        if (b.isEmpty()) {
            InstabugSDKLogger.d(this, "Cache to migrate from doesn't contain any elements, not going further with the migration " + b);
            return;
        }
        for (Object next : b) {
            if (next != null) {
                InstabugSDKLogger.v(this, "Adding value " + next + " with key " + aVar.a(next));
                cVar2.a(aVar.a(next), next);
            }
        }
    }
}
