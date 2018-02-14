package com.instabug.library.a;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.instabug.library.util.InstabugSDKLogger;

public final class b extends BroadcastReceiver {
    private a a;

    public interface a {
        void a(boolean z);
    }

    public b(a aVar) {
        this.a = aVar;
    }

    public final void onReceive(Context context, Intent intent) {
        InstabugSDKLogger.v(this, " - onReceive");
        this.a.a(intent.getExtras().getBoolean("SDK invoking state"));
    }
}
