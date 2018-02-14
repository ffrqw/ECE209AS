package com.rachio.iro.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import java.text.DecimalFormat;
import java.util.List;

public class LocationUtils {
    public static double[] getLocation(Context context, boolean round) {
        LocationManager lm = (LocationManager) context.getSystemService("location");
        List<String> providers = lm.getProviders(true);
        Location l = null;
        for (int i = providers.size() - 1; i >= 0; i--) {
            l = lm.getLastKnownLocation((String) providers.get(i));
            if (l != null) {
                break;
            }
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        double[] gps = new double[2];
        if (l != null) {
            gps[0] = l.getLatitude();
            gps[1] = l.getLongitude();
        }
        return gps;
    }
}
