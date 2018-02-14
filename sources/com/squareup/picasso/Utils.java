package com.squareup.picasso;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.StatFs;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import okio.BufferedSource;
import okio.ByteString;

final class Utils {
    static final StringBuilder MAIN_THREAD_KEY_BUILDER = new StringBuilder();
    private static final ByteString WEBP_FILE_HEADER_RIFF = ByteString.encodeUtf8("RIFF");
    private static final ByteString WEBP_FILE_HEADER_WEBP = ByteString.encodeUtf8("WEBP");

    private static class PicassoThread extends Thread {
        public PicassoThread(Runnable r) {
            super(r);
        }

        public final void run() {
            Process.setThreadPriority(10);
            super.run();
        }
    }

    static class PicassoThreadFactory implements ThreadFactory {
        PicassoThreadFactory() {
        }

        public final Thread newThread(Runnable r) {
            return new PicassoThread(r);
        }
    }

    static int getBitmapBytes(Bitmap bitmap) {
        int result = VERSION.SDK_INT >= 19 ? bitmap.getAllocationByteCount() : bitmap.getByteCount();
        if (result >= 0) {
            return result;
        }
        throw new IllegalStateException("Negative size: " + bitmap);
    }

    static <T> T checkNotNull(T value, String message) {
        if (value != null) {
            return value;
        }
        throw new NullPointerException(message);
    }

    static String getLogIdsForHunter(BitmapHunter hunter) {
        return getLogIdsForHunter(hunter, "");
    }

    static String getLogIdsForHunter(BitmapHunter hunter, String prefix) {
        StringBuilder builder = new StringBuilder(prefix);
        Action action = hunter.action;
        if (action != null) {
            builder.append(action.request.logId());
        }
        List<Action> actions = hunter.actions;
        if (actions != null) {
            int count = actions.size();
            for (int i = 0; i < count; i++) {
                if (i > 0 || action != null) {
                    builder.append(", ");
                }
                builder.append(((Action) actions.get(i)).request.logId());
            }
        }
        return builder.toString();
    }

    static void log(String owner, String verb, String logId) {
        log(owner, verb, logId, "");
    }

    static void log(String owner, String verb, String logId, String extras) {
        Log.d("Picasso", String.format("%1$-11s %2$-12s %3$s %4$s", new Object[]{owner, verb, logId, extras}));
    }

    static String createKey(Request data) {
        String result = createKey(data, MAIN_THREAD_KEY_BUILDER);
        MAIN_THREAD_KEY_BUILDER.setLength(0);
        return result;
    }

    static String createKey(Request data, StringBuilder builder) {
        if (data.stableKey != null) {
            builder.ensureCapacity(data.stableKey.length() + 50);
            builder.append(data.stableKey);
        } else if (data.uri != null) {
            String path = data.uri.toString();
            builder.ensureCapacity(path.length() + 50);
            builder.append(path);
        } else {
            builder.ensureCapacity(50);
            builder.append(data.resourceId);
        }
        builder.append('\n');
        if (data.rotationDegrees != 0.0f) {
            builder.append("rotation:").append(data.rotationDegrees);
            if (data.hasRotationPivot) {
                builder.append('@').append(data.rotationPivotX).append('x').append(data.rotationPivotY);
            }
            builder.append('\n');
        }
        if (data.hasSize()) {
            builder.append("resize:").append(data.targetWidth).append('x').append(data.targetHeight);
            builder.append('\n');
        }
        if (data.centerCrop) {
            builder.append("centerCrop:").append(data.centerCropGravity).append('\n');
        } else if (data.centerInside) {
            builder.append("centerInside\n");
        }
        if (data.transformations != null) {
            int count = data.transformations.size();
            for (int i = 0; i < count; i++) {
                builder.append(((Transformation) data.transformations.get(i)).key());
                builder.append('\n');
            }
        }
        return builder.toString();
    }

