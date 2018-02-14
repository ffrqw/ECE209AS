package com.instabug.library;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.instabug.library.a.c.b;
import com.instabug.library.internal.d.a.f;
import com.instabug.library.internal.d.a.h;
import com.instabug.library.internal.d.a.j;
import com.instabug.library.internal.d.a.k;
import com.instabug.library.util.InstabugSDKLogger;

public final class t {
    private static t a;
    private long b;

    public static t a() {
        if (a == null) {
            a = new t();
        }
        return a;
    }

    private t() {
    }

    final void a(Context context) {
        InstabugSDKLogger.d(this, "Session started");
        InstabugSDKLogger.d(this, "Preparing caches");
        j.b();
        f.a();
        h.a();
        k.a();
        if (InstabugFeaturesManager.getInstance().isFeatureAvailable(Feature.INSTABUG)) {
            InstabugSDKLogger.d(this, "Handling session started");
            this.b = System.currentTimeMillis() / 1000;
            if (s.e()) {
                s.b(false);
                if (!s.d()) {
                    k.a(new com.instabug.library.model.j(0, String.valueOf(System.currentTimeMillis() / 1000), 0));
                }
            }
            a(b.Start, context);
        }
    }

    final void b(Context context) {
        InstabugSDKLogger.d(this, "Session finished");
        InstabugSDKLogger.d(this, "Dumping caches");
        j.c();
        f.b();
        h.b();
        com.instabug.library.internal.d.a.b.b(context);
        if (!InstabugFeaturesManager.getInstance().isFeatureAvailable(Feature.INSTABUG)) {
            return;
        }
        if (this.b != 0) {
            InstabugSDKLogger.d(this, "Handling session finished");
            int i = -1;
            if (s.i()) {
                i = 1;
                s.d(false);
            }
            com.instabug.library.model.j jVar = new com.instabug.library.model.j(i, String.valueOf(this.b), (System.currentTimeMillis() / 1000) - this.b);
            InstabugSDKLogger.v(this, "Adding session " + jVar + " to cache " + k.a().b());
            k.a(jVar);
            k.b();
            a(b.Finish, context);
            return;
        }
        InstabugSDKLogger.d(this, "Instabug SDK is enabled after session started, Session ignored");
    }

    private static void a(b bVar, Context context) {
        if (bVar.equals(b.Finish)) {
            s.c(false);
        } else {
            s.c(true);
        }
        Intent intent = new Intent();
        intent.setAction("Session state changed");
        intent.putExtra("Session state", bVar);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        context.startService(new Intent(context, InstabugSessionUploaderService.class));
        context.startService(new Intent(context, InstabugIssueUploaderService.class));
        context.startService(new Intent(context, InstabugMessageUploaderService.class));
        context.startService(new Intent(context, InstabugFeaturesFetcherService.class));
    }

    public final long b() {
        return (System.currentTimeMillis() / 1000) - this.b;
    }
}
