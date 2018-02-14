package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement.ConditionalUserProperty;
import com.google.android.gms.measurement.AppMeasurement.zzb;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

public final class zzchl extends zzchj {
    protected zzchy zzbto;
    private final Set<Object> zzbtq = new CopyOnWriteArraySet();
    private boolean zzbtr;
    private final AtomicReference<String> zzbts = new AtomicReference();

    protected zzchl(zzcgl zzcgl) {
        super(zzcgl);
    }

    public static int getMaxUserProperties(String str) {
        zzbo.zzcF(str);
        return zzcem.zzxu();
    }

    private final void zza(ConditionalUserProperty conditionalUserProperty) {
        long currentTimeMillis = super.zzkq().currentTimeMillis();
        zzbo.zzu(conditionalUserProperty);
        zzbo.zzcF(conditionalUserProperty.mName);
        zzbo.zzcF(conditionalUserProperty.mOrigin);
        zzbo.zzu(conditionalUserProperty.mValue);
        conditionalUserProperty.mCreationTimestamp = currentTimeMillis;
        String str = conditionalUserProperty.mName;
        Object obj = conditionalUserProperty.mValue;
        if (super.zzwB().zzes(str) != 0) {
            super.zzwF().zzyx().zzj("Invalid conditional user property name", super.zzwA().zzdY(str));
        } else if (super.zzwB().zzl(str, obj) != 0) {
            super.zzwF().zzyx().zze("Invalid conditional user property value", super.zzwA().zzdY(str), obj);
        } else {
            super.zzwB();
            Object zzm = zzcjl.zzm(str, obj);
            if (zzm == null) {
                super.zzwF().zzyx().zze("Unable to normalize conditional user property value", super.zzwA().zzdY(str), obj);
                return;
            }
            conditionalUserProperty.mValue = zzm;
            long j = conditionalUserProperty.mTriggerTimeout;
            if (TextUtils.isEmpty(conditionalUserProperty.mTriggerEventName) || (j <= zzcem.zzxw() && j >= 1)) {
                j = conditionalUserProperty.mTimeToLive;
                if (j > zzcem.zzxx() || j < 1) {
                    super.zzwF().zzyx().zze("Invalid conditional user property time to live", super.zzwA().zzdY(str), Long.valueOf(j));
                    return;
                } else {
                    super.zzwE().zzj(new zzchn(this, conditionalUserProperty));
                    return;
                }
            }
            super.zzwF().zzyx().zze("Invalid conditional user property timeout", super.zzwA().zzdY(str), Long.valueOf(j));
        }
    }

    private final void zza(String str, String str2, long j, Object obj) {
        super.zzwE().zzj(new zzchu(this, str, str2, obj, j));
    }

    private final void zza(String str, String str2, String str3, Bundle bundle) {
        long currentTimeMillis = super.zzkq().currentTimeMillis();
        zzbo.zzcF(str2);
        ConditionalUserProperty conditionalUserProperty = new ConditionalUserProperty();
        conditionalUserProperty.mAppId = str;
        conditionalUserProperty.mName = str2;
        conditionalUserProperty.mCreationTimestamp = currentTimeMillis;
        if (str3 != null) {
            conditionalUserProperty.mExpiredEventName = str3;
            conditionalUserProperty.mExpiredEventParams = bundle;
        }
        super.zzwE().zzj(new zzcho(this, conditionalUserProperty));
    }

    private final Map<String, Object> zzb(String str, String str2, String str3, boolean z) {
        if (super.zzwE().zzyM()) {
            super.zzwF().zzyx().log("Cannot get user properties from analytics worker thread");
            return Collections.emptyMap();
        }
        super.zzwE();
        if (zzcgg.zzS()) {
            super.zzwF().zzyx().log("Cannot get user properties from main thread");
            return Collections.emptyMap();
        }
        AtomicReference atomicReference = new AtomicReference();
        synchronized (atomicReference) {
            this.zzboe.zzwE().zzj(new zzchq(this, atomicReference, str, str2, str3, z));
            try {
                atomicReference.wait(5000);
            } catch (InterruptedException e) {
                super.zzwF().zzyz().zzj("Interrupted waiting for get user properties", e);
            }
        }
        List<zzcji> list = (List) atomicReference.get();
        if (list == null) {
            super.zzwF().zzyz().log("Timed out waiting for get user properties");
            return Collections.emptyMap();
        }
        Map<String, Object> arrayMap = new ArrayMap(list.size());
        for (zzcji zzcji : list) {
            arrayMap.put(zzcji.name, zzcji.getValue());
        }
        return arrayMap;
    }

