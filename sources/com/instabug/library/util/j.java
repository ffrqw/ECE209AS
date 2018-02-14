package com.instabug.library.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;

public class j {
    private j() {
    }

    public static void a(Activity activity) {
        if (activity != null) {
            InstabugSDKLogger.d(j.class, "Unlocking orientation for activity " + activity.toString());
            try {
                activity.setRequestedOrientation(activity.getPackageManager().getActivityInfo(new ComponentName(activity, activity.getClass()), 128).screenOrientation);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
                activity.setRequestedOrientation(-1);
            } catch (Exception e2) {
                activity.setRequestedOrientation(-1);
            }
        }
    }

    public static void b(Activity activity) {
        InstabugSDKLogger.d(j.class, "Locking orientation for activity " + activity.toString());
        int rotation;
        switch (activity.getResources().getConfiguration().orientation) {
            case 1:
                if (VERSION.SDK_INT < 8) {
                    activity.setRequestedOrientation(1);
                    return;
                }
                rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                if (rotation == 1 || rotation == 2) {
                    activity.setRequestedOrientation(9);
                    return;
                } else {
                    activity.setRequestedOrientation(1);
                    return;
                }
            case 2:
                if (VERSION.SDK_INT < 8) {
                    activity.setRequestedOrientation(0);
                    return;
                }
                rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                if (rotation == 0 || rotation == 1) {
                    activity.setRequestedOrientation(0);
                    return;
                } else {
                    activity.setRequestedOrientation(8);
                    return;
                }
            default:
                return;
        }
    }
}
