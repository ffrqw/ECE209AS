package com.rachio.iro.model;

import com.rachio.iro.R;

public enum WateringScheduleType {
    NONE(R.drawable.ic_watering_schedule_none),
    FIXED(R.drawable.ic_watering_schedule_fixed),
    FLEX(R.drawable.ic_watering_schedule_flex),
    BOTH(R.drawable.ic_watering_schedule_both),
    FIXED_TODAY(R.drawable.ic_watering_schedule_fixed_today),
    FLEX_TODAY(R.drawable.ic_watering_schedule_flex),
    BOTH_TODAY(R.drawable.ic_watering_schedule_both_today),
    RAINDELAYED(R.drawable.ic_watering_schedule_raindelay),
    RAINDELAYED_TODAY(R.drawable.ic_watering_schedule_raindelay_today);
    
    public final int resourceId;

    private WateringScheduleType(int resourceId) {
        this.resourceId = resourceId;
    }

    public static WateringScheduleType fromOrdinal(int ordinal) {
        WateringScheduleType[] allTypes = values();
        if (ordinal < 0 || ordinal >= allTypes.length) {
            return NONE;
        }
        return allTypes[ordinal];
    }
}