    private final List<ConditionalUserProperty> zzl(String str, String str2, String str3) {
        if (super.zzwE().zzyM()) {
            super.zzwF().zzyx().log("Cannot get conditional user properties from analytics worker thread");
            return Collections.emptyList();
        }
        super.zzwE();
        if (zzcgg.zzS()) {
            super.zzwF().zzyx().log("Cannot get conditional user properties from main thread");
            return Collections.emptyList();
        }
        AtomicReference atomicReference = new AtomicReference();
        synchronized (atomicReference) {
            this.zzboe.zzwE().zzj(new zzchp(this, atomicReference, str, str2, str3));
            try {
                atomicReference.wait(5000);
            } catch (InterruptedException e) {
                super.zzwF().zzyz().zze("Interrupted waiting for get conditional user properties", str, e);
            }
        }
        List<zzcek> list = (List) atomicReference.get();
        if (list == null) {
            super.zzwF().zzyz().zzj("Timed out waiting for get conditional user properties", str);
            return Collections.emptyList();
        }
        List<ConditionalUserProperty> arrayList = new ArrayList(list.size());
        for (zzcek zzcek : list) {
            ConditionalUserProperty conditionalUserProperty = new ConditionalUserProperty();
            conditionalUserProperty.mAppId = str;
            conditionalUserProperty.mOrigin = str2;
            conditionalUserProperty.mCreationTimestamp = zzcek.zzbpe;
            conditionalUserProperty.mName = zzcek.zzbpd.name;
            conditionalUserProperty.mValue = zzcek.zzbpd.getValue();
            conditionalUserProperty.mActive = zzcek.zzbpf;
            conditionalUserProperty.mTriggerEventName = zzcek.zzbpg;
            if (zzcek.zzbph != null) {
                conditionalUserProperty.mTimedOutEventName = zzcek.zzbph.name;
                if (zzcek.zzbph.zzbpM != null) {
                    conditionalUserProperty.mTimedOutEventParams = zzcek.zzbph.zzbpM.zzyt();
                }
            }
            conditionalUserProperty.mTriggerTimeout = zzcek.zzbpi;
            if (zzcek.zzbpj != null) {
                conditionalUserProperty.mTriggeredEventName = zzcek.zzbpj.name;
                if (zzcek.zzbpj.zzbpM != null) {
                    conditionalUserProperty.mTriggeredEventParams = zzcek.zzbpj.zzbpM.zzyt();
                }
            }
            conditionalUserProperty.mTriggeredTimestamp = zzcek.zzbpd.zzbuy;
            conditionalUserProperty.mTimeToLive = zzcek.zzbpk;
            if (zzcek.zzbpl != null) {
                conditionalUserProperty.mExpiredEventName = zzcek.zzbpl.name;
                if (zzcek.zzbpl.zzbpM != null) {
                    conditionalUserProperty.mExpiredEventParams = zzcek.zzbpl.zzbpM.zzyt();
                }
            }
            arrayList.add(conditionalUserProperty);
        }
        return arrayList;
    }

    public final void clearConditionalUserProperty(String str, String str2, Bundle bundle) {
        super.zzwp();
        zza(null, str, str2, bundle);
    }

    public final void clearConditionalUserPropertyAs(String str, String str2, String str3, Bundle bundle) {
        zzbo.zzcF(str);
        super.zzwo();
        zza(str, str2, str3, bundle);
    }

    public final List<ConditionalUserProperty> getConditionalUserProperties(String str, String str2) {
        super.zzwp();
        return zzl(null, str, str2);
    }

