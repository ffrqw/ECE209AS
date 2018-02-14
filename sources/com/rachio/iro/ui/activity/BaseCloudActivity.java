package com.rachio.iro.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

public class BaseCloudActivity extends BaseActivity {
    private static final String TAG = BaseCloudActivity.class.getCanonicalName();
    private static final IntentFilter connectivityChangeFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    private BroadcastReceiver connectivityChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d(BaseCloudActivity.TAG, "connectivity state has changed, " + (BaseCloudActivity.this.isOnline ? "online" : "offline"));
            BaseCloudActivity.this.isOnline;
            BaseCloudActivity.onConnectStatusChanged$1385ff();
        }
    };
    private ConnectivityManager connectivityManager;
    private boolean isOnline = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.connectivityManager = (ConnectivityManager) getSystemService("connectivity");
    }

    protected void onResume() {
        boolean z;
        super.onResume();
        NetworkInfo activeNetworkInfo = this.connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnectedOrConnecting()) {
            z = false;
        } else {
            z = true;
        }
        this.isOnline = z;
        registerReceiver(this.connectivityChangeReceiver, connectivityChangeFilter);
    }

    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(this.connectivityChangeReceiver);
        } catch (IllegalArgumentException e) {
        }
    }

    protected static void onConnectStatusChanged$1385ff() {
    }
}
