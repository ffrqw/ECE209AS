package com.rachio.iro.model.device;

import android.text.TextUtils;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonView;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.rachio.iro.fcm.EventHandler.DeltaApplyOptions;
import com.rachio.iro.model.IroProperties;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.TransmittableView;
import com.rachio.iro.model.annotation.DatabaseOptions;
import com.rachio.iro.model.annotation.RestClientOptions;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.model.db.DatabaseObject;
import com.rachio.iro.model.device.ShallowDevice.RoughStatus;
import com.rachio.iro.model.schedule.ScheduleExecution;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.model.schedule.ZoneInfo;
import com.rachio.iro.model.user.User;
import com.rachio.iro.utils.StringUtils;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;

@DatabaseTable(tableName = "Device")
@DatabaseOptions(altparentcol = "manager_user_id", broadcast = "com.rachio.iro.intent.DATABASE_DEVICE_DATA_CHANGED", descendants = {Zone.class, ScheduleRule.class}, parent = User.class, parentcol = "user_id")
@RestClientOptions(path = "/1/device/model")
@DeltaApplyOptions(ignoredProperties = {"scheduleGroup", "heartbeatDate"}, silentProperties = {"lastUpdateDate"})
@JsonIgnoreProperties({"apiKey", "secretKey", "nestProtectIntegration", "webhooks", "paused", "utcOffset", "wateringSchedule", "on", "scheduleGroup", "scheduleModeType", "externalUrl", "pin", "heartbeatDate"})
public class Device extends ShallowDevice<Device> implements Serializable {
    public static final String BROADCAST_DATABASE_DEVICE_DATA_CHANGED = "com.rachio.iro.intent.DATABASE_DEVICE_DATA_CHANGED";
    private static final String TAG = Device.class.getName();
    private static final long serialVersionUID = 1;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public String address;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public AttachedSensor[] attachedSensors;
    @DatabaseField
    public double elevation;
    public boolean existed;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public boolean includeAllWeatherStations;
    public String lastKnownFirmwareVersion;
    public String lastKnownWifiFirmwareVersion;
    @DatabaseField
    public String macAddress;
    @JsonIgnore
    @DatabaseField(columnName = "manager_user_id", foreign = true)
    public User managerUser = null;
    @JsonIgnore
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public ArrayList<Manager> managers;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public boolean masterValve;
    @JsonIgnore
    public boolean preferredHasPrecip;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public String preferredStation;
    @JsonIgnore
    public double preferredStationLatitude;
    @JsonIgnore
    public double preferredStationLongitude;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public boolean rainSensor;
    @JsonIgnore
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public ScheduleExecution scheduleExecution;
    public String scheduleGroupId;
    @JsonIgnore
    private Map<String, ScheduleRule> scheduleRuleMap = null;
    @ForeignCollectionField(orderAscending = true, orderColumnName = "createDate")
    public ForeignCollection<ScheduleRule> scheduleRules;
    @DatabaseField
    public String serialNumber;
    public Date statusNotificationDate;
    @DatabaseField(canBeNull = false)
    public String timeZone;
    @JsonIgnore
    public List<ScheduleRule> transientScheduleRules;
    @JsonIgnore
    public List<Zone> transientZones;
    @JsonIgnore
    @DatabaseField(columnName = "user_id", foreign = true)
    public User user = null;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public boolean waterHammer;
    private HashMap<String, Zone> zoneMap;
    @ForeignCollectionField(orderAscending = true, orderColumnName = "zoneNumber")
    public ForeignCollection<Zone> zones;

    @RestClientOptions(path = "/1/attached_sensor/model")
    public static class AttachedSensor extends ModelObject<AttachedSensor> implements Serializable {
        private static final long serialVersionUID = 1;
        public long createDate;
        @JsonView({TransmittableView.class})
        public boolean enabled;
        @JsonView({TransmittableView.class})
        public String id;
        @JsonView({TransmittableView.class})
        public float kfactor;
        public long lastUpdateDate;
        @JsonView({TransmittableView.class})
        public String make;
        @JsonView({TransmittableView.class})
        public String model;
        @JsonView({TransmittableView.class})
        public int physicalSensorId;
        @JsonView({TransmittableView.class})
        public float sensorOffset;
        @JsonView({TransmittableView.class})
        public Type type;

