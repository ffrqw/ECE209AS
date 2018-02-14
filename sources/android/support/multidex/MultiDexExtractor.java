package android.support.multidex;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.os.Build.VERSION;
import android.util.Log;
import com.rachio.iro.gen2.MrvlProvService;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

final class MultiDexExtractor {
    private static Method sApplyMethod;

    static List<File> load(Context context, ApplicationInfo applicationInfo, File dexDir, boolean forceReload) throws IOException {
        List<File> files;
        Log.i("MultiDex", "MultiDexExtractor.load(" + applicationInfo.sourceDir + ", " + forceReload + ")");
        File sourceApk = new File(applicationInfo.sourceDir);
        long currentCrc = ZipUtil.getZipCrc(sourceApk);
        if (currentCrc == -1) {
            currentCrc--;
        }
        if (!forceReload) {
            Object obj;
            SharedPreferences multiDexPreferences = getMultiDexPreferences(context);
            if (multiDexPreferences.getLong("timestamp", -1) == getTimeStamp(sourceApk) && multiDexPreferences.getLong("crc", -1) == currentCrc) {
                obj = null;
            } else {
                obj = 1;
            }
            if (obj == null) {
                try {
                    files = loadExistingExtractions(context, sourceApk, dexDir);
                } catch (IOException ioe) {
                    Log.w("MultiDex", "Failed to reload existing extracted secondary dex files, falling back to fresh extraction", ioe);
                    files = performExtractions(sourceApk, dexDir);
                    putStoredApkInfo(context, getTimeStamp(sourceApk), currentCrc, files.size() + 1);
                }
                Log.i("MultiDex", "load found " + files.size() + " secondary dex files");
                return files;
            }
        }
        Log.i("MultiDex", "Detected that extraction must be performed.");
        files = performExtractions(sourceApk, dexDir);
        putStoredApkInfo(context, getTimeStamp(sourceApk), currentCrc, files.size() + 1);
        Log.i("MultiDex", "load found " + files.size() + " secondary dex files");
        return files;
    }

    private static List<File> loadExistingExtractions(Context context, File sourceApk, File dexDir) throws IOException {
        Log.i("MultiDex", "loading existing secondary dex files");
        String extractedFilePrefix = sourceApk.getName() + ".classes";
        int totalDexNumber = getMultiDexPreferences(context).getInt("dex.number", 1);
        List<File> files = new ArrayList(totalDexNumber);
        int secondaryNumber = 2;
        while (secondaryNumber <= totalDexNumber) {
            File extractedFile = new File(dexDir, extractedFilePrefix + secondaryNumber + ".zip");
            if (extractedFile.isFile()) {
                files.add(extractedFile);
                if (verifyZipFile(extractedFile)) {
                    secondaryNumber++;
                } else {
                    Log.i("MultiDex", "Invalid zip file: " + extractedFile);
                    throw new IOException("Invalid ZIP file.");
                }
            }
            throw new IOException("Missing extracted secondary dex file '" + extractedFile.getPath() + "'");
        }
        return files;
    }

    private static long getTimeStamp(File archive) {
        long timeStamp = archive.lastModified();
        if (timeStamp == -1) {
            return timeStamp - 1;
        }
        return timeStamp;
    }

    private static List<File> performExtractions(File sourceApk, File dexDir) throws IOException {
        String extractedFilePrefix = sourceApk.getName() + ".classes";
        prepareDexDir(dexDir, extractedFilePrefix);
        List<File> files = new ArrayList();
        ZipFile apk = new ZipFile(sourceApk);
        int secondaryNumber = 2;
        try {
            ZipEntry dexFile = apk.getEntry("classes" + 2 + ".dex");
            while (dexFile != null) {
                File extractedFile = new File(dexDir, extractedFilePrefix + secondaryNumber + ".zip");
                files.add(extractedFile);
                Log.i("MultiDex", "Extraction is needed for file " + extractedFile);
                int numAttempts = 0;
                boolean isExtractionSuccessful = false;
                while (numAttempts < 3 && !isExtractionSuccessful) {
                    numAttempts++;
                    extract(apk, dexFile, extractedFile, extractedFilePrefix);
                    isExtractionSuccessful = verifyZipFile(extractedFile);
                    Log.i("MultiDex", "Extraction " + (isExtractionSuccessful ? MrvlProvService.BROADCAST_SUCCESS : MrvlProvService.BROADCAST_FAILED) + " - length " + extractedFile.getAbsolutePath() + ": " + extractedFile.length());
                    if (!isExtractionSuccessful) {
                        extractedFile.delete();
                        if (extractedFile.exists()) {
                            Log.w("MultiDex", "Failed to delete corrupted secondary dex '" + extractedFile.getPath() + "'");
                        }
                    }
                }
                if (isExtractionSuccessful) {
                    secondaryNumber++;
                    dexFile = apk.getEntry("classes" + secondaryNumber + ".dex");
                } else {
                    throw new IOException("Could not create zip file " + extractedFile.getAbsolutePath() + " for secondary dex (" + secondaryNumber + ")");
                }
            }
            return files;
        } finally {
            try {
                apk.close();
            } catch (IOException e) {
                Log.w("MultiDex", "Failed to close resource", e);
            }
        }
    }

    private static void putStoredApkInfo(Context context, long timeStamp, long crc, int totalDexNumber) {
        Editor edit = getMultiDexPreferences(context).edit();
        edit.putLong("timestamp", timeStamp);
        edit.putLong("crc", crc);
        edit.putInt("dex.number", totalDexNumber);
        if (sApplyMethod != null) {
            try {
                sApplyMethod.invoke(edit, new Object[0]);
                return;
            } catch (InvocationTargetException e) {
            } catch (IllegalAccessException e2) {
            }
        }
        edit.commit();
    }

