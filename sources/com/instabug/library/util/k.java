package com.instabug.library.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build.VERSION;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

public class k {
    public static boolean a(Context context, String str) {
        boolean z = false;
        try {
            if (VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(context, str) == 0) {
                    z = true;
                }
                InstabugSDKLogger.d(k.class, "Permission " + str + " state is " + (z ? "" : "NOT ") + "granted");
                return z;
            }
            if (context.checkCallingOrSelfPermission(str) == 0) {
                z = true;
            }
            InstabugSDKLogger.d(k.class, "Permission " + str + " state is " + (z ? "" : "NOT ") + "granted");
            return z;
        } catch (Exception e) {
            return true;
        } catch (Error e2) {
            return true;
        }
    }

    public static void a(Activity activity, String str, int i, Runnable runnable, Runnable runnable2) {
        if (a(activity, str)) {
            InstabugSDKLogger.d(k.class, "Permission " + str + " already granted, running after permission granted runnable");
            a(null);
            return;
        }
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, str)) {
            a(null);
        }
        InstabugSDKLogger.d(k.class, "Permission " + str + " not granted, requesting it");
        ActivityCompat.requestPermissions(activity, new String[]{str}, 1);
    }

    public static void a(Fragment fragment, String str, int i, Runnable runnable, Runnable runnable2) {
        if (a(fragment.getContext(), str)) {
            InstabugSDKLogger.d(k.class, "Permission " + str + " already granted, running after permission granted runnable");
            a(runnable2);
            return;
        }
        if (!fragment.shouldShowRequestPermissionRationale(str)) {
            a(runnable);
        }
        InstabugSDKLogger.d(k.class, "Permission " + str + " not granted, requesting it");
        fragment.requestPermissions(new String[]{str}, 1);
    }

    private static void a(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
    }
}
