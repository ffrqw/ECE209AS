package com.rachio.iro.async.command;

import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.WateringScheduleType;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.model.schedule.ScheduleCalendar;
import com.rachio.iro.utils.CalendarUtil;
import com.rachio.iro.utils.DateUtils;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchZonesCommand extends CommandThatMayNeedToPullADevice<ZonesMetaHolder> {
    private final String deviceId;
    private final boolean includeDisabled;
    private final FetchZonesListener listener;
    private final boolean readOnly;

    public interface FetchZonesListener {
        void onZonesFetched(ZonesMetaHolder zonesMetaHolder);
    }

    public static class ZonesMetaHolder {
        public final int activeZone;
        public final int activeZoneDuration;
        public final boolean allZonesEnabled;
        public final long deviceTotalZones;
        public final boolean includesDisabled;
        public final boolean readOnly;
        public final List<Zone> zones;

        public ZonesMetaHolder(List<Zone> zones, int activeZone, int activeZoneDuration, boolean includesDisabled, long deviceTotalZones, boolean readOnly, boolean allZonesEnabled) {
            this.zones = zones;
            this.activeZone = activeZone;
            this.activeZoneDuration = activeZoneDuration;
            this.includesDisabled = includesDisabled;
            this.deviceTotalZones = deviceTotalZones;
            this.readOnly = readOnly;
            this.allZonesEnabled = allZonesEnabled;
        }
    }

    protected final /* bridge */ /* synthetic */ void handleResult(Object obj) {
        ZonesMetaHolder zonesMetaHolder = (ZonesMetaHolder) obj;
        if (this.listener != null) {
            this.listener.onZonesFetched(zonesMetaHolder);
        }
    }

    protected final /* bridge */ /* synthetic */ Object loadResult() {
        Device fetchDevice = fetchDevice(this.deviceId);
        if (fetchDevice != null) {
            List enabledZones = fetchDevice.getEnabledZones();
            List zonesAsList = fetchDevice.getZonesAsList();
            if (enabledZones == null || zonesAsList == null) {
                throw new RuntimeException();
            }
            List<Zone> list = this.includeDisabled ? zonesAsList : enabledZones;
            Collections.sort(list);
            Calendar instance = Calendar.getInstance();
            CalendarUtil.setToStartOfDay(instance);
            instance.add(6, -1);
            long timeInMillis = instance.getTimeInMillis();
            instance.add(6, 31);
            long timeInMillis2 = instance.getTimeInMillis();
            Map hashMap = new HashMap();
            hashMap.put("startTime", Long.toString(timeInMillis));
            hashMap.put("endTime", Long.toString(timeInMillis2));
            ScheduleCalendar scheduleCalendar = (ScheduleCalendar) this.restClient.getObjectById(this.database, this.deviceId, ScheduleCalendar.class, hashMap, new HttpResponseErrorHandler());
            if (scheduleCalendar != null) {
                int i;
                int i2;
                scheduleCalendar.sort();
                for (Zone zone : list) {
                    Date date = zone.nextWaterDate;
                    zone.nextWaterDate = scheduleCalendar.getNextWateringTimeForZone(fetchDevice.rainDelayExpirationDate, zone.id);
                    WateringScheduleType wateringScheduleType = zone.scheduledWateringTypes;
                    zone.scheduledWateringTypes = scheduleCalendar.getTypeForZone(zone.zoneNumber);
                    Object obj = wateringScheduleType != zone.scheduledWateringTypes ? 1 : null;
                    if (!DateUtils.equals(date, zone.nextWaterDate)) {
                        obj = 1;
                    }
                    boolean isZoneInFlexSchedule = fetchDevice.isZoneInFlexSchedule(zone.zoneNumber);
                    if (zone.isInFlexSchedule != isZoneInFlexSchedule) {
                        zone.isInFlexSchedule = isZoneInFlexSchedule;
                        obj = 1;
                    }
                    if (obj != null) {
                        this.database.save(zone);
                    }
                }
                if (fetchDevice.isWatering()) {
                    Map zonesMap = fetchDevice.getZonesMap();
                    if (zonesMap != null && zonesMap.containsKey(fetchDevice.scheduleExecution.zoneId)) {
                        i = ((Zone) zonesMap.get(fetchDevice.scheduleExecution.zoneId)).zoneNumber;
                        i2 = fetchDevice.scheduleExecution.zoneDuration;
                        return new ZonesMetaHolder(list, i, i2, this.includeDisabled, (long) zonesAsList.size(), this.readOnly, enabledZones.size() != zonesAsList.size());
                    }
                }
                i2 = 0;
                i = -1;
                if (enabledZones.size() != zonesAsList.size()) {
                }
                return new ZonesMetaHolder(list, i, i2, this.includeDisabled, (long) zonesAsList.size(), this.readOnly, enabledZones.size() != zonesAsList.size());
            }
        }
        return null;
    }

    public FetchZonesCommand(FetchZonesListener listener, String deviceId, boolean includeDisabled, boolean readOnly) {
        this.listener = listener;
        this.deviceId = deviceId;
        this.includeDisabled = includeDisabled;
        this.readOnly = readOnly;
        BaseCommand.component(listener).inject(this);
    }
}
