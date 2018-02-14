package com.rachio.iro.utils;

import com.rachio.iro.model.Event.SubType;
import com.rachio.iro.model.Event.Type;

public class EventUtils {
    private static final SubType[] wateringSubTypes = new SubType[]{SubType.SCHEDULE_STARTED, SubType.SCHEDULE_STOPPED, SubType.SCHEDULE_COMPLETED, SubType.ZONE_STARTED, SubType.ZONE_STOPPED, SubType.ZONE_COMPLETED, SubType.ZONE_CYCLING};
    private static final Type[] wateringTypes = new Type[]{Type.SCHEDULE_STATUS, Type.ZONE_STATUS};
}
