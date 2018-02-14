package com.instabug.library;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import com.instabug.library.util.InstabugSDKLogger;

public class InstabugNetworkReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo() != null) {
            InstabugSDKLogger.d(this, "Network state changed, checking local cache");
            context.getPackageManager().setComponentEnabledSetting(new ComponentName(context, InstabugNetworkReceiver.class), 2, 1);
            context.startService(new Intent(context, InstabugSessionUploaderService.class));
            context.startService(new Intent(context, InstabugIssueUploaderService.class));
            context.startService(new Intent(context, InstabugMessageUploaderService.class));
            context.startService(new Intent(context, InstabugFeaturesFetcherService.class));
        }
    }
}
