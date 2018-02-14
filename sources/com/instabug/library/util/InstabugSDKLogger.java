package com.instabug.library.util;

import android.util.Log;
import com.instabug.library.Instabug;

public final class InstabugSDKLogger {
    private static final String LOG_TAG = "INSTABUG - ";

    private InstabugSDKLogger() {
    }

    public static String logTag(Object obj) {
        String simpleName;
        if (obj instanceof Class) {
            simpleName = ((Class) obj).getSimpleName();
        } else {
            simpleName = obj.getClass().getSimpleName();
        }
        return new StringBuilder(LOG_TAG).append(simpleName).toString();
    }

    public static void v(Object obj, String str) {
    }

    public static void d(Object obj, String str) {
        if (Instabug.isDebugEnabled()) {
            String logTag = logTag(obj);
            if (str.length() > 4000) {
                int length = str.length() / 4000;
                Log.d(logTag, "logMessage length = " + str.length() + " divided to " + (length + 1) + " chunks");
                for (int i = 0; i <= length; i++) {
                    String substring;
                    int i2 = (i + 1) * 4000;
                    if (i2 >= str.length()) {
                        substring = str.substring(i * 4000);
                    } else {
                        substring = str.substring(i * 4000, i2);
                    }
                    Log.d(logTag, "chunk " + (i + 1) + " of " + (length + 1) + ":\n" + substring);
                }
                return;
            }
            Log.d(logTag, str);
        }
    }

    public static void i(Object obj, String str) {
        Log.i(logTag(obj), str);
    }

    public static void w(Object obj, String str) {
        Log.w(logTag(obj), str);
    }

    public static void e(Object obj, String str) {
        Log.e(logTag(obj), str);
    }

    public static void e(Object obj, String str, Throwable th) {
        Log.e(logTag(obj), str, th);
    }

    public static void wtf(Object obj, String str) {
        Log.wtf(logTag(obj), str);
    }

    public static void wtf(Object obj, String str, Throwable th) {
        Log.wtf(logTag(obj), str, th);
    }
}
