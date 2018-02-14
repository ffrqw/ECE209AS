package com.rachio.iro.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.j256.ormlite.field.DatabaseField;
import com.rachio.iro.model.annotation.DatabaseOptions;
import com.rachio.iro.model.db.Database.DatabasePreSaveCheckException;
import com.rachio.iro.model.db.DatabaseObject;
import com.rachio.iro.model.device.Device;
import java.io.Serializable;

@DatabaseOptions(parent = Device.class, parentcol = "device_id")
public class DeviceChildModelObject<T extends DeviceChildModelObject> extends DatabaseObject<T> implements Serializable {
    public static final String COL_DEVICEID = "device_id";
    private static final long serialVersionUID = 1;
    @JsonIgnore
    @DatabaseField(canBeNull = false, columnName = "device_id", foreign = true)
    public Device device;

    @JsonProperty("device")
    @JsonView({TransmittableView.class})
    public PostId getDevice() {
        if (this.device != null) {
            return new PostId(this.device.id);
        }
        return null;
    }

    public void doPreSaveSanityCheck() throws DatabasePreSaveCheckException {
        if (this.device == null) {
            throw new DatabasePreSaveCheckException();
        }
    }

    public void setParent(DatabaseObject parent) {
        this.device = (Device) parent;
    }

    public DatabaseObject getParent() {
        return this.device;
    }
}