    private static SharedPreferences getMultiDexPreferences(Context context) {
        return context.getSharedPreferences("multidex.version", VERSION.SDK_INT < 11 ? 0 : 4);
    }

    private static void prepareDexDir(File dexDir, final String extractedFilePrefix) throws IOException {
        mkdirChecked(dexDir.getParentFile());
        mkdirChecked(dexDir);
        File[] files = dexDir.listFiles(new FileFilter() {
            public final boolean accept(File pathname) {
                return !pathname.getName().startsWith(extractedFilePrefix);
            }
        });
        if (files == null) {
            Log.w("MultiDex", "Failed to list secondary dex dir content (" + dexDir.getPath() + ").");
            return;
        }
        File[] arr$ = files;
        int len$ = files.length;
        for (int i$ = 0; i$ < len$; i$++) {
            File oldFile = arr$[i$];
            Log.i("MultiDex", "Trying to delete old file " + oldFile.getPath() + " of size " + oldFile.length());
            if (oldFile.delete()) {
                Log.i("MultiDex", "Deleted old file " + oldFile.getPath());
            } else {
                Log.w("MultiDex", "Failed to delete old file " + oldFile.getPath());
            }
        }
    }

    private static void mkdirChecked(File dir) throws IOException {
        dir.mkdir();
        if (!dir.isDirectory()) {
            File parent = dir.getParentFile();
            if (parent == null) {
                Log.e("MultiDex", "Failed to create dir " + dir.getPath() + ". Parent file is null.");
            } else {
                Log.e("MultiDex", "Failed to create dir " + dir.getPath() + ". parent file is a dir " + parent.isDirectory() + ", a file " + parent.isFile() + ", exists " + parent.exists() + ", readable " + parent.canRead() + ", writable " + parent.canWrite());
            }
            throw new IOException("Failed to create cache directory " + dir.getPath());
        }
    }

    private static void extract(ZipFile apk, ZipEntry dexFile, File extractTo, String extractedFilePrefix) throws IOException, FileNotFoundException {
        InputStream in = apk.getInputStream(dexFile);
        File tmp = File.createTempFile(extractedFilePrefix, ".zip", extractTo.getParentFile());
        Log.i("MultiDex", "Extracting " + tmp.getPath());
        ZipOutputStream out;
        try {
            out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(tmp)));
            ZipEntry classesDex = new ZipEntry("classes.dex");
            classesDex.setTime(dexFile.getTime());
            out.putNextEntry(classesDex);
            byte[] buffer = new byte[16384];
            for (int length = in.read(buffer); length != -1; length = in.read(buffer)) {
                out.write(buffer, 0, length);
            }
            out.closeEntry();
            out.close();
            Log.i("MultiDex", "Renaming to " + extractTo.getPath());
            if (tmp.renameTo(extractTo)) {
                closeQuietly(in);
                tmp.delete();
                return;
            }
            throw new IOException("Failed to rename \"" + tmp.getAbsolutePath() + "\" to \"" + extractTo.getAbsolutePath() + "\"");
        } catch (Throwable th) {
            closeQuietly(in);
            tmp.delete();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static boolean verifyZipFile(java.io.File r5) {
        /*
        r1 = new java.util.zip.ZipFile;	 Catch:{ ZipException -> 0x0025, IOException -> 0x0045 }
        r1.<init>(r5);	 Catch:{ ZipException -> 0x0025, IOException -> 0x0045 }
        r1.close();	 Catch:{ IOException -> 0x000a, ZipException -> 0x0025 }
        r2 = 1;
    L_0x0009:
        return r2;
    L_0x000a:
        r2 = move-exception;
        r2 = "MultiDex";
        r3 = new java.lang.StringBuilder;	 Catch:{ ZipException -> 0x0025, IOException -> 0x0045 }
        r4 = "Failed to close zip file: ";
        r3.<init>(r4);	 Catch:{ ZipException -> 0x0025, IOException -> 0x0045 }
        r4 = r5.getAbsolutePath();	 Catch:{ ZipException -> 0x0025, IOException -> 0x0045 }
        r3 = r3.append(r4);	 Catch:{ ZipException -> 0x0025, IOException -> 0x0045 }
        r3 = r3.toString();	 Catch:{ ZipException -> 0x0025, IOException -> 0x0045 }
        android.util.Log.w(r2, r3);	 Catch:{ ZipException -> 0x0025, IOException -> 0x0045 }
    L_0x0023:
        r2 = 0;
        goto L_0x0009;
    L_0x0025:
        r0 = move-exception;
        r2 = "MultiDex";
        r3 = new java.lang.StringBuilder;
        r4 = "File ";
        r3.<init>(r4);
        r4 = r5.getAbsolutePath();
        r3 = r3.append(r4);
        r4 = " is not a valid zip file.";
        r3 = r3.append(r4);
        r3 = r3.toString();
        android.util.Log.w(r2, r3, r0);
        goto L_0x0023;
    L_0x0045:
        r0 = move-exception;
        r2 = "MultiDex";
        r3 = new java.lang.StringBuilder;
        r4 = "Got an IOException trying to open zip file: ";
        r3.<init>(r4);
        r4 = r5.getAbsolutePath();
        r3 = r3.append(r4);
        r3 = r3.toString();
        android.util.Log.w(r2, r3, r0);
        goto L_0x0023;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.multidex.MultiDexExtractor.verifyZipFile(java.io.File):boolean");
    }

    private static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            Log.w("MultiDex", "Failed to close resource", e);
        }
    }

    static {
        try {
            sApplyMethod = Editor.class.getMethod("apply", new Class[0]);
        } catch (NoSuchMethodException e) {
            sApplyMethod = null;
        }
    }
}
