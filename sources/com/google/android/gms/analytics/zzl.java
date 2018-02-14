package com.google.android.gms.analytics;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Process;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzalk;
import com.google.android.gms.internal.zzalp;
import com.google.android.gms.internal.zzaos;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class zzl {
    private static volatile zzl zzaed;
    private final Context mContext;
    private final List<Object> zzaee = new CopyOnWriteArrayList();
    private final zzg zzaef = new zzg();
    private final zza zzaeg = new zza(this);
    private volatile zzalk zzaeh;
    private UncaughtExceptionHandler zzaei;

    class zza extends ThreadPoolExecutor {
        final /* synthetic */ zzl zzaek;

        public zza(zzl zzl) {
            this.zzaek = zzl;
            super(1, 1, 1, TimeUnit.MINUTES, new LinkedBlockingQueue());
            setThreadFactory(new zzb());
            allowCoreThreadTimeOut(true);
        }

        protected final <T> RunnableFuture<T> newTaskFor(Runnable runnable, T t) {
            return new zzn(this, runnable, t);
        }
    }

    static class zzb implements ThreadFactory {
        private static final AtomicInteger zzaem = new AtomicInteger();

        private zzb() {
        }

        public final Thread newThread(Runnable runnable) {
            return new zzc(runnable, "measurement-" + zzaem.incrementAndGet());
        }
    }

    static class zzc extends Thread {
        zzc(Runnable runnable, String str) {
            super(runnable, str);
        }

        public final void run() {
            Process.setThreadPriority(10);
            super.run();
        }
    }

    private zzl(Context context) {
        Context applicationContext = context.getApplicationContext();
        zzbo.zzu(applicationContext);
        this.mContext = applicationContext;
    }

    public static zzl zzae(Context context) {
        zzbo.zzu(context);
        if (zzaed == null) {
            synchronized (zzl.class) {
                if (zzaed == null) {
                    zzaed = new zzl(context);
                }
            }
        }
        return zzaed;
    }

    public static void zzjC() {
        if (!(Thread.currentThread() instanceof zzc)) {
            throw new IllegalStateException("Call expected from worker thread");
        }
    }

    public final Context getContext() {
        return this.mContext;
    }

    public final void zza(UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.zzaei = uncaughtExceptionHandler;
    }

    public final <V> Future<V> zzd(Callable<V> callable) {
        zzbo.zzu(callable);
        if (!(Thread.currentThread() instanceof zzc)) {
            return this.zzaeg.submit(callable);
        }
        Future futureTask = new FutureTask(callable);
        futureTask.run();
        return futureTask;
    }

    final void zze(zzi zzi) {
        if (zzi.zzjx()) {
            throw new IllegalStateException("Measurement prototype can't be submitted");
        } else if (zzi.zzju()) {
            throw new IllegalStateException("Measurement can only be submitted once");
        } else {
            zzi zzjp = zzi.zzjp();
            zzjp.zzjv();
            this.zzaeg.execute(new zzm(this, zzjp));
        }
    }

    public final void zzf(Runnable runnable) {
        zzbo.zzu(runnable);
        this.zzaeg.submit(runnable);
    }

    public final zzalk zzjA() {
        if (this.zzaeh == null) {
            synchronized (this) {
                if (this.zzaeh == null) {
                    zzalk zzalk = new zzalk();
                    PackageManager packageManager = this.mContext.getPackageManager();
                    String packageName = this.mContext.getPackageName();
                    zzalk.setAppId(packageName);
                    zzalk.setAppInstallerId(packageManager.getInstallerPackageName(packageName));
                    String str = null;
                    try {
                        PackageInfo packageInfo = packageManager.getPackageInfo(this.mContext.getPackageName(), 0);
                        if (packageInfo != null) {
                            CharSequence applicationLabel = packageManager.getApplicationLabel(packageInfo.applicationInfo);
                            if (!TextUtils.isEmpty(applicationLabel)) {
                                packageName = applicationLabel.toString();
                            }
                            str = packageInfo.versionName;
                        }
                    } catch (NameNotFoundException e) {
                        String str2 = "GAv4";
                        String str3 = "Error retrieving package info: appName set to ";
                        String valueOf = String.valueOf(packageName);
                        Log.e(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
                    }
                    zzalk.setAppName(packageName);
                    zzalk.setAppVersion(str);
                    this.zzaeh = zzalk;
                }
            }
        }
        return this.zzaeh;
    }

    public final zzalp zzjB() {
        DisplayMetrics displayMetrics = this.mContext.getResources().getDisplayMetrics();
        zzalp zzalp = new zzalp();
        zzalp.setLanguage(zzaos.zza(Locale.getDefault()));
        zzalp.zzNY = displayMetrics.widthPixels;
        zzalp.zzNZ = displayMetrics.heightPixels;
        return zzalp;
    }

    static /* synthetic */ void zza$1aa35484(zzi zzi) {
        zzbo.zzcG("deliver should be called from worker thread");
        zzbo.zzb(zzi.zzju(), (Object) "Measurement must be submitted");
        List<zzo> zzjr = zzi.zzjr();
        if (!zzjr.isEmpty()) {
            Set hashSet = new HashSet();
            for (zzo zzo : zzjr) {
                Uri zzjl = zzo.zzjl();
                if (!hashSet.contains(zzjl)) {
                    hashSet.add(zzjl);
                    zzo.zzb(zzi);
                }
            }
        }
    }
}
