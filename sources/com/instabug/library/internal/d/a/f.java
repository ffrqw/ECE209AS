package com.instabug.library.internal.d.a;

import com.instabug.library.internal.d.a.e.a;
import com.instabug.library.model.c;
import com.instabug.library.model.g;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class f {
    public static g<String, c> a() throws IllegalArgumentException {
        if (!e.a().b("CONVERSATIONS_MEMORY_CACHE")) {
            InstabugSDKLogger.d(f.class, "In-memory cache not found, loading it from disk " + e.a().a("CONVERSATIONS_MEMORY_CACHE"));
            e.a().a("CONVERSATIONS_DISK_CACHE", "CONVERSATIONS_MEMORY_CACHE", new a<String, c>() {
                public final /* bridge */ /* synthetic */ Object a(Object obj) {
                    return ((c) obj).a();
                }
            });
            InstabugSDKLogger.d(f.class, "In-memory cache restored from disk, " + e.a().a("CONVERSATIONS_MEMORY_CACHE").b().size() + " elements restored");
        }
        InstabugSDKLogger.d(f.class, "In-memory cache found");
        return (g) e.a().a("CONVERSATIONS_MEMORY_CACHE");
    }

    public static void b() throws IllegalArgumentException {
        if (e.a().b("CONVERSATIONS_MEMORY_CACHE")) {
            InstabugSDKLogger.d(f.class, "Saving In-memory cache to disk, no. of items to save is " + e.a().a("CONVERSATIONS_MEMORY_CACHE").c());
            e.a().a("CONVERSATIONS_MEMORY_CACHE", "CONVERSATIONS_DISK_CACHE", new a<String, c>() {
                public final /* bridge */ /* synthetic */ Object a(Object obj) {
                    return ((c) obj).a();
                }
            });
            InstabugSDKLogger.d(f.class, "In-memory cache had been persisted on-disk, " + e.a().a("CONVERSATIONS_DISK_CACHE").b().size() + " elements saved");
        }
    }

    public static c a(String str) {
        for (c cVar : a().b()) {
            if (cVar.a().equals(str)) {
                return cVar;
            }
        }
        return null;
    }

    public static int c() {
        int i = 0;
        for (c b : a().b()) {
            for (g i2 : b.b()) {
                if (i2.i().equals(g.c.SYNCED)) {
                    i++;
                }
            }
        }
        return i;
    }

    public static List<g> d() {
        List<g> arrayList = new ArrayList();
        for (c b : a().b()) {
            for (g gVar : b.b()) {
                if (gVar.i().equals(g.c.NOT_SENT)) {
                    arrayList.add(gVar);
                }
            }
        }
        return arrayList;
    }

    public static String e() {
        List arrayList = new ArrayList();
        for (c b : a().b()) {
            arrayList.addAll(b.b());
        }
        Collections.sort(arrayList, new g.a());
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            g gVar = (g) arrayList.get(size);
            if (gVar.a() != null) {
                return gVar.e();
            }
        }
        return "";
    }

    public static int f() {
        int i = 0;
        for (c c : a().b()) {
            i = c.c() + i;
        }
        return i;
    }

    public static List<g> g() {
        List<g> arrayList = new ArrayList();
        for (c b : a().b()) {
            for (g gVar : b.b()) {
                if (gVar.i() == g.c.NOT_SENT) {
                    arrayList.add(gVar);
                }
            }
        }
        a().a();
        return arrayList;
    }

    public static void a(g gVar) throws IOException {
        c cVar = (c) a().d(gVar.g());
        List b = cVar.b();
        int i = 0;
        while (i < b.size()) {
            if (((g) b.get(i)).e().equalsIgnoreCase(gVar.e()) && ((g) b.get(i)).a().equalsIgnoreCase(gVar.a()) && ((g) b.get(i)).i().equals(g.c.SENT) && ((g) b.get(i)).j().size() == gVar.j().size()) {
                cVar.b().set(i, gVar);
                InstabugSDKLogger.v(f.class, "messages number: " + cVar.b().size());
                InstabugSDKLogger.v(f.class, "messages: " + cVar.b().get(i));
                a().a(cVar.a(), cVar);
                return;
            }
            i++;
        }
    }
}
