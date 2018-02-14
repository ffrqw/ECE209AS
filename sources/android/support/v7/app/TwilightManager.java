package android.support.v7.app;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import java.util.Calendar;

final class TwilightManager {
    private static TwilightManager sInstance;
    private final Context mContext;
    private final LocationManager mLocationManager;
    private final TwilightState mTwilightState = new TwilightState();

    private static class TwilightState {
        boolean isNight;
        long nextUpdate;
        long todaySunrise;
        long todaySunset;
        long tomorrowSunrise;
        long yesterdaySunset;

        TwilightState() {
        }
    }

    static TwilightManager getInstance(Context context) {
        if (sInstance == null) {
            context = context.getApplicationContext();
            sInstance = new TwilightManager(context, (LocationManager) context.getSystemService("location"));
        }
        return sInstance;
    }

    private TwilightManager(Context context, LocationManager locationManager) {
        this.mContext = context;
        this.mLocationManager = locationManager;
    }

    final boolean isNight() {
        Object obj;
        TwilightState state = this.mTwilightState;
        if (this.mTwilightState.nextUpdate > System.currentTimeMillis()) {
            obj = 1;
        } else {
            obj = null;
        }
        if (obj != null) {
            return state.isNight;
        }
        Location location = null;
        Location location2 = null;
        if (PermissionChecker.checkSelfPermission(this.mContext, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            location = getLastKnownLocationForProvider("network");
        }
        if (PermissionChecker.checkSelfPermission(this.mContext, "android.permission.ACCESS_FINE_LOCATION") == 0) {
            location2 = getLastKnownLocationForProvider("gps");
        }
        if (location2 == null || location == null) {
            if (location2 == null) {
                location2 = location;
            }
        } else if (location2.getTime() <= location.getTime()) {
            location2 = location;
        }
        if (location2 != null) {
            boolean z;
            long j;
            TwilightState twilightState = this.mTwilightState;
            long currentTimeMillis = System.currentTimeMillis();
            TwilightCalculator instance = TwilightCalculator.getInstance();
            instance.calculateTwilight(currentTimeMillis - 86400000, location2.getLatitude(), location2.getLongitude());
            long j2 = instance.sunset;
            instance.calculateTwilight(currentTimeMillis, location2.getLatitude(), location2.getLongitude());
            if (instance.state == 1) {
                z = true;
            } else {
                z = false;
            }
            long j3 = instance.sunrise;
            long j4 = instance.sunset;
            instance.calculateTwilight(86400000 + currentTimeMillis, location2.getLatitude(), location2.getLongitude());
            long j5 = instance.sunrise;
            if (j3 == -1 || j4 == -1) {
                j = 43200000 + currentTimeMillis;
            } else {
                if (currentTimeMillis > j4) {
                    j = 0 + j5;
                } else if (currentTimeMillis > j3) {
                    j = 0 + j4;
                } else {
                    j = 0 + j3;
                }
                j += 60000;
            }
            twilightState.isNight = z;
            twilightState.yesterdaySunset = j2;
            twilightState.todaySunrise = j3;
            twilightState.todaySunset = j4;
            twilightState.tomorrowSunrise = j5;
            twilightState.nextUpdate = j;
            return state.isNight;
        }
        Log.i("TwilightManager", "Could not get last known location. This is probably because the app does not have any location permissions. Falling back to hardcoded sunrise/sunset values.");
        int hour = Calendar.getInstance().get(11);
        return hour < 6 || hour >= 22;
    }

    private Location getLastKnownLocationForProvider(String provider) {
        if (this.mLocationManager != null) {
            try {
                if (this.mLocationManager.isProviderEnabled(provider)) {
                    return this.mLocationManager.getLastKnownLocation(provider);
                }
            } catch (Exception e) {
                Log.d("TwilightManager", "Failed to get last known location", e);
            }
        }
        return null;
    }
}
