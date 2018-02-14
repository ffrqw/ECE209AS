package com.rachio.iro.ui.activity.user;

import android.content.Intent;
import android.os.Bundle;
import com.rachio.iro.ui.activity.BaseCloudActivity;

public class BaseLoginActivity extends BaseCloudActivity {
    protected String deviceId;
    protected boolean goToHistory;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.goToHistory = extras.getBoolean("gotohistory", false);
            if (this.goToHistory) {
                this.deviceId = extras.getString("DEVICEID");
            }
        }
    }

    protected final void addExtrasToIntent(Intent intent) {
        intent.putExtra("gotohistory", this.goToHistory);
        intent.putExtra("DEVICEID", this.deviceId);
    }
}
