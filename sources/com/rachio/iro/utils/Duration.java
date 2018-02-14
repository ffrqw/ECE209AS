package com.rachio.iro.utils;

import java.util.Date;

public class Duration {
    public static boolean isContainedWithin(Date start, Date end, Date when) {
        return when.getTime() >= start.getTime() && when.getTime() <= end.getTime();
    }
}
