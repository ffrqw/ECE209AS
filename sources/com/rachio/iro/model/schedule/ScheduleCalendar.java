package com.rachio.iro.model.schedule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.WateringScheduleType;
import com.rachio.iro.model.annotation.RestClientOptions;
import com.rachio.iro.model.annotation.TimeToLive;
import com.rachio.iro.model.schedule.ScheduleItem.AbsoluteDateComparator;
import com.rachio.iro.utils.CalendarUtil;
import com.rachio.iro.utils.StringUtils;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

@TimeToLive(timeToLive = 3600000)
@RestClientOptions(path = "/1/schedule/date_entries")
@JsonIgnoreProperties({"error", "code"})
public class ScheduleCalendar extends ModelObject implements Serializable {
    private static final long serialVersionUID = 1;
    public DateEntry[] dateEntries;
    transient Map<Long, DateEntry> entriesMap = null;
    public long timestamp;

    public static class DateEntry implements Serializable, Comparable<DateEntry> {
        private static final long serialVersionUID = 1;
        public Date date;
        public String iso8601Date;
        public ScheduleItem[] scheduleItems;
        public String scheduleType;

        public int compareTo(DateEntry another) {
            return this.date.compareTo(another.date);
        }
    }

    public static class OutOfRangeException extends Exception {
    }

    public void sort() {
        if (this.dateEntries != null) {
            Arrays.sort(this.dateEntries);
            for (DateEntry de : this.dateEntries) {
                if (de.scheduleItems != null) {
                    Arrays.sort(de.scheduleItems, new AbsoluteDateComparator());
                }
            }
        }
    }

    public Date nextWateringTime(TimeZone deviceTimeZone, Date limit) {
        Calendar c = Calendar.getInstance(deviceTimeZone);
        CalendarUtil.setToStartOfDay(c);
        Date today = c.getTime();
        Date now = new Date();
        if (this.dateEntries != null) {
            for (DateEntry de : this.dateEntries) {
                if ((de.date.equals(today) || (de.date.after(today) && de.date.before(limit))) && de.scheduleItems != null) {
                    for (ScheduleItem si : de.scheduleItems) {
                        if (si.absoluteStartDate.after(now)) {
                            return si.absoluteStartDate;
                        }
                    }
                    continue;
                }
            }
        }
        return null;
    }

    public boolean isScheduledToWater(Date when) {
        for (DateEntry de : this.dateEntries) {
            if (de.date.equals(when) && de.scheduleItems != null) {
                return true;
            }
        }
        return false;
    }

    public boolean willRuleRunOnDate(String ruleId, Date when) {
        DateEntry[] dateEntryArr = this.dateEntries;
        int length = dateEntryArr.length;
        int i = 0;
        while (i < length) {
            DateEntry de = dateEntryArr[i];
            if (!de.date.equals(when)) {
                i++;
            } else if (de.scheduleItems == null) {
                return false;
            } else {
                for (ScheduleItem si : de.scheduleItems) {
                    System.out.println(ruleId + " " + si.scheduleRuleId);
                    if (StringUtils.equals(si.scheduleRuleId, ruleId)) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public Date getNextWateringTimeForZone(Date rainDelayExpiry, String zoneId) {
        long after = Math.max(rainDelayExpiry != null ? rainDelayExpiry.getTime() : 0, System.currentTimeMillis());
        if (this.dateEntries != null) {
            for (DateEntry de : this.dateEntries) {
                if (de.scheduleItems != null) {
                    for (ScheduleItem si : de.scheduleItems) {
                        Iterator it = si.zones.iterator();
                        while (it.hasNext()) {
                            if (((ZoneInfo) it.next()).zoneId.equals(zoneId) && si.absoluteStartDate.getTime() >= after) {
                                return si.absoluteStartDate;
                            }
                        }
                    }
                    continue;
                }
            }
        }
        return null;
    }

    public WateringScheduleType getTypeForDate(TimeZone deviceTimeZone, Date when, Date rainDelayStart, Date rainDelayExpiry) throws OutOfRangeException {
        Calendar now = Calendar.getInstance(deviceTimeZone);
        CalendarUtil.setToStartOfDay(now);
        boolean today = now.getTimeInMillis() == when.getTime();
        buildMap();
        long time = when.getTime();
        if (this.entriesMap.containsKey(Long.valueOf(time))) {
            DateEntry de = (DateEntry) this.entriesMap.get(Long.valueOf(time));
            if (de.scheduleItems == null) {
                return WateringScheduleType.NONE;
            }
            if (rainDelayStart == null || rainDelayExpiry == null || !de.date.after(rainDelayStart) || !de.date.before(rainDelayExpiry)) {
                boolean haveFixed = false;
                boolean haveFlex = false;
                for (ScheduleItem si : de.scheduleItems) {
                    if (StringUtils.equals(si.scheduleType, "FLEX")) {
                        haveFlex = true;
                    } else if (StringUtils.equals(si.scheduleType, "FIXED")) {
                        haveFixed = true;
                    } else {
                        throw new RuntimeException("unhandled type " + si.scheduleType);
                    }
                    if (haveFixed && haveFlex) {
                        break;
                    }
                }
                if (haveFixed && haveFlex) {
                    return today ? WateringScheduleType.BOTH_TODAY : WateringScheduleType.BOTH;
                } else {
                    if (haveFixed) {
                        return today ? WateringScheduleType.FIXED_TODAY : WateringScheduleType.FIXED;
                    } else {
                        if (haveFlex) {
                            return today ? WateringScheduleType.FLEX_TODAY : WateringScheduleType.FLEX;
                        }
                    }
                }
            } else if (today) {
                return WateringScheduleType.RAINDELAYED_TODAY;
            } else {
                return WateringScheduleType.RAINDELAYED;
            }
        }
        throw new OutOfRangeException();
    }

    public WateringScheduleType getTypeForZone(int zoneNumber) {
        boolean haveFixed = false;
        boolean haveFlex = false;
        if (this.dateEntries != null) {
            for (DateEntry de : this.dateEntries) {
                if (de.scheduleItems != null) {
                    for (ScheduleItem si : de.scheduleItems) {
                        Iterator it = si.zones.iterator();
                        while (it.hasNext()) {
                            if (((ZoneInfo) it.next()).zoneNumber == zoneNumber) {
                                if (StringUtils.equals(si.scheduleType, "FIXED")) {
                                    haveFixed = true;
                                } else if (StringUtils.equals(si.scheduleType, "FLEX")) {
                                    haveFlex = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (haveFixed && haveFlex) {
            return WateringScheduleType.BOTH;
        }
        if (haveFixed) {
            return WateringScheduleType.FIXED;
        }
        if (haveFlex) {
            return WateringScheduleType.FLEX;
        }
        return WateringScheduleType.NONE;
    }

    public ScheduleItem getScheduleItemForDate(Date when, String ruleId) {
        if (when == null || ruleId == null) {
            throw new IllegalArgumentException();
        }
        buildMap();
        DateEntry de = (DateEntry) this.entriesMap.get(Long.valueOf(when.getTime()));
        if (!(de == null || de.scheduleItems == null)) {
            for (ScheduleItem si : de.scheduleItems) {
                if (StringUtils.equals(ruleId, si.scheduleRuleId)) {
                    return si;
                }
            }
        }
        return null;
    }

    public synchronized void buildMap() {
        if (this.entriesMap == null) {
            this.entriesMap = new TreeMap();
            if (this.dateEntries != null) {
                for (DateEntry de : this.dateEntries) {
                    this.entriesMap.put(Long.valueOf(de.date.getTime()), de);
                }
            }
        }
    }
}
