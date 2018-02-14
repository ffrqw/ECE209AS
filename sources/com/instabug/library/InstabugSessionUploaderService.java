package com.instabug.library;

import com.instabug.library.e.a.f;
import com.instabug.library.e.c.a;
import com.instabug.library.internal.d.a.k;
import com.instabug.library.model.j;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.IOException;
import java.util.List;
import org.json.JSONException;

public class InstabugSessionUploaderService extends n {
    protected final void b() throws IOException, JSONException {
        List<j> c = k.c();
        InstabugSDKLogger.d(this, "Found " + c.size() + " sessions in cache");
        for (final j jVar : c) {
            InstabugSDKLogger.d(this, "Syncing session " + jVar);
            f.a().a(this, jVar, new a<Boolean, Throwable>(this) {
                final /* synthetic */ InstabugSessionUploaderService b;

                public final /* bridge */ /* synthetic */ void a(Object obj) {
                    InstabugSDKLogger.d(this.b, "Something went wrong while sending session: " + jVar);
                }

                public final /* synthetic */ void b(Object obj) {
                    InstabugSDKLogger.d(this.b, "Session " + jVar + " synced successfully");
                    InstabugSDKLogger.d(this.b, "Session deleted: " + k.b(jVar));
                    k.b();
                }
            });
        }
    }
}
