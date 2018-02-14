package com.instabug.library.a;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.instabug.library.util.InstabugSDKLogger;

public final class c extends BroadcastReceiver {
    private a a;

    public interface a {
        void a(b bVar);
    }

    public enum b {
        Start,
        Active,
        Finish
    }

    public c(a aVar) {
        this.a = aVar;
    }

    public final void onReceive(Context context, Intent intent) {
        InstabugSDKLogger.v(this, " - onReceive");
        b bVar = (b) intent.getExtras().getSerializable("Session state");
        if (bVar == b.Start) {
            this.a.a(b.Start);
        } else if (bVar == b.Finish) {
            this.a.a(b.Finish);
        }
    }
}
