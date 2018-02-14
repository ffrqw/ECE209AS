package com.rachio.iro.ui.prodevicelist.activity;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.activity.DashboardActivity;
import com.rachio.iro.utils.CrashReporterUtils;
import com.rachio.iro.utils.PermissionRequester;
import com.rachio.iro.utils.PermissionRequester.Listener;

public class BaseProDeviceActivity extends BaseActivity implements Listener {
    private GoogleApiClient googleApiClient;
    private boolean haveLocationPermission = false;
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            BaseProDeviceActivity.this.onLocationChanged(location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };
    protected PermissionRequester permissionRequester;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.permissionRequester = new PermissionRequester((BaseActivity) this, "android.permission.ACCESS_FINE_LOCATION", (Listener) this);
        this.googleApiClient = new Builder(this).addConnectionCallbacks(new ConnectionCallbacks() {
            public final void onConnected(Bundle bundle) {
                BaseProDeviceActivity.this.updateLocation();
            }

            public final void onConnectionSuspended(int i) {
            }
        }).addOnConnectionFailedListener(new OnConnectionFailedListener() {
            public final void onConnectionFailed(ConnectionResult connectionResult) {
            }
        }).addApi(LocationServices.API).build();
    }

    protected void onStart() {
        super.onStart();
        this.googleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        this.googleApiClient.disconnect();
    }

    protected void onPause() {
        super.onPause();
    }

    protected final void updateLocation() {
        if (this.haveLocationPermission && this.googleApiClient.isConnected()) {
            try {
                Location last = LocationServices.FusedLocationApi.getLastLocation(this.googleApiClient);
                if (last != null) {
                    onLocationChanged(last);
                }
            } catch (SecurityException se) {
                CrashReporterUtils.silentExceptionThatCrashesDebugBuilds(se);
            }
        }
    }

    protected void onLocationChanged(Location location) {
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        this.permissionRequester.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onPermissionsGranted() {
        this.haveLocationPermission = true;
        updateLocation();
    }

    public void onPermissionsDenied() {
    }

    protected final void switchDevice(String deviceId) {
        DashboardActivity.goToDashboard(this, this.prefsWrapper, this.prefsWrapper.getLoggedInUserId(), true, false, null, deviceId, false, true);
    }
}
