package com.rachio.iro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.rachio.iro.cloud.RestClient;

public class BootCompleteReceiver extends BroadcastReceiver {
    PrefsWrapper prefsWrapper;
    RestClient restClient;

    public void onReceive(Context context, Intent intent) {
        IroApplication.get(context).component().inject(this);
        if (this.prefsWrapper.getLoggedInUserId() != null) {
            this.restClient.setUserHeaders(this.prefsWrapper.getLoggedInUserCredentials().getSessionKeys());
        }
    }
}