        public enum Type {
            RAIN,
            FLOW
        }

        public AttachedSensor getTransmittableVersion() {
            return (AttachedSensor) ModelObject.transmittableClone(AttachedSensor.class, this);
        }
    }

    public static class Manager implements Serializable {
        private static final long serialVersionUID = 1;
        public String email;
        public String fullName;
        public String id;
        public String username;
    }

    @JsonSetter
    public void setManagers(ArrayList<Manager> managers) {
        this.managers = managers;
    }

    @JsonGetter("zones")
    public synchronized Collection<Zone> getZones() {
        Collection<Zone> collection;
        if (this.zones != null) {
            collection = this.zones;
        } else {
            collection = this.transientZones;
        }
        return collection;
    }

    @JsonIgnore
    public List<Zone> getZonesAsList() {
        return getZonesAsList(false, null);
    }

    @JsonIgnore
    public synchronized List<Zone> getZonesAsList(boolean filterZonesInFlex, String exclude) {
        List<Zone> zonesAsList;
        Collection<Zone> zones = getZones();
        zonesAsList = null;
        if (zones != null) {
            zonesAsList = new ArrayList(zones);
            if (filterZonesInFlex) {
                Iterator<Zone> iterator = zonesAsList.iterator();
                while (iterator.hasNext()) {
                    if (isZoneInFlexRule(((Zone) iterator.next()).id, false, exclude)) {
                        iterator.remove();
                    }
                }
            }
        }
        return zonesAsList;
    }

    public List<Zone> getEnabledZones() {
        return getEnabledZones(false, null);
    }

    @JsonIgnore
    public synchronized List<Zone> getEnabledZones(boolean filterZonesInFlex, String exclude) {
        List<Zone> zones;
        zones = getZonesAsList(filterZonesInFlex, exclude);
        if (zones != null) {
            Iterator<Zone> iterator = zones.iterator();
            while (iterator.hasNext()) {
                if (!((Zone) iterator.next()).enabled) {
                    iterator.remove();
                }
            }
        }
        return zones;
    }

    @JsonSetter("zones")
    public void setZones(Collection<Zone> zones) {
        if (zones == null) {
            this.transientZones = null;
            this.zones = null;
            return;
        }
        this.zones = null;
        this.transientZones = new ArrayList(zones);
    }

    private synchronized void generateZoneMap() {
        Collection<Zone> zones = getZones();
        if (zones != null && ((this.zoneMap == null || !(this.zoneMap == null || this.zoneMap.size() == zones.size())) && zones != null)) {
            HashMap<String, Zone> map = new HashMap();
            for (Zone z : zones) {
                map.put(z.id, z);
            }
            this.zoneMap = map;
        }
    }

    private synchronized void generateScheduleRuleMap() {
        if (this.scheduleRuleMap == null) {
            this.scheduleRuleMap = new TreeMap();
            for (ScheduleRule sr : getAllScheduleRules()) {
                this.scheduleRuleMap.put(sr.id, sr);
            }
        }
    }

    public synchronized void setScheduleRules(Collection<ScheduleRule> scheduleRules) {
        this.scheduleRules = null;
        if (scheduleRules != null) {
            if (this.transientScheduleRules == null) {
                this.transientScheduleRules = new ArrayList();
            }
            this.transientScheduleRules.addAll(scheduleRules);
        }
    }

    public synchronized void setFlexScheduleRules(Collection<ScheduleRule> scheduleRules) {
        this.scheduleRules = null;
        if (scheduleRules != null) {
            if (this.transientScheduleRules == null) {
                this.transientScheduleRules = new ArrayList();
            }
            this.transientScheduleRules.addAll(scheduleRules);
        }
    }

    public boolean equals(Object o) {
        if (o instanceof Device) {
            return ((Device) o).id.equals(this.id);
        }
        return super.equals(o);
    }

    public boolean isValid() {
        return !TextUtils.isEmpty(this.id);
    }

