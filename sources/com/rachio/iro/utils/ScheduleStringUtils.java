package com.rachio.iro.utils;

import android.content.Context;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.model.schedule.ScheduleRule.ScheduleJobType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

public class ScheduleStringUtils {
    public static final void buildSummary(Context context, ScheduleRule rule) {
        if (rule.isFlex()) {
            rule.summary = String.format("Every day at %s as needed", new Object[]{buildTimeStr(rule)});
            return;
        }
        String when = null;
        switch (rule.getFrequencyOperator()) {
            case ASNEEDED:
                when = "As Needed";
                break;
            case WEEKDAY:
                when = "Every " + dayOfWeekJobsToString(rule.scheduleJobTypes);
                break;
            case INTERVAL:
                when = rule.scheduleJobTypes[0].toReadableString(context);
                break;
        }
        rule.summary = String.format("%s at %s", new Object[]{when, buildTimeStr(rule)});
    }

    private static String buildTimeStr(ScheduleRule rule) {
        return TimeStringUtil.getTimeOfDay(rule.startHour, rule.startMinute);
    }

    public static String dayOfWeekJobsToString(ScheduleJobType[] scheduleJobTypes) {
        ArrayList<ScheduleJobType> weekdayJobTypes = new ArrayList();
        if (scheduleJobTypes != null) {
            for (ScheduleJobType sjt : scheduleJobTypes) {
                if (Arrays.binarySearch(ScheduleRule.weekdays, sjt) > 0 && sjt != ScheduleJobType.ANY) {
                    weekdayJobTypes.add(sjt);
                }
            }
        }
        if (weekdayJobTypes.size() <= 0) {
            return "Never";
        }
        StringBuffer sb = new StringBuffer();
        Calendar calendar = GregorianCalendar.getInstance();
        Iterator it = weekdayJobTypes.iterator();
        while (it.hasNext()) {
            calendar.set(7, ScheduleRule.getDayWeekdayForJobType((ScheduleJobType) it.next()));
            sb.append(DateFormats.dayOfWeekShort.format(calendar.getTime()));
            sb.append(",");
        }
        if (sb.length() > 0) {
            return sb.substring(0, sb.length() - 1);
        }
        return null;
    }
}
