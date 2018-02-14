package com.rachio.iro.binder;

public class DeviceUpdatesBinder extends BaseEventBinder {
    protected final String getTitle() {
        return "Device Updates";
    }

    protected final String getTopic() {
        return "DEVICE";
    }
}
