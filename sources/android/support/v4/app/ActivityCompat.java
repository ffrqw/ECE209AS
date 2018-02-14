package android.support.v4.app;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompatApi23.RequestPermissionsRequestCodeValidator;
import android.support.v4.content.ContextCompat;

public final class ActivityCompat extends ContextCompat {

    public interface OnRequestPermissionsResultCallback {
        void onRequestPermissionsResult(int i, String[] strArr, int[] iArr);
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode, Bundle options) {
        if (VERSION.SDK_INT >= 16) {
            activity.startActivityForResult(intent, requestCode, options);
        } else {
            activity.startActivityForResult(intent, requestCode);
        }
    }

    public static void startIntentSenderForResult(Activity activity, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws SendIntentException {
        if (VERSION.SDK_INT >= 16) {
            activity.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
        } else {
            activity.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags);
        }
    }

    public static void finishAffinity(Activity activity) {
        if (VERSION.SDK_INT >= 16) {
            activity.finishAffinity();
        } else {
            activity.finish();
        }
    }

    public static void requestPermissions(final Activity activity, final String[] permissions, final int requestCode) {
        if (VERSION.SDK_INT >= 23) {
            if (activity instanceof RequestPermissionsRequestCodeValidator) {
                ((RequestPermissionsRequestCodeValidator) activity).validateRequestPermissionsRequestCode(requestCode);
            }
            activity.requestPermissions(permissions, requestCode);
        } else if (activity instanceof OnRequestPermissionsResultCallback) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public final void run() {
                    int[] grantResults = new int[permissions.length];
                    PackageManager packageManager = activity.getPackageManager();
                    String packageName = activity.getPackageName();
                    int permissionCount = permissions.length;
                    for (int i = 0; i < permissionCount; i++) {
                        grantResults[i] = packageManager.checkPermission(permissions[i], packageName);
                    }
                    ((OnRequestPermissionsResultCallback) activity).onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            });
        }
    }

    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        if (VERSION.SDK_INT >= 23) {
            return activity.shouldShowRequestPermissionRationale(permission);
        }
        return false;
    }
}
