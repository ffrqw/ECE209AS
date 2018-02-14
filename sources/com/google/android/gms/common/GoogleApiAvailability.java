package com.google.android.gms.common;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.app.Notification.BigTextStyle;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import com.google.android.gms.common.api.GoogleApiActivity;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzs;
import com.google.android.gms.common.internal.zzt;
import com.google.android.gms.common.util.zzj;
import com.google.android.gms.common.util.zzq;
import com.google.android.gms.internal.zzbdk;
import com.google.android.gms.internal.zzbdl;
import com.google.android.gms.internal.zzbdt;
import com.rachio.iro.R;

public final class GoogleApiAvailability extends zze {
    public static final int GOOGLE_PLAY_SERVICES_VERSION_CODE = zze.GOOGLE_PLAY_SERVICES_VERSION_CODE;
    private static final GoogleApiAvailability zzaAa = new GoogleApiAvailability();

    @SuppressLint({"HandlerLeak"})
    class zza extends Handler {
        private final Context mApplicationContext;
        private /* synthetic */ GoogleApiAvailability zzaAb;

        public zza(GoogleApiAvailability googleApiAvailability, Context context) {
            this.zzaAb = googleApiAvailability;
            super(Looper.myLooper() == null ? Looper.getMainLooper() : Looper.myLooper());
            this.mApplicationContext = context.getApplicationContext();
        }

