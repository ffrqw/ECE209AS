package com.rachio.iro.ui.fragment.blinkup;

import com.rachio.iro.model.BirthDevice;
import com.rachio.iro.ui.activity.BlinkUpActivity;
import com.rachio.iro.ui.activity.BlinkUpActivity.BlinkUpView;

public class AddDeviceFragment extends BaseAddDeviceFragment {
    public final void onContinue(String name, String zip, boolean masterValve) {
        BirthDevice device = new BirthDevice();
        device.setName(name);
        device.setZip(zip);
        device.masterValve = Boolean.valueOf(masterValve);
        ((BlinkUpActivity) getActivity()).setBlinkUpDevice(device);
        ((BlinkUpActivity) getActivity()).moveToStep(BlinkUpView.WifiStart);
    }
}
