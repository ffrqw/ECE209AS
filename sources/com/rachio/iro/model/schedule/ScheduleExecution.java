package com.rachio.iro.model.schedule;

import android.util.Log;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rachio.iro.model.Event;
import com.rachio.iro.model.Event.SubType;
import com.rachio.iro.model.Event.Type;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.annotation.RestClientOptions;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.model.schedule.ScheduleRule.ScheduleRuleType;
import com.rachio.iro.utils.CrashReporterUtils;
import com.rachio.iro.utils.StringUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

@RestClientOptions(path = "/1/scheduleexecution/model")
@JsonIgnoreProperties({"zoneIndex", "estimatedEndTime", "paused", "zoneDurationRemaining"})
public class ScheduleExecution extends ModelObject implements Serializable {
    private static final String STATUS_PROCESSING = "PROCESSING";
    private static final long serialVersionUID = 1;
    public Date createDate;
    public int cycleCount;
    public boolean cycling;
    public String deviceGeneratedScheduleId;
    public String deviceId;
    public int duration;
    public int durationNoCycle;
    public int durationRemaining;
    public int estimatedDuration;
    public String id;
    public Date lastUpdateDate;
    public String scheduleId;
    public String scheduleRuleId;
    public Date startDate;
    public String status;
    public int totalCycleCount;
    public ScheduleRuleType type;
    public int zoneCount;
    public int zoneDuration;
    @JsonIgnore
    public List<Event> zoneEvents;
    public String zoneId;
    public Date zoneStartDate;

    public double percentComplete() {
        double schedulePercentComplete = (((double) (new Date().getTime() / 1000)) - ((double) (this.startDate.getTime() / 1000))) / ((double) this.duration);
        if (schedulePercentComplete == Double.NaN) {
            return 0.0d;
        }
        return schedulePercentComplete;
    }

    public double zonePercentComplete() {
        double zonePercentComplete = ((double) ((new Date().getTime() / 1000) - (this.zoneStartDate.getTime() / 1000))) / ((double) this.zoneDuration);
        if (zonePercentComplete == Double.NaN) {
            return 0.0d;
        }
        return zonePercentComplete;
    }

    public double timeRemaining() {
        return Math.max(((double) this.duration) - (((double) (System.currentTimeMillis() / 1000)) - ((double) (this.startDate.getTime() / 1000))), 0.0d);
    }

    public double zoneTimeRemaining() {
        return Math.max(((double) this.zoneDuration) - (((double) (System.currentTimeMillis() / 1000)) - ((double) (this.zoneStartDate.getTime() / 1000))), 0.0d);
    }

    @JsonIgnore
    public boolean isRunning() {
        return StringUtils.equals(this.status, STATUS_PROCESSING) && this.durationRemaining > 0;
    }

    @JsonIgnore
    public void getZoneEvents(Database database) {
        List<Event> zoneEvents = Event.findEventsByCorrelation(database, this.id, Type.ZONE_STATUS);
        List<Event> correctedEvents = new ArrayList();
        List<List<Event>> buckets = new ArrayList();
        TreeMap<String, List<Event>> bucketTree = new TreeMap();
        for (Event e : zoneEvents) {
            Event e2;
            String zoneId = e2.getStringValue("zoneId");
            List<Event> bucket = (List) bucketTree.get(zoneId);
            if (bucket == null) {
                bucket = new ArrayList();
                buckets.add(bucket);
                bucketTree.put(zoneId, bucket);
            }
            bucket.add(e2);
        }
        for (List<Event> bucket2 : buckets) {
            Collections.sort(bucket2, new Comparator<Event>() {
                public int compare(Event lhs, Event rhs) {
                    return lhs.eventDate.compareTo(rhs.eventDate);
                }
            });
        }
        Collections.sort(buckets, new Comparator<List<Event>>() {
            public int compare(List<Event> lhs, List<Event> rhs) {
                return ((Event) lhs.get(0)).eventDate.compareTo(((Event) rhs.get(0)).eventDate);
            }
        });
        for (List<Event> bucket22 : buckets) {
            for (int i = bucket22.size() - 1; i >= 0; i--) {
                e2 = (Event) bucket22.get(i);
                if (e2.subType == SubType.ZONE_COMPLETED || e2.subType == SubType.ZONE_STARTED) {
                    correctedEvents.add(e2);
                    break;
                }
            }
        }
        Collections.reverse(correctedEvents);
        for (Event e22 : correctedEvents) {
            Log.d("--", e22.subType + " " + e22.getStringValue("zoneId"));
        }
        this.zoneEvents = correctedEvents;
    }

    public ScheduleRuleType getType() {
        if (this.type != null) {
            return this.type;
        }
        CrashReporterUtils.silentException(new Exception("execution type is null!"));
        return ScheduleRuleType.MANUAL;
    }
}
