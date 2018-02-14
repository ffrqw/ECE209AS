package com.instabug.library.internal.d.a;

import com.instabug.library.internal.d.a.e.a;
import com.instabug.library.model.d;
import com.instabug.library.model.e;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.File;
import java.util.List;

public class h {
    public static g<String, d> a() throws IllegalArgumentException {
        if (!e.a().b("issues_memory_cache")) {
            InstabugSDKLogger.d(h.class, "In-memory Issues cache not found, loading it from disk " + e.a().a("issues_memory_cache"));
            e.a().a("issues_disk_cache", "issues_memory_cache", new a<String, d>() {
                public final /* bridge */ /* synthetic */ Object a(Object obj) {
                    return ((d) obj).d();
                }
            });
            InstabugSDKLogger.d(h.class, "In-memory Issues cache restored from disk, " + e.a().a("issues_memory_cache").b().size() + " elements restored");
        }
        InstabugSDKLogger.d(h.class, "In-memory Issues cache found");
        return (g) e.a().a("issues_memory_cache");
    }

    public static void b() {
        if (e.a().b("issues_memory_cache")) {
            InstabugSDKLogger.d(h.class, "Saving In-memory Issues cache to disk, no. of issues to save is " + e.a().a("issues_memory_cache").c());
            e.a().a(e.a().a("issues_memory_cache"), e.a().a("issues_disk_cache"), new a<String, d>() {
                public final /* bridge */ /* synthetic */ Object a(Object obj) {
                    return ((d) obj).d();
                }
            });
            InstabugSDKLogger.d(h.class, "In-memory Issues cache had been persisted on-disk, " + e.a().a("issues_disk_cache").c() + " issues saved");
        }
    }

    public static void c() {
        InstabugSDKLogger.d(h.class, "cleanHangingIssue");
        d d = d();
        if (d != null) {
            for (e e : d.b()) {
                new File(e.e()).delete();
            }
            a(d.d());
        }
    }

    public static d d() {
        for (d dVar : a().b()) {
            if (dVar.e().equals("in_progress_issue_occurrence_id")) {
                return dVar;
            }
        }
        return null;
    }

    public static void a(d dVar) {
        dVar.a("in_progress_issue_occurrence_id");
        a().a(dVar.d(), dVar);
    }

    public static d a(String str) {
        return (d) a().a(str);
    }

    public static void b(d dVar) {
        a().a(dVar.d(), dVar);
    }

    public static List<d> e() {
        return a().b();
    }
}
