package com.rachio.iro.async.command;

import com.rachio.iro.cloud.PushPull;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.user.User;

public abstract class CommandThatMayNeedToPullADevice<T> extends BaseCommand<T> {
    protected final Device fetchDevice(String id) {
        Device device = (Device) this.database.find(Device.class, id);
        if (device != null) {
            return device;
        }
        User loggedInUser = User.getLoggedInUser(this.database, this.prefsWrapper);
        if (loggedInUser != null) {
            if (loggedInUser.deviceMap.containsKey(id)) {
                return (Device) PushPull.pullEntityAndSave(this.database, this.restClient, Device.class, id, loggedInUser.id);
            }
            if (loggedInUser.managedDeviceMap.containsKey(id)) {
                return (Device) PushPull.pullEntityAndSave(this.database, this.restClient, Device.class, id, loggedInUser.id, true);
            }
        }
        return null;
    }
}