    @JsonIgnore
    public boolean isVirtual() {
        return (this.serialNumber != null && this.serialNumber.startsWith("VIRO")) || StringUtils.equals(this.serialNumber, "VIRTUAL");
    }

    @JsonIgnore
    public List<ScheduleRule> getAllScheduleRules() {
        if (this.transientScheduleRules != null) {
            return this.transientScheduleRules;
        }
        if (this.scheduleRules != null) {
            return new ArrayList(this.scheduleRules);
        }
        return null;
    }

    @JsonIgnore
    public boolean isShared() {
        return this.managers != null && this.managers.size() > 0;
    }

    @JsonIgnore
    public boolean belongsToSomeoneElse() {
        return this.managerUser != null;
    }

    @JsonIgnore
    public Date getLastRunDate() {
        Date lastestRun = null;
        Collection<Zone> zones = getZones();
        if (zones != null) {
            for (Zone z : zones) {
                if (lastestRun == null || (z.lastWateredDate != null && z.lastWateredDate.after(lastestRun))) {
                    lastestRun = z.lastWateredDate;
                }
            }
        }
        return lastestRun;
    }

    @JsonIgnore
    public boolean isZoneInFlexSchedule(int zoneNumber) {
        Collection<ScheduleRule> rules = getAllScheduleRules();
        if (rules != null) {
            for (ScheduleRule fsr : rules) {
                if (fsr.isFlex()) {
                    Iterator it = fsr.zones.iterator();
                    while (it.hasNext()) {
                        if (((ZoneInfo) it.next()).zoneNumber == zoneNumber) {
                            return true;
                        }
                    }
                    continue;
                }
            }
        }
        return false;
    }

    @JsonIgnore
    public int getRainDelayDays() {
        if (isInRainDelay()) {
            return (int) ((this.rainDelayExpirationDate.getTime() - this.rainDelayStartDate.getTime()) / 86400000);
        }
        return 0;
    }

    @JsonIgnore
    public Map<String, Zone> getZonesMap() {
        generateZoneMap();
        return this.zoneMap;
    }

    @JsonIgnore
    public User getLocalUser() {
        return this.managerUser != null ? this.managerUser : this.user;
    }

    public ScheduleRule getScheduleRuleById(String id) {
        if (id == null) {
            return null;
        }
        generateScheduleRuleMap();
        return (ScheduleRule) this.scheduleRuleMap.get(id);
    }

    @JsonIgnore
    private int[] countRules() {
        int[] result = new int[]{0, 0};
        for (ScheduleRule rule : getAllScheduleRules()) {
            if (rule.isFlex()) {
                result[1] = result[1] + 1;
            } else {
                result[0] = result[0] + 1;
            }
        }
        return result;
    }

