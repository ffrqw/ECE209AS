package com.rachio.iro.utils;

import java.util.Calendar;

public class CalendarUtil {
    public static void setToStartOfDay(Calendar calendar) {
        calendar.set(14, 0);
        calendar.set(13, 0);
        calendar.set(12, 0);
        calendar.set(11, 0);
    }

    public static void setToEndOfDay(Calendar calendar) {
        calendar.set(14, calendar.getMaximum(14));
        calendar.set(13, calendar.getMaximum(13));
        calendar.set(12, calendar.getMaximum(12));
        calendar.set(11, calendar.getMaximum(11));
    }

    public static void setToSundayBeforeStartOfMonth(Calendar calendar) {
        setToStartOfMonth(calendar);
        while (calendar.get(7) != 1) {
            calendar.add(6, -1);
        }
    }

    public static void setToStartOfMonth(Calendar calendar) {
        calendar.set(5, calendar.getActualMinimum(5));
        setToStartOfDay(calendar);
    }

    public static void setToSaturdayAfterEndOfMonth(Calendar calendar) {
        setToEndOfMonth(calendar);
        while (calendar.get(7) != 7) {
            calendar.add(6, 1);
        }
    }

    public static void setToEndOfMonth(Calendar calendar) {
        calendar.set(5, calendar.getActualMaximum(5));
        setToEndOfDay(calendar);
    }

    public static void setToStartOfYear(Calendar calendar) {
        calendar.set(6, calendar.getMinimum(6));
        setToStartOfDay(calendar);
    }

    public static void setToEndOfYear(Calendar calendar) {
        calendar.set(6, calendar.getMaximum(6));
        setToEndOfDay(calendar);
    }
}
