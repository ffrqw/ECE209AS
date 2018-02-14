package com.instabug.library.e.a;

import android.content.Context;
import com.instabug.library.e.c.b;
import com.instabug.library.e.c.d;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.IOException;
import org.json.JSONException;
import rx.Subscriber;

public final class a {
    private static a a;
    private com.instabug.library.e.a b = new com.instabug.library.e.a();

    public static a a() {
        if (a == null) {
            a = new a();
        }
        return a;
    }

    private a() {
    }

    public final void a(Context context, final com.instabug.library.e.c.a<String, Throwable> aVar) throws JSONException, IOException {
        InstabugSDKLogger.d(this, "Getting enabled features for this application");
        this.b.a(this.b.a(context, b.AppSettings, d.Get)).subscribe(new Subscriber<com.instabug.library.e.d>(this) {
            final /* synthetic */ a b;

            public final /* synthetic */ void onNext(Object obj) {
                com.instabug.library.e.d dVar = (com.instabug.library.e.d) obj;
                InstabugSDKLogger.v(this, "getAppFeatures request onNext, Response code: " + dVar.a() + ", Response body: " + dVar.b());
                aVar.b((String) dVar.b());
            }

            public final void onStart() {
                InstabugSDKLogger.d(this, "getAppFeatures request started");
            }

            public final void onCompleted() {
                InstabugSDKLogger.d(this, "getAppFeatures request completed");
            }

            public final void onError(Throwable th) {
                InstabugSDKLogger.d(this, "getAppFeatures request got error: " + th.getMessage());
                aVar.a(th);
            }
        });
    }
}
