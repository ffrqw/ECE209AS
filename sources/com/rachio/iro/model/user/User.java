package com.rachio.iro.model.user;

import android.content.Context;
import android.text.TextUtils;
import com.crashlytics.android.Crashlytics;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.fcm.EventHandler.DeltaApplyOptions;
import com.rachio.iro.model.Event;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.ResponseCacheItem;
import com.rachio.iro.model.TransmittableView;
import com.rachio.iro.model.annotation.DatabaseOptions;
import com.rachio.iro.model.annotation.RestClientOptions;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.model.db.DatabaseObject;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.device.ShallowDevice;
import com.rachio.iro.model.zoneproperties.Crop;
import com.rachio.iro.model.zoneproperties.Nozzle;
import com.rachio.iro.model.zoneproperties.Shade;
import com.rachio.iro.model.zoneproperties.Slope;
import com.rachio.iro.model.zoneproperties.Soil;
import com.rachio.iro.utils.StringUtils;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

@DeltaApplyOptions(ignoredProperties = {"password"})
@DatabaseTable(tableName = "User")
@DatabaseOptions(descendants = {Device.class})
@RestClientOptions(path = "/1/person/model/", shallow = true)
@JsonIgnoreProperties({"oneTimePersons", "errors"})
public class User extends DatabaseObject implements Serializable {
    private static final long serialVersionUID = 1;
    public final TreeMap<String, ShallowDevice> allDeviceMap = new TreeMap();
    private final ArrayList<Device> allDevices = new ArrayList();
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public Crop[] crops;
    public final TreeMap<String, ShallowDevice> deviceMap = new TreeMap();
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public ArrayList<ShallowDevice> devices;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public DisplayUnit displayUnit;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public String email;
    @DatabaseField
    public boolean enabled;
    @DatabaseField
    public String externalPlanId;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public String fullName;
    public Date lastLoginDate;
    public final TreeMap<String, ShallowDevice> managedDeviceMap = new TreeMap();
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public ArrayList<ShallowDevice> managedDevices;
    @DatabaseField
    public String messagingAuthKey;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public Nozzle[] nozzles;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public Role[] roles;
    @JsonView({TransmittableView.class})
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public Setting[] settingNames;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public Shade[] shades;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public Slope[] slopes;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public Soil[] soils;
    @DatabaseField
    public String username;

    public enum DisplayUnit {
        US,
        METRIC
    }

    public static class Role implements Serializable {
        private static final long serialVersionUID = 1;
        public Date createDate;
        public String id;
        public Date lastUpdateDate;
        public String name;
        public Permission[] permissions;

        public static class Permission implements Serializable {
            private static final long serialVersionUID = 1;
            public Date createDate;
            public String id;
            public Date lastUpdateDate;
            public String name;

            public enum Name {
                BASIC,
                READ_ONLY
            }
        }
    }

    public enum Setting {
        DISABLE_ALL,
        RAIN_DELAY_EMAIL,
        WATER_BUDGET_EMAIL,
        WEATHER_INTELLIGENCE_NOTIFICATION,
        WATER_BUDGET_NOTIFICATION,
        DEVICE_STATUS_NOTIFICATION,
        SCHEDULE_STATUS_NOTIFICATION,
        RAIN_SENSOR_NOTIFICATION
    }

    public void setId(String id) {
        if (TextUtils.isEmpty(id)) {
            throw new IllegalArgumentException("user id cannot be null or empty");
        }
        this.id = id;
    }

    public boolean haveDevices() {
        return this.devices.size() > 0 || this.managedDevices.size() > 0;
    }

    public static User getLoggedInUser(Database database, PrefsWrapper prefs) {
        String loggedInUserId = prefs.getLoggedInUserId();
        if (loggedInUserId == null) {
            return null;
        }
        return (User) database.find(User.class, loggedInUserId);
    }

    public Crop getCropById(String cropId) {
        if (cropId == null) {
            throw new IllegalArgumentException();
        }
        if (this.crops != null) {
            for (Crop c : this.crops) {
                if (StringUtils.equals(c.id, cropId)) {
                    return c;
                }
            }
        }
        return null;
    }

    public Soil getSoilById(String soilId) {
        if (soilId == null) {
            throw new IllegalArgumentException();
        }
        if (this.soils != null) {
            for (Soil s : this.soils) {
                if (StringUtils.equals(s.id, soilId)) {
                    return s;
                }
            }
        }
        return null;
    }

    public Shade getShadeById(String shadeId) {
        if (shadeId == null) {
            throw new IllegalArgumentException();
        }
        if (this.shades != null) {
            for (Shade s : this.shades) {
                if (StringUtils.equals(s.id, shadeId)) {
                    return s;
                }
            }
        }
        return null;
    }

    public Slope getSlopeById(String slopeId) {
        if (slopeId == null) {
            throw new IllegalArgumentException();
        }
        if (this.slopes != null) {
            for (Slope s : this.slopes) {
                if (StringUtils.equals(s.id, slopeId)) {
                    return s;
                }
            }
        }
        return null;
    }

