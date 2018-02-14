package com.google.android.gms.location;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzf;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzccb;
import com.google.android.gms.internal.zzccq;
import com.google.android.gms.internal.zzcdj;
import com.google.android.gms.internal.zzcdu;

public final class LocationServices {
    public static final Api<Object> API = new Api("LocationServices.API", zzajS, zzajR);
    public static final FusedLocationProviderApi FusedLocationApi = new zzccb();
    public static final GeofencingApi GeofencingApi = new zzccq();
    public static final SettingsApi SettingsApi = new zzcdu();
    private static final zzf<zzcdj> zzajR = new zzf();
    private static final zza<zzcdj, Object> zzajS = new zzs();

    public static zzcdj zzg(GoogleApiClient googleApiClient) {
        boolean z = true;
        zzbo.zzb(googleApiClient != null, (Object) "GoogleApiClient parameter is required.");
        zzcdj zzcdj = (zzcdj) googleApiClient.zza(zzajR);
        if (zzcdj == null) {
            z = false;
        }
        zzbo.zza(z, "GoogleApiClient is not configured to use the LocationServices.API Api. Pass thisinto GoogleApiClient.Builder#addApi() to use this feature.");
        return zzcdj;
    }
}
