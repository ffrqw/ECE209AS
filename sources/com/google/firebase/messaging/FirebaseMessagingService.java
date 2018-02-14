package com.google.firebase.messaging;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.firebase.iid.zzb;
import com.google.firebase.iid.zzq;
import java.util.Iterator;

public class FirebaseMessagingService extends zzb {
    static boolean zzJ(Bundle bundle) {
        return bundle == null ? false : "1".equals(bundle.getString("google.c.a.e"));
    }

    static void zzt(Bundle bundle) {
        Iterator it = bundle.keySet().iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (str != null && str.startsWith("google.c.")) {
                it.remove();
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void handleIntent(android.content.Intent r6) {
        /*
        r5 = this;
        r3 = 1;
        r1 = 0;
        r2 = -1;
        r0 = r6.getAction();
        if (r0 != 0) goto L_0x000b;
    L_0x0009:
        r0 = "";
    L_0x000b:
        r4 = r0.hashCode();
        switch(r4) {
            case 75300319: goto L_0x003a;
            case 366519424: goto L_0x0030;
            default: goto L_0x0012;
        };
    L_0x0012:
        r0 = r2;
    L_0x0013:
        switch(r0) {
            case 0: goto L_0x0044;
            case 1: goto L_0x00fe;
            default: goto L_0x0016;
        };
    L_0x0016:
        r1 = "FirebaseMessaging";
        r2 = "Unknown intent action: ";
        r0 = r6.getAction();
        r0 = java.lang.String.valueOf(r0);
        r3 = r0.length();
        if (r3 == 0) goto L_0x010d;
    L_0x0028:
        r0 = r2.concat(r0);
    L_0x002c:
        android.util.Log.d(r1, r0);
    L_0x002f:
        return;
    L_0x0030:
        r4 = "com.google.android.c2dm.intent.RECEIVE";
        r0 = r0.equals(r4);
        if (r0 == 0) goto L_0x0012;
    L_0x0038:
        r0 = r1;
        goto L_0x0013;
    L_0x003a:
        r4 = "com.google.firebase.messaging.NOTIFICATION_DISMISS";
        r0 = r0.equals(r4);
        if (r0 == 0) goto L_0x0012;
    L_0x0042:
        r0 = r3;
        goto L_0x0013;
    L_0x0044:
        r0 = "message_type";
        r0 = r6.getStringExtra(r0);
        if (r0 != 0) goto L_0x004e;
    L_0x004c:
        r0 = "gcm";
    L_0x004e:
        r4 = r0.hashCode();
        switch(r4) {
            case -2062414158: goto L_0x0078;
            case 102161: goto L_0x006f;
            case 814694033: goto L_0x008c;
            case 814800675: goto L_0x0082;
            default: goto L_0x0055;
        };
    L_0x0055:
        r1 = r2;
    L_0x0056:
        switch(r1) {
            case 0: goto L_0x0096;
            case 1: goto L_0x002f;
            case 2: goto L_0x00d6;
            case 3: goto L_0x00dd;
            default: goto L_0x0059;
        };
    L_0x0059:
        r1 = "FirebaseMessaging";
        r2 = "Received message with unknown type: ";
        r0 = java.lang.String.valueOf(r0);
        r3 = r0.length();
        if (r3 == 0) goto L_0x00f7;
    L_0x0067:
        r0 = r2.concat(r0);
    L_0x006b:
        android.util.Log.w(r1, r0);
        goto L_0x002f;
    L_0x006f:
        r3 = "gcm";
        r3 = r0.equals(r3);
        if (r3 == 0) goto L_0x0055;
    L_0x0077:
        goto L_0x0056;
    L_0x0078:
        r1 = "deleted_messages";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0055;
    L_0x0080:
        r1 = r3;
        goto L_0x0056;
    L_0x0082:
        r1 = "send_event";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0055;
    L_0x008a:
        r1 = 2;
        goto L_0x0056;
    L_0x008c:
        r1 = "send_error";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0055;
    L_0x0094:
        r1 = 3;
        goto L_0x0056;
    L_0x0096:
        r0 = r6.getExtras();
        r0 = zzJ(r0);
        if (r0 == 0) goto L_0x00a3;
    L_0x00a0:
        com.google.firebase.messaging.zzd.zzg(r5, r6);
    L_0x00a3:
        r0 = r6.getExtras();
        if (r0 != 0) goto L_0x00ae;
    L_0x00a9:
        r0 = new android.os.Bundle;
        r0.<init>();
    L_0x00ae:
        r1 = "android.support.content.wakelockid";
        r0.remove(r1);
        r1 = com.google.firebase.messaging.zza.zzG(r0);
        if (r1 == 0) goto L_0x00cc;
    L_0x00b9:
        r1 = com.google.firebase.messaging.zza.zzbM(r5);
        r1 = r1.zzv(r0);
        if (r1 != 0) goto L_0x002f;
    L_0x00c3:
        r1 = zzJ(r0);
        if (r1 == 0) goto L_0x00cc;
    L_0x00c9:
        com.google.firebase.messaging.zzd.zzj(r5, r6);
    L_0x00cc:
        r1 = new com.google.firebase.messaging.RemoteMessage;
        r1.<init>(r0);
        r5.onMessageReceived(r1);
        goto L_0x002f;
    L_0x00d6:
        r0 = "google.message_id";
        r6.getStringExtra(r0);
        goto L_0x002f;
    L_0x00dd:
        r0 = "google.message_id";
        r0 = r6.getStringExtra(r0);
        if (r0 != 0) goto L_0x00ea;
    L_0x00e5:
        r0 = "message_id";
        r6.getStringExtra(r0);
    L_0x00ea:
        r0 = new com.google.firebase.messaging.SendException;
        r1 = "error";
        r1 = r6.getStringExtra(r1);
        r0.<init>(r1);
        goto L_0x002f;
    L_0x00f7:
        r0 = new java.lang.String;
        r0.<init>(r2);
        goto L_0x006b;
    L_0x00fe:
        r0 = r6.getExtras();
        r0 = zzJ(r0);
        if (r0 == 0) goto L_0x002f;
    L_0x0108:
        com.google.firebase.messaging.zzd.zzi(r5, r6);
        goto L_0x002f;
    L_0x010d:
        r0 = new java.lang.String;
        r0.<init>(r2);
        goto L_0x002c;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.firebase.messaging.FirebaseMessagingService.handleIntent(android.content.Intent):void");
    }

    public void onMessageReceived(RemoteMessage remoteMessage) {
    }

    protected final Intent zzn(Intent intent) {
        return zzq.zzJX().zzJY();
    }

    public final boolean zzo(Intent intent) {
        if (!"com.google.firebase.messaging.NOTIFICATION_OPEN".equals(intent.getAction())) {
            return false;
        }
        PendingIntent pendingIntent = (PendingIntent) intent.getParcelableExtra("pending_intent");
        if (pendingIntent != null) {
            try {
                pendingIntent.send();
            } catch (CanceledException e) {
                Log.e("FirebaseMessaging", "Notification pending intent canceled");
            }
        }
        if (zzJ(intent.getExtras())) {
            zzd.zzh(this, intent);
        }
        return true;
    }
}
