package io.fabric.sdk.android.services.common;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Debug;
import android.os.StatFs;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import io.fabric.sdk.android.Fabric;
import java.io.Closeable;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public final class CommonUtils {
    public static final Comparator<File> FILE_MODIFIED_COMPARATOR = new Comparator<File>() {
        public final /* bridge */ /* synthetic */ int compare(Object obj, Object obj2) {
            return (int) (((File) obj).lastModified() - ((File) obj2).lastModified());
        }
    };
    private static final char[] HEX_VALUES = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static Boolean clsTrace = null;
    private static long totalRamInBytes = -1;

    enum Architecture {
        X86_32,
        X86_64,
        ARM_UNKNOWN,
        PPC,
        PPC64,
        ARMV6,
        ARMV7,
        UNKNOWN,
        ARMV7S,
        ARM64;
        
        private static final Map<String, Architecture> matcher = null;

        static {
            Map hashMap = new HashMap(4);
            matcher = hashMap;
            hashMap.put("armeabi-v7a", ARMV7);
            matcher.put("armeabi", ARMV6);
            matcher.put("arm64-v8a", ARM64);
            matcher.put("x86", X86_32);
        }

        static Architecture getValue() {
            String arch = Build.CPU_ABI;
            if (TextUtils.isEmpty(arch)) {
                Fabric.getLogger().d("Fabric", "Architecture#getValue()::Build.CPU_ABI returned null or empty");
                return UNKNOWN;
            }
            Architecture value = (Architecture) matcher.get(arch.toLowerCase(Locale.US));
            if (value == null) {
                return UNKNOWN;
            }
            return value;
        }
    }

    public static java.lang.String getAppIconHashOrNull(android.content.Context r7) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.ssa.SSATransform.placePhi(SSATransform.java:82)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:50)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r3 = 0;
        r1 = 0;
        r4 = r7.getResources();	 Catch:{ Exception -> 0x001f, all -> 0x0032 }
        r5 = getAppIconResourceId(r7);	 Catch:{ Exception -> 0x001f, all -> 0x0032 }
        r1 = r4.openRawResource(r5);	 Catch:{ Exception -> 0x001f, all -> 0x0032 }
        r2 = hash$4e276518(r1);	 Catch:{ Exception -> 0x001f, all -> 0x0032 }
        r4 = isNullOrEmpty(r2);	 Catch:{ Exception -> 0x001f, all -> 0x0032 }
        if (r4 == 0) goto L_0x0019;
    L_0x0018:
        r2 = r3;
    L_0x0019:
        r3 = "Failed to close icon input stream.";
        closeOrLog(r1, r3);
    L_0x001e:
        return r2;
    L_0x001f:
        r0 = move-exception;
        r4 = io.fabric.sdk.android.Fabric.getLogger();	 Catch:{ Exception -> 0x001f, all -> 0x0032 }
        r5 = "Fabric";	 Catch:{ Exception -> 0x001f, all -> 0x0032 }
        r6 = "Could not calculate hash for app icon.";	 Catch:{ Exception -> 0x001f, all -> 0x0032 }
        r4.e(r5, r6, r0);	 Catch:{ Exception -> 0x001f, all -> 0x0032 }
        r4 = "Failed to close icon input stream.";
        closeOrLog(r1, r4);
        r2 = r3;
        goto L_0x001e;
    L_0x0032:
        r3 = move-exception;
        r4 = "Failed to close icon input stream.";
        closeOrLog(r1, r4);
        throw r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.fabric.sdk.android.services.common.CommonUtils.getAppIconHashOrNull(android.content.Context):java.lang.String");
    }

    public static SharedPreferences getSharedPrefs(Context context) {
        return context.getSharedPreferences("com.crashlytics.prefs", 0);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String extractFieldFromSystemFile(java.io.File r11, java.lang.String r12) {
        /*
        r9 = 1;
        r6 = 0;
        r7 = r11.exists();
        if (r7 == 0) goto L_0x003a;
    L_0x0008:
        r0 = 0;
        r1 = new java.io.BufferedReader;	 Catch:{ Exception -> 0x003b }
        r7 = new java.io.FileReader;	 Catch:{ Exception -> 0x003b }
        r7.<init>(r11);	 Catch:{ Exception -> 0x003b }
        r8 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r1.<init>(r7, r8);	 Catch:{ Exception -> 0x003b }
    L_0x0015:
        r3 = r1.readLine();	 Catch:{ Exception -> 0x0064, all -> 0x0061 }
        if (r3 == 0) goto L_0x0035;
    L_0x001b:
        r7 = "\\s*:\\s*";
        r4 = java.util.regex.Pattern.compile(r7);	 Catch:{ Exception -> 0x0064, all -> 0x0061 }
        r7 = 2;
        r5 = r4.split(r3, r7);	 Catch:{ Exception -> 0x0064, all -> 0x0061 }
        r7 = r5.length;	 Catch:{ Exception -> 0x0064, all -> 0x0061 }
        if (r7 <= r9) goto L_0x0015;
    L_0x0029:
        r7 = 0;
        r7 = r5[r7];	 Catch:{ Exception -> 0x0064, all -> 0x0061 }
        r7 = r7.equals(r12);	 Catch:{ Exception -> 0x0064, all -> 0x0061 }
        if (r7 == 0) goto L_0x0015;
    L_0x0032:
        r7 = 1;
        r6 = r5[r7];	 Catch:{ Exception -> 0x0064, all -> 0x0061 }
    L_0x0035:
        r7 = "Failed to close system file reader.";
        closeOrLog(r1, r7);
    L_0x003a:
        return r6;
    L_0x003b:
        r2 = move-exception;
    L_0x003c:
        r7 = io.fabric.sdk.android.Fabric.getLogger();	 Catch:{ all -> 0x005a }
        r8 = "Fabric";
        r9 = new java.lang.StringBuilder;	 Catch:{ all -> 0x005a }
        r10 = "Error parsing ";
        r9.<init>(r10);	 Catch:{ all -> 0x005a }
        r9 = r9.append(r11);	 Catch:{ all -> 0x005a }
        r9 = r9.toString();	 Catch:{ all -> 0x005a }
        r7.e(r8, r9, r2);	 Catch:{ all -> 0x005a }
        r7 = "Failed to close system file reader.";
        closeOrLog(r0, r7);
        goto L_0x003a;
    L_0x005a:
        r7 = move-exception;
    L_0x005b:
        r8 = "Failed to close system file reader.";
        closeOrLog(r0, r8);
        throw r7;
    L_0x0061:
        r7 = move-exception;
        r0 = r1;
        goto L_0x005b;
    L_0x0064:
        r2 = move-exception;
        r0 = r1;
        goto L_0x003c;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.fabric.sdk.android.services.common.CommonUtils.extractFieldFromSystemFile(java.io.File, java.lang.String):java.lang.String");
    }

    public static int getCpuArchitectureInt() {
        return Architecture.getValue().ordinal();
    }

    public static synchronized long getTotalRamInBytes() {
        long j;
        synchronized (CommonUtils.class) {
            if (totalRamInBytes == -1) {
                long bytes = 0;
                String result = extractFieldFromSystemFile(new File("/proc/meminfo"), "MemTotal");
                if (!TextUtils.isEmpty(result)) {
                    result = result.toUpperCase(Locale.US);
                    try {
                        if (result.endsWith("KB")) {
                            bytes = convertMemInfoToBytes(result, "KB", 1024);
                        } else if (result.endsWith("MB")) {
                            bytes = convertMemInfoToBytes(result, "MB", 1048576);
                        } else if (result.endsWith("GB")) {
                            bytes = convertMemInfoToBytes(result, "GB", 1073741824);
                        } else {
                            Fabric.getLogger().d("Fabric", "Unexpected meminfo format while computing RAM: " + result);
                        }
                    } catch (NumberFormatException e) {
                        Fabric.getLogger().e("Fabric", "Unexpected meminfo format while computing RAM: " + result, e);
                    }
                }
                totalRamInBytes = bytes;
            }
            j = totalRamInBytes;
        }
        return j;
    }

    private static long convertMemInfoToBytes(String memInfo, String notation, int notationMultiplier) {
        return Long.parseLong(memInfo.split(notation)[0].trim()) * ((long) notationMultiplier);
    }

    public static RunningAppProcessInfo getAppProcessInfo(String packageName, Context context) {
        List<RunningAppProcessInfo> processes = ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses();
        if (processes == null) {
            return null;
        }
        for (RunningAppProcessInfo info : processes) {
            if (info.processName.equals(packageName)) {
                return info;
            }
        }
        return null;
    }

    public static String streamToString(InputStream is) throws IOException {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String sha1(String source) {
        return hash(source.getBytes(), "SHA-1");
    }

    private static String hash$4e276518(InputStream source) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] buffer = new byte[1024];
            while (true) {
                int length = source.read(buffer);
                if (length == -1) {
                    return hexify(digest.digest());
                }
                digest.update(buffer, 0, length);
            }
        } catch (Exception e) {
            Fabric.getLogger().e("Fabric", "Could not calculate hash for app icon.", e);
            return "";
        }
    }

    private static String hash(byte[] bytes, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.update(bytes);
            return hexify(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            Fabric.getLogger().e("Fabric", "Could not create hashing algorithm: " + algorithm + ", returning empty string.", e);
            return "";
        }
    }

    public static String createInstanceIdFrom(String... sliceIds) {
        List<String> sliceIdList = new ArrayList();
        for (int i = 0; i <= 0; i++) {
            String id = sliceIds[0];
            if (id != null) {
                sliceIdList.add(id.replace("-", "").toLowerCase(Locale.US));
            }
        }
        Collections.sort(sliceIdList);
        StringBuilder sb = new StringBuilder();
        for (String id2 : sliceIdList) {
            sb.append(id2);
        }
        String concatValue = sb.toString();
        return concatValue.length() > 0 ? sha1(concatValue) : null;
    }

    public static long calculateFreeRamInBytes(Context context) {
        MemoryInfo mi = new MemoryInfo();
        ((ActivityManager) context.getSystemService("activity")).getMemoryInfo(mi);
        return mi.availMem;
    }

    public static long calculateUsedDiskSpaceInBytes(String path) {
        StatFs statFs = new StatFs(path);
        long blockSizeBytes = (long) statFs.getBlockSize();
        return (blockSizeBytes * ((long) statFs.getBlockCount())) - (blockSizeBytes * ((long) statFs.getAvailableBlocks()));
    }

    public static Float getBatteryLevel(Context context) {
        Intent battery = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        if (battery == null) {
            return null;
        }
        return Float.valueOf(((float) battery.getIntExtra("level", -1)) / ((float) battery.getIntExtra("scale", -1)));
    }

    public static boolean getProximitySensorEnabled(Context context) {
        if (isEmulator(context) || ((SensorManager) context.getSystemService("sensor")).getDefaultSensor(8) == null) {
            return false;
        }
        return true;
    }

    public static void logControlled(Context context, String msg) {
        if (isClsTrace(context)) {
            Fabric.getLogger().d("Fabric", msg);
        }
    }

    public static void logControlledError$43da9ce8(Context context, String msg) {
        if (isClsTrace(context)) {
            Fabric.getLogger().e("Fabric", msg);
        }
    }

    public static void logControlled$3aaf2084(Context context, int level, String msg) {
        if (isClsTrace(context)) {
            Fabric.getLogger().log(4, "Fabric", msg);
        }
    }

    private static boolean isClsTrace(Context context) {
        if (clsTrace == null) {
            clsTrace = Boolean.valueOf(getBooleanResourceValue(context, "com.crashlytics.Trace", false));
        }
        return clsTrace.booleanValue();
    }

    public static boolean getBooleanResourceValue(Context context, String key, boolean defaultValue) {
        if (context == null) {
            return defaultValue;
        }
        Resources resources = context.getResources();
        if (resources == null) {
            return defaultValue;
        }
        int id = getResourcesIdentifier(context, key, "bool");
        if (id > 0) {
            return resources.getBoolean(id);
        }
        id = getResourcesIdentifier(context, key, "string");
        if (id > 0) {
            return Boolean.parseBoolean(context.getString(id));
        }
        return defaultValue;
    }

    public static int getResourcesIdentifier(Context context, String key, String resourceType) {
        String resourcePackageName;
        Resources resources = context.getResources();
        int i = context.getApplicationContext().getApplicationInfo().icon;
        if (i > 0) {
            resourcePackageName = context.getResources().getResourcePackageName(i);
        } else {
            resourcePackageName = context.getPackageName();
        }
        return resources.getIdentifier(key, resourceType, resourcePackageName);
    }

    public static boolean isEmulator(Context context) {
        return "sdk".equals(Build.PRODUCT) || "google_sdk".equals(Build.PRODUCT) || Secure.getString(context.getContentResolver(), "android_id") == null;
    }

    public static boolean isRooted(Context context) {
        boolean isEmulator = isEmulator(context);
        String buildTags = Build.TAGS;
        if ((!isEmulator && buildTags != null && buildTags.contains("test-keys")) || new File("/system/app/Superuser.apk").exists()) {
            return true;
        }
        File file = new File("/system/xbin/su");
        if (isEmulator || !file.exists()) {
            return false;
        }
        return true;
    }

    public static int getDeviceState(Context context) {
        Object obj;
        int deviceState = 0;
        if (isEmulator(context)) {
            deviceState = 1;
        }
        if (isRooted(context)) {
            deviceState |= 2;
        }
        if (Debug.isDebuggerConnected() || Debug.waitingForDebugger()) {
            obj = 1;
        } else {
            obj = null;
        }
        if (obj != null) {
            return deviceState | 4;
        }
        return deviceState;
    }

    public static int getBatteryVelocity(Context context, boolean powerConnected) {
        Float batteryLevel = getBatteryLevel(context);
        if (!powerConnected || batteryLevel == null) {
            return 1;
        }
        if (((double) batteryLevel.floatValue()) >= 99.0d) {
            return 3;
        }
        if (((double) batteryLevel.floatValue()) < 99.0d) {
            return 2;
        }
        return 0;
    }

    public static String hexify(byte[] bytes) {
        char[] hexChars = new char[(bytes.length << 1)];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 255;
            hexChars[i << 1] = HEX_VALUES[v >>> 4];
            hexChars[(i << 1) + 1] = HEX_VALUES[v & 15];
        }
        return new String(hexChars);
    }

    public static boolean isAppDebuggable(Context context) {
        return (context.getApplicationInfo().flags & 2) != 0;
    }

    public static String getStringsFileValue(Context context, String key) {
        int id = getResourcesIdentifier(context, key, "string");
        if (id > 0) {
            return context.getString(id);
        }
        return "";
    }

    public static void closeOrLog(Closeable c, String message) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                Fabric.getLogger().e("Fabric", message, e);
            }
        }
    }

    public static void flushOrLog(Flushable f, String message) {
        if (f != null) {
            try {
                f.flush();
            } catch (IOException e) {
                Fabric.getLogger().e("Fabric", message, e);
            }
        }
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static String padWithZerosToMaxIntWidth(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("value must be zero or greater");
        }
        return String.format(Locale.US, "%1$10s", new Object[]{Integer.valueOf(value)}).replace(' ', '0');
    }

    public static void copyStream(InputStream is, OutputStream os, byte[] buffer) throws IOException {
        while (true) {
            int count = is.read(buffer);
            if (count != -1) {
                os.write(buffer, 0, count);
            } else {
                return;
            }
        }
    }

    public static String logPriorityToString(int priority) {
        switch (priority) {
            case 2:
                return "V";
            case 3:
                return "D";
            case 4:
                return "I";
            case 5:
                return "W";
            case 6:
                return "E";
            case 7:
                return "A";
            default:
                return "?";
        }
    }

    public static int getAppIconResourceId(Context context) {
        return context.getApplicationContext().getApplicationInfo().icon;
    }

    public static String resolveBuildId(Context context) {
        int id = getResourcesIdentifier(context, "io.fabric.android.build_id", "string");
        if (id == 0) {
            id = getResourcesIdentifier(context, "com.crashlytics.android.build_id", "string");
        }
        if (id == 0) {
            return null;
        }
        String buildId = context.getResources().getString(id);
        Fabric.getLogger().d("Fabric", "Build ID is: " + buildId);
        return buildId;
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception e) {
            }
        }
    }

    public static boolean canTryConnection(Context context) {
        boolean z;
        if (context.checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") == 0) {
            z = true;
        } else {
            z = false;
        }
        if (!z) {
            return true;
        }
        NetworkInfo activeNetwork = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetwork == null || !activeNetwork.isConnectedOrConnecting()) {
            return false;
        }
        return true;
    }
}
