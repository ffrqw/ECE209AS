package com.google.android.gms.internal;

import android.location.Location;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;

public final class zzccb implements FusedLocationProviderApi {
    public final Location getLastLocation(GoogleApiClient googleApiClient) {
        try {
            return LocationServices.zzg(googleApiClient).getLastLocation();
        } catch (Exception e) {
            return null;
        }
    }
}
