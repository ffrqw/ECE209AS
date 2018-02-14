package com.rachio.iro.utils;

import android.content.Context;
import com.electricimp.blinkup.BlinkupController;

public class WifiUtils {
    public static final String getNetworkName(Context context) {
        return BlinkupController.getCurrentWifiSSID(context);
    }
}
