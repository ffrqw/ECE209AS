package com.instabug.library.internal.d.a;

import com.instabug.library.internal.d.a.e.a;
import com.instabug.library.model.i;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.List;
import java.util.ListIterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class j {
    private static j a;

    public static j a() {
        if (a == null) {
            a = new j();
        }
        return a;
    }

    public static g<Integer, i> b() {
        if (!e.a().b("read_queue_memory_cache_key") || e.a().a("read_queue_memory_cache_key").b().size() > 0) {
            InstabugSDKLogger.d(j.class, "In-memory cache not found, loading it from disk " + e.a().a("read_queue_memory_cache_key"));
            e.a().a("read_queue_disk_cache_key", "read_queue_memory_cache_key", new a<Integer, i>() {
                public final /* bridge */ /* synthetic */ Object a(Object obj) {
                    return Integer.valueOf(((i) obj).a());
                }
            });
            InstabugSDKLogger.d(j.class, "In-memory cache restored from disk, " + e.a().a("read_queue_memory_cache_key").b().size() + " elements restored");
        }
        InstabugSDKLogger.d(j.class, "In-memory cache found");
        return (g) e.a().a("read_queue_memory_cache_key");
    }

    public static void c() {
        if (e.a().b("read_queue_memory_cache_key")) {
            InstabugSDKLogger.d(j.class, "Saving In-memory cache to disk, no. of items to save is " + e.a().a("read_queue_memory_cache_key").b());
            e.a().a(e.a().a("read_queue_memory_cache_key"), e.a().a("read_queue_disk_cache_key"), new a<String, i>() {
                public final /* bridge */ /* synthetic */ Object a(Object obj) {
                    return String.valueOf(((i) obj).a());
                }
            });
            InstabugSDKLogger.d(j.class, "In-memory cache had been persisted on-disk, " + e.a().a("read_queue_disk_cache_key").b().size() + " elements saved");
        }
    }

    public j() {
        InstabugSDKLogger.d(this, "Initializing ReadQueueCacheManager");
        e.a().a(new g("read_queue_memory_cache_key"));
    }

    public final JSONArray d() {
        JSONArray jSONArray = new JSONArray();
        for (i iVar : e()) {
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("issue_id", iVar.a());
                jSONObject.put("last_email_id", iVar.c());
                jSONObject.put("read_at", iVar.b());
                jSONArray.put(jSONObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jSONArray;
    }

    public static List<i> e() {
        return e.a().a("read_queue_memory_cache_key").b();
    }

    public final void a(List<i> list) {
        ListIterator listIterator = e().listIterator();
        while (listIterator.hasNext()) {
            i iVar = (i) listIterator.next();
            for (i iVar2 : list) {
                if (iVar.a() == iVar2.a() && iVar.c() == iVar2.c()) {
                    e.a().a("read_queue_memory_cache_key").a(Integer.valueOf(iVar2.a()));
                }
            }
        }
    }
}
