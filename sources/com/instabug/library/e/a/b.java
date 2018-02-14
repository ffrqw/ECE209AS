package com.instabug.library.e.a;

import android.content.Context;
import com.instabug.library.e.a;
import com.instabug.library.e.c;
import com.instabug.library.e.c.d;
import com.instabug.library.util.InstabugSDKLogger;
import org.json.JSONException;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class b {
    private static b a;
    private a b = new a();

    public static b a() {
        if (a == null) {
            a = new b();
        }
        return a;
    }

    private b() {
    }

    public final Subscription a(Context context, final com.instabug.library.model.a aVar, final c.a<com.instabug.library.model.a, Throwable> aVar2) {
        c a$1ab3202;
        JSONException e;
        InstabugSDKLogger.d(this, "Downloading file request");
        try {
            a$1ab3202 = this.b.a$1ab3202(context, aVar.b(), d.Get, a.a.c$656280e9);
            try {
                a$1ab3202.b(aVar.c().getPath());
            } catch (JSONException e2) {
                e = e2;
                InstabugSDKLogger.d(this, "create downloadFile request got error: " + e.getMessage());
                return this.b.a(a$1ab3202).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<com.instabug.library.e.d>(this) {
                    final /* synthetic */ b c;

                    public final /* synthetic */ void onNext(Object obj) {
                        com.instabug.library.e.d dVar = (com.instabug.library.e.d) obj;
                        InstabugSDKLogger.v(this, "downloadFile request onNext, Response code: " + dVar.a() + ", Response body: " + dVar.b());
                        aVar2.b(aVar);
                    }

                    public final void onStart() {
                        InstabugSDKLogger.d(this, "downloadFile request started");
                    }

                    public final void onCompleted() {
                        InstabugSDKLogger.d(this, "downloadFile request completed");
                    }

                    public final void onError(Throwable th) {
                        InstabugSDKLogger.e(this, "downloadFile request got error: " + th.getMessage());
                        aVar2.a(th);
                    }
                });
            }
        } catch (JSONException e3) {
            JSONException jSONException = e3;
            a$1ab3202 = null;
            e = jSONException;
            InstabugSDKLogger.d(this, "create downloadFile request got error: " + e.getMessage());
            return this.b.a(a$1ab3202).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(/* anonymous class already generated */);
        }
        return this.b.a(a$1ab3202).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(/* anonymous class already generated */);
    }
}
