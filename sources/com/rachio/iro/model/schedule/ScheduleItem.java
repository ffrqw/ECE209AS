package com.rachio.iro.model.schedule;

import com.rachio.iro.utils.StringUtils;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

public class ScheduleItem extends ScheduleObject {
    private static final long serialVersionUID = 1;
    public Date absoluteStartDate;
    public String iso8601Date;
    public String scheduleRuleId;
    public String scheduleType;
    public int totalDuration;

    public static final class AbsoluteDateComparator implements Comparator<ScheduleItem> {
        public final int compare(ScheduleItem lhs, ScheduleItem rhs) {
            return lhs.absoluteStartDate.compareTo(rhs.absoluteStartDate);
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof ScheduleItem)) {
            return super.equals(o);
        }
        ScheduleItem si = (ScheduleItem) o;
        return si.date.equals(this.date) && si.startHour == this.startHour && si.startMinute == this.startMinute && StringUtils.equals(this.scheduleRuleId, si.scheduleRuleId) && si.zones.size() == this.zones.size() && si.zones.containsAll(this.zones) && si.cycleSoak == this.cycleSoak && si.cycleDuration == this.cycleDuration && si.totalCycleCount == this.totalCycleCount;
    }

    public int getTotalDurationNoCycle() {
        int duration = 0;
        Iterator it = this.zones.iterator();
        while (it.hasNext()) {
            duration += ((ZoneInfo) it.next()).duration.intValue();
        }
        return duration;
    }
}
