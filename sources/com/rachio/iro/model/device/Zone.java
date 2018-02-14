package com.rachio.iro.model.device;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.rachio.iro.fcm.EventHandler.DeltaApplyOptions;
import com.rachio.iro.model.DeviceChildModelObject;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.TransmittableView;
import com.rachio.iro.model.WateringScheduleType;
import com.rachio.iro.model.annotation.DatabaseOptions;
import com.rachio.iro.model.annotation.RestClientOptions;
import com.rachio.iro.model.db.Database;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

@DatabaseTable(tableName = "Zone")
@DatabaseOptions(broadcast = "com.rachio.iro.intent.DATABASE_ZONE_DATA_CHANGED", parent = Device.class, parentcol = "device_id")
@RestClientOptions(path = "/1/zone/model")
@DeltaApplyOptions(ignoredProperties = {"wateringAdjustment", "imageKey", "lastWateredDuration", "electricalCurrent"})
@JsonIgnoreProperties({"status", "externalName", "runtime", "adjustedManagementAllowedDepletion", "saturatedDepthOfWater", "maxRuntime", "saturationAllowance", "scheduleDataModified", "wateringAdjustment", "fixedRuntime", "lastWateredDuration", "electricalCurrent"})
public class Zone extends DeviceChildModelObject<Zone> implements Serializable, Comparable<Zone> {
    public static final String BROADCAST_DATABASE_ZONE_DATA_CHANGED = "com.rachio.iro.intent.DATABASE_ZONE_DATA_CHANGED";
    private static final long serialVersionUID = 1;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public double availableWater;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public double cropCoefficient;
    @JsonView({TransmittableView.class})
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public PropertyIdHolder customCrop;
    @JsonView({TransmittableView.class})
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public PropertyIdHolder customNozzle;
    @JsonView({TransmittableView.class})
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public PropertyIdHolder customShade;
    @JsonView({TransmittableView.class})
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public PropertyIdHolder customSlope;
    @JsonView({TransmittableView.class})
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public PropertyIdHolder customSoil;
    @DatabaseField
    public double depthOfWater;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public double efficiency;
    public int electricalCurrentOff;
    public int electricalCurrentOn;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public boolean enabled;
    @DatabaseField
    public String imageUrl;
    @JsonIgnore
    @DatabaseField
    public boolean isInFlexSchedule;
    @DatabaseField(dataType = DataType.DATE_LONG)
    public Date lastWateredDate;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public double managementAllowedDepletion;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public String name;
    @JsonIgnore
    @DatabaseField(dataType = DataType.DATE_LONG)
    public Date nextWaterDate = null;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public double rootZoneDepth;
    @DatabaseField
    public int runtimeNoMultiplier;
    @JsonIgnore
    @DatabaseField
    public WateringScheduleType scheduledWateringTypes;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public float waterAdjustment;
    public int wateringAdjustment;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public TreeMap<Integer, Integer> wateringAdjustmentRuntimes;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public double yardAreaSquareFeet;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public int zoneNumber;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class PropertyIdHolder implements Serializable {
        private static final long serialVersionUID = 1;
        @JsonView({TransmittableView.class})
        public String id;
    }

    public boolean equals(Object o) {
        if (o instanceof Zone) {
            return this.id.equals(((Zone) o).id);
        }
        return super.equals(o);
    }

    public int compareTo(Zone another) {
        return Integer.valueOf(this.zoneNumber).compareTo(Integer.valueOf(another.zoneNumber));
    }

    public static List<Zone> findEnabledZonesForDevice(Database database, String deviceId) {
        try {
            return database.getDatabaseHelper().getDao(Zone.class, (Object) deviceId).queryBuilder().where().eq(DeviceChildModelObject.COL_DEVICEID, deviceId).and().eq("enabled", Boolean.valueOf(true)).query();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        return this.name;
    }

    public Zone getTransmittableVersion() {
        Zone clone = (Zone) ModelObject.transmittableClone(Zone.class, this);
        clone.device = this.device;
        return clone;
    }
}
