package com.google.android.gms.location;

import android.location.Location;
import com.google.android.gms.common.api.GoogleApiClient;

public interface FusedLocationProviderApi {
    Location getLastLocation(GoogleApiClient googleApiClient);
}
