package com.google.firebase.iid;

import android.util.Log;

final class zzg implements Runnable {
    private /* synthetic */ zzd zzckn;
    private /* synthetic */ zzf zzcko;

    zzg(zzf zzf, zzd zzd) {
        this.zzcko = zzf;
        this.zzckn = zzd;
    }

    public final void run() {
        if (Log.isLoggable("EnhancedIntentService", 3)) {
            Log.d("EnhancedIntentService", "bg processing of the intent starting now");
        }
        this.zzcko.zzckm.handleIntent(this.zzckn.intent);
        this.zzckn.finish();
    }
}
