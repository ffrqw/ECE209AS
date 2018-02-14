package com.instabug.library.a;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.instabug.library.util.InstabugSDKLogger;

public final class a extends BroadcastReceiver {
    private a a;

    public interface a {
        void c();
    }

    public a(a aVar) {
        this.a = aVar;
    }

    public final void onReceive(Context context, Intent intent) {
        InstabugSDKLogger.v(this, " - onReceive");
        intent.getExtras().getLong("last_contacted_at");
        this.a.c();
    }
}
