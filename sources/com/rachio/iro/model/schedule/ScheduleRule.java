package com.rachio.iro.model.schedule;

import android.content.Context;
import android.text.TextUtils;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonView;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.rachio.iro.R;
import com.rachio.iro.fcm.EventHandler.DeltaApplyOptions;
import com.rachio.iro.model.DeviceChildModelObject;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.TransmittableView;
import com.rachio.iro.model.annotation.RestClientOptions;
import com.rachio.iro.utils.CalendarUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

@DeltaApplyOptions(deltasAreIncomplete = true)
@DatabaseTable(tableName = "ScheduleRule")
@RestClientOptions(path = "/1/schedulerule/model")
public class ScheduleRule extends DeviceChildModelObject<ScheduleRule> implements Serializable {
    private static final Date NEVERENDDATE = new Date(NEVERENDTIME);
    private static final long NEVERENDTIME = 2147397247000L;
    public static final String OPERATOR_STARTTIME = "START_TIME";
    private static final long serialVersionUID = 1;
    public static final ScheduleJobType[] weekdays = new ScheduleJobType[]{ScheduleJobType.ANY, ScheduleJobType.DAY_OF_WEEK_0, ScheduleJobType.DAY_OF_WEEK_1, ScheduleJobType.DAY_OF_WEEK_2, ScheduleJobType.DAY_OF_WEEK_3, ScheduleJobType.DAY_OF_WEEK_4, ScheduleJobType.DAY_OF_WEEK_5, ScheduleJobType.DAY_OF_WEEK_6};
    @DatabaseField(dataType = DataType.DATE_LONG)
    public Date absoluteStartDate;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public boolean anyDay = true;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public boolean cycleSoak = true;
    @DatabaseField
    public CycleSoakStatus cycleSoakStatus;
    @DatabaseField
    public int cycles;
    public String droughtRestriction;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public boolean enabled = true;
    @DatabaseField(dataType = DataType.DATE_LONG)
    public Date endDate;
    @DatabaseField
    public int endHour;
    @DatabaseField
    public int endMinute;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public boolean etSkip = true;
    @DatabaseField
    public String externalName;
    @JsonView({TransmittableView.class})
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public FreezeDelay freezeDelay = new FreezeDelay();
    @JsonView({TransmittableView.class})
    @DatabaseField
    public String name;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public Operator operator = Operator.START_TIME;
    @JsonView({TransmittableView.class})
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public PrecipDelay precipDelay = new PrecipDelay();
    @JsonView({TransmittableView.class})
    @DatabaseField
    public boolean rainDelay;
    public boolean recalculateDurations;
    @JsonView({TransmittableView.class})
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public ScheduleJobType[] restrictedDays = new ScheduleJobType[0];
    @DatabaseField
    public boolean restriction;
    @JsonView({TransmittableView.class})
    @DatabaseField(canBeNull = false, dataType = DataType.SERIALIZABLE)
    public ScheduleJobType[] scheduleJobTypes = new ScheduleJobType[0];
    @JsonView({TransmittableView.class})
    @DatabaseField
    public float seasonalAdjustment;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public int sortOrder;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public int startDay;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public int startHour = 5;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public int startMinute;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public int startMonth;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public int startYear;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public String summary;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public int totalDuration;
    @DatabaseField
    public int totalDurationNoCycle;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public ScheduleRuleType type = ScheduleRuleType.MANUAL;
    public boolean updateDurations = true;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public boolean waterBudget = true;
    public Date waterBudgetUpdateDate;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public double weatherIntelligenceSensitivity;
    @JsonView({TransmittableView.class})
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public ArrayList<ZoneInfo> zones;

    public enum CycleSoakStatus {
        DISABLED,
        OFF,
        ON
    }

    @RestClientOptions(path = "/1/flexschedulerule/model")
    public static final class FlexScheduleRule extends ScheduleRule {
        public final /* bridge */ /* synthetic */ ModelObject getTransmittableVersion() {
            return super.getTransmittableVersion();
        }
    }

