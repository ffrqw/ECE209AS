package com.google.firebase.iid;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

public class FirebaseInstanceIdService extends zzb {
    private static Object zzckB = new Object();
    private static boolean zzckC = false;
    private boolean zzckD = false;

    static class zza extends BroadcastReceiver {
        private static BroadcastReceiver receiver;
        private int zzckE;

        private zza(int i) {
            this.zzckE = i;
        }

        static synchronized void zzl(Context context, int i) {
            synchronized (zza.class) {
                if (receiver == null) {
                    receiver = new zza(i);
                    context.getApplicationContext().registerReceiver(receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
                }
            }
        }

        public void onReceive(Context context, Intent intent) {
            synchronized (zza.class) {
                if (receiver != this) {
                } else if (FirebaseInstanceIdService.zzbJ(context)) {
                    if (Log.isLoggable("FirebaseInstanceId", 3)) {
                        Log.d("FirebaseInstanceId", "connectivity changed. starting background sync.");
                    }
                    context.getApplicationContext().unregisterReceiver(this);
                    receiver = null;
                    zzq.zzJX().zze(context, FirebaseInstanceIdService.zzbZ(this.zzckE));
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void zza(android.content.Context r2, com.google.firebase.iid.FirebaseInstanceId r3) {
        /*
        r1 = zzckB;
        monitor-enter(r1);
        r0 = zzckC;	 Catch:{ all -> 0x0026 }
        if (r0 == 0) goto L_0x0009;
    L_0x0007:
        monitor-exit(r1);	 Catch:{ all -> 0x0026 }
    L_0x0008:
        return;
    L_0x0009:
        monitor-exit(r1);	 Catch:{ all -> 0x0026 }
        r0 = r3.zzJQ();
        if (r0 == 0) goto L_0x0022;
    L_0x0010:
        r1 = com.google.firebase.iid.zzj.zzbgW;
        r0 = r0.zzhp(r1);
        if (r0 != 0) goto L_0x0022;
    L_0x0018:
        r0 = com.google.firebase.iid.FirebaseInstanceId.zzJS();
        r0 = r0.zzJV();
        if (r0 == 0) goto L_0x0008;
    L_0x0022:
        zzbI(r2);
        goto L_0x0008;
    L_0x0026:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0026 }
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.firebase.iid.FirebaseInstanceIdService.zza(android.content.Context, com.google.firebase.iid.FirebaseInstanceId):void");
    }

    private final void zza(Intent intent, String str) {
        int i = 28800;
        boolean zzbJ = zzbJ(this);
        int intExtra = intent == null ? 10 : intent.getIntExtra("next_retry_delay_in_seconds", 0);
        if (intExtra < 10 && !zzbJ) {
            i = 30;
        } else if (intExtra < 10) {
            i = 10;
        } else if (intExtra <= 28800) {
            i = intExtra;
        }
        Log.d("FirebaseInstanceId", new StringBuilder(String.valueOf(str).length() + 47).append("background sync failed: ").append(str).append(", retry in ").append(i).append("s").toString());
        synchronized (zzckB) {
            ((AlarmManager) getSystemService("alarm")).set(3, SystemClock.elapsedRealtime() + ((long) (i * 1000)), zzq.zza$4e95509e(this, zzbZ(i << 1)));
            zzckC = true;
        }
        if (!zzbJ) {
            if (this.zzckD) {
                Log.d("FirebaseInstanceId", "device not connected. Connectivity change received registered");
            }
            zza.zzl(this, i);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void zza$26ff95ce(android.content.Intent r9, boolean r10) {
        /*
        r8 = this;
        r2 = 1;
        r1 = 0;
        r3 = zzckB;
        monitor-enter(r3);
        r0 = 0;
        zzckC = r0;	 Catch:{ all -> 0x0010 }
        monitor-exit(r3);	 Catch:{ all -> 0x0010 }
        r0 = com.google.firebase.iid.zzl.zzbd(r8);
        if (r0 != 0) goto L_0x0013;
    L_0x000f:
        return;
    L_0x0010:
        r0 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0010 }
        throw r0;
    L_0x0013:
        r0 = com.google.firebase.iid.FirebaseInstanceId.getInstance();
        r3 = r0.zzJQ();
        if (r3 == 0) goto L_0x0025;
    L_0x001d:
        r4 = com.google.firebase.iid.zzj.zzbgW;
        r4 = r3.zzhp(r4);
        if (r4 == 0) goto L_0x0063;
    L_0x0025:
        r1 = r0.zzJR();	 Catch:{ IOException -> 0x004b, SecurityException -> 0x005a }
        if (r1 == 0) goto L_0x0054;
    L_0x002b:
        r2 = r8.zzckD;	 Catch:{ IOException -> 0x004b, SecurityException -> 0x005a }
        if (r2 == 0) goto L_0x0036;
    L_0x002f:
        r2 = "FirebaseInstanceId";
        r4 = "get master token succeeded";
        android.util.Log.d(r2, r4);	 Catch:{ IOException -> 0x004b, SecurityException -> 0x005a }
    L_0x0036:
        zza(r8, r0);	 Catch:{ IOException -> 0x004b, SecurityException -> 0x005a }
        if (r10 != 0) goto L_0x0047;
    L_0x003b:
        if (r3 == 0) goto L_0x0047;
    L_0x003d:
        if (r3 == 0) goto L_0x000f;
    L_0x003f:
        r0 = r3.zzbPJ;	 Catch:{ IOException -> 0x004b, SecurityException -> 0x005a }
        r0 = r1.equals(r0);	 Catch:{ IOException -> 0x004b, SecurityException -> 0x005a }
        if (r0 != 0) goto L_0x000f;
    L_0x0047:
        r8.onTokenRefresh();	 Catch:{ IOException -> 0x004b, SecurityException -> 0x005a }
        goto L_0x000f;
    L_0x004b:
        r0 = move-exception;
        r0 = r0.getMessage();
        r8.zza(r9, r0);
        goto L_0x000f;
    L_0x0054:
        r0 = "returned token is null";
        r8.zza(r9, r0);	 Catch:{ IOException -> 0x004b, SecurityException -> 0x005a }
        goto L_0x000f;
    L_0x005a:
        r0 = move-exception;
        r1 = "FirebaseInstanceId";
        r2 = "Unable to get master token";
        android.util.Log.e(r1, r2, r0);
        goto L_0x000f;
    L_0x0063:
        r4 = com.google.firebase.iid.FirebaseInstanceId.zzJS();
        r0 = r4.zzJV();
        r3 = r0;
    L_0x006c:
        if (r3 == 0) goto L_0x00d4;
    L_0x006e:
        r0 = "!";
        r0 = r3.split(r0);
        r5 = r0.length;
        r6 = 2;
        if (r5 != r6) goto L_0x0087;
    L_0x0078:
        r5 = r0[r1];
        r6 = r0[r2];
        r0 = -1;
        r7 = r5.hashCode();	 Catch:{ IOException -> 0x00b7 }
        switch(r7) {
            case 83: goto L_0x0090;
            case 84: goto L_0x0084;
            case 85: goto L_0x009a;
            default: goto L_0x0084;
        };
    L_0x0084:
        switch(r0) {
            case 0: goto L_0x00a4;
            case 1: goto L_0x00c1;
            default: goto L_0x0087;
        };
    L_0x0087:
        r4.zzhj(r3);
        r0 = r4.zzJV();
        r3 = r0;
        goto L_0x006c;
    L_0x0090:
        r7 = "S";
        r5 = r5.equals(r7);	 Catch:{ IOException -> 0x00b7 }
        if (r5 == 0) goto L_0x0084;
    L_0x0098:
        r0 = r1;
        goto L_0x0084;
    L_0x009a:
        r7 = "U";
        r5 = r5.equals(r7);	 Catch:{ IOException -> 0x00b7 }
        if (r5 == 0) goto L_0x0084;
    L_0x00a2:
        r0 = r2;
        goto L_0x0084;
    L_0x00a4:
        r0 = com.google.firebase.iid.FirebaseInstanceId.getInstance();	 Catch:{ IOException -> 0x00b7 }
        r0.zzhg(r6);	 Catch:{ IOException -> 0x00b7 }
        r0 = r8.zzckD;	 Catch:{ IOException -> 0x00b7 }
        if (r0 == 0) goto L_0x0087;
    L_0x00af:
        r0 = "FirebaseInstanceId";
        r5 = "subscribe operation succeeded";
        android.util.Log.d(r0, r5);	 Catch:{ IOException -> 0x00b7 }
        goto L_0x0087;
    L_0x00b7:
        r0 = move-exception;
        r0 = r0.getMessage();
        r8.zza(r9, r0);
        goto L_0x000f;
    L_0x00c1:
        r0 = com.google.firebase.iid.FirebaseInstanceId.getInstance();	 Catch:{ IOException -> 0x00b7 }
        r0.zzhh(r6);	 Catch:{ IOException -> 0x00b7 }
        r0 = r8.zzckD;	 Catch:{ IOException -> 0x00b7 }
        if (r0 == 0) goto L_0x0087;
    L_0x00cc:
        r0 = "FirebaseInstanceId";
        r5 = "unsubscribe operation succeeded";
        android.util.Log.d(r0, r5);	 Catch:{ IOException -> 0x00b7 }
        goto L_0x0087;
    L_0x00d4:
        r0 = "FirebaseInstanceId";
        r1 = "topic sync succeeded";
        android.util.Log.d(r0, r1);
        goto L_0x000f;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.firebase.iid.FirebaseInstanceIdService.zza$26ff95ce(android.content.Intent, boolean):void");
    }

    static void zzbI(Context context) {
        if (zzl.zzbd(context) != null) {
            synchronized (zzckB) {
                if (!zzckC) {
                    zzq.zzJX().zze(context, zzbZ(0));
                    zzckC = true;
                }
            }
        }
    }

    private static boolean zzbJ(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static Intent zzbZ(int i) {
        Intent intent = new Intent("ACTION_TOKEN_REFRESH_RETRY");
        intent.putExtra("next_retry_delay_in_seconds", i);
        return intent;
    }

    private final zzj zzhi(String str) {
        if (str == null) {
            return zzj.zzb(this, null);
        }
        Bundle bundle = new Bundle();
        bundle.putString("subtype", str);
        return zzj.zzb(this, bundle);
    }

    private static String zzp(Intent intent) {
        String stringExtra = intent.getStringExtra("subtype");
        return stringExtra == null ? "" : stringExtra;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void handleIntent(android.content.Intent r9) {
        /*
        r8 = this;
        r1 = 0;
        r7 = 1;
        r0 = r9.getAction();
        if (r0 != 0) goto L_0x000a;
    L_0x0008:
        r0 = "";
    L_0x000a:
        r2 = -1;
        r3 = r0.hashCode();
        switch(r3) {
            case -1737547627: goto L_0x0093;
            default: goto L_0x0012;
        };
    L_0x0012:
        r0 = r2;
    L_0x0013:
        switch(r0) {
            case 0: goto L_0x009e;
            default: goto L_0x0016;
        };
    L_0x0016:
        r0 = zzp(r9);
        r1 = r8.zzhi(r0);
        r2 = "CMD";
        r2 = r9.getStringExtra(r2);
        r3 = r8.zzckD;
        if (r3 == 0) goto L_0x0078;
    L_0x0028:
        r3 = "FirebaseInstanceId";
        r4 = r9.getExtras();
        r4 = java.lang.String.valueOf(r4);
        r5 = java.lang.String.valueOf(r0);
        r5 = r5.length();
        r5 = r5 + 18;
        r6 = java.lang.String.valueOf(r2);
        r6 = r6.length();
        r5 = r5 + r6;
        r6 = java.lang.String.valueOf(r4);
        r6 = r6.length();
        r5 = r5 + r6;
        r6 = new java.lang.StringBuilder;
        r6.<init>(r5);
        r5 = "Service command ";
        r5 = r6.append(r5);
        r5 = r5.append(r0);
        r6 = " ";
        r5 = r5.append(r6);
        r5 = r5.append(r2);
        r6 = " ";
        r5 = r5.append(r6);
        r4 = r5.append(r4);
        r4 = r4.toString();
        android.util.Log.d(r3, r4);
    L_0x0078:
        r3 = "unregistered";
        r3 = r9.getStringExtra(r3);
        if (r3 == 0) goto L_0x00a2;
    L_0x0080:
        r1 = com.google.firebase.iid.zzj.zzJT();
        if (r0 != 0) goto L_0x0088;
    L_0x0086:
        r0 = "";
    L_0x0088:
        r1.zzdr(r0);
        r0 = com.google.firebase.iid.zzj.zzJU();
        r0.zzi(r9);
    L_0x0092:
        return;
    L_0x0093:
        r3 = "ACTION_TOKEN_REFRESH_RETRY";
        r0 = r0.equals(r3);
        if (r0 == 0) goto L_0x0012;
    L_0x009b:
        r0 = r1;
        goto L_0x0013;
    L_0x009e:
        r8.zza$26ff95ce(r9, r1);
        goto L_0x0092;
    L_0x00a2:
        r3 = "gcm.googleapis.com/refresh";
        r4 = "from";
        r4 = r9.getStringExtra(r4);
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x00bb;
    L_0x00b0:
        r1 = com.google.firebase.iid.zzj.zzJT();
        r1.zzdr(r0);
        r8.zza$26ff95ce(r9, r7);
        goto L_0x0092;
    L_0x00bb:
        r3 = "RST";
        r3 = r3.equals(r2);
        if (r3 == 0) goto L_0x00ca;
    L_0x00c3:
        r1.zzvL();
        r8.zza$26ff95ce(r9, r7);
        goto L_0x0092;
    L_0x00ca:
        r3 = "RST_FULL";
        r3 = r3.equals(r2);
        if (r3 == 0) goto L_0x00ea;
    L_0x00d2:
        r0 = com.google.firebase.iid.zzj.zzJT();
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x0092;
    L_0x00dc:
        r1.zzvL();
        r0 = com.google.firebase.iid.zzj.zzJT();
        r0.zzvP();
        r8.zza$26ff95ce(r9, r7);
        goto L_0x0092;
    L_0x00ea:
        r1 = "SYNC";
        r1 = r1.equals(r2);
        if (r1 == 0) goto L_0x00fd;
    L_0x00f2:
        r1 = com.google.firebase.iid.zzj.zzJT();
        r1.zzdr(r0);
        r8.zza$26ff95ce(r9, r7);
        goto L_0x0092;
    L_0x00fd:
        r0 = "PING";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x0092;
    L_0x0105:
        r0 = r9.getExtras();
        r1 = com.google.firebase.iid.zzl.zzbd(r8);
        if (r1 != 0) goto L_0x0118;
    L_0x010f:
        r0 = "FirebaseInstanceId";
        r1 = "Unable to respond to ping due to missing target package";
        android.util.Log.w(r0, r1);
        goto L_0x0092;
    L_0x0118:
        r2 = new android.content.Intent;
        r3 = "com.google.android.gcm.intent.SEND";
        r2.<init>(r3);
        r2.setPackage(r1);
        r2.putExtras(r0);
        com.google.firebase.iid.zzl.zzd(r8, r2);
        r0 = "google.to";
        r1 = "google.com/iid";
        r2.putExtra(r0, r1);
        r0 = "google.message_id";
        r1 = com.google.firebase.iid.zzl.zzvO();
        r2.putExtra(r0, r1);
        r0 = "com.google.android.gtalkservice.permission.GTALK_SERVICE";
        r8.sendOrderedBroadcast(r2, r0);
        goto L_0x0092;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.firebase.iid.FirebaseInstanceIdService.handleIntent(android.content.Intent):void");
    }

    public void onTokenRefresh() {
    }

    protected final Intent zzn(Intent intent) {
        return (Intent) zzq.zzJX().zzckP.poll();
    }

    public final boolean zzo(Intent intent) {
        this.zzckD = Log.isLoggable("FirebaseInstanceId", 3);
        if (intent.getStringExtra("error") == null && intent.getStringExtra("registration_id") == null) {
            return false;
        }
        String zzp = zzp(intent);
        if (this.zzckD) {
            String str = "FirebaseInstanceId";
            String str2 = "Register result in service ";
            String valueOf = String.valueOf(zzp);
            Log.d(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
        }
        zzhi(zzp);
        zzj.zzJU().zzi(intent);
        return true;
    }
}
