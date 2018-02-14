package com.instabug.library.internal.a;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Process;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.instabug.library.Feature;
import com.instabug.library.Feature.State;
import com.instabug.library.InstabugFeaturesManager;
import com.instabug.library.s;
import com.instabug.library.util.InstabugSDKLogger;
import com.rachio.iro.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.UUID;

public final class b {
    private boolean a = false;
    private String b = "";
    private String c = null;
    private String d = "";
    private String e = "";
    private String f = "";
    private String g = null;
    private String h = "Unknown";
    private String i = "Unknown";
    private String j = null;
    private String k;
    private String l;
    private String m;
    private String o;

    private static class a {
        public static boolean a() {
            return Environment.getExternalStorageState().equals("mounted");
        }

        public static String a(long j) {
            if (j == 0) {
                return "Unavailable";
            }
            String str = null;
            if (j >= 1024) {
                String str2 = " KB";
                long j2 = j / 1024;
                if (j >= 1024) {
                    j = j2 / 1024;
                    str = " MB";
                } else {
                    j = j2;
                    str = str2;
                }
            }
            StringBuilder stringBuilder = new StringBuilder(Long.toString(j));
            if (str != null) {
                stringBuilder.append(str);
            }
            return stringBuilder.toString();
        }
    }

    public b(Context context) {
        try {
            this.a = context.getResources().getBoolean(R.bool.isTablet);
        } catch (Exception e) {
            this.a = false;
        }
        if (context.getResources().getConfiguration().orientation == 2) {
            this.b = "landscape";
        } else {
            this.b = "portrait";
        }
        this.f = context.getResources().getConfiguration().locale.toString();
        if (this.g == null) {
            PackageInfo packageInfo = new PackageInfo();
            try {
                packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            } catch (NameNotFoundException e2) {
                e2.printStackTrace();
            }
            this.g = String.format("%s (%s)", new Object[]{packageInfo.versionName, Integer.valueOf(packageInfo.versionCode)});
        }
        try {
            Intent registerReceiver = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
            this.h = ((int) ((((float) registerReceiver.getIntExtra("level", -1)) / ((float) registerReceiver.getIntExtra("scale", -1))) * 100.0f)) + "%";
        } catch (Exception e3) {
        }
        try {
            String str;
            registerReceiver = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
            int intExtra = registerReceiver.getIntExtra("status", -1);
            boolean z = intExtra == 2 || intExtra == 5;
            int intExtra2 = registerReceiver.getIntExtra("plugged", -1);
            if (intExtra2 == 2) {
                intExtra = 1;
            } else {
                boolean z2 = false;
            }
            if (intExtra2 == 1) {
                intExtra2 = 1;
            } else {
                boolean z3 = false;
            }
            if (z) {
                StringBuilder stringBuilder = new StringBuilder("Charging");
                str = intExtra2 != 0 ? " through AC Charger" : intExtra != 0 ? " through USB cable" : "";
                str = stringBuilder.append(str).toString();
            } else {
                str = "Unplugged";
            }
            this.i = str;
        } catch (Exception e4) {
        }
        Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        if (VERSION.SDK_INT >= 17) {
            defaultDisplay.getRealMetrics(displayMetrics);
        }
        this.e = String.format("%sx%s", new Object[]{Integer.valueOf(displayMetrics.widthPixels), Integer.valueOf(displayMetrics.heightPixels)});
        if (displayMetrics.densityDpi < 160) {
            this.d = "ldpi";
        } else if (displayMetrics.densityDpi < 240) {
            this.d = "mdpi";
        } else if (displayMetrics.densityDpi < 320) {
            this.d = "hdpi";
        } else if (displayMetrics.densityDpi < 480) {
            this.d = "xhdpi";
        } else if (displayMetrics.densityDpi < 640) {
            this.d = "xxhdpi";
        } else {
            this.d = "xxxhdpi";
        }
        if (this.c == null) {
            try {
                this.c = context.getApplicationInfo().packageName;
            } catch (Exception e5) {
                this.c = "Could not get information";
            }
        }
        if (this.j == null) {
            this.j = s.p();
            if (this.j == null) {
                this.j = UUID.randomUUID().toString();
                s.d(this.j);
            }
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        MemoryInfo memoryInfo = new MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        this.k = a.a(memoryInfo.availMem);
        try {
            this.l = ((TelephonyManager) context.getSystemService("phone")).getNetworkOperatorName();
        } catch (Exception e6) {
            this.l = "Unknown";
        }
        try {
            if (((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(1).isConnected()) {
                try {
                    this.m = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo().getSSID();
                } catch (SecurityException e7) {
                    InstabugSDKLogger.d(this, "Could not read wifi SSID. To enable please add the following line in your AndroidManifest.xml <uses-permission android:name=\"android.permission.ACCESS_WIFI_STATE\"/>");
                    this.m = "Connected";
                }
                activityManager = (ActivityManager) context.getSystemService("activity");
                memoryInfo = new MemoryInfo();
                activityManager.getMemoryInfo(memoryInfo);
                this.o = a.a(c(context) - memoryInfo.availMem);
                a.a(c(context));
            }
            this.m = "Not Connected";
            activityManager = (ActivityManager) context.getSystemService("activity");
            memoryInfo = new MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            this.o = a.a(c(context) - memoryInfo.availMem);
            a.a(c(context));
        } catch (Exception e8) {
            this.m = "Could not get information";
        }
    }

    public static String a() {
        return Build.MANUFACTURER + " " + Build.MODEL;
    }

    public final String c() {
        return this.b;
    }

    public final String d() {
        return this.d;
    }

    public final String e() {
        return this.e;
    }

    public final String f() {
        try {
            if (c.a()) {
                return "Rooted";
            }
        } catch (Exception e) {
            InstabugSDKLogger.w(this, "Something went wrong while checking if device is rooted or not " + e.getMessage());
        }
        return "Not rooted";
    }

    public final String g() {
        return this.h;
    }

    public final String h() {
        return this.i;
    }

    public final String i() {
        return this.l;
    }

    public final String j() {
        return this.g;
    }

    public final String k() {
        return this.c;
    }

    public final String l() {
        return this.j;
    }

    public final String m() {
        return this.m;
    }

    public final String n() {
        if (InstabugFeaturesManager.getInstance().getFeatureState(Feature.CONSOLE_LOGS) != State.ENABLED) {
            return "";
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("logcat -v time -d " + Process.myPid()).getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            ArrayList arrayList = new ArrayList();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                arrayList.add(readLine);
            }
            bufferedReader.close();
            arrayList.trimToSize();
            int i = 0;
            if (arrayList.size() > 700) {
                i = arrayList.size() - 700;
            }
            for (int i2 = i; i2 < arrayList.size(); i2++) {
                stringBuilder.append((String) arrayList.get(i2));
                stringBuilder.append("\n");
            }
            arrayList.clear();
            return stringBuilder.toString();
        } catch (Throwable e) {
            InstabugSDKLogger.e(this, "Could not read logcat log", e);
            return "Error in reading Log File";
        }
    }

    public final String o() {
        return this.o;
    }

    private long c(Context context) {
        if (VERSION.SDK_INT < 16) {
            return v();
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        MemoryInfo memoryInfo = new MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.totalMem;
    }

    private static long v() {
        RandomAccessFile randomAccessFile;
        IOException e;
        Throwable th;
        RandomAccessFile randomAccessFile2 = null;
        try {
            randomAccessFile = new RandomAccessFile("/proc/meminfo", "r");
            try {
                String trim = randomAccessFile.readLine().split(":")[1].trim();
                trim = trim.substring(0, trim.length() - 3).trim();
                randomAccessFile.close();
                long parseLong = Long.parseLong(trim) << 10;
                try {
                    randomAccessFile.close();
                    return parseLong;
                } catch (IOException e2) {
                    e2.printStackTrace();
                    return parseLong;
                }
            } catch (IOException e3) {
                e = e3;
                randomAccessFile2 = randomAccessFile;
                try {
                    e.printStackTrace();
                    if (randomAccessFile2 != null) {
                        try {
                            randomAccessFile2.close();
                        } catch (IOException e4) {
                            e4.printStackTrace();
                        }
                    }
                    return 0;
                } catch (Throwable th2) {
                    th = th2;
                    randomAccessFile = randomAccessFile2;
                    if (randomAccessFile != null) {
                        try {
                            randomAccessFile.close();
                        } catch (IOException e5) {
                            e5.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
                throw th;
            }
        } catch (IOException e6) {
            e4 = e6;
            e4.printStackTrace();
            if (randomAccessFile2 != null) {
                randomAccessFile2.close();
            }
            return 0;
        } catch (Throwable th4) {
            th = th4;
            randomAccessFile = null;
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
            throw th;
        }
    }

    public final String q() {
        return this.k;
    }

    public final String u() {
        return this.f;
    }

    public static String r() {
        if (a.a()) {
            return a.a(Environment.getExternalStorageDirectory().getTotalSpace() - Environment.getExternalStorageDirectory().getFreeSpace());
        }
        return "Unavailable";
    }

    public static String s() {
        if (a.a()) {
            return a.a(Environment.getExternalStorageDirectory().getUsableSpace());
        }
        return "Unavailable";
    }

    public static String t() {
        if (a.a()) {
            return a.a(Environment.getExternalStorageDirectory().getTotalSpace());
        }
        return "Unavailable";
    }
}