    static File createDefaultCacheDir(Context context) {
        File cache = new File(context.getApplicationContext().getCacheDir(), "picasso-cache");
        if (!cache.exists()) {
            cache.mkdirs();
        }
        return cache;
    }

    @TargetApi(18)
    static long calculateDiskCacheSize(File dir) {
        long size = 5242880;
        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            size = ((VERSION.SDK_INT < 18 ? (long) statFs.getBlockCount() : statFs.getBlockCountLong()) * (VERSION.SDK_INT < 18 ? (long) statFs.getBlockSize() : statFs.getBlockSizeLong())) / 50;
        } catch (IllegalArgumentException e) {
        }
        return Math.max(Math.min(size, 52428800), 5242880);
    }

    static int calculateMemoryCacheSize(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService("activity");
        return (int) ((1048576 * ((long) ((context.getApplicationInfo().flags & 1048576) != 0 ? am.getLargeMemoryClass() : am.getMemoryClass()))) / 7);
    }

    static boolean isAirplaneModeOn(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        try {
            if (VERSION.SDK_INT < 17) {
                if (System.getInt(contentResolver, "airplane_mode_on", 0) != 0) {
                    return true;
                }
                return false;
            } else if (Global.getInt(contentResolver, "airplane_mode_on", 0) == 0) {
                return false;
            } else {
                return true;
            }
        } catch (NullPointerException e) {
            return false;
        } catch (SecurityException e2) {
            return false;
        }
    }

    static <T> T getService(Context context, String service) {
        return context.getSystemService(service);
    }

    static boolean hasPermission(Context context, String permission) {
        return context.checkCallingOrSelfPermission(permission) == 0;
    }

    static boolean isWebPFile(BufferedSource source) throws IOException {
        return source.rangeEquals(0, WEBP_FILE_HEADER_RIFF) && source.rangeEquals(8, WEBP_FILE_HEADER_WEBP);
    }

    static int getResourceId(Resources resources, Request data) throws FileNotFoundException {
        if (data.resourceId != 0 || data.uri == null) {
            return data.resourceId;
        }
        String pkg = data.uri.getAuthority();
        if (pkg == null) {
            throw new FileNotFoundException("No package provided: " + data.uri);
        }
        List<String> segments = data.uri.getPathSegments();
        if (segments == null || segments.isEmpty()) {
            throw new FileNotFoundException("No path segments: " + data.uri);
        } else if (segments.size() == 1) {
            try {
                return Integer.parseInt((String) segments.get(0));
            } catch (NumberFormatException e) {
                throw new FileNotFoundException("Last path segment is not a resource ID: " + data.uri);
            }
        } else if (segments.size() == 2) {
            return resources.getIdentifier((String) segments.get(1), (String) segments.get(0), pkg);
        } else {
            throw new FileNotFoundException("More than two path segments: " + data.uri);
        }
    }

    static Resources getResources(Context context, Request data) throws FileNotFoundException {
        if (data.resourceId != 0 || data.uri == null) {
            return context.getResources();
        }
        String pkg = data.uri.getAuthority();
        if (pkg == null) {
            throw new FileNotFoundException("No package provided: " + data.uri);
        }
        try {
            return context.getPackageManager().getResourcesForApplication(pkg);
        } catch (NameNotFoundException e) {
            throw new FileNotFoundException("Unable to obtain resources for package: " + data.uri);
        }
    }

    static void flushStackLocalLeaks(Looper looper) {
        Handler handler = new Handler(looper) {
            public final void handleMessage(Message msg) {
                sendMessageDelayed(obtainMessage(), 1000);
            }
        };
        handler.sendMessageDelayed(handler.obtainMessage(), 1000);
    }

    static void checkMain() {
        Object obj;
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            obj = 1;
        } else {
            obj = null;
        }
        if (obj == null) {
            throw new IllegalStateException("Method call should happen from the main thread.");
        }
    }
}
