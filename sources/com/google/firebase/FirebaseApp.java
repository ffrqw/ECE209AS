package com.google.firebase;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import com.google.android.gms.common.internal.zzbe;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zzr;
import com.google.android.gms.internal.aab;
import com.google.android.gms.internal.aac;
import com.google.android.gms.internal.zzbaw;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class FirebaseApp {
    private static final List<String> zzbUV = Arrays.asList(new String[]{"com.google.firebase.auth.FirebaseAuth", "com.google.firebase.iid.FirebaseInstanceId"});
    private static final List<String> zzbUW = Collections.singletonList("com.google.firebase.crash.FirebaseCrash");
    private static final List<String> zzbUX = Arrays.asList(new String[]{"com.google.android.gms.measurement.AppMeasurement"});
    private static final List<String> zzbUY = Arrays.asList(new String[0]);
    private static final Set<String> zzbUZ = Collections.emptySet();
    static final Map<String, FirebaseApp> zzbgQ = new ArrayMap();
    private static final Object zzuF = new Object();
    private final Context mApplicationContext;
    private final String mName;
    private final FirebaseOptions zzbVa;
    private final AtomicBoolean zzbVb = new AtomicBoolean(false);
    private final AtomicBoolean zzbVc = new AtomicBoolean();
    private final List<Object> zzbVd = new CopyOnWriteArrayList();
    private final List<Object> zzbVe = new CopyOnWriteArrayList();
    private final List<Object> zzbVf = new CopyOnWriteArrayList();
    private zzb zzbVh;

    public interface zzb {
    }

    @TargetApi(24)
    static class zzd extends BroadcastReceiver {
        private static AtomicReference<zzd> zzbVi = new AtomicReference();
        private final Context mApplicationContext;

        private zzd(Context context) {
            this.mApplicationContext = context;
        }

        public final void onReceive(Context context, Intent intent) {
            synchronized (FirebaseApp.zzuF) {
                for (FirebaseApp zza : FirebaseApp.zzbgQ.values()) {
                    FirebaseApp.zza(zza);
                }
            }
            this.mApplicationContext.unregisterReceiver(this);
        }

        static /* synthetic */ void zzbC(Context context) {
            if (zzbVi.get() == null) {
                BroadcastReceiver zzd = new zzd(context);
                if (zzbVi.compareAndSet(null, zzd)) {
                    context.registerReceiver(zzd, new IntentFilter("android.intent.action.USER_UNLOCKED"));
                }
            }
        }
    }

    private FirebaseApp(Context context, String str, FirebaseOptions firebaseOptions) {
        this.mApplicationContext = (Context) zzbo.zzu(context);
        this.mName = zzbo.zzcF(str);
        this.zzbVa = (FirebaseOptions) zzbo.zzu(firebaseOptions);
        this.zzbVh = new aab();
    }

    public static FirebaseApp getInstance() {
        FirebaseApp firebaseApp;
        synchronized (zzuF) {
            firebaseApp = (FirebaseApp) zzbgQ.get("[DEFAULT]");
            if (firebaseApp == null) {
                String valueOf = String.valueOf(zzr.zzsf());
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 116).append("Default FirebaseApp is not initialized in this process ").append(valueOf).append(". Make sure to call FirebaseApp.initializeApp(Context) first.").toString());
            }
        }
        return firebaseApp;
    }

    private String getName() {
        zzEp();
        return this.mName;
    }

    public static FirebaseApp initializeApp(Context context) {
        FirebaseApp instance;
        synchronized (zzuF) {
            if (zzbgQ.containsKey("[DEFAULT]")) {
                instance = getInstance();
            } else {
                FirebaseOptions fromResource = FirebaseOptions.fromResource(context);
                if (fromResource == null) {
                    instance = null;
                } else {
                    instance = initializeApp(context, fromResource, "[DEFAULT]");
                }
            }
        }
        return instance;
    }

    private static FirebaseApp initializeApp(Context context, FirebaseOptions firebaseOptions, String str) {
        FirebaseApp firebaseApp;
        aac.zzbL(context);
        if (context.getApplicationContext() instanceof Application) {
            zzbaw.zza((Application) context.getApplicationContext());
            zzbaw.zzpv().zza(new zza());
        }
        String trim = str.trim();
        if (context.getApplicationContext() != null) {
            Object applicationContext = context.getApplicationContext();
        }
        synchronized (zzuF) {
            zzbo.zza(!zzbgQ.containsKey(trim), new StringBuilder(String.valueOf(trim).length() + 33).append("FirebaseApp name ").append(trim).append(" already exists!").toString());
            zzbo.zzb(applicationContext, (Object) "Application context cannot be null.");
            firebaseApp = new FirebaseApp(applicationContext, trim, firebaseOptions);
            zzbgQ.put(trim, firebaseApp);
        }
        aac.zze$4c05e04e();
        firebaseApp.zza(FirebaseApp.class, firebaseApp, zzbUV);
        if (firebaseApp.zzEq()) {
            firebaseApp.zza(FirebaseApp.class, firebaseApp, zzbUW);
            firebaseApp.zza(Context.class, firebaseApp.getApplicationContext(), zzbUX);
        }
        return firebaseApp;
    }

    private final void zzEp() {
        zzbo.zza(!this.zzbVc.get(), "FirebaseApp was deleted");
    }

    private boolean zzEq() {
        return "[DEFAULT]".equals(getName());
    }

    private final <T> void zza(Class<T> cls, T t, Iterable<String> iterable) {
        String valueOf;
        boolean isDeviceProtectedStorage = ContextCompat.isDeviceProtectedStorage(this.mApplicationContext);
        if (isDeviceProtectedStorage) {
            zzd.zzbC(this.mApplicationContext);
        }
        for (String valueOf2 : iterable) {
            if (isDeviceProtectedStorage) {
                try {
                    if (!zzbUY.contains(valueOf2)) {
                    }
                } catch (ClassNotFoundException e) {
                    if (zzbUZ.contains(valueOf2)) {
                        throw new IllegalStateException(String.valueOf(valueOf2).concat(" is missing, but is required. Check if it has been removed by Proguard."));
                    }
                    Log.d("FirebaseApp", String.valueOf(valueOf2).concat(" is not linked. Skipping initialization."));
                } catch (NoSuchMethodException e2) {
                    throw new IllegalStateException(String.valueOf(valueOf2).concat("#getInstance has been removed by Proguard. Add keep rule to prevent it."));
                } catch (Throwable e3) {
                    Log.wtf("FirebaseApp", "Firebase API initialization failure.", e3);
                } catch (Throwable e4) {
                    String str = "FirebaseApp";
                    String str2 = "Failed to initialize ";
                    valueOf2 = String.valueOf(valueOf2);
                    Log.wtf(str, valueOf2.length() != 0 ? str2.concat(valueOf2) : new String(str2), e4);
                }
            }
            Method method = Class.forName(valueOf2).getMethod("getInstance", new Class[]{cls});
            int modifiers = method.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
                method.invoke(null, new Object[]{t});
            }
        }
    }

    public static void zzac(boolean z) {
        synchronized (zzuF) {
            ArrayList arrayList = new ArrayList(zzbgQ.values());
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                Object obj = arrayList.get(i);
                i++;
                FirebaseApp firebaseApp = (FirebaseApp) obj;
                if (firebaseApp.zzbVb.get()) {
                    firebaseApp.zzav(z);
                }
            }
        }
    }

    private final void zzav(boolean z) {
        Log.d("FirebaseApp", "Notifying background state change listeners.");
        Iterator it = this.zzbVe.iterator();
        while (it.hasNext()) {
            it.next();
        }
    }

    public boolean equals(Object obj) {
        return !(obj instanceof FirebaseApp) ? false : this.mName.equals(((FirebaseApp) obj).getName());
    }

    public final Context getApplicationContext() {
        zzEp();
        return this.mApplicationContext;
    }

    public final FirebaseOptions getOptions() {
        zzEp();
        return this.zzbVa;
    }

    public int hashCode() {
        return this.mName.hashCode();
    }

    public String toString() {
        return zzbe.zzt(this).zzg("name", this.mName).zzg("options", this.zzbVa).toString();
    }

    static /* synthetic */ void zza(FirebaseApp firebaseApp) {
        firebaseApp.zza(FirebaseApp.class, firebaseApp, zzbUV);
        if (firebaseApp.zzEq()) {
            firebaseApp.zza(FirebaseApp.class, firebaseApp, zzbUW);
            firebaseApp.zza(Context.class, firebaseApp.mApplicationContext, zzbUX);
        }
    }
}
