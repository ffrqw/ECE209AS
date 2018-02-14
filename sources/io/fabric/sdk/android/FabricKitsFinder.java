package io.fabric.sdk.android;

import android.os.SystemClock;
import android.text.TextUtils;
import io.fabric.sdk.android.services.common.CommonUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

final class FabricKitsFinder implements Callable<Map<String, KitInfo>> {
    final String apkFileName;

    FabricKitsFinder(String apkFileName) {
        this.apkFileName = apkFileName;
    }

    private static Map<String, KitInfo> findImplicitKits() {
        Map<String, KitInfo> implicitKits = new HashMap();
        try {
            Class.forName("com.google.android.gms.ads.AdView");
            KitInfo admobKitInfo = new KitInfo("com.google.firebase.firebase-ads", "0.0.0", "binary");
            implicitKits.put(admobKitInfo.getIdentifier(), admobKitInfo);
            Fabric.getLogger().v("Fabric", "Found kit: com.google.firebase.firebase-ads");
        } catch (Exception e) {
        }
        return implicitKits;
    }

    private Map<String, KitInfo> findRegisteredKits() throws Exception {
        Map<String, KitInfo> registeredKits = new HashMap();
        ZipFile apkFile = new ZipFile(this.apkFileName);
        Enumeration<? extends ZipEntry> entries = apkFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            if (entry.getName().startsWith("fabric/") && entry.getName().length() > 7) {
                KitInfo kitInfo = loadKitInfo(entry, apkFile);
                if (kitInfo != null) {
                    registeredKits.put(kitInfo.getIdentifier(), kitInfo);
                    Fabric.getLogger().v("Fabric", String.format("Found kit:[%s] version:[%s]", new Object[]{kitInfo.getIdentifier(), kitInfo.getVersion()}));
                }
            }
        }
        try {
            apkFile.close();
        } catch (IOException e) {
        }
        return registeredKits;
    }

    private static KitInfo loadKitInfo(ZipEntry fabricFile, ZipFile apk) {
        KitInfo kitInfo;
        InputStream inputStream = null;
        try {
            inputStream = apk.getInputStream(fabricFile);
            Properties properties = new Properties();
            properties.load(inputStream);
            String id = properties.getProperty("fabric-identifier");
            String version = properties.getProperty("fabric-version");
            String buildType = properties.getProperty("fabric-build-type");
            if (TextUtils.isEmpty(id) || TextUtils.isEmpty(version)) {
                throw new IllegalStateException("Invalid format of fabric file," + fabricFile.getName());
            }
            kitInfo = new KitInfo(id, version, buildType);
            return kitInfo;
        } catch (IOException ie) {
            kitInfo = Fabric.getLogger();
            kitInfo.e("Fabric", "Error when parsing fabric properties " + fabricFile.getName(), ie);
            return null;
        } finally {
            CommonUtils.closeQuietly(inputStream);
        }
    }

    public final /* bridge */ /* synthetic */ Object call() throws Exception {
        Map hashMap = new HashMap();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        hashMap.putAll(findImplicitKits());
        hashMap.putAll(findRegisteredKits());
        Fabric.getLogger().v("Fabric", "finish scanning in " + (SystemClock.elapsedRealtime() - elapsedRealtime));
        return hashMap;
    }
}
