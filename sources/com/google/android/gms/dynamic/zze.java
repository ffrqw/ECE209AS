package com.google.android.gms.dynamic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

final class zze implements zzi {
    private /* synthetic */ zza zzaSv;
    private /* synthetic */ FrameLayout zzaSx;
    private /* synthetic */ LayoutInflater zzaSy;
    private /* synthetic */ ViewGroup zzaSz;
    private /* synthetic */ Bundle zzxV;

    zze(zza zza, FrameLayout frameLayout, LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.zzaSv = zza;
        this.zzaSx = frameLayout;
        this.zzaSy = layoutInflater;
        this.zzaSz = viewGroup;
        this.zzxV = bundle;
    }

    public final int getState() {
        return 2;
    }

    public final void zzb$6728a24f() {
        this.zzaSx.removeAllViews();
        this.zzaSx.addView(this.zzaSv.zzaSr.onCreateView(this.zzaSy, this.zzaSz, this.zzxV));
    }
}
