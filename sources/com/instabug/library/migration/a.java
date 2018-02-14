package com.instabug.library.migration;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.instabug.library.e.a.e;
import com.instabug.library.e.d;
import com.instabug.library.s;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.g;
import java.io.IOException;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;

public final class a extends AbstractMigration {
    private static String b = "last_contacted_at_migration";
    private Context a;

    static /* synthetic */ void a(a aVar, d dVar, Subscriber subscriber) {
        try {
            JSONArray jSONArray = new JSONObject((String) dVar.b()).getJSONArray("emails");
            if (jSONArray.length() != 0) {
                for (int i = 0; i <= jSONArray.length(); i++) {
                    if (jSONArray.getJSONObject(i).getString("direction").equals("inbound")) {
                        Date b = g.b(jSONArray.getJSONObject(i).getString("created_at"));
                        s.a(b);
                        Intent intent = new Intent();
                        intent.setAction("User last contact at changed");
                        intent.putExtra("last_contacted_at", b.getTime());
                        LocalBroadcastManager.getInstance(aVar.a).sendBroadcast(intent);
                        break;
                    }
                }
            }
            s.a(false);
            InstabugSDKLogger.d(aVar, "last message contacted at: " + s.f());
            subscriber.onNext(aVar.getMigrationId());
        } catch (JSONException e) {
            InstabugSDKLogger.d(aVar, "Something went wrong while parsing last_contacted_at response " + e.getMessage());
        }
    }

    public a() {
        super(b);
    }

    public final String getMigrationId() {
        return b;
    }

    public final int getMigrationVersion() {
        return 1;
    }

    public final void initialize(Context context) {
        this.a = context;
    }

    public final Observable<String> migrate() {
        return Observable.create(new OnSubscribe<String>(this) {
            final /* synthetic */ a a;

            {
                this.a = r1;
            }

            public final /* synthetic */ void call(Object obj) {
                Throwable e;
                final Subscriber subscriber = (Subscriber) obj;
                try {
                    e.a().a(this.a.a, null, 0, null, new com.instabug.library.e.c.a<d, Throwable>(this) {
                        final /* synthetic */ AnonymousClass1 b;

                        public final /* synthetic */ void b(Object obj) {
                            a.a(this.b.a, (d) obj, subscriber);
                        }

                        public final /* bridge */ /* synthetic */ void a(Object obj) {
                            InstabugSDKLogger.d(this, "Something went wrong while migrate last contacted at");
                        }
                    });
                    subscriber.onCompleted();
                } catch (JSONException e2) {
                    e = e2;
                    try {
                        e.printStackTrace();
                        subscriber.onError(e);
                    } finally {
                        subscriber.onCompleted();
                    }
                } catch (IOException e3) {
                    e = e3;
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    public final boolean shouldMigrate() {
        return s.d() && s.f() == 0;
    }
}
