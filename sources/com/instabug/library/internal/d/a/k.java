package com.instabug.library.internal.d.a;

import com.instabug.library.internal.d.a.e.a;
import com.instabug.library.model.j;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.List;

public class k {
    public static g<String, j> a() throws IllegalArgumentException {
        if (!e.a().b("sessions_memory_cache")) {
            InstabugSDKLogger.d(k.class, "In-memory Sessions cache not found, loading it from disk " + e.a().a("sessions_memory_cache"));
            e.a().a("sessions_disk_cache", "sessions_memory_cache", new a<String, j>() {
                public final /* bridge */ /* synthetic */ Object a(Object obj) {
                    return ((j) obj).b();
                }
            });
            InstabugSDKLogger.d(k.class, "In-memory Sessions cache restored from disk, " + e.a().a("sessions_memory_cache").b().size() + " elements restored");
        }
        InstabugSDKLogger.d(k.class, "In-memory Sessions cache found");
        return (g) e.a().a("sessions_memory_cache");
    }

    public static void b() {
        InstabugSDKLogger.d(k.class, "Checking old values cached " + e.a().a("sessions_disk_cache").b());
        InstabugSDKLogger.d(k.class, "Saving In-memory Sessions cache to disk, no. of sessions to save is " + e.a().a("sessions_memory_cache").c());
        e.a().a(e.a().a("sessions_memory_cache"), e.a().a("sessions_disk_cache"), new a<String, j>() {
            public final /* bridge */ /* synthetic */ Object a(Object obj) {
                return ((j) obj).b();
            }
        });
        InstabugSDKLogger.d(k.class, "In-memory Sessions cache had been persisted on-disk, " + e.a().a("sessions_disk_cache").b().size() + " sessions saved");
    }

    public static void a(j jVar) {
        a().a(jVar.b(), jVar);
    }

    public static j b(j jVar) {
        return (j) a().a(jVar.b());
    }

    public static List<j> c() {
        return a().b();
    }
}