    public enum FrequencyOperator {
        ASNEEDED,
        INTERVAL,
        WEEKDAY
    }

    public enum Operator {
        AFTER,
        BEFORE,
        START_TIME
    }

    public enum ScheduleJobType {
        ODD,
        EVEN,
        ANY,
        INTERVAL_1,
        INTERVAL_2,
        INTERVAL_3,
        INTERVAL_4,
        INTERVAL_5,
        INTERVAL_6,
        INTERVAL_7,
        INTERVAL_8,
        INTERVAL_9,
        INTERVAL_10,
        INTERVAL_11,
        INTERVAL_12,
        INTERVAL_13,
        INTERVAL_14,
        INTERVAL_15,
        INTERVAL_16,
        INTERVAL_17,
        INTERVAL_18,
        INTERVAL_19,
        INTERVAL_20,
        INTERVAL_21,
        INTERVAL_22,
        INTERVAL_23,
        INTERVAL_24,
        INTERVAL_25,
        INTERVAL_26,
        INTERVAL_27,
        INTERVAL_28,
        DAY_OF_WEEK_0,
        DAY_OF_WEEK_1,
        DAY_OF_WEEK_2,
        DAY_OF_WEEK_3,
        DAY_OF_WEEK_4,
        DAY_OF_WEEK_5,
        DAY_OF_WEEK_6;
        
        public static final ScheduleJobType[] flexInterval = null;
        public static final ScheduleJobType[] intervals = null;
        public static final ScheduleJobType[] onlyWeekdays = null;
        public static final int[] weekdaysCalendarMapping = null;

        static {
            intervals = new ScheduleJobType[]{ODD, EVEN, INTERVAL_1, INTERVAL_2, INTERVAL_3, INTERVAL_4, INTERVAL_5, INTERVAL_6, INTERVAL_7, INTERVAL_8, INTERVAL_9, INTERVAL_10, INTERVAL_11, INTERVAL_12, INTERVAL_13, INTERVAL_14, INTERVAL_15, INTERVAL_16, INTERVAL_17, INTERVAL_18, INTERVAL_19, INTERVAL_20, INTERVAL_21};
            flexInterval = new ScheduleJobType[]{ODD, EVEN};
            onlyWeekdays = new ScheduleJobType[]{DAY_OF_WEEK_0, DAY_OF_WEEK_1, DAY_OF_WEEK_2, DAY_OF_WEEK_3, DAY_OF_WEEK_4, DAY_OF_WEEK_5, DAY_OF_WEEK_6};
            weekdaysCalendarMapping = new int[]{1, 2, 3, 4, 5, 6, 7};
        }

        public final String toReadableString(Context context) {
            int intervalPos = Arrays.binarySearch(intervals, this);
            if (intervalPos >= 0) {
                return context.getResources().getStringArray(R.array.add_schedule_repeat_labels)[intervalPos];
            }
            return null;
        }
    }

    public enum ScheduleRuleType {
        AUTOMATED,
        AUTOMATIC,
        MANUAL,
        FLEX
    }

    @JsonIgnore
    public static int getDayWeekdayForJobType(ScheduleJobType jobType) {
        return ScheduleJobType.weekdaysCalendarMapping[getWeekdayPosForJobType(jobType)];
    }

    @JsonIgnore
    public static int getWeekdayPosForJobType(ScheduleJobType jobType) {
        return Arrays.binarySearch(ScheduleJobType.onlyWeekdays, jobType);
    }

    public boolean equals(Object o) {
        if (o instanceof ScheduleRule) {
            return this.id.equals(((ScheduleRule) o).id);
        }
        return super.equals(o);
    }

    @JsonIgnore
    public Date getStartDate() {
        Calendar c = Calendar.getInstance();
        c.set(this.startYear, this.startMonth - 1, this.startDay);
        CalendarUtil.setToStartOfDay(c);
        return c.getTime();
    }

