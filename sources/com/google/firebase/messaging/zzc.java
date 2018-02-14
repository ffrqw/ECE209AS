package com.google.firebase.messaging;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.internal.ado;
import com.google.android.gms.internal.adp;
import com.google.android.gms.internal.aem;
import com.google.android.gms.internal.aen;
import com.google.android.gms.measurement.AppMeasurement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class zzc {
    private static String zzS(Object obj) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return (String) Class.forName("com.google.android.gms.measurement.AppMeasurement$ConditionalUserProperty").getField("mName").get(obj);
    }

    private static String zzT(Object obj) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return (String) Class.forName("com.google.android.gms.measurement.AppMeasurement$ConditionalUserProperty").getField("mValue").get(obj);
    }

    private static Bundle zzX(String str, String str2) {
        Bundle bundle = new Bundle();
        bundle.putString(str, str2);
        return bundle;
    }

    private static Bundle zza(aen aen) {
        return zzX(aen.zzcuc, aen.zzcud);
    }

    private static Object zza(aen aen, String str, zzb zzb) {
        Object newInstance;
        Throwable e;
        Object obj = null;
        try {
            Class cls = Class.forName("com.google.android.gms.measurement.AppMeasurement$ConditionalUserProperty");
            Bundle zza = zza(aen);
            newInstance = cls.getConstructor(new Class[0]).newInstance(new Object[0]);
            try {
                cls.getField("mOrigin").set(newInstance, str);
                cls.getField("mCreationTimestamp").set(newInstance, Long.valueOf(aen.zzcue));
                cls.getField("mName").set(newInstance, aen.zzcuc);
                cls.getField("mValue").set(newInstance, aen.zzcud);
                if (!TextUtils.isEmpty(aen.zzcuf)) {
                    obj = aen.zzcuf;
                }
                cls.getField("mTriggerEventName").set(newInstance, obj);
                cls.getField("mTimedOutEventName").set(newInstance, !TextUtils.isEmpty(aen.zzcuk) ? aen.zzcuk : zzb.zzEw());
                cls.getField("mTimedOutEventParams").set(newInstance, zza);
                cls.getField("mTriggerTimeout").set(newInstance, Long.valueOf(aen.zzcug));
                cls.getField("mTriggeredEventName").set(newInstance, !TextUtils.isEmpty(aen.zzcui) ? aen.zzcui : zzb.zzEv());
                cls.getField("mTriggeredEventParams").set(newInstance, zza);
                cls.getField("mTimeToLive").set(newInstance, Long.valueOf(aen.zzaLt));
                cls.getField("mExpiredEventName").set(newInstance, !TextUtils.isEmpty(aen.zzcul) ? aen.zzcul : zzb.zzEx());
                cls.getField("mExpiredEventParams").set(newInstance, zza);
            } catch (ClassNotFoundException e2) {
                e = e2;
                Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", e);
                return newInstance;
            } catch (NoSuchMethodException e3) {
                e = e3;
                Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", e);
                return newInstance;
            } catch (IllegalAccessException e4) {
                e = e4;
                Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", e);
                return newInstance;
            } catch (InvocationTargetException e5) {
                e = e5;
                Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", e);
                return newInstance;
            } catch (NoSuchFieldException e6) {
                e = e6;
                Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", e);
                return newInstance;
            } catch (InstantiationException e7) {
                e = e7;
                Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", e);
                return newInstance;
            }
        } catch (ClassNotFoundException e8) {
            e = e8;
            newInstance = null;
            Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", e);
            return newInstance;
        } catch (NoSuchMethodException e9) {
            e = e9;
            newInstance = null;
            Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", e);
            return newInstance;
        } catch (IllegalAccessException e10) {
            e = e10;
            newInstance = null;
            Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", e);
            return newInstance;
        } catch (InvocationTargetException e11) {
            e = e11;
            newInstance = null;
            Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", e);
            return newInstance;
        } catch (NoSuchFieldException e12) {
            e = e12;
            newInstance = null;
            Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", e);
            return newInstance;
        } catch (InstantiationException e13) {
            e = e13;
            newInstance = null;
            Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", e);
            return newInstance;
        }
        return newInstance;
    }

    private static String zza(aen aen, zzb zzb) {
        return (aen == null || TextUtils.isEmpty(aen.zzcuj)) ? zzb.zzEy() : aen.zzcuj;
    }

    private static List<Object> zza(AppMeasurement appMeasurement, String str) {
        List<Object> list;
        Throwable e;
        Object obj;
        ArrayList arrayList = new ArrayList();
        try {
            Method declaredMethod = AppMeasurement.class.getDeclaredMethod("getConditionalUserProperties", new Class[]{String.class, String.class});
            declaredMethod.setAccessible(true);
            list = (List) declaredMethod.invoke(appMeasurement, new Object[]{str, ""});
        } catch (NoSuchMethodException e2) {
            e = e2;
            Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", e);
            obj = arrayList;
            if (Log.isLoggable("FirebaseAbtUtil", 2)) {
                Log.v("FirebaseAbtUtil", new StringBuilder(String.valueOf(str).length() + 55).append("Number of currently set _Es for origin: ").append(str).append(" is ").append(list.size()).toString());
            }
            return list;
        } catch (IllegalAccessException e3) {
            e = e3;
            Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", e);
            obj = arrayList;
            if (Log.isLoggable("FirebaseAbtUtil", 2)) {
                Log.v("FirebaseAbtUtil", new StringBuilder(String.valueOf(str).length() + 55).append("Number of currently set _Es for origin: ").append(str).append(" is ").append(list.size()).toString());
            }
            return list;
        } catch (InvocationTargetException e4) {
            e = e4;
            Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", e);
            obj = arrayList;
            if (Log.isLoggable("FirebaseAbtUtil", 2)) {
                Log.v("FirebaseAbtUtil", new StringBuilder(String.valueOf(str).length() + 55).append("Number of currently set _Es for origin: ").append(str).append(" is ").append(list.size()).toString());
            }
            return list;
        }
        if (Log.isLoggable("FirebaseAbtUtil", 2)) {
            Log.v("FirebaseAbtUtil", new StringBuilder(String.valueOf(str).length() + 55).append("Number of currently set _Es for origin: ").append(str).append(" is ").append(list.size()).toString());
        }
        return list;
    }

    private static void zza(Context context, String str, String str2, String str3, String str4) {
        Throwable e;
        if (Log.isLoggable("FirebaseAbtUtil", 2)) {
            String str5 = "FirebaseAbtUtil";
            String str6 = "_CE(experimentId) called by ";
            String valueOf = String.valueOf(str);
            Log.v(str5, valueOf.length() != 0 ? str6.concat(valueOf) : new String(str6));
        }
        if (zzbD(context)) {
            AppMeasurement zzaQ = zzaQ(context);
            try {
                Method declaredMethod = AppMeasurement.class.getDeclaredMethod("clearConditionalUserProperty", new Class[]{String.class, String.class, Bundle.class});
                declaredMethod.setAccessible(true);
                if (Log.isLoggable("FirebaseAbtUtil", 2)) {
                    Log.v("FirebaseAbtUtil", new StringBuilder((String.valueOf(str2).length() + 17) + String.valueOf(str3).length()).append("Clearing _E: [").append(str2).append(", ").append(str3).append("]").toString());
                }
                declaredMethod.invoke(zzaQ, new Object[]{str2, str4, zzX(str2, str3)});
            } catch (NoSuchMethodException e2) {
                e = e2;
                Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", e);
            } catch (IllegalAccessException e3) {
                e = e3;
                Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", e);
            } catch (InvocationTargetException e4) {
                e = e4;
                Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", e);
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void zza$10d5fda6(com.google.android.gms.measurement.AppMeasurement r7, android.content.Context r8, java.lang.String r9, com.google.android.gms.internal.aen r10, com.google.firebase.messaging.zzb r11) {
        /*
        r1 = 1;
        r2 = 2;
        r0 = "FirebaseAbtUtil";
        r0 = android.util.Log.isLoggable(r0, r2);
        if (r0 == 0) goto L_0x004b;
    L_0x000a:
        r0 = "FirebaseAbtUtil";
        r2 = r10.zzcuc;
        r2 = java.lang.String.valueOf(r2);
        r3 = r10.zzcud;
        r3 = java.lang.String.valueOf(r3);
        r4 = java.lang.String.valueOf(r2);
        r4 = r4.length();
        r4 = r4 + 7;
        r5 = java.lang.String.valueOf(r3);
        r5 = r5.length();
        r4 = r4 + r5;
        r5 = new java.lang.StringBuilder;
        r5.<init>(r4);
        r4 = "_SEI: ";
        r4 = r5.append(r4);
        r2 = r4.append(r2);
        r4 = " ";
        r2 = r2.append(r4);
        r2 = r2.append(r3);
        r2 = r2.toString();
        android.util.Log.v(r0, r2);
    L_0x004b:
        r0 = "com.google.android.gms.measurement.AppMeasurement$ConditionalUserProperty";
        java.lang.Class.forName(r0);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r2 = zza(r7, r9);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r0 = zzb(r7, r9);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r3 = zza(r7, r9);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r3 = r3.size();	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        if (r3 < r0) goto L_0x00af;
    L_0x0062:
        r0 = r10.zzcum;	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        if (r0 == 0) goto L_0x012a;
    L_0x0066:
        r0 = r10.zzcum;	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
    L_0x0068:
        if (r0 != r1) goto L_0x012d;
    L_0x006a:
        r0 = 0;
        r0 = r2.get(r0);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r1 = zzS(r0);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r0 = zzT(r0);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r3 = "FirebaseAbtUtil";
        r4 = 2;
        r3 = android.util.Log.isLoggable(r3, r4);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        if (r3 == 0) goto L_0x00a8;
    L_0x0080:
        r3 = "FirebaseAbtUtil";
        r4 = java.lang.String.valueOf(r1);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r4 = r4.length();	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r4 = r4 + 38;
        r5 = new java.lang.StringBuilder;	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r5.<init>(r4);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r4 = "Clearing _E due to overflow policy: [";
        r4 = r5.append(r4);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r4 = r4.append(r1);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r5 = "]";
        r4 = r4.append(r5);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r4 = r4.toString();	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        android.util.Log.v(r3, r4);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
    L_0x00a8:
        r3 = zza(r10, r11);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        zza(r8, r9, r1, r0, r3);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
    L_0x00af:
        r0 = r2.iterator();	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
    L_0x00b3:
        r1 = r0.hasNext();	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        if (r1 == 0) goto L_0x0180;
    L_0x00b9:
        r1 = r0.next();	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r2 = zzS(r1);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r1 = zzT(r1);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r3 = r10.zzcuc;	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r3 = r2.equals(r3);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        if (r3 == 0) goto L_0x00b3;
    L_0x00cd:
        r3 = r10.zzcud;	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r3 = r1.equals(r3);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        if (r3 != 0) goto L_0x00b3;
    L_0x00d5:
        r3 = "FirebaseAbtUtil";
        r4 = 2;
        r3 = android.util.Log.isLoggable(r3, r4);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        if (r3 == 0) goto L_0x00b3;
    L_0x00de:
        r3 = "FirebaseAbtUtil";
        r4 = java.lang.String.valueOf(r2);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r4 = r4.length();	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r4 = r4 + 77;
        r5 = java.lang.String.valueOf(r1);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r5 = r5.length();	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r4 = r4 + r5;
        r5 = new java.lang.StringBuilder;	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r5.<init>(r4);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r4 = "Clearing _E, as only one _V of the same _E can be set atany given time: [";
        r4 = r5.append(r4);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r4 = r4.append(r2);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r5 = ", ";
        r4 = r4.append(r5);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r4 = r4.append(r1);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r5 = "].";
        r4 = r4.append(r5);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r4 = r4.toString();	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        android.util.Log.v(r3, r4);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r3 = zza(r10, r11);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        zza(r8, r9, r2, r1, r3);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        goto L_0x00b3;
    L_0x0121:
        r0 = move-exception;
    L_0x0122:
        r1 = "FirebaseAbtUtil";
        r2 = "Could not complete the operation due to an internal error.";
        android.util.Log.e(r1, r2, r0);
    L_0x0129:
        return;
    L_0x012a:
        r0 = r1;
        goto L_0x0068;
    L_0x012d:
        r0 = "FirebaseAbtUtil";
        r1 = 2;
        r0 = android.util.Log.isLoggable(r0, r1);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        if (r0 == 0) goto L_0x0129;
    L_0x0136:
        r0 = "FirebaseAbtUtil";
        r1 = r10.zzcuc;	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r1 = java.lang.String.valueOf(r1);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r2 = r10.zzcud;	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r2 = java.lang.String.valueOf(r2);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r3 = java.lang.String.valueOf(r1);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r3 = r3.length();	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r3 = r3 + 44;
        r4 = java.lang.String.valueOf(r2);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r4 = r4.length();	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r3 = r3 + r4;
        r4 = new java.lang.StringBuilder;	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r4.<init>(r3);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r3 = "_E won't be set due to overflow policy. [";
        r3 = r4.append(r3);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r1 = r3.append(r1);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r3 = ", ";
        r1 = r1.append(r3);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r1 = r1.append(r2);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r2 = "]";
        r1 = r1.append(r2);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r1 = r1.toString();	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        android.util.Log.v(r0, r1);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        goto L_0x0129;
    L_0x017e:
        r0 = move-exception;
        goto L_0x0122;
    L_0x0180:
        r1 = zza(r10, r9, r11);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        if (r1 != 0) goto L_0x01db;
    L_0x0186:
        r0 = "FirebaseAbtUtil";
        r1 = 2;
        r0 = android.util.Log.isLoggable(r0, r1);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        if (r0 == 0) goto L_0x0129;
    L_0x018f:
        r0 = "FirebaseAbtUtil";
        r1 = r10.zzcuc;	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r1 = java.lang.String.valueOf(r1);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r2 = r10.zzcud;	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r2 = java.lang.String.valueOf(r2);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r3 = java.lang.String.valueOf(r1);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r3 = r3.length();	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r3 = r3 + 42;
        r4 = java.lang.String.valueOf(r2);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r4 = r4.length();	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r3 = r3 + r4;
        r4 = new java.lang.StringBuilder;	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r4.<init>(r3);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r3 = "Could not create _CUP for: [";
        r3 = r4.append(r3);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r1 = r3.append(r1);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r3 = ", ";
        r1 = r1.append(r3);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r1 = r1.append(r2);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r2 = "]. Skipping.";
        r1 = r1.append(r2);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r1 = r1.toString();	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        android.util.Log.v(r0, r1);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        goto L_0x0129;
    L_0x01d8:
        r0 = move-exception;
        goto L_0x0122;
    L_0x01db:
        r0 = "FirebaseAbtUtil";
        r2 = 2;
        r0 = android.util.Log.isLoggable(r0, r2);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        if (r0 == 0) goto L_0x0244;
    L_0x01e4:
        r0 = "FirebaseAbtUtil";
        r2 = r10.zzcuc;	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r2 = java.lang.String.valueOf(r2);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r3 = r10.zzcud;	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r3 = java.lang.String.valueOf(r3);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r4 = r10.zzcuf;	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r4 = java.lang.String.valueOf(r4);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r5 = java.lang.String.valueOf(r2);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r5 = r5.length();	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r5 = r5 + 27;
        r6 = java.lang.String.valueOf(r3);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r6 = r6.length();	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r5 = r5 + r6;
        r6 = java.lang.String.valueOf(r4);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r6 = r6.length();	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r5 = r5 + r6;
        r6 = new java.lang.StringBuilder;	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r6.<init>(r5);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r5 = "Setting _CUP for _E: [";
        r5 = r6.append(r5);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r2 = r5.append(r2);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r5 = ", ";
        r2 = r2.append(r5);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r2 = r2.append(r3);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r3 = ", ";
        r2 = r2.append(r3);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r2 = r2.append(r4);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r3 = "]";
        r2 = r2.append(r3);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        r2 = r2.toString();	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        android.util.Log.v(r0, r2);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
    L_0x0244:
        r0 = "com.google.android.gms.measurement.AppMeasurement$ConditionalUserProperty";
        r0 = java.lang.Class.forName(r0);	 Catch:{ ClassNotFoundException -> 0x0278, NoSuchMethodException -> 0x0289, IllegalAccessException -> 0x0287, InvocationTargetException -> 0x028b, NoSuchFieldException -> 0x01d8 }
        r2 = com.google.android.gms.measurement.AppMeasurement.class;
        r3 = "setConditionalUserProperty";
        r4 = 1;
        r4 = new java.lang.Class[r4];	 Catch:{ ClassNotFoundException -> 0x0278, NoSuchMethodException -> 0x0289, IllegalAccessException -> 0x0287, InvocationTargetException -> 0x028b, NoSuchFieldException -> 0x01d8 }
        r5 = 0;
        r4[r5] = r0;	 Catch:{ ClassNotFoundException -> 0x0278, NoSuchMethodException -> 0x0289, IllegalAccessException -> 0x0287, InvocationTargetException -> 0x028b, NoSuchFieldException -> 0x01d8 }
        r2 = r2.getDeclaredMethod(r3, r4);	 Catch:{ ClassNotFoundException -> 0x0278, NoSuchMethodException -> 0x0289, IllegalAccessException -> 0x0287, InvocationTargetException -> 0x028b, NoSuchFieldException -> 0x01d8 }
        r0 = 1;
        r2.setAccessible(r0);	 Catch:{ ClassNotFoundException -> 0x0278, NoSuchMethodException -> 0x0289, IllegalAccessException -> 0x0287, InvocationTargetException -> 0x028b, NoSuchFieldException -> 0x01d8 }
        r0 = r10.zzcuh;	 Catch:{ ClassNotFoundException -> 0x0278, NoSuchMethodException -> 0x0289, IllegalAccessException -> 0x0287, InvocationTargetException -> 0x028b, NoSuchFieldException -> 0x01d8 }
        r0 = android.text.TextUtils.isEmpty(r0);	 Catch:{ ClassNotFoundException -> 0x0278, NoSuchMethodException -> 0x0289, IllegalAccessException -> 0x0287, InvocationTargetException -> 0x028b, NoSuchFieldException -> 0x01d8 }
        if (r0 != 0) goto L_0x0282;
    L_0x0264:
        r0 = r10.zzcuh;	 Catch:{ ClassNotFoundException -> 0x0278, NoSuchMethodException -> 0x0289, IllegalAccessException -> 0x0287, InvocationTargetException -> 0x028b, NoSuchFieldException -> 0x01d8 }
    L_0x0266:
        r3 = zza(r10);	 Catch:{ ClassNotFoundException -> 0x0278, NoSuchMethodException -> 0x0289, IllegalAccessException -> 0x0287, InvocationTargetException -> 0x028b, NoSuchFieldException -> 0x01d8 }
        r7.logEventInternal(r9, r0, r3);	 Catch:{ ClassNotFoundException -> 0x0278, NoSuchMethodException -> 0x0289, IllegalAccessException -> 0x0287, InvocationTargetException -> 0x028b, NoSuchFieldException -> 0x01d8 }
        r0 = 1;
        r0 = new java.lang.Object[r0];	 Catch:{ ClassNotFoundException -> 0x0278, NoSuchMethodException -> 0x0289, IllegalAccessException -> 0x0287, InvocationTargetException -> 0x028b, NoSuchFieldException -> 0x01d8 }
        r3 = 0;
        r0[r3] = r1;	 Catch:{ ClassNotFoundException -> 0x0278, NoSuchMethodException -> 0x0289, IllegalAccessException -> 0x0287, InvocationTargetException -> 0x028b, NoSuchFieldException -> 0x01d8 }
        r2.invoke(r7, r0);	 Catch:{ ClassNotFoundException -> 0x0278, NoSuchMethodException -> 0x0289, IllegalAccessException -> 0x0287, InvocationTargetException -> 0x028b, NoSuchFieldException -> 0x01d8 }
        goto L_0x0129;
    L_0x0278:
        r0 = move-exception;
    L_0x0279:
        r1 = "FirebaseAbtUtil";
        r2 = "Could not complete the operation due to an internal error.";
        android.util.Log.e(r1, r2, r0);	 Catch:{ ClassNotFoundException -> 0x0121, IllegalAccessException -> 0x017e, NoSuchFieldException -> 0x01d8 }
        goto L_0x0129;
    L_0x0282:
        r0 = r11.zzEu();	 Catch:{ ClassNotFoundException -> 0x0278, NoSuchMethodException -> 0x0289, IllegalAccessException -> 0x0287, InvocationTargetException -> 0x028b, NoSuchFieldException -> 0x01d8 }
        goto L_0x0266;
    L_0x0287:
        r0 = move-exception;
        goto L_0x0279;
    L_0x0289:
        r0 = move-exception;
        goto L_0x0279;
    L_0x028b:
        r0 = move-exception;
        goto L_0x0279;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.firebase.messaging.zzc.zza$10d5fda6(com.google.android.gms.measurement.AppMeasurement, android.content.Context, java.lang.String, com.google.android.gms.internal.aen, com.google.firebase.messaging.zzb):void");
    }

    public static void zza$30cbbb43(Context context, String str, byte[] bArr, zzb zzb) {
        Throwable e;
        if (Log.isLoggable("FirebaseAbtUtil", 2)) {
            String str2 = "FirebaseAbtUtil";
            String str3 = "_SE called by ";
            String valueOf = String.valueOf(str);
            Log.v(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
        }
        if (zzbD(context)) {
            AppMeasurement zzaQ = zzaQ(context);
            aen zzA = zzA(bArr);
            if (zzA != null) {
                try {
                    Class.forName("com.google.android.gms.measurement.AppMeasurement$ConditionalUserProperty");
                    Object obj = null;
                    for (Object next : zza(zzaQ, str)) {
                        Object next2;
                        String zzS = zzS(next2);
                        String zzT = zzT(next2);
                        long longValue = ((Long) Class.forName("com.google.android.gms.measurement.AppMeasurement$ConditionalUserProperty").getField("mCreationTimestamp").get(next2)).longValue();
                        if (zzA.zzcuc.equals(zzS) && zzA.zzcud.equals(zzT)) {
                            if (Log.isLoggable("FirebaseAbtUtil", 2)) {
                                Log.v("FirebaseAbtUtil", new StringBuilder((String.valueOf(zzS).length() + 23) + String.valueOf(zzT).length()).append("_E is already set. [").append(zzS).append(", ").append(zzT).append("]").toString());
                            }
                            obj = 1;
                        } else {
                            next2 = null;
                            aem[] aemArr = zzA.zzcun;
                            int length = aemArr.length;
                            int i = 0;
                            while (i < length) {
                                if (aemArr[i].zzcuc.equals(zzS)) {
                                    if (Log.isLoggable("FirebaseAbtUtil", 2)) {
                                        Log.v("FirebaseAbtUtil", new StringBuilder((String.valueOf(zzS).length() + 33) + String.valueOf(zzT).length()).append("_E is found in the _OE list. [").append(zzS).append(", ").append(zzT).append("]").toString());
                                    }
                                    next2 = 1;
                                    if (next2 != null) {
                                        continue;
                                    } else if (zzA.zzcue > longValue) {
                                        if (Log.isLoggable("FirebaseAbtUtil", 2)) {
                                            Log.v("FirebaseAbtUtil", new StringBuilder((String.valueOf(zzS).length() + 115) + String.valueOf(zzT).length()).append("Clearing _E as it was not in the _OE list, andits start time is older than the start time of the _E to be set. [").append(zzS).append(", ").append(zzT).append("]").toString());
                                        }
                                        zza(context, str, zzS, zzT, zza(zzA, zzb));
                                    } else if (Log.isLoggable("FirebaseAbtUtil", 2)) {
                                        Log.v("FirebaseAbtUtil", new StringBuilder((String.valueOf(zzS).length() + 109) + String.valueOf(zzT).length()).append("_E was not found in the _OE list, but not clearing it as it has a new start time than the _E to be set.  [").append(zzS).append(", ").append(zzT).append("]").toString());
                                    }
                                } else {
                                    i++;
                                }
                            }
                            if (next2 != null) {
                                continue;
                            } else if (zzA.zzcue > longValue) {
                                if (Log.isLoggable("FirebaseAbtUtil", 2)) {
                                    Log.v("FirebaseAbtUtil", new StringBuilder((String.valueOf(zzS).length() + 115) + String.valueOf(zzT).length()).append("Clearing _E as it was not in the _OE list, andits start time is older than the start time of the _E to be set. [").append(zzS).append(", ").append(zzT).append("]").toString());
                                }
                                zza(context, str, zzS, zzT, zza(zzA, zzb));
                            } else if (Log.isLoggable("FirebaseAbtUtil", 2)) {
                                Log.v("FirebaseAbtUtil", new StringBuilder((String.valueOf(zzS).length() + 109) + String.valueOf(zzT).length()).append("_E was not found in the _OE list, but not clearing it as it has a new start time than the _E to be set.  [").append(zzS).append(", ").append(zzT).append("]").toString());
                            }
                        }
                    }
                    if (obj == null) {
                        zza$10d5fda6(zzaQ, context, str, zzA, zzb);
                        return;
                    } else if (Log.isLoggable("FirebaseAbtUtil", 2)) {
                        str2 = String.valueOf(zzA.zzcuc);
                        str3 = String.valueOf(zzA.zzcud);
                        Log.v("FirebaseAbtUtil", new StringBuilder((String.valueOf(str2).length() + 44) + String.valueOf(str3).length()).append("_E is already set. Not setting it again [").append(str2).append(", ").append(str3).append("]").toString());
                        return;
                    } else {
                        return;
                    }
                } catch (ClassNotFoundException e2) {
                    e = e2;
                } catch (IllegalAccessException e3) {
                    e = e3;
                } catch (NoSuchFieldException e4) {
                    e = e4;
                }
            } else if (Log.isLoggable("FirebaseAbtUtil", 2)) {
                Log.v("FirebaseAbtUtil", "_SE failed; either _P was not set, or we couldn't deserialize the _P.");
                return;
            } else {
                return;
            }
        }
        return;
        Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", e);
    }

    private static AppMeasurement zzaQ(Context context) {
        try {
            return AppMeasurement.getInstance(context);
        } catch (NoClassDefFoundError e) {
            return null;
        }
    }

    private static int zzb(AppMeasurement appMeasurement, String str) {
        Throwable e;
        try {
            Method declaredMethod = AppMeasurement.class.getDeclaredMethod("getMaxUserProperties", new Class[]{String.class});
            declaredMethod.setAccessible(true);
            return ((Integer) declaredMethod.invoke(appMeasurement, new Object[]{str})).intValue();
        } catch (NoSuchMethodException e2) {
            e = e2;
            Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", e);
            return 20;
        } catch (IllegalAccessException e3) {
            e = e3;
            Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", e);
            return 20;
        } catch (InvocationTargetException e4) {
            e = e4;
            Log.e("FirebaseAbtUtil", "Could not complete the operation due to an internal error.", e);
            return 20;
        }
    }

    private static boolean zzbD(Context context) {
        if (zzaQ(context) != null) {
            try {
                Class.forName("com.google.android.gms.measurement.AppMeasurement$ConditionalUserProperty");
                return true;
            } catch (ClassNotFoundException e) {
                if (!Log.isLoggable("FirebaseAbtUtil", 2)) {
                    return false;
                }
                Log.v("FirebaseAbtUtil", "Firebase Analytics library is missing support for abt. Please update to a more recent version.");
                return false;
            }
        } else if (!Log.isLoggable("FirebaseAbtUtil", 2)) {
            return false;
        } else {
            Log.v("FirebaseAbtUtil", "Firebase Analytics not available");
            return false;
        }
    }

    private static aen zzA(byte[] bArr) {
        try {
            return (aen) adp.zza(new aen(), bArr);
        } catch (ado e) {
            return null;
        }
    }
}
