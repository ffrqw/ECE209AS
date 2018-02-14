package android.support.multidex;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.util.Log;
import dalvik.system.DexFile;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

public final class MultiDex {
    private static final boolean IS_VM_MULTIDEX_CAPABLE = isVMMultidexCapable(System.getProperty("java.vm.version"));
    private static final String SECONDARY_FOLDER_NAME = ("code_cache" + File.separator + "secondary-dexes");
    private static final Set<String> installedApk = new HashSet();

    private static final class V14 {
        static /* synthetic */ void access$100(ClassLoader x0, List x1, File x2) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException {
            Object obj = MultiDex.findField(x0, "pathList").get(x0);
            ArrayList arrayList = new ArrayList(x1);
            MultiDex.access$400(obj, "dexElements", (Object[]) MultiDex.findMethod(obj, "makeDexElements", ArrayList.class, File.class).invoke(obj, new Object[]{arrayList, x2}));
        }
    }

    private static final class V19 {
        static /* synthetic */ void access$000(ClassLoader x0, List x1, File x2) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException {
            Object obj = MultiDex.findField(x0, "pathList").get(x0);
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList(x1);
            MultiDex.access$400(obj, "dexElements", (Object[]) MultiDex.findMethod(obj, "makeDexElements", ArrayList.class, File.class, ArrayList.class).invoke(obj, new Object[]{arrayList2, x2, arrayList}));
            if (arrayList.size() > 0) {
                Object obj2;
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    Log.w("MultiDex", "Exception in makeDexElement", (IOException) it.next());
                }
                Field access$300 = MultiDex.findField(x0, "dexElementsSuppressedExceptions");
                IOException[] iOExceptionArr = (IOException[]) access$300.get(x0);
                if (iOExceptionArr == null) {
                    obj2 = (IOException[]) arrayList.toArray(new IOException[arrayList.size()]);
                } else {
                    obj = new IOException[(arrayList.size() + iOExceptionArr.length)];
                    arrayList.toArray(obj);
                    System.arraycopy(iOExceptionArr, 0, obj, arrayList.size(), iOExceptionArr.length);
                    obj2 = obj;
                }
                access$300.set(x0, obj2);
            }
        }
    }

    private static final class V4 {
        static /* synthetic */ void access$200(ClassLoader x0, List x1) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, IOException {
            int size = x1.size();
            Field access$300 = MultiDex.findField(x0, "path");
            StringBuilder stringBuilder = new StringBuilder((String) access$300.get(x0));
            String[] strArr = new String[size];
            File[] fileArr = new File[size];
            ZipFile[] zipFileArr = new ZipFile[size];
            DexFile[] dexFileArr = new DexFile[size];
            ListIterator listIterator = x1.listIterator();
            while (listIterator.hasNext()) {
                File file = (File) listIterator.next();
                String absolutePath = file.getAbsolutePath();
                stringBuilder.append(':').append(absolutePath);
                int previousIndex = listIterator.previousIndex();
                strArr[previousIndex] = absolutePath;
                fileArr[previousIndex] = file;
                zipFileArr[previousIndex] = new ZipFile(file);
                dexFileArr[previousIndex] = DexFile.loadDex(absolutePath, absolutePath + ".dex", 0);
            }
            access$300.set(x0, stringBuilder.toString());
            MultiDex.access$400(x0, "mPaths", strArr);
            MultiDex.access$400(x0, "mFiles", fileArr);
            MultiDex.access$400(x0, "mZips", zipFileArr);
            MultiDex.access$400(x0, "mDexs", dexFileArr);
        }
    }

    static /* synthetic */ void access$400(Object x0, String x1, Object[] x2) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field findField = findField(x0, x1);
        Object[] objArr = (Object[]) findField.get(x0);
        Object[] objArr2 = (Object[]) Array.newInstance(objArr.getClass().getComponentType(), objArr.length + x2.length);
        System.arraycopy(objArr, 0, objArr2, 0, objArr.length);
        System.arraycopy(x2, 0, objArr2, objArr.length, x2.length);
        findField.set(x0, objArr2);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void install(android.content.Context r11) {
        /*
        r7 = "MultiDex";
        r8 = "install";
        android.util.Log.i(r7, r8);
        r7 = IS_VM_MULTIDEX_CAPABLE;
        if (r7 == 0) goto L_0x0013;
    L_0x000b:
        r7 = "MultiDex";
        r8 = "VM has multidex support, MultiDex support library is disabled.";
        android.util.Log.i(r7, r8);
    L_0x0012:
        return;
    L_0x0013:
        r7 = android.os.Build.VERSION.SDK_INT;
        r8 = 4;
        if (r7 >= r8) goto L_0x003b;
    L_0x0018:
        r7 = new java.lang.RuntimeException;
        r8 = new java.lang.StringBuilder;
        r9 = "Multi dex installation failed. SDK ";
        r8.<init>(r9);
        r9 = android.os.Build.VERSION.SDK_INT;
        r8 = r8.append(r9);
        r9 = " is unsupported. Min SDK version is 4";
        r8 = r8.append(r9);
        r9 = ".";
        r8 = r8.append(r9);
        r8 = r8.toString();
        r7.<init>(r8);
        throw r7;
    L_0x003b:
        r1 = getApplicationInfo(r11);	 Catch:{ Exception -> 0x0053 }
        if (r1 == 0) goto L_0x0012;
    L_0x0041:
        r8 = installedApk;	 Catch:{ Exception -> 0x0053 }
        monitor-enter(r8);	 Catch:{ Exception -> 0x0053 }
        r0 = r1.sourceDir;	 Catch:{ all -> 0x0050 }
        r7 = installedApk;	 Catch:{ all -> 0x0050 }
        r7 = r7.contains(r0);	 Catch:{ all -> 0x0050 }
        if (r7 == 0) goto L_0x007a;
    L_0x004e:
        monitor-exit(r8);	 Catch:{ all -> 0x0050 }
        goto L_0x0012;
    L_0x0050:
        r7 = move-exception;
        monitor-exit(r8);	 Catch:{ all -> 0x0050 }
        throw r7;	 Catch:{ Exception -> 0x0053 }
    L_0x0053:
        r3 = move-exception;
        r7 = "MultiDex";
        r8 = "Multidex installation failure";
        android.util.Log.e(r7, r8, r3);
        r7 = new java.lang.RuntimeException;
        r8 = new java.lang.StringBuilder;
        r9 = "Multi dex installation failed (";
        r8.<init>(r9);
        r9 = r3.getMessage();
        r8 = r8.append(r9);
        r9 = ").";
        r8 = r8.append(r9);
        r8 = r8.toString();
        r7.<init>(r8);
        throw r7;
    L_0x007a:
        r7 = installedApk;	 Catch:{ all -> 0x0050 }
        r7.add(r0);	 Catch:{ all -> 0x0050 }
        r7 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x0050 }
        r9 = 20;
        if (r7 <= r9) goto L_0x00bd;
    L_0x0085:
        r7 = "MultiDex";
        r9 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0050 }
        r10 = "MultiDex is not guaranteed to work in SDK version ";
        r9.<init>(r10);	 Catch:{ all -> 0x0050 }
        r10 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x0050 }
        r9 = r9.append(r10);	 Catch:{ all -> 0x0050 }
        r10 = ": SDK version higher than 20";
        r9 = r9.append(r10);	 Catch:{ all -> 0x0050 }
        r10 = " should be backed by runtime with built-in multidex capabilty but it's not the ";
        r9 = r9.append(r10);	 Catch:{ all -> 0x0050 }
        r10 = "case here: java.vm.version=\"";
        r9 = r9.append(r10);	 Catch:{ all -> 0x0050 }
        r10 = "java.vm.version";
        r10 = java.lang.System.getProperty(r10);	 Catch:{ all -> 0x0050 }
        r9 = r9.append(r10);	 Catch:{ all -> 0x0050 }
        r10 = "\"";
        r9 = r9.append(r10);	 Catch:{ all -> 0x0050 }
        r9 = r9.toString();	 Catch:{ all -> 0x0050 }
        android.util.Log.w(r7, r9);	 Catch:{ all -> 0x0050 }
    L_0x00bd:
        r5 = r11.getClassLoader();	 Catch:{ RuntimeException -> 0x00cd }
        if (r5 != 0) goto L_0x00d8;
    L_0x00c3:
        r7 = "MultiDex";
        r9 = "Context class loader is null. Must be running in test mode. Skip patching.";
        android.util.Log.e(r7, r9);	 Catch:{ all -> 0x0050 }
        monitor-exit(r8);	 Catch:{ all -> 0x0050 }
        goto L_0x0012;
    L_0x00cd:
        r3 = move-exception;
        r7 = "MultiDex";
        r9 = "Failure while trying to obtain Context class loader. Must be running in test mode. Skip patching.";
        android.util.Log.w(r7, r9, r3);	 Catch:{ all -> 0x0050 }
        monitor-exit(r8);	 Catch:{ all -> 0x0050 }
        goto L_0x0012;
    L_0x00d8:
        clearOldDexDir(r11);	 Catch:{ Throwable -> 0x00fc }
    L_0x00db:
        r2 = new java.io.File;	 Catch:{ all -> 0x0050 }
        r7 = r1.dataDir;	 Catch:{ all -> 0x0050 }
        r9 = SECONDARY_FOLDER_NAME;	 Catch:{ all -> 0x0050 }
        r2.<init>(r7, r9);	 Catch:{ all -> 0x0050 }
        r7 = 0;
        r4 = android.support.multidex.MultiDexExtractor.load(r11, r1, r2, r7);	 Catch:{ all -> 0x0050 }
        r7 = checkValidZipFiles(r4);	 Catch:{ all -> 0x0050 }
        if (r7 == 0) goto L_0x0105;
    L_0x00ef:
        installSecondaryDexes(r5, r2, r4);	 Catch:{ all -> 0x0050 }
    L_0x00f2:
        monitor-exit(r8);	 Catch:{ all -> 0x0050 }
        r7 = "MultiDex";
        r8 = "install done";
        android.util.Log.i(r7, r8);
        goto L_0x0012;
    L_0x00fc:
        r6 = move-exception;
        r7 = "MultiDex";
        r9 = "Something went wrong when trying to clear old MultiDex extraction, continuing without cleaning.";
        android.util.Log.w(r7, r9, r6);	 Catch:{ all -> 0x0050 }
        goto L_0x00db;
    L_0x0105:
        r7 = "MultiDex";
        r9 = "Files were not valid zip files.  Forcing a reload.";
        android.util.Log.w(r7, r9);	 Catch:{ all -> 0x0050 }
        r7 = 1;
        r4 = android.support.multidex.MultiDexExtractor.load(r11, r1, r2, r7);	 Catch:{ all -> 0x0050 }
        r7 = checkValidZipFiles(r4);	 Catch:{ all -> 0x0050 }
        if (r7 == 0) goto L_0x011b;
    L_0x0117:
        installSecondaryDexes(r5, r2, r4);	 Catch:{ all -> 0x0050 }
        goto L_0x00f2;
    L_0x011b:
        r7 = new java.lang.RuntimeException;	 Catch:{ all -> 0x0050 }
        r9 = "Zip files were not valid.";
        r7.<init>(r9);	 Catch:{ all -> 0x0050 }
        throw r7;	 Catch:{ all -> 0x0050 }
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.multidex.MultiDex.install(android.content.Context):void");
    }

    private static ApplicationInfo getApplicationInfo(Context context) throws NameNotFoundException {
        try {
            PackageManager pm = context.getPackageManager();
            String packageName = context.getPackageName();
            if (pm == null || packageName == null) {
                return null;
            }
            return pm.getApplicationInfo(packageName, 128);
        } catch (RuntimeException e) {
            Log.w("MultiDex", "Failure while trying to obtain ApplicationInfo from Context. Must be running in test mode. Skip patching.", e);
            return null;
        }
    }

    private static boolean isVMMultidexCapable(String versionString) {
        boolean isMultidexCapable = false;
        if (versionString != null) {
            Matcher matcher = Pattern.compile("(\\d+)\\.(\\d+)(\\.\\d+)?").matcher(versionString);
            if (matcher.matches()) {
                try {
                    int major = Integer.parseInt(matcher.group(1));
                    isMultidexCapable = major > 2 || (major == 2 && Integer.parseInt(matcher.group(2)) > 0);
                } catch (NumberFormatException e) {
                }
            }
        }
        Log.i("MultiDex", "VM with version " + versionString + (isMultidexCapable ? " has multidex support" : " does not have multidex support"));
        return isMultidexCapable;
    }

    private static void installSecondaryDexes(ClassLoader loader, File dexDir, List<File> files) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IOException {
        if (!files.isEmpty()) {
            if (VERSION.SDK_INT >= 19) {
                V19.access$000(loader, files, dexDir);
            } else if (VERSION.SDK_INT >= 14) {
                V14.access$100(loader, files, dexDir);
            } else {
                V4.access$200(loader, files);
            }
        }
    }

    private static boolean checkValidZipFiles(List<File> files) {
        for (File file : files) {
            if (!MultiDexExtractor.verifyZipFile(file)) {
                return false;
            }
        }
        return true;
    }

    private static Field findField(Object instance, String name) throws NoSuchFieldException {
        Class<?> clazz = instance.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(name);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                return field;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field " + name + " not found in " + instance.getClass());
    }

    private static Method findMethod(Object instance, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Class<?> clazz = instance.getClass();
        while (clazz != null) {
            try {
                Method method = clazz.getDeclaredMethod(name, parameterTypes);
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                return method;
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchMethodException("Method " + name + " with parameters " + Arrays.asList(parameterTypes) + " not found in " + instance.getClass());
    }

    private static void clearOldDexDir(Context context) throws Exception {
        File dexDir = new File(context.getFilesDir(), "secondary-dexes");
        if (dexDir.isDirectory()) {
            Log.i("MultiDex", "Clearing old secondary dex dir (" + dexDir.getPath() + ").");
            File[] files = dexDir.listFiles();
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
            if (dexDir.delete()) {
                Log.i("MultiDex", "Deleted old secondary dex dir " + dexDir.getPath());
            } else {
                Log.w("MultiDex", "Failed to delete secondary dex dir " + dexDir.getPath());
            }
        }
    }
}
