package com.instabug.library.gcm;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.instabug.library.e.a.c;
import com.instabug.library.e.c.a;
import com.instabug.library.n;
import com.instabug.library.s;
import com.instabug.library.util.InstabugSDKLogger;
import org.json.JSONException;
import org.json.JSONObject;

public class InstabugGcmRegistrationIntentService extends n {
    private SharedPreferences a;

    protected final void b() throws Exception {
        this.a = PreferenceManager.getDefaultSharedPreferences(this);
        String v = s.v();
        InstabugSDKLogger.d(this, "GCM Registration Token: " + v);
        if (!this.a.getBoolean("sentInstabugTokenToServer", false)) {
            c.a().a(this, v, new a<String, Throwable>(this) {
                final /* synthetic */ InstabugGcmRegistrationIntentService a;

                {
                    this.a = r1;
                }

                public final /* synthetic */ void b(Object obj) {
                    try {
                        if (new JSONObject((String) obj).getString("status").equalsIgnoreCase("ok")) {
                            this.a.a.edit().putBoolean("sentInstabugTokenToServer", true).apply();
                            InstabugSDKLogger.d(this, "GCM Token sent to server");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                public final /* bridge */ /* synthetic */ void a(Object obj) {
                    InstabugSDKLogger.d(this, "Something went wrong while register GCM");
                }
            });
        }
    }
}
