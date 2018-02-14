package com.rachio.iro.ui.fragment.dashboard;

import android.os.Bundle;
import com.rachio.iro.async.command.FetchDeviceCommand;
import com.rachio.iro.ui.activity.DashboardActivity.OnDeviceDataChangedListener;
import com.rachio.iro.ui.activity.DashboardActivity.OnSelectedDeviceChangedListener;

public abstract class BaseDeviceDashboardFragment extends DashboardFragment implements OnDeviceDataChangedListener, OnSelectedDeviceChangedListener {
    protected FetchDeviceCommand fetchDeviceCommand;
    protected String mDeviceId;

    protected final void initState(Bundle bundle) {
        if (bundle != null) {
            super.initState(bundle);
            this.mDeviceId = bundle.getString("DEVICEID");
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("DEVICEID", this.mDeviceId);
    }

    public void onSelectedDeviceChanged(String newDeviceId) {
        if (this.fetchDeviceCommand != null) {
            this.fetchDeviceCommand.isCancelled = true;
            this.fetchDeviceCommand = null;
        }
        this.mDeviceId = newDeviceId;
        onDeviceDataChanged(this.mDeviceId);
    }

    public static Bundle createArgs(String deviceId) {
        if (deviceId == null) {
            throw new IllegalArgumentException("device id cannot be nulll");
        }
        Bundle args = new Bundle();
        args.putString("DEVICEID", deviceId);
        return args;
    }
}
