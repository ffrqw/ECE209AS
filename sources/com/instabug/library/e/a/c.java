package com.instabug.library.e.a;

import android.content.Context;
import com.instabug.library.e.a;
import com.instabug.library.e.c.b;
import com.instabug.library.e.c.d;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.IOException;
import org.json.JSONException;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public final class c {
    private static c a;
    private a b = new a();

    public static c a() {
        if (a == null) {
            a = new c();
        }
        return a;
    }

    private c() {
    }

    public final void a(Context context, String str, final com.instabug.library.e.c.a<String, Throwable> aVar) throws JSONException, IOException {
        InstabugSDKLogger.d(this, "Registering GCM");
        com.instabug.library.e.c a = this.b.a(context, b.RegisterPushNotifications, d.put);
        com.instabug.library.internal.module.a aVar2 = new com.instabug.library.internal.module.a();
        a.a("device_token", com.instabug.library.internal.module.a.a(context).l());
        a.a("push_token", (Object) str);
        this.b.a(a).subscribeOn(Schedulers.io()).subscribe(new Subscriber<com.instabug.library.e.d>(this) {
            final /* synthetic */ c b;

            public final /* synthetic */ void onNext(Object obj) {
                com.instabug.library.e.d dVar = (com.instabug.library.e.d) obj;
                InstabugSDKLogger.v(this, "registerGCM request onNext, Response code: " + dVar.a() + ", Response body: " + dVar.b());
                aVar.b((String) dVar.b());
            }

            public final void onStart() {
                InstabugSDKLogger.d(this, "registerGCM request started");
            }

            public final void onCompleted() {
                InstabugSDKLogger.d(this, "registerGCM request completed");
            }

            public final void onError(Throwable th) {
                InstabugSDKLogger.d(this, "registerGCM request got error: " + th.getMessage());
                aVar.a(th);
            }
        });
    }
}