    public Nozzle getNozzleById(String nozzleId) {
        if (nozzleId == null) {
            throw new IllegalArgumentException();
        }
        if (this.nozzles != null) {
            for (Nozzle n : this.nozzles) {
                if (StringUtils.equals(n.id, nozzleId)) {
                    return n;
                }
            }
        }
        return null;
    }

    @JsonIgnore
    private boolean hasSetting(Setting setting) {
        for (Setting s : this.settingNames) {
            if (s == setting) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public boolean areNotificationsDisabled() {
        return hasSetting(Setting.DISABLE_ALL);
    }

    public static void logout(Context context, Database database, PrefsWrapper prefs, RestClient restClient) {
        try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
            FirebaseInstanceId.getInstance().getToken();
        } catch (IOException e) {
        }
        Crashlytics.setUserIdentifier(null);
        Crashlytics.setUserName(null);
        DatabaseObject user = getLoggedInUser(database, prefs);
        if (user != null) {
            database.delete(user);
        }
        database.deleteAll(Event.class);
        database.deleteAll(ResponseCacheItem.class);
        prefs.clear();
        restClient.onLogout();
    }

    public boolean hasReadOnlyRole() {
        if (this.roles == null) {
            return false;
        }
        for (Role r : this.roles) {
            if (StringUtils.equals(r.name, "READ_ONLY")) {
                return true;
            }
        }
        return false;
    }

    public void pruneCollections(Database database) throws SQLException {
        Set<String> ids;
        Iterator it;
        super.pruneCollections(database);
        if (this.devices != null) {
            ids = new TreeSet();
            it = this.devices.iterator();
            while (it.hasNext()) {
                ids.add(((ShallowDevice) it.next()).id);
            }
            Set<String> savedDevices = database.findIdsForParent(Device.class, this.id);
            if (savedDevices != null) {
                for (String sd : savedDevices) {
                    if (!ids.contains(sd)) {
                        database.deleteById(Device.class, sd);
                    }
                }
            }
        }
        if (this.managedDevices != null) {
            ids = new TreeSet();
            it = this.managedDevices.iterator();
            while (it.hasNext()) {
                ids.add(((ShallowDevice) it.next()).id);
            }
            Set<String> savedManagedDevices = database.findIdsForAltParent(Device.class, this.id);
            if (savedManagedDevices != null) {
                for (String sd2 : savedManagedDevices) {
                    if (!ids.contains(sd2)) {
                        database.deleteById(Device.class, sd2);
                    }
                }
            }
        }
    }

    public void removeDevice(Database database, Device device) {
        database.delete((DatabaseObject) device);
    }

    public User getTransmittableVersion() {
        return (User) ModelObject.transmittableClone(User.class, this);
    }

    public void preSave() {
        super.preSave();
        if (this.nozzles != null) {
            Arrays.sort(this.nozzles, new Comparator<Nozzle>() {
                public int compare(Nozzle lhs, Nozzle rhs) {
                    return lhs.name.compareTo(rhs.name);
                }
            });
        }
    }

    public synchronized void createDeviceMap() {
        if (!(this.deviceMap.size() == this.devices.size() && this.managedDeviceMap.size() == this.managedDevices.size())) {
            ShallowDevice d;
            this.deviceMap.clear();
            this.managedDeviceMap.clear();
            this.allDeviceMap.clear();
            Iterator it = this.devices.iterator();
            while (it.hasNext()) {
                d = (ShallowDevice) it.next();
                this.deviceMap.put(d.id, d);
                this.allDeviceMap.put(d.id, d);
            }
            it = this.managedDevices.iterator();
            while (it.hasNext()) {
                d = (ShallowDevice) it.next();
                this.managedDeviceMap.put(d.id, d);
                this.allDeviceMap.put(d.id, d);
            }
        }
    }

    public ShallowDevice getDeviceById(String id) {
        createDeviceMap();
        return (ShallowDevice) this.allDeviceMap.get(id);
    }

    public boolean deviceBelongsToSomeoneElse(String id) {
        createDeviceMap();
        return this.managedDeviceMap.containsKey(id);
    }

    public String getSelectedDeviceId(PrefsWrapper prefsWrapper) {
        createDeviceMap();
        String selectedId = prefsWrapper.getSelectedDeviceId();
        if (selectedId != null && this.allDeviceMap.containsKey(selectedId)) {
            return selectedId;
        }
        if (this.devices.size() > 0) {
            selectedId = ((ShallowDevice) this.devices.iterator().next()).id;
            prefsWrapper.setSelectedDeviceId(selectedId);
            return selectedId;
        } else if (this.managedDevices.size() <= 0) {
            return selectedId;
        } else {
            selectedId = ((ShallowDevice) this.managedDevices.iterator().next()).id;
            prefsWrapper.setSelectedDeviceId(selectedId);
            return selectedId;
        }
    }

    public List<ShallowDevice> getAllShadowDevices() {
        List<ShallowDevice> allDevices = new ArrayList();
        if (this.devices != null) {
            allDevices.addAll(this.devices);
        }
        if (this.managedDevices != null) {
            allDevices.addAll(this.managedDevices);
        }
        return allDevices;
    }

    public boolean allDevicesBelongToUser() {
        return this.managedDevices == null || this.managedDevices.size() == 0;
    }
}
