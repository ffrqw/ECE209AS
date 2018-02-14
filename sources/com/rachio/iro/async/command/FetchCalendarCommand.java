package com.rachio.iro.async.command;

import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.Event;
import com.rachio.iro.model.Event.Type;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.model.schedule.ScheduleCalendar;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.utils.CalendarUtil;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class FetchCalendarCommand extends CommandThatMayNeedToPullADevice<ScheduleCalendarMeta> {
    private final String deviceId;
    private final long end;
    private final FetchCalendarListener listener;
    private final long start;

    public interface FetchCalendarListener {
        void onCalendarLoaded(ScheduleCalendarMeta scheduleCalendarMeta);
    }

    public static class ScheduleCalendarMeta implements Serializable {
        public final ScheduleCalendar calendar;
        public final TimeZone deviceTimeZone;
        public final long end;
        public final Date rainDelayEnd;
        public final Date rainDelayStart;
        public final List<ScheduleRule> scheduleRules;
        public final long start;
        public final List<Zone> zones;
        public final Map<String, Zone> zonesMap = new TreeMap();

        public ScheduleCalendarMeta(ScheduleCalendar calendar, TimeZone deviceTimeZone, long start, long end, Date rainDelayStart, Date rainDelayEnd, List<Zone> zones, List<ScheduleRule> scheduleRules) {
            this.calendar = calendar;
            this.deviceTimeZone = deviceTimeZone;
            this.start = start;
            this.end = end;
            this.rainDelayStart = rainDelayStart;
            this.rainDelayEnd = rainDelayEnd;
            this.zones = zones;
            this.scheduleRules = scheduleRules;
            for (Zone z : zones) {
                this.zonesMap.put(z.id, z);
            }
        }
    }

    protected final /* bridge */ /* synthetic */ void handleResult(Object obj) {
        ScheduleCalendarMeta scheduleCalendarMeta = (ScheduleCalendarMeta) obj;
        if (this.listener != null) {
            this.listener.onCalendarLoaded(scheduleCalendarMeta);
        }
    }

    public static long[] defaultStartEnd() {
        Calendar c = Calendar.getInstance();
        CalendarUtil.setToStartOfDay(c);
        c.add(6, -1);
        long start = c.getTimeInMillis();
        c.add(6, 21);
        long end = c.getTimeInMillis();
        return new long[]{start, end};
    }

    public FetchCalendarCommand(FetchCalendarListener listener, String deviceId) {
        long[] startEnd = defaultStartEnd();
        this.start = startEnd[0];
        this.end = startEnd[1];
        this.listener = listener;
        this.deviceId = deviceId;
        BaseCommand.component(listener).inject(this);
    }

    public FetchCalendarCommand(FetchCalendarListener listener, String deviceId, long start, long end) {
        this.listener = listener;
        this.deviceId = deviceId;
        this.start = start;
        this.end = end;
        BaseCommand.component(listener).inject(this);
    }

    public static ScheduleCalendarMeta loadCalendar(Database database, PrefsWrapper prefsWrapper, RestClient restClient, Device device, long start, long end) {
        if (device == null) {
            return null;
        }
        List<ScheduleRule> rules = device.getAllScheduleRules();
        if (rules == null) {
            return null;
        }
        long lastWeatherIntelligenceEvent;
        Collections.sort(rules, new Comparator<ScheduleRule>() {
            public final /* bridge */ /* synthetic */ int compare(Object obj, Object obj2) {
                ScheduleRule scheduleRule = (ScheduleRule) obj;
                ScheduleRule scheduleRule2 = (ScheduleRule) obj2;
                return Integer.valueOf((scheduleRule.startHour * 60) + scheduleRule.startMinute).compareTo(Integer.valueOf((scheduleRule2.startHour * 60) + scheduleRule2.startMinute));
            }
        });
        FetchEventsCommand.fetchEvents(database, prefsWrapper, restClient, device.id, "SCHEDULE", 0, 1);
        List<Event> events = Event.findEventsByDevice(database, device.id, Type.WEATHER_INTELLIGENCE);
        if (events == null || events.size() <= 0) {
            lastWeatherIntelligenceEvent = -1;
        } else {
            lastWeatherIntelligenceEvent = ((Event) events.get(0)).eventDate.getTime();
        }
        TimeZone deviceTz = device.getTimeZoneAsTimeZone();
        HashMap<String, String> queryParams = new HashMap();
        queryParams.put("startTime", Long.toString(start));
        queryParams.put("endTime", Long.toString(end));
        ScheduleCalendar result = (ScheduleCalendar) restClient.getObjectById(database, device.id, ScheduleCalendar.class, queryParams, lastWeatherIntelligenceEvent, new HttpResponseErrorHandler());
        List<Zone> zones = device.getZonesAsList();
        if (result == null || zones == null) {
            return null;
        }
        result.sort();
        result.timestamp = System.currentTimeMillis();
        return new ScheduleCalendarMeta(result, deviceTz, start, end, device.rainDelayStartDate, device.rainDelayExpirationDate, zones, rules);
    }

    protected final /* bridge */ /* synthetic */ Object loadResult() {
        return loadCalendar(this.database, this.prefsWrapper, this.restClient, fetchDevice(this.deviceId), this.start, this.end);
    }
}
