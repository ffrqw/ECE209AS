package com.rachio.iro.async.command;

import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.device.Device;

public class FetchDeviceCommand extends CommandThatMayNeedToPullADevice<Device> {
    private final String mDeviceId;
    private final FetchDeviceListener mListener;

    public interface FetchDeviceListener {
        void onDeviceLoaded(Device device);
    }

    protected final /* bridge */ /* synthetic */ void handleResult(Object obj) {
        this.mListener.onDeviceLoaded((Device) obj);
    }

    public FetchDeviceCommand(FetchDeviceListener listener, String deviceId) {
        if (listener == null || deviceId == null) {
            throw new IllegalArgumentException("deviceId cannot be null");
        }
        this.mListener = listener;
        this.mDeviceId = deviceId;
        BaseCommand.component(listener).inject(this);
    }

    protected final /* bridge */ /* synthetic */ Object loadResult() {
        this.database.lock();
        ModelObject fetchDevice = fetchDevice(this.mDeviceId);
        if (fetchDevice != null) {
            this.database.refresh(fetchDevice);
            if (fetchDevice.scheduleExecution != null && fetchDevice.scheduleExecution.isRunning()) {
                fetchDevice.getScheduleRuleById(fetchDevice.scheduleExecution.scheduleRuleId);
                fetchDevice.scheduleExecution.getZoneEvents(this.database);
            }
            if (fetchDevice.zones == null || fetchDevice.zones.size() == 0) {
                this.database.refresh(fetchDevice);
            }
        }
        this.database.unlock();
        return fetchDevice;
    }
}
