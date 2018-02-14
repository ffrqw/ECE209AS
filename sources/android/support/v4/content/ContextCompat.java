package android.support.v4.content;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.os.BuildCompat;
import android.util.TypedValue;
import java.io.File;

public class ContextCompat {
    private static final Object sLock = new Object();
    private static TypedValue sTempValue;

    public static boolean startActivities(Context context, Intent[] intents, Bundle options) {
        int version = VERSION.SDK_INT;
        if (version >= 16) {
            context.startActivities(intents, options);
            return true;
        } else if (version < 11) {
            return false;
        } else {
            context.startActivities(intents);
            return true;
        }
    }

    public static File[] getExternalFilesDirs(Context context, String type) {
        if (VERSION.SDK_INT >= 19) {
            return context.getExternalFilesDirs(null);
        }
        return new File[]{context.getExternalFilesDir(null)};
    }

    public static File[] getExternalCacheDirs(Context context) {
        if (VERSION.SDK_INT >= 19) {
            return context.getExternalCacheDirs();
        }
        return new File[]{context.getExternalCacheDir()};
    }

    public static final Drawable getDrawable(Context context, int id) {
        int version = VERSION.SDK_INT;
        if (version >= 21) {
            return context.getDrawable(id);
        }
        if (version >= 16) {
            return context.getResources().getDrawable(id);
        }
        int resolvedId;
        synchronized (sLock) {
            if (sTempValue == null) {
                sTempValue = new TypedValue();
            }
            context.getResources().getValue(id, sTempValue, true);
            resolvedId = sTempValue.resourceId;
        }
        return context.getResources().getDrawable(resolvedId);
    }

    public static final ColorStateList getColorStateList(Context context, int id) {
        if (VERSION.SDK_INT >= 23) {
            return context.getColorStateList(id);
        }
        return context.getResources().getColorStateList(id);
    }

    public static final int getColor(Context context, int id) {
        if (VERSION.SDK_INT >= 23) {
            return context.getColor(id);
        }
        return context.getResources().getColor(id);
    }

    public static int checkSelfPermission(Context context, String permission) {
        if (permission != null) {
            return context.checkPermission(permission, Process.myPid(), Process.myUid());
        }
        throw new IllegalArgumentException("permission is null");
    }

    public static boolean isDeviceProtectedStorage(Context context) {
        if (BuildCompat.isAtLeastN()) {
            return context.isDeviceProtectedStorage();
        }
        return false;
    }
}