        public final void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    int isGooglePlayServicesAvailable = this.zzaAb.isGooglePlayServicesAvailable(this.mApplicationContext);
                    if (this.zzaAb.isUserResolvableError(isGooglePlayServicesAvailable)) {
                        this.zzaAb.showErrorNotification(this.mApplicationContext, isGooglePlayServicesAvailable);
                        return;
                    }
                    return;
                default:
                    Log.w("GoogleApiAvailability", "Don't know how to handle this message: " + message.what);
                    return;
            }
        }
    }

    GoogleApiAvailability() {
    }

    public static Dialog getErrorDialog(Activity activity, int i, int i2, OnCancelListener onCancelListener) {
        return zza((Context) activity, i, zzt.zza(activity, zze.zza(activity, i, "d"), i2), onCancelListener);
    }

    public static GoogleApiAvailability getInstance() {
        return zzaAa;
    }

    public static Dialog zza(Activity activity, OnCancelListener onCancelListener) {
        View progressBar = new ProgressBar(activity, null, 16842874);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(0);
        Builder builder = new Builder(activity);
        builder.setView(progressBar);
        builder.setMessage(zzs.zzi(activity, 18));
        builder.setPositiveButton("", null);
        Dialog create = builder.create();
        zza(activity, create, "GooglePlayServicesUpdatingDialog", onCancelListener);
        return create;
    }

    static Dialog zza(Context context, int i, zzt zzt, OnCancelListener onCancelListener) {
        Builder builder = null;
        if (i == 0) {
            return null;
        }
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(16843529, typedValue, true);
        if ("Theme.Dialog.Alert".equals(context.getResources().getResourceEntryName(typedValue.resourceId))) {
            builder = new Builder(context, 5);
        }
        if (builder == null) {
            builder = new Builder(context);
        }
        builder.setMessage(zzs.zzi(context, i));
        if (onCancelListener != null) {
            builder.setOnCancelListener(onCancelListener);
        }
        CharSequence zzk = zzs.zzk(context, i);
        if (zzk != null) {
            builder.setPositiveButton(zzk, zzt);
        }
        zzk = zzs.zzg(context, i);
        if (zzk != null) {
            builder.setTitle(zzk);
        }
        return builder.create();
    }

    public static zzbdk zza(Context context, zzbdl zzbdl) {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter.addDataScheme("package");
        BroadcastReceiver zzbdk = new zzbdk(zzbdl);
        context.registerReceiver(zzbdk, intentFilter);
        zzbdk.setContext(context);
        if (zzo.zzy(context, "com.google.android.gms")) {
            return zzbdk;
        }
        zzbdl.zzpA();
        zzbdk.unregister();
        return null;
    }

    static void zza(Activity activity, Dialog dialog, String str, OnCancelListener onCancelListener) {
        if (activity instanceof FragmentActivity) {
            SupportErrorDialogFragment.newInstance(dialog, onCancelListener).show(((FragmentActivity) activity).getSupportFragmentManager(), str);
            return;
        }
        ErrorDialogFragment.newInstance(dialog, onCancelListener).show(activity.getFragmentManager(), str);
    }

    @TargetApi(20)
    private final void zza$53da80fe(Context context, int i, PendingIntent pendingIntent) {
        if (i == 18) {
            zzar(context);
        } else if (pendingIntent != null) {
            Notification build;
            int i2;
            CharSequence zzh = zzs.zzh(context, i);
            CharSequence zzj = zzs.zzj(context, i);
            Resources resources = context.getResources();
            if (zzj.zzaH(context)) {
                zzbo.zzae(zzq.zzsd());
                build = new Notification.Builder(context).setSmallIcon(context.getApplicationInfo().icon).setPriority(2).setAutoCancel(true).setContentTitle(zzh).setStyle(new BigTextStyle().bigText(zzj)).addAction(R.drawable.common_full_open_on_phone, resources.getString(R.string.common_open_on_phone), pendingIntent).build();
            } else {
                NotificationCompat.Builder ticker = new NotificationCompat.Builder(context).setSmallIcon(17301642).setTicker(resources.getString(R.string.common_google_play_services_notification_ticker));
                ticker.mNotification.when = System.currentTimeMillis();
                build = ticker.setAutoCancel(true).setContentIntent(pendingIntent).setContentTitle(zzh).setContentText(zzj).setLocalOnly(true).setStyle(new NotificationCompat.BigTextStyle().bigText(zzj)).build();
            }
            switch (i) {
                case 1:
                case 2:
                case 3:
                    zzo.zzaAs.set(false);
                    i2 = 10436;
                    break;
                default:
                    i2 = 39789;
                    break;
            }
            ((NotificationManager) context.getSystemService("notification")).notify(i2, build);
        } else if (i == 6) {
            Log.w("GoogleApiAvailability", "Missing resolution for ConnectionResult.RESOLUTION_REQUIRED. Call GoogleApiAvailability#showErrorNotification(Context, ConnectionResult) instead.");
        }
    }

    public static boolean zza$63824b53(Activity activity, zzbdt zzbdt, int i, OnCancelListener onCancelListener) {
        Dialog zza = zza((Context) activity, i, zzt.zza$53204fe5(zzbdt, zze.zza(activity, i, "d")), onCancelListener);
        if (zza == null) {
            return false;
        }
        zza(activity, zza, GooglePlayServicesUtil.GMS_ERROR_DIALOG, onCancelListener);
        return true;
    }

    public final PendingIntent getErrorResolutionPendingIntent(Context context, int i, int i2) {
        return super.getErrorResolutionPendingIntent(context, i, i2);
    }

    public final String getErrorString(int i) {
        return super.getErrorString(i);
    }

    public final int isGooglePlayServicesAvailable(Context context) {
        return super.isGooglePlayServicesAvailable(context);
    }

    public final boolean isUserResolvableError(int i) {
        return super.isUserResolvableError(i);
    }

    public final boolean showErrorDialogFragment(Activity activity, int i, int i2, OnCancelListener onCancelListener) {
        Dialog errorDialog = getErrorDialog(activity, i, i2, onCancelListener);
        if (errorDialog == null) {
            return false;
        }
        zza(activity, errorDialog, GooglePlayServicesUtil.GMS_ERROR_DIALOG, onCancelListener);
        return true;
    }

    public final void showErrorNotification(Context context, int i) {
        zza$53da80fe(context, i, zze.zza(context, i, 0, "n"));
    }

    public final boolean zza(Context context, ConnectionResult connectionResult, int i) {
        PendingIntent resolution = connectionResult.hasResolution() ? connectionResult.getResolution() : super.getErrorResolutionPendingIntent(context, connectionResult.getErrorCode(), 0);
        if (resolution == null) {
            return false;
        }
        zza$53da80fe(context, connectionResult.getErrorCode(), GoogleApiActivity.zza(context, resolution, i));
        return true;
    }

    final void zzar(Context context) {
        new zza(this, context).sendEmptyMessageDelayed(1, 120000);
    }
}