    private boolean hasEnabledZonesNotInFlex() {
        for (Zone z : getZones()) {
            if (z.enabled && !isZoneInFlexRule(z.id, false, null)) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public boolean canCreateNewFixedSchedule(IroProperties iroProperties) {
        if (!this.model.isGen2 || countRules()[0] < iroProperties.schedule.gen2FixedScheduleLimit) {
            return true;
        }
        return false;
    }

    @JsonIgnore
    public boolean canCreateNewFlexSchedule(IroProperties iroProperties) {
        int[] ruleCount = countRules();
        if (!this.model.isGen2) {
            return hasEnabledZonesNotInFlex();
        }
        if (ruleCount[0] >= iroProperties.schedule.gen2FixedScheduleLimit || ruleCount[1] >= iroProperties.schedule.gen2FlexScheduleLimit || !hasEnabledZonesNotInFlex()) {
            return false;
        }
        return true;
    }

    public synchronized void saveTransients(Database database) throws SQLException {
        super.saveTransients(database);
        if (this.transientZones != null) {
            for (Zone z : this.transientZones) {
                z.device = this;
                database.save(z, false, true, false);
            }
            if (this.zones == null) {
                database.refresh(this);
            } else {
                this.zones.refreshCollection();
            }
        } else {
            database.deleteForParent(Zone.class, this.id);
        }
        this.transientZones = null;
        if (this.transientScheduleRules != null) {
            for (ScheduleRule s : this.transientScheduleRules) {
                s.device = this;
                database.save(s, false, true, false);
            }
            if (this.scheduleRules == null) {
                database.refresh(this);
            } else {
                this.scheduleRules.refreshCollection();
            }
        } else {
            database.deleteForParent(ScheduleRule.class, this.id);
        }
        this.transientScheduleRules = null;
    }

    public void preSave() {
        super.preSave();
        if (this.user == null && this.managerUser == null) {
            throw new IllegalStateException("user or managerUser must be set");
        } else if (this.user != null && this.managerUser != null) {
            throw new IllegalStateException("only one of user or managerUser can be set");
        } else if (this.attachedSensors != null) {
            Arrays.sort(this.attachedSensors, new Comparator<AttachedSensor>() {
                public int compare(AttachedSensor lhs, AttachedSensor rhs) {
                    return Double.valueOf((double) lhs.physicalSensorId).compareTo(Double.valueOf((double) rhs.physicalSensorId));
                }
            });
        }
    }

    public void pruneCollections(Database database) throws SQLException {
        Set<String> ids;
        super.pruneCollections(database);
        if (this.transientZones == null || this.transientZones.size() <= 0) {
            database.deleteForParent(Zone.class, this.id);
        } else {
            ids = new TreeSet();
            for (Zone tz : this.transientZones) {
                ids.add(tz.id);
            }
            Set<String> savedZones = database.findIdsForParent(Zone.class, this.id);
            if (savedZones != null) {
                for (String z : savedZones) {
                    if (!ids.contains(z)) {
                        database.deleteById(Zone.class, z);
                    }
                }
            }
        }
        if (this.transientScheduleRules == null || this.transientScheduleRules.size() <= 0) {
            database.deleteForParent(ScheduleRule.class, this.id);
            return;
        }
        ids = new TreeSet();
        for (ScheduleRule tz2 : this.transientScheduleRules) {
            ids.add(tz2.id);
        }
        Set<String> savedRules = database.findIdsForParent(ScheduleRule.class, this.id);
        if (savedRules != null) {
            for (String sr : savedRules) {
                if (!ids.contains(sr)) {
                    database.deleteById(ScheduleRule.class, sr);
                }
            }
        }
    }

    public void setParent(DatabaseObject parent) {
        super.setParent(parent);
        this.user = (User) parent;
    }

    public void setAltParent(DatabaseObject parent) {
        super.setAltParent(parent);
        this.managerUser = (User) parent;
    }

    public Device getTransmittableVersion() {
        Device clone = (Device) ModelObject.transmittableClone(Device.class, this);
        clone.user = this.user;
        clone.managerUser = this.managerUser;
        return clone;
    }

    @JsonIgnore
    public boolean isZoneInFlexRule(String zoneId, boolean filterDisabledRules, String excludeRule) {
        Collection<ScheduleRule> scheduleRules = getAllScheduleRules();
        if (scheduleRules != null) {
            for (ScheduleRule sr : scheduleRules) {
                if (!StringUtils.equals(excludeRule, sr.id) && sr.isFlex()) {
                    if (!filterDisabledRules || sr.enabled) {
                        Iterator it = sr.zones.iterator();
                        while (it.hasNext()) {
                            if (StringUtils.equals(zoneId, ((ZoneInfo) it.next()).zoneId)) {
                                return true;
                            }
                        }
                        continue;
                    }
                }
            }
        }
        return false;
    }

    @JsonIgnore
    public TimeZone getTimeZoneAsTimeZone() {
        return TimeZone.getTimeZone(this.timeZone);
    }

    @JsonIgnore
    public boolean isWatering() {
        return this.scheduleExecution != null && this.scheduleExecution.isRunning();
    }

    @JsonSetter
    public void setProcessingScheduleExecution(ScheduleExecution scheduleExecution) {
        this.scheduleExecution = scheduleExecution;
    }

    public RoughStatus getRoughStatus() {
        if (isWatering()) {
            return RoughStatus.WATERING;
        }
        return super.getRoughStatus();
    }
}