    @JsonIgnore
    public Date getStartTime() {
        Calendar c = Calendar.getInstance();
        c.setTime(getStartDate());
        c.set(11, this.startHour);
        c.set(12, this.startMinute);
        return c.getTime();
    }

    @JsonIgnore
    public void setStartDate(Date date) {
        this.startYear = date.getYear() + 1900;
        this.startMonth = date.getMonth() + 1;
        this.startDay = date.getDate();
    }

    @JsonIgnore
    public String getNameOrExternalName() {
        if (TextUtils.isEmpty(this.name)) {
            return this.externalName;
        }
        return this.name;
    }

    public void preSave() {
        super.preSave();
        if (this.scheduleJobTypes != null) {
            Arrays.sort(this.scheduleJobTypes);
        }
        if (this.zones != null) {
            Collections.sort(this.zones);
        }
    }

    public ScheduleRule getTransmittableVersion() {
        ScheduleRule clone = (ScheduleRule) ModelObject.transmittableClone(ScheduleRule.class, this);
        clone.device = this.device;
        return clone;
    }

    @JsonIgnore
    public FrequencyOperator getFrequencyOperator() {
        int i = 0;
        if (!isFlex() && this.anyDay) {
            return FrequencyOperator.ASNEEDED;
        }
        if (isFlex() && this.scheduleJobTypes.length == 1 && this.scheduleJobTypes[0] == ScheduleJobType.ANY) {
            return FrequencyOperator.ASNEEDED;
        }
        boolean hasWeekdayJobTypes = false;
        boolean hasIntervalJobTypes = false;
        if (this.scheduleJobTypes != null) {
            ScheduleJobType[] scheduleJobTypeArr = this.scheduleJobTypes;
            int length = scheduleJobTypeArr.length;
            while (i < length) {
                ScheduleJobType sjt = scheduleJobTypeArr[i];
                if (Arrays.binarySearch(ScheduleJobType.onlyWeekdays, sjt) >= 0) {
                    hasWeekdayJobTypes = true;
                } else if (Arrays.binarySearch(ScheduleJobType.intervals, sjt) >= 0) {
                    hasIntervalJobTypes = true;
                }
                if (hasWeekdayJobTypes && hasIntervalJobTypes) {
                    break;
                }
                i++;
            }
        }
        if (hasWeekdayJobTypes && !hasIntervalJobTypes) {
            return FrequencyOperator.WEEKDAY;
        }
        if (hasIntervalJobTypes && !hasWeekdayJobTypes) {
            return FrequencyOperator.INTERVAL;
        }
        throw new IllegalStateException("schedule rule is busted");
    }

    @JsonIgnore
    public boolean isFlex() {
        return this.type == ScheduleRuleType.FLEX;
    }

    @JsonSetter("endDate")
    public void setEndDateJson(Date endDate) {
        if (endDate == null || endDate.getTime() < NEVERENDTIME) {
            this.endDate = endDate;
        } else {
            this.endDate = null;
        }
    }

    @JsonGetter("endDate")
    @JsonView({TransmittableView.class})
    public Date getEndDateJson() {
        if (this.endDate != null) {
            return this.endDate;
        }
        Date endDate = new Date();
        endDate.setTime(NEVERENDTIME);
        return endDate;
    }

    public static Date clampDate(Date date) {
        if (date.getTime() > NEVERENDDATE.getTime()) {
            return NEVERENDDATE;
        }
        return date;
    }

    public static Date clampDateToNumberOfYears(Date date, TimeZone timezone, int years) {
        Calendar calendar = Calendar.getInstance(timezone);
        CalendarUtil.setToStartOfDay(calendar);
        calendar.add(1, years);
        if (date.getTime() > calendar.getTimeInMillis()) {
            return calendar.getTime();
        }
        return date;
    }

    public static Date clampStartDate(Date date, TimeZone timezone) {
        return clampDateToNumberOfYears(clampDate(date), timezone, 1);
    }

    public static Date clampEndDate(Date date, TimeZone timezone) {
        return clampDate(date);
    }
}
