package com.crashlytics.android.beta;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.cache.ValueLoader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class DeviceTokenLoader implements ValueLoader<String> {
    private String load(Context context) throws Exception {
        IOException e;
        Throwable th;
        FileNotFoundException e2;
        long start = System.nanoTime();
        String token = "";
        ZipInputStream zis = null;
        try {
            ZipInputStream zis2 = new ZipInputStream(new FileInputStream(context.getPackageManager().getApplicationInfo("io.crash.air", 0).sourceDir));
            try {
                ZipEntry nextEntry = zis2.getNextEntry();
                if (nextEntry != null) {
                    String name = nextEntry.getName();
                    if (name.startsWith("assets/com.crashlytics.android.beta/dirfactor-device-token=")) {
                        token = name.substring(59, name.length() - 1);
                        zis2.close();
                        zis = zis2;
                        Fabric.getLogger().d("Beta", "Beta device token load took " + (((double) (System.nanoTime() - start)) / 1000000.0d) + "ms");
                        return token;
                    }
                }
                token = "";
                try {
                    zis2.close();
                    zis = zis2;
                } catch (IOException e3) {
                    Fabric.getLogger().e("Beta", "Failed to close the APK file", e3);
                    zis = zis2;
                }
            } catch (NameNotFoundException e4) {
                zis = zis2;
                try {
                    Fabric.getLogger().d("Beta", "Beta by Crashlytics app is not installed");
                    if (zis != null) {
                        try {
                            zis.close();
                        } catch (IOException e32) {
                            Fabric.getLogger().e("Beta", "Failed to close the APK file", e32);
                        }
                    }
                    Fabric.getLogger().d("Beta", "Beta device token load took " + (((double) (System.nanoTime() - start)) / 1000000.0d) + "ms");
                    return token;
                } catch (Throwable th2) {
                    th = th2;
                    if (zis != null) {
                        try {
                            zis.close();
                        } catch (IOException e322) {
                            Fabric.getLogger().e("Beta", "Failed to close the APK file", e322);
                        }
                    }
                    throw th;
                }
            } catch (FileNotFoundException e5) {
                e2 = e5;
                zis = zis2;
                Fabric.getLogger().e("Beta", "Failed to find the APK file", e2);
                if (zis != null) {
                    try {
                        zis.close();
                    } catch (IOException e3222) {
                        Fabric.getLogger().e("Beta", "Failed to close the APK file", e3222);
                    }
                }
                Fabric.getLogger().d("Beta", "Beta device token load took " + (((double) (System.nanoTime() - start)) / 1000000.0d) + "ms");
                return token;
            } catch (IOException e6) {
                e3222 = e6;
                zis = zis2;
                Fabric.getLogger().e("Beta", "Failed to read the APK file", e3222);
                if (zis != null) {
                    try {
                        zis.close();
                    } catch (IOException e32222) {
                        Fabric.getLogger().e("Beta", "Failed to close the APK file", e32222);
                    }
                }
                Fabric.getLogger().d("Beta", "Beta device token load took " + (((double) (System.nanoTime() - start)) / 1000000.0d) + "ms");
                return token;
            } catch (Throwable th3) {
                th = th3;
                zis = zis2;
                if (zis != null) {
                    zis.close();
                }
                throw th;
            }
        } catch (NameNotFoundException e7) {
            Fabric.getLogger().d("Beta", "Beta by Crashlytics app is not installed");
            if (zis != null) {
                zis.close();
            }
            Fabric.getLogger().d("Beta", "Beta device token load took " + (((double) (System.nanoTime() - start)) / 1000000.0d) + "ms");
            return token;
        } catch (FileNotFoundException e8) {
            e2 = e8;
            Fabric.getLogger().e("Beta", "Failed to find the APK file", e2);
            if (zis != null) {
                zis.close();
            }
            Fabric.getLogger().d("Beta", "Beta device token load took " + (((double) (System.nanoTime() - start)) / 1000000.0d) + "ms");
            return token;
        } catch (IOException e9) {
            e32222 = e9;
            Fabric.getLogger().e("Beta", "Failed to read the APK file", e32222);
            if (zis != null) {
                zis.close();
            }
            Fabric.getLogger().d("Beta", "Beta device token load took " + (((double) (System.nanoTime() - start)) / 1000000.0d) + "ms");
            return token;
        }
        Fabric.getLogger().d("Beta", "Beta device token load took " + (((double) (System.nanoTime() - start)) / 1000000.0d) + "ms");
        return token;
    }
}
