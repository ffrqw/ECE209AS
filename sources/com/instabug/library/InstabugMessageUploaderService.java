package com.instabug.library;

import com.instabug.library.e.a.e;
import com.instabug.library.e.c.a;
import com.instabug.library.internal.d.a.f;
import com.instabug.library.model.c;
import com.instabug.library.model.g;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.List;

public class InstabugMessageUploaderService extends n {
    protected final void b() throws Exception {
        List d = f.d();
        InstabugSDKLogger.d(this, "Found " + d.size() + " messages in cache");
        for (int i = 0; i < d.size(); i++) {
            final g gVar = (g) d.get(i);
            InstabugSDKLogger.d(this, "Uploading message: " + d.get(i));
            e.a().a(this, gVar.c(), gVar.g(), new a<Boolean, Throwable>(this) {
                final /* synthetic */ InstabugMessageUploaderService b;

                public final /* synthetic */ void b(Object obj) {
                    InstabugSDKLogger.v(this.b, "Send message response: " + ((Boolean) obj));
                    c a = f.a(gVar.g());
                    a.b().remove(gVar);
                    gVar.a(g.c.SENT);
                    InstabugSDKLogger.v(this.b, "Adding sent message with body \"" + gVar.c() + "\" to conversation " + a + " cache " + f.a());
                    a.b().add(gVar);
                    f.a().a(a.a(), a);
                    f.b();
                }

                public final /* bridge */ /* synthetic */ void a(Object obj) {
                    InstabugSDKLogger.d(this.b, "Something went wrong while uploading cached message");
                }
            });
        }
    }
}
