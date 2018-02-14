package com.instabug.library.internal.d.a;

import android.content.Context;
import android.os.Environment;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import rx.Subscription;

public class b {
    public static LinkedHashMap<String, a> a = new LinkedHashMap();

    public interface b {
        void a(com.instabug.library.model.a aVar);

        void a(Throwable th);
    }

    public static class a {
        com.instabug.library.model.a a;
        Subscription b;
        List<b> c = new ArrayList();

        a() {
        }
    }

    private static a a() {
        if (!e.a().b("assets_memory_cache")) {
            InstabugSDKLogger.d(h.class, "In-memory assets cache not found, create it");
            e.a().a(new a("assets_memory_cache"));
            InstabugSDKLogger.d(h.class, "In-memory assets created successfully");
        }
        InstabugSDKLogger.d(h.class, "In-memory assets cache found");
        return (a) e.a().a("assets_memory_cache");
    }

    public static com.instabug.library.model.a a$5bd48f4a(Context context, String str, int i) {
        return new com.instabug.library.model.a(String.valueOf(str.hashCode()), i, str, new File(c(context), String.valueOf(str.hashCode())));
    }

    public static void a(Context context, final com.instabug.library.model.a aVar, b bVar) {
        com.instabug.library.model.a aVar2 = (com.instabug.library.model.a) a().d(aVar.a());
        if (aVar2 != null) {
            InstabugSDKLogger.d(b.class, "Get file from cache");
            bVar.a(aVar2);
            return;
        }
        Object obj;
        if (a.get(aVar.a()) != null) {
            obj = 1;
        } else {
            obj = null;
        }
        if (obj != null) {
            InstabugSDKLogger.d(b.class, "File currently downloading, wait download to finish");
            List list = ((a) a.get(aVar.a())).c;
            list.add(bVar);
            ((a) a.get(aVar.a())).c = list;
            return;
        }
        InstabugSDKLogger.d(b.class, "File not exist download it");
        a aVar3 = new a();
        aVar3.a = aVar;
        list = aVar3.c;
        list.add(bVar);
        aVar3.c = list;
        aVar3.b = com.instabug.library.e.a.b.a().a(context, aVar, new com.instabug.library.e.c.a<com.instabug.library.model.a, Throwable>() {
            public final /* bridge */ /* synthetic */ void a(Object obj) {
                Throwable th = (Throwable) obj;
                InstabugSDKLogger.e(this, "downloading asset entity got error: ", th);
                b.a(aVar, th);
            }

            public final /* synthetic */ void b(Object obj) {
                com.instabug.library.model.a aVar = (com.instabug.library.model.a) obj;
                b.a(aVar);
                b.b(aVar);
            }
        });
        a.put(aVar3.a.a(), aVar3);
    }

    public static void a(com.instabug.library.model.a aVar) {
        a().a(aVar.a(), aVar);
    }

    private static File c(Context context) {
        String absolutePath;
        if (!Environment.getExternalStorageState().equals("mounted") || context.getExternalCacheDir() == null) {
            InstabugSDKLogger.d(b.class, "External storage not available, saving file to internal storage.");
            absolutePath = context.getCacheDir().getAbsolutePath();
        } else {
            InstabugSDKLogger.d(b.class, "Media Mounted");
            absolutePath = context.getExternalCacheDir().getPath();
        }
        File file = new File(absolutePath + "/instabug/assetCache");
        if (!file.exists()) {
            InstabugSDKLogger.d(b.class, "Is created: " + file.mkdirs());
            try {
                new File(file, ".nomedia").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static void b(Context context) {
        for (Entry value : a.entrySet()) {
            ((a) value.getValue()).b.unsubscribe();
        }
        if (e.a().b("assets_memory_cache")) {
            e.a().a("assets_memory_cache").a();
        }
        File[] listFiles = c(context).listFiles();
        if (listFiles != null) {
            for (File delete : listFiles) {
                delete.delete();
            }
        }
    }

    static /* synthetic */ void b(com.instabug.library.model.a aVar) {
        for (b bVar : ((a) a.get(aVar.a())).c) {
            if (bVar != null) {
                bVar.a(aVar);
            }
        }
    }

    static /* synthetic */ void a(com.instabug.library.model.a aVar, Throwable th) {
        for (b bVar : ((a) a.get(aVar.a())).c) {
            if (bVar != null) {
                bVar.a(th);
            }
        }
    }
}
