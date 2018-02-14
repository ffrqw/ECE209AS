package com.rachio.iro.model;

import android.util.Log;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.rachio.iro.model.EventData.DeltaContainer;
import com.rachio.iro.model.annotation.DatabaseOptions;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.model.db.DatabaseObject;
import com.rachio.iro.model.user.User.DisplayUnit;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@DatabaseOptions
public class Event extends DatabaseObject {
    private static final String TAG = Event.class.getName();
    private static final long serialVersionUID = 1;
    @DatabaseField
    public Category category;
    @DatabaseField
    public String correlationId;
    @DatabaseField(canBeNull = false)
    public String deviceId;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public EventData[] eventDatas;
    public Map<String, EventData> eventDatasMap;
    @DatabaseField(dataType = DataType.DATE_LONG)
    public Date eventDate;
    public boolean hidden;
    @DatabaseField
    public String iconUrl;
    @DatabaseField
    public long sequence = -1;
    @DatabaseField
    public String shortSummary;
    @DatabaseField
    public SubType subType;
    @DatabaseField
    public String summary;
    @DatabaseField
    public String topic;
    @DatabaseField
    public Type type;
    @DatabaseField(canBeNull = false)
    public DisplayUnit units;

    public static final class Action {
        public long expiry;
        public String label;
        public String name;
        public String url;

        public final boolean isActive() {
            return this.expiry > System.currentTimeMillis();
        }
    }

    public enum Category {
        DEVICE,
        SCHEDULE,
        PERSON,
        WEATHER
    }

    public enum SubType {
        RAIN_DELAY_ON,
        RAIN_DELAY_OFF,
        SLEEP_MODE_OFF,
        SLEEP_MODE_ON,
        RECONNECT,
        COLD_REBOOT,
        ONLINE,
        OFFLINE,
        RAIN_SENSOR_DETECTION_ON,
        RAIN_SENSOR_DETECTION_OFF,
        SCHEDULE_STARTED,
        SCHEDULE_STOPPED,
        SCHEDULE_COMPLETED,
        SCHEDULE_SKIPPED,
        SCHEDULE_SKIPPED_RAIN_SENSOR,
        PAUSED,
        RESUMED,
        ZONE_STARTED,
        ZONE_STOPPED,
        ZONE_COMPLETED,
        ZONE_CYCLING,
        ZONE_CYCLING_COMPLETE,
        ZONE_WATER_BUDGET,
        ZONE_SKIPPED,
        ADD_DEVICE,
        WEATHER_INTELLIGENCE_FREEZE,
        WEATHER_INTELLIGENCE_SKIP,
        WEATHER_INTELLIGENCE_NO_SKIP,
        OFFLINE_NOTIFICATION,
        ZONE_DELTA,
        SCHEDULE_RULE_DELTA,
        DEVICE_DELTA,
        PERSON_DELTA,
        WATER_BUDGET,
        COLLABORATOR_ADDED,
        COLLABORATOR_REMOVED,
        FLEX_SCHEDULE_ADDED,
        ZONE_CYCLING_COMPLETED,
        FLEX_SCHEDULE_RULE_DELTA,
        BROWNOUT_VALVE,
        BROWNOUT_MASTER_VALVE
    }

    public enum Type {
        DEVICE_STATUS,
        RAIN_DELAY,
        WEATHER_INTELLIGENCE,
        DEVICE_SLEEP,
        DEVICE_ADD,
        SCHEDULE_STATUS,
        ZONE_STATUS,
        WATER_BUDGET,
        RAIN_SENSOR_DETECTION,
        DELTA,
        BROWNOUT,
        ZONE_DELTA,
        USER_ACTION
    }

    public String toDescriptiveString() {
        return this.summary;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Event)) {
            return false;
        }
        return this.id.equals(((Event) o).id);
    }

    private synchronized void buildEventDatasMap() {
        if (this.eventDatas != null) {
            this.eventDatasMap = new TreeMap();
            for (EventData ed : this.eventDatas) {
                this.eventDatasMap.put(ed.key, ed);
            }
        }
    }

    public Action getAction() {
        String str = null;
        buildEventDatasMap();
        Action action = new Action();
        EventData name = (EventData) this.eventDatasMap.get("actionName");
        EventData label = (EventData) this.eventDatasMap.get("actionLabel");
        EventData url = (EventData) this.eventDatasMap.get("actionUrl");
        EventData expiry = (EventData) this.eventDatasMap.get("actionExpirationDate");
        if (name == null || label == null || url == null || expiry == null) {
            return null;
        }
        String str2;
        if (name != null) {
            str2 = name.convertedValue;
        } else {
            str2 = null;
        }
        action.name = str2;
        if (label != null) {
            str2 = label.convertedValue;
        } else {
            str2 = null;
        }
        action.label = str2;
        if (url != null) {
            str = url.convertedValue;
        }
        action.url = str;
        action.expiry = expiry != null ? Long.parseLong(expiry.convertedValue) : 0;
        return action;
    }

    public String getStringValue(String key) {
        buildEventDatasMap();
        if (this.eventDatasMap.containsKey(key)) {
            return ((EventData) this.eventDatasMap.get(key)).convertedValue;
        }
        return null;
    }

    public int getIntValue(String key) {
        buildEventDatasMap();
        if (this.eventDatasMap.containsKey(key)) {
            return Integer.parseInt(((EventData) this.eventDatasMap.get(key)).convertedValue);
        }
        return 0;
    }

    public long getLongValue(String key) {
        buildEventDatasMap();
        if (this.eventDatasMap.containsKey(key)) {
            return Long.parseLong(((EventData) this.eventDatasMap.get(key)).convertedValue);
        }
        return 0;
    }

    public DeltaContainer getDeltaContainer(String key) {
        buildEventDatasMap();
        if (this.eventDatasMap.containsKey(key)) {
            return ((EventData) this.eventDatasMap.get(key)).deltaContainer;
        }
        return null;
    }

    public static void deleteEventsWithDifferentDisplayUnit(Database database, DisplayUnit units) {
        Dao<Event, ?> eventDao = database.getDatabaseHelper().findDao(Event.class);
        synchronized (eventDao) {
            try {
                DeleteBuilder<Event, ?> db = eventDao.deleteBuilder();
                db.where().ne("units", units);
                Log.d(TAG, "deleted " + db.delete() + " events with different units");
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static void cleanEvents(Database database) {
        Dao<Event, ?> eventDao = database.getDatabaseHelper().findDao(Event.class);
        synchronized (eventDao) {
            try {
                DeleteBuilder<Event, ?> db = eventDao.deleteBuilder();
                Calendar c = GregorianCalendar.getInstance();
                c.add(6, -31);
                db.where().lt("eventDate", c.getTime());
                Log.d(TAG, "deleted " + db.delete() + " old events");
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static List<Event> findEventsByCorrelation(Database database, String correlationId, Type type) {
        List<Event> events;
        Dao<Event, ?> eventDao = database.getDatabaseHelper().findDao(Event.class);
        synchronized (eventDao) {
            try {
                events = eventDao.query(eventDao.queryBuilder().orderBy("eventDate", false).where().eq("correlationId", correlationId).and().eq("type", type).prepare());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        return events;
    }

    public static List<Event> findEventsByDevice(Database database, String deviceId, Type type) {
        List<Event> events;
        Dao<Event, ?> eventDao = database.getDatabaseHelper().findDao(Event.class);
        synchronized (eventDao) {
            try {
                events = eventDao.query(eventDao.queryBuilder().orderBy("eventDate", false).where().eq("deviceId", deviceId).and().eq("type", type).prepare());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        return events;
    }
}
