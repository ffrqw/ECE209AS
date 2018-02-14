package com.instabug.library;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import com.instabug.library.e.a;
import com.instabug.library.util.InstabugSDKLogger;

public abstract class g extends IntentService {
    protected abstract void b() throws Exception;

    public g() {
        super(g.class.getSimpleName());
        setIntentRedelivery(true);
    }

    public void onCreate() {
        super.onCreate();
        InstabugSDKLogger.v(this, "New " + getClass().getSimpleName() + " created");
    }

    protected void onHandleIntent(Intent intent) {
        InstabugSDKLogger.v(this, getClass().getSimpleName() + " started with intent " + intent);
        if (a.a((Context) this)) {
            InstabugSDKLogger.v(this, "Internet is good to go");
            try {
                InstabugSDKLogger.v(this, "Starting " + getClass().getSimpleName() + " task");
                b();
            } catch (Exception e) {
                InstabugSDKLogger.d(this, "An error occurred while doing " + getClass().getSimpleName() + "'s required task " + e);
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        InstabugSDKLogger.v(this, getClass().getSimpleName() + " destroyed");
    }
}
