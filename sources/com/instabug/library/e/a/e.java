package com.instabug.library.e.a;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import com.instabug.library.e.a;
import com.instabug.library.e.c;
import com.instabug.library.e.c.b;
import com.instabug.library.e.c.d;
import com.instabug.library.s;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public final class e {
    private static e a;
    private a b = new a();

    public static e a() {
        if (a == null) {
            a = new e();
        }
        return a;
    }

    private e() {
    }

    public final void a(final Context context, String str, String str2, final c.a<Boolean, Throwable> aVar) throws JSONException, IOException {
        InstabugSDKLogger.d(this, "Sending message");
        c a = this.b.a(context, b.SendMessage, d.Post);
        a.a(a.a().replaceAll(":issue_number", str2));
        com.instabug.library.internal.module.a aVar2 = new com.instabug.library.internal.module.a();
        a.a("device_token", com.instabug.library.internal.module.a.a(context).l());
        a.a("email", new JSONObject().put("body", str));
        this.b.a(a).subscribe(new Subscriber<com.instabug.library.e.d>(this) {
            final /* synthetic */ e c;

            public final /* synthetic */ void onNext(Object obj) {
                com.instabug.library.e.d dVar = (com.instabug.library.e.d) obj;
                InstabugSDKLogger.v(this, "sendMessage request onNext, Response code: " + dVar.a() + "Response body: " + dVar.b());
                if (dVar.a() == Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                    aVar.b(Boolean.valueOf(true));
                    Calendar instance = Calendar.getInstance(Locale.ENGLISH);
                    InstabugSDKLogger.d(this, "Updating last_contacted_at to " + instance);
                    s.a(instance.getTime());
                    Intent intent = new Intent();
                    intent.setAction("User last contact at changed");
                    intent.putExtra("last_contacted_at", instance.getTime().getTime());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    return;
                }
                aVar.b(Boolean.valueOf(false));
            }

            public final void onStart() {
                InstabugSDKLogger.d(this, "sendMessage request started");
            }

            public final void onCompleted() {
                InstabugSDKLogger.d(this, "sendMessage request completed");
            }

            public final void onError(Throwable th) {
                InstabugSDKLogger.d(this, "sendMessage request got error: " + th.getMessage());
                aVar.a(th);
            }
        });
    }

    public final void a(Context context, String str, int i, JSONArray jSONArray, final c.a<com.instabug.library.e.d, Throwable> aVar) throws JSONException, IOException {
        InstabugSDKLogger.d(this, "Syncing messages with server");
        c a = this.b.a(context, b.SyncMessages, d.Post);
        com.instabug.library.internal.module.a aVar2 = new com.instabug.library.internal.module.a();
        a.a("device_token", com.instabug.library.internal.module.a.a(context).l());
        a.a("last_email_created_at", (Object) str);
        a.a("emails_count", Integer.valueOf(i));
        if (!(jSONArray == null || jSONArray.length() == 0)) {
            a.a("read_emails", (Object) jSONArray);
        }
        this.b.a(a).subscribeOn(Schedulers.io()).subscribe(new Subscriber<com.instabug.library.e.d>(this) {
            final /* synthetic */ e b;

            public final /* synthetic */ void onNext(Object obj) {
                com.instabug.library.e.d dVar = (com.instabug.library.e.d) obj;
                InstabugSDKLogger.v(this, "syncMessages request onNext, Response code: " + dVar.a() + "Response body: " + dVar.b());
                aVar.b(dVar);
            }

            public final void onStart() {
                InstabugSDKLogger.d(this, "syncMessages request started");
            }

            public final void onCompleted() {
                InstabugSDKLogger.d(this, "syncMessages request completed");
            }

            public final void onError(Throwable th) {
                InstabugSDKLogger.d(this, "syncMessages request got error: " + th.getMessage());
                aVar.a(th);
            }
        });
    }
}
