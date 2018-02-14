package com.instabug.library.d.a;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import com.instabug.library.a.c;
import com.instabug.library.a.c.b;
import com.instabug.library.d.d;
import com.instabug.library.e.a.e;
import com.instabug.library.internal.d.a.f;
import com.instabug.library.internal.d.a.j;
import com.instabug.library.s;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.IOException;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.functions.Action1;

public final class a implements com.instabug.library.a.a.a, com.instabug.library.a.c.a {
    private static a b = null;
    com.instabug.library.internal.module.a a;
    private Context c;
    private Handler d;
    private a e;
    private LocalBroadcastManager f;
    private com.instabug.library.a.a g;
    private c h;
    private boolean i = false;
    private boolean j = false;
    private boolean k = false;

    private class a implements Runnable {
        final /* synthetic */ a a;

        private a(a aVar) {
            this.a = aVar;
        }

        public final void run() {
            a.a(this.a, new Action1<Long>(this) {
                final /* synthetic */ a a;

                {
                    this.a = r1;
                }

                public final /* synthetic */ void call(Object obj) {
                    Long l = (Long) obj;
                    if (this.a.a.i) {
                        InstabugSDKLogger.d(this, "Waiting " + l + " seconds for next sync");
                        this.a.a.j = false;
                        this.a.a.d.postDelayed(this.a.a.e, l.longValue() * 1000);
                    }
                }
            });
        }
    }

    private a(Context context) {
        this.c = context;
        this.a = new com.instabug.library.internal.module.a();
        this.f = LocalBroadcastManager.getInstance(this.c);
    }

    public static a a(Context context) {
        if (b == null) {
            b = new a(context);
        }
        return b;
    }

    public final void a() {
        InstabugSDKLogger.v(this, "initializing SynchronizationManager");
        this.d = new Handler();
        this.e = new a();
        if (s.f() != 0) {
            g();
            this.k = true;
        }
        this.g = new com.instabug.library.a.a(this);
        this.f.registerReceiver(this.g, new IntentFilter("User last contact at changed"));
    }

    public final void b() {
        this.i = true;
        this.d.post(this.e);
    }

    private void f() {
        this.i = false;
        if (this.d != null && this.e != null) {
            this.d.removeCallbacks(this.e);
        }
    }

    public final void d() {
        f();
        this.d = null;
        this.e = null;
        this.f.unregisterReceiver(this.g);
        this.g = null;
        this.f.unregisterReceiver(this.h);
        this.h = null;
        this.k = false;
    }

    private void g() {
        this.h = new c(this);
        this.f.registerReceiver(this.h, new IntentFilter("Session state changed"));
    }

    public final void a(b bVar) {
        InstabugSDKLogger.d(this, "SessionStateChanged: " + bVar);
        if (bVar == b.Finish) {
            f();
        } else if (!this.j) {
            b();
        }
    }

    public final void c() {
        InstabugSDKLogger.d(this, "LastContactedAtChanged");
        if (!this.k) {
            InstabugSDKLogger.d(this, "Register session state receivers");
            this.k = true;
            g();
        }
        if (s.g() && !this.j) {
            f();
            b();
        }
    }

    static /* synthetic */ void a(a aVar, JSONArray jSONArray, boolean z) throws JSONException {
        if (jSONArray.length() != 0) {
            InstabugSDKLogger.d(aVar, "new messages received: " + jSONArray.toString());
            JSONObject[] jSONObjectArr = new JSONObject[jSONArray.length()];
            for (int i = 0; i < jSONArray.length(); i++) {
                jSONObjectArr[i] = jSONArray.getJSONObject(i);
            }
            d.a().a(z, jSONObjectArr);
        }
    }

    static /* synthetic */ void a(a aVar, long j, Action1 action1) throws JSONException {
        InstabugSDKLogger.d(aVar, "Next TTL: " + j);
        if (j != -1) {
            s.a(j);
            action1.call(Long.valueOf(j));
        }
    }

    static /* synthetic */ void a(a aVar, final Action1 action1) {
        Exception e;
        if (com.instabug.library.e.a.a(aVar.c)) {
            try {
                j.a();
                final List e2 = j.e();
                aVar.j = true;
                e.a().a(aVar.c, f.e(), f.c(), j.a().d(), new com.instabug.library.e.c.a<com.instabug.library.e.d, Throwable>(aVar) {
                    final /* synthetic */ a c;

                    public final /* bridge */ /* synthetic */ void a(Object obj) {
                        InstabugSDKLogger.d(this, "Something went wrong while sync messages: " + ((Throwable) obj).getMessage());
                        action1.call(Long.valueOf(s.l()));
                    }

                    public final /* synthetic */ void b(Object obj) {
                        com.instabug.library.e.d dVar = (com.instabug.library.e.d) obj;
                        InstabugSDKLogger.d(this, "Message synced successfully");
                        try {
                            a.a(this.c, new JSONObject((String) dVar.b()).getJSONArray("emails"), dVar.a() == 203);
                            a.a(this.c, new JSONObject((String) dVar.b()).getLong("TTL"), action1);
                        } catch (Throwable e) {
                            InstabugSDKLogger.e(this, "error message: " + e.getMessage(), e);
                            e.printStackTrace();
                            action1.call(Long.valueOf(s.l()));
                        }
                        j.a().a(e2);
                    }
                });
                return;
            } catch (IOException e3) {
                e = e3;
            } catch (JSONException e4) {
                e = e4;
            }
        } else {
            InstabugSDKLogger.w(aVar, "device is offline, can't sync");
            action1.call(Long.valueOf(s.l()));
            return;
        }
        InstabugSDKLogger.d(aVar, "Something went wrong while making sync messaging" + e.getMessage());
        action1.call(Long.valueOf(s.l()));
    }
}
