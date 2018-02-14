package com.instabug.library.e.a;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import com.instabug.library.Instabug;
import com.instabug.library.e.a;
import com.instabug.library.e.c;
import com.instabug.library.e.c.d;
import com.instabug.library.internal.a.b;
import com.instabug.library.model.j;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.IOException;
import org.json.JSONException;
import rx.Subscriber;

public final class f {
    private static f a;
    private a b = new a();

    public static f a() {
        if (a == null) {
            a = new f();
        }
        return a;
    }

    private f() {
    }

    public final void a(Context context, j jVar, final c.a<Boolean, Throwable> aVar) throws JSONException, IOException {
        InstabugSDKLogger.d(this, "Sending session");
        com.instabug.library.internal.module.a aVar2 = new com.instabug.library.internal.module.a();
        b a = com.instabug.library.internal.module.a.a(context);
        c a2 = this.b.a(context, c.b.SendSession, d.Post);
        a2.a("device", b.a()).a("os", new StringBuilder(Instabug.SDK_LEVEL).append(Integer.toString(VERSION.SDK_INT)).toString()).a("app_version", a.j()).a("bundle_id", a.k()).a("sdk_version", Instabug.SDK_VERSION).a("email", Instabug.getUserEmail()).a("name", Instabug.getUsername()).a("started_at", jVar.b()).a("duration", Long.valueOf(jVar.c()));
        if (jVar.a() != -1) {
            a2.a("session_number", Integer.valueOf(jVar.a()));
        }
        this.b.a(a2).subscribe(new Subscriber<com.instabug.library.e.d>(this) {
            final /* synthetic */ f b;

            public final /* synthetic */ void onNext(Object obj) {
                com.instabug.library.e.d dVar = (com.instabug.library.e.d) obj;
                InstabugSDKLogger.v(this, "sendSession request onNext, Response code: " + dVar.a() + ", Response body: " + dVar.b());
                if (dVar.a() != Callback.DEFAULT_DRAG_ANIMATION_DURATION || dVar.b() == null) {
                    aVar.b(Boolean.valueOf(false));
                } else {
                    aVar.b(Boolean.valueOf(true));
                }
            }

            public final void onStart() {
                InstabugSDKLogger.d(this, "sendSession request started");
            }

            public final void onCompleted() {
                InstabugSDKLogger.d(this, "sendSession request completed");
            }

            public final void onError(Throwable th) {
                InstabugSDKLogger.d(this, "sendSession request got error: " + th.getMessage());
                aVar.a(th);
            }
        });
    }
}
