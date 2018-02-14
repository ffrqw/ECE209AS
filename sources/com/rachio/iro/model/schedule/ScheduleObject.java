package com.rachio.iro.model.schedule;

import android.util.Log;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.utils.StringUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TimeZone;

public class ScheduleObject extends ModelObject {
    private static final String TAG = ScheduleObject.class.getCanonicalName();
    private static final long serialVersionUID = 1;
    @DatabaseField
    public long cycleDuration;
    @DatabaseField
    public boolean cycleSoak;
    @DatabaseField(dataType = DataType.DATE_LONG)
    public Date date;
    @JsonIgnore
    private Date endTime = null;
    @DatabaseField
    public int startHour;
    @DatabaseField
    public int startMinute;
    @JsonIgnore
    private Date startTime = null;
    @DatabaseField
    public int totalCycleCount;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public ArrayList<ZoneInfo> zones;

    public synchronized int calculateTotalRunningTime() {
        int duration;
        duration = 0;
        Iterator<ZoneInfo> zii = this.zones.iterator();
        while (zii.hasNext()) {
            duration += ((ZoneInfo) zii.next()).duration.intValue();
        }
        return duration;
    }

    public synchronized int calculateTotalDurationSpanned() {
        int span;
        if (this.cycleSoak) {
            span = (((int) this.cycleDuration) * (this.totalCycleCount - 1)) + (calculateTotalRunningTime() / this.totalCycleCount);
        } else {
            int duration = 0;
            Iterator<ZoneInfo> zii = this.zones.iterator();
            while (zii.hasNext()) {
                duration += ((ZoneInfo) zii.next()).duration.intValue();
            }
            span = duration;
        }
        return span;
    }

    @JsonIgnore
    public synchronized Date getStartTime(Device device) {
        if (this.startTime == null) {
            String timezoneId;
            if (device.timeZone == null) {
                timezoneId = TimeZone.getDefault().getID();
                String deviceName = device.name == null ? "" : "device name: " + device.name;
                String deviceId = device.id == null ? "" : "device id: " + device.name;
                String deviceDescription = StringUtils.join(" ", deviceName, deviceId);
                Log.w(TAG, String.format("Warning: the timezone for device [%s] was null so we used the user's mobile device timezone, instead", new Object[]{deviceDescription}));
            } else {
                timezoneId = device.timeZone;
            }
            Calendar c = GregorianCalendar.getInstance(TimeZone.getTimeZone(timezoneId));
            c.setTimeInMillis(this.date.getTime());
            c.set(11, this.startHour);
            c.set(12, this.startMinute);
            Calendar local = GregorianCalendar.getInstance();
            local.setTimeInMillis(c.getTimeInMillis());
            this.startTime = local.getTime();
        }
        return this.startTime;
    }

    @JsonIgnore
    public Date getEndTime(Device device) {
        if (this.endTime == null) {
            Calendar c = Calendar.getInstance();
            c.setTime(getStartTime(device));
            c.add(13, calculateTotalDurationSpanned());
            this.endTime = c.getTime();
        }
        return this.endTime;
    }
}