    public final List<ConditionalUserProperty> getConditionalUserPropertiesAs(String str, String str2, String str3) {
        zzbo.zzcF(str);
        super.zzwo();
        return zzl(str, str2, str3);
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public final Map<String, Object> getUserProperties(String str, String str2, boolean z) {
        super.zzwp();
        return zzb(null, str, str2, z);
    }

    public final Map<String, Object> getUserPropertiesAs(String str, String str2, String str3, boolean z) {
        zzbo.zzcF(str);
        super.zzwo();
        return zzb(str, str2, str3, z);
    }

    public final void setConditionalUserProperty(ConditionalUserProperty conditionalUserProperty) {
        zzbo.zzu(conditionalUserProperty);
        super.zzwp();
        ConditionalUserProperty conditionalUserProperty2 = new ConditionalUserProperty(conditionalUserProperty);
        if (!TextUtils.isEmpty(conditionalUserProperty2.mAppId)) {
            super.zzwF().zzyz().log("Package name should be null when calling setConditionalUserProperty");
        }
        conditionalUserProperty2.mAppId = null;
        zza(conditionalUserProperty2);
    }

    public final void setConditionalUserPropertyAs(ConditionalUserProperty conditionalUserProperty) {
        zzbo.zzu(conditionalUserProperty);
        zzbo.zzcF(conditionalUserProperty.mAppId);
        super.zzwo();
        zza(new ConditionalUserProperty(conditionalUserProperty));
    }

    public final void zzb(String str, String str2, Object obj) {
        int i = 0;
        zzbo.zzcF(str);
        long currentTimeMillis = super.zzkq().currentTimeMillis();
        int zzes = super.zzwB().zzes(str2);
        String zza;
        if (zzes != 0) {
            super.zzwB();
            zza = zzcjl.zza(str2, zzcem.zzxi(), true);
            if (str2 != null) {
                i = str2.length();
            }
            this.zzboe.zzwB().zza(zzes, "_ev", zza, i);
        } else if (obj != null) {
            zzes = super.zzwB().zzl(str2, obj);
            if (zzes != 0) {
                super.zzwB();
                zza = zzcjl.zza(str2, zzcem.zzxi(), true);
                if ((obj instanceof String) || (obj instanceof CharSequence)) {
                    i = String.valueOf(obj).length();
                }
                this.zzboe.zzwB().zza(zzes, "_ev", zza, i);
                return;
            }
            super.zzwB();
            Object zzm = zzcjl.zzm(str2, obj);
            if (zzm != null) {
                zza(str, str2, currentTimeMillis, zzm);
            }
        } else {
            zza(str, str2, currentTimeMillis, null);
        }
    }

    public final void zzd(String str, String str2, Bundle bundle) {
        Bundle bundle2;
        super.zzwp();
        long currentTimeMillis = super.zzkq().currentTimeMillis();
        if (bundle == null) {
            bundle2 = new Bundle();
        } else {
            bundle2 = new Bundle(bundle);
            for (String str3 : bundle2.keySet()) {
                Object obj = bundle2.get(str3);
                if (obj instanceof Bundle) {
                    bundle2.putBundle(str3, new Bundle((Bundle) obj));
                } else if (obj instanceof Parcelable[]) {
                    Parcelable[] parcelableArr = (Parcelable[]) obj;
                    for (r2 = 0; r2 < parcelableArr.length; r2++) {
                        if (parcelableArr[r2] instanceof Bundle) {
                            parcelableArr[r2] = new Bundle((Bundle) parcelableArr[r2]);
                        }
                    }
                } else if (obj instanceof ArrayList) {
                    ArrayList arrayList = (ArrayList) obj;
                    for (r2 = 0; r2 < arrayList.size(); r2++) {
                        Object obj2 = arrayList.get(r2);
                        if (obj2 instanceof Bundle) {
                            arrayList.set(r2, new Bundle((Bundle) obj2));
                        }
                    }
                }
            }
        }
        super.zzwE().zzj(new zzcht(this, str, str2, currentTimeMillis, bundle2, true, true, false, null));
    }

    final void zzee(String str) {
        this.zzbts.set(str);
    }

    public final /* bridge */ /* synthetic */ void zzjC() {
        super.zzjC();
    }

    protected final void zzjD() {
    }

    public final /* bridge */ /* synthetic */ zze zzkq() {
        return super.zzkq();
    }

    public final /* bridge */ /* synthetic */ zzcfj zzwA() {
        return super.zzwA();
    }

    public final /* bridge */ /* synthetic */ zzcjl zzwB() {
        return super.zzwB();
    }

    public final /* bridge */ /* synthetic */ zzcgf zzwC() {
        return super.zzwC();
    }

    public final /* bridge */ /* synthetic */ zzcja zzwD() {
        return super.zzwD();
    }

    public final /* bridge */ /* synthetic */ zzcgg zzwE() {
        return super.zzwE();
    }

    public final /* bridge */ /* synthetic */ zzcfl zzwF() {
        return super.zzwF();
    }

    public final /* bridge */ /* synthetic */ zzcfw zzwG() {
        return super.zzwG();
    }

    public final /* bridge */ /* synthetic */ zzcem zzwH() {
        return super.zzwH();
    }

    public final /* bridge */ /* synthetic */ void zzwo() {
        super.zzwo();
    }

    public final /* bridge */ /* synthetic */ void zzwp() {
        super.zzwp();
    }

    public final /* bridge */ /* synthetic */ void zzwq() {
        super.zzwq();
    }

    public final /* bridge */ /* synthetic */ zzcec zzwr() {
        return super.zzwr();
    }

    public final /* bridge */ /* synthetic */ zzcej zzws() {
        return super.zzws();
    }

    public final /* bridge */ /* synthetic */ zzchl zzwt() {
        return super.zzwt();
    }

    public final /* bridge */ /* synthetic */ zzcfg zzwu() {
        return super.zzwu();
    }

    public final /* bridge */ /* synthetic */ zzcet zzwv() {
        return super.zzwv();
    }

    public final /* bridge */ /* synthetic */ zzcid zzww() {
        return super.zzww();
    }

    public final /* bridge */ /* synthetic */ zzchz zzwx() {
        return super.zzwx();
    }

    public final /* bridge */ /* synthetic */ zzcfh zzwy() {
        return super.zzwy();
    }

    public final /* bridge */ /* synthetic */ zzcen zzwz() {
        return super.zzwz();
    }

    public final String zzyH() {
        super.zzwp();
        return (String) this.zzbts.get();
    }

    static /* synthetic */ void zza(zzchl zzchl, String str, String str2, long j, Bundle bundle, boolean z, boolean z2, boolean z3, String str3) {
        zzbo.zzcF(str);
        zzbo.zzcF(str2);
        zzbo.zzu(bundle);
        super.zzjC();
        zzchl.zzkD();
        if (zzchl.zzboe.isEnabled()) {
            if (!zzchl.zzbtr) {
                zzchl.zzbtr = true;
                try {
                    try {
                        Class.forName("com.google.android.gms.tagmanager.TagManagerService").getDeclaredMethod("initialize", new Class[]{Context.class}).invoke(null, new Object[]{super.getContext()});
                    } catch (Exception e) {
                        super.zzwF().zzyz().zzj("Failed to invoke Tag Manager's initialize() method", e);
                    }
                } catch (ClassNotFoundException e2) {
                    super.zzwF().zzyB().log("Tag Manager is not found and thus will not be used");
                }
            }
            boolean equals = "am".equals(str);
            zzcjl.zzex(str2);
            if (zzchl.zzboe.zzyP()) {
                int zzeq = super.zzwB().zzeq(str2);
                if (zzeq != 0) {
                    super.zzwB();
                    zzchl.zzboe.zzwB().zza$2c2ba1f5(zzeq, "_ev", zzcjl.zza(str2, zzcem.zzxh(), true), str2 != null ? str2.length() : 0);
                    return;
                }
                int i;
                Bundle zza;
                List singletonList = Collections.singletonList("_o");
                Bundle zza2 = super.zzwB().zza(str2, bundle, singletonList, z3, true);
                List arrayList = new ArrayList();
                arrayList.add(zza2);
                long nextLong = super.zzwB().zzzt().nextLong();
                int i2 = 0;
                String[] strArr = (String[]) zza2.keySet().toArray(new String[bundle.size()]);
                Arrays.sort(strArr);
                int length = strArr.length;
                int i3 = 0;
                while (i3 < length) {
                    int length2;
                    String str4 = strArr[i3];
                    Object obj = zza2.get(str4);
                    super.zzwB();
                    Bundle[] zzC = zzcjl.zzC(obj);
                    if (zzC != null) {
                        zza2.putInt(str4, zzC.length);
                        for (i = 0; i < zzC.length; i++) {
                            zza = super.zzwB().zza("_ep", zzC[i], singletonList, z3, false);
                            zza.putString("_en", str2);
                            zza.putLong("_eid", nextLong);
                            zza.putString("_gn", str4);
                            zza.putInt("_ll", zzC.length);
                            zza.putInt("_i", i);
                            arrayList.add(zza);
                        }
                        length2 = zzC.length + i2;
                    } else {
                        length2 = i2;
                    }
                    i3++;
                    i2 = length2;
                }
                if (i2 != 0) {
                    zza2.putLong("_eid", nextLong);
                    zza2.putInt("_epc", i2);
                }
                zzcem.zzxE();
                zzb zzzh = super.zzwx().zzzh();
                if (!(zzzh == null || zza2.containsKey("_sc"))) {
                    zzzh.zzbtS = true;
                }
                i = 0;
                while (i < arrayList.size()) {
                    zza = (Bundle) arrayList.get(i);
                    String str5 = (i != 0 ? 1 : null) != null ? "_ep" : str2;
                    zza.putString("_o", str);
                    if (!zza.containsKey("_sc")) {
                        zzchz.zza(zzzh, zza);
                    }
                    Bundle zzB = z2 ? super.zzwB().zzB(zza) : zza;
                    super.zzwF().zzyC().zze("Logging event (FE)", super.zzwA().zzdW(str2), super.zzwA().zzA(zzB));
                    super.zzww().zzc(new zzcez(str5, new zzcew(zzB), str, j), str3);
                    if (!equals) {
                        Iterator it = zzchl.zzbtq.iterator();
                        while (it.hasNext()) {
                            it.next();
                            Bundle bundle2 = new Bundle(zzB);
                        }
                    }
                    i++;
                }
                zzcem.zzxE();
                if (super.zzwx().zzzh() != null && "_ae".equals(str2)) {
                    super.zzwD().zzap(true);
                    return;
                }
                return;
            }
            return;
        }
        super.zzwF().zzyC().log("Event not sent since app measurement is disabled");
    }

    static /* synthetic */ void zza(zzchl zzchl, String str, String str2, Object obj, long j) {
        zzbo.zzcF(str);
        zzbo.zzcF(str2);
        super.zzjC();
        super.zzwp();
        zzchl.zzkD();
        if (!zzchl.zzboe.isEnabled()) {
            super.zzwF().zzyC().log("User property not set since app measurement is disabled");
        } else if (zzchl.zzboe.zzyP()) {
            super.zzwF().zzyC().zze("Setting user property (FE)", super.zzwA().zzdW(str2), obj);
            super.zzww().zzb(new zzcji(str2, j, obj, str));
        }
    }

    static /* synthetic */ void zza(zzchl zzchl, ConditionalUserProperty conditionalUserProperty) {
        super.zzjC();
        zzchl.zzkD();
        zzbo.zzu(conditionalUserProperty);
        zzbo.zzcF(conditionalUserProperty.mName);
        zzbo.zzcF(conditionalUserProperty.mOrigin);
        zzbo.zzu(conditionalUserProperty.mValue);
        if (zzchl.zzboe.isEnabled()) {
            zzcji zzcji = new zzcji(conditionalUserProperty.mName, conditionalUserProperty.mTriggeredTimestamp, conditionalUserProperty.mValue, conditionalUserProperty.mOrigin);
            try {
                zzcez zza$23a98d66 = super.zzwB().zza$23a98d66(conditionalUserProperty.mTriggeredEventName, conditionalUserProperty.mTriggeredEventParams, conditionalUserProperty.mOrigin, 0);
                super.zzww().zzf(new zzcek(conditionalUserProperty.mAppId, conditionalUserProperty.mOrigin, zzcji, conditionalUserProperty.mCreationTimestamp, false, conditionalUserProperty.mTriggerEventName, super.zzwB().zza$23a98d66(conditionalUserProperty.mTimedOutEventName, conditionalUserProperty.mTimedOutEventParams, conditionalUserProperty.mOrigin, 0), conditionalUserProperty.mTriggerTimeout, zza$23a98d66, conditionalUserProperty.mTimeToLive, super.zzwB().zza$23a98d66(conditionalUserProperty.mExpiredEventName, conditionalUserProperty.mExpiredEventParams, conditionalUserProperty.mOrigin, 0)));
                return;
            } catch (IllegalArgumentException e) {
                return;
            }
        }
        super.zzwF().zzyC().log("Conditional property not sent since Firebase Analytics is disabled");
    }

    static /* synthetic */ void zzb(zzchl zzchl, ConditionalUserProperty conditionalUserProperty) {
        super.zzjC();
        zzchl.zzkD();
        zzbo.zzu(conditionalUserProperty);
        zzbo.zzcF(conditionalUserProperty.mName);
        if (zzchl.zzboe.isEnabled()) {
            zzcji zzcji = new zzcji(conditionalUserProperty.mName, 0, null, null);
            try {
                super.zzww().zzf(new zzcek(conditionalUserProperty.mAppId, conditionalUserProperty.mOrigin, zzcji, conditionalUserProperty.mCreationTimestamp, conditionalUserProperty.mActive, conditionalUserProperty.mTriggerEventName, null, conditionalUserProperty.mTriggerTimeout, null, conditionalUserProperty.mTimeToLive, super.zzwB().zza$23a98d66(conditionalUserProperty.mExpiredEventName, conditionalUserProperty.mExpiredEventParams, conditionalUserProperty.mOrigin, conditionalUserProperty.mCreationTimestamp)));
                return;
            } catch (IllegalArgumentException e) {
                return;
            }
        }
        super.zzwF().zzyC().log("Conditional property not cleared since Firebase Analytics is disabled");
    }
}
