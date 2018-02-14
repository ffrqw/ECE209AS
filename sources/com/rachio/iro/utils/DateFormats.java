package com.rachio.iro.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateFormats {
    public static final SimpleDateFormat dayAndYear = new SimpleDateFormat("EEEE d MMMM yyyy");
    public static final SimpleDateFormat dayAndYearCommas = new SimpleDateFormat("EEEE, d MMMM, yyyy");
    public static final SimpleDateFormat dayMonthAndtime = new SimpleDateFormat("MMM d h:mm a");
    public static final SimpleDateFormat dayNameMonthDayAndTime = new SimpleDateFormat("EEEE, MMMM d 'at' hh:mma", Locale.US);
    public static final SimpleDateFormat dayOfWeek = new SimpleDateFormat("EEEE");
    public static final SimpleDateFormat dayOfWeekShort = new SimpleDateFormat("EEE", Locale.US);
    public static final SimpleDateFormat getDayOfWeekMonthAndDate = new SimpleDateFormat("EEEE, MMMM d", Locale.US);
    public static final SimpleDateFormat month = new SimpleDateFormat("MMMM");
    public static final SimpleDateFormat monthAndDay = new SimpleDateFormat("MMM d");
    public static final SimpleDateFormat monthAndYear = new SimpleDateFormat("MMMM yyyy");
    public static final SimpleDateFormat monthDayAndYear = new SimpleDateFormat("MMM d, yyy");
    public static final SimpleDateFormat time = new SimpleDateFormat("h:mm a");
    public static final SimpleDateFormat year = new SimpleDateFormat("yyyy");

    public static String formatDayMonthAtTimeWithTodayYesterday(Date when) {
        Calendar today = Calendar.getInstance();
        CalendarUtil.setToStartOfDay(today);
        Calendar whenCal = Calendar.getInstance();
        whenCal.setTime(when);
        CalendarUtil.setToStartOfDay(whenCal);
        if (today.getTimeInMillis() == whenCal.getTimeInMillis()) {
            return "today at " + time.format(when);
        }
        today.add(6, -1);
        if (today.getTimeInMillis() == whenCal.getTimeInMillis()) {
            return "yesterday at " + time.format(when);
        }
        return dayNameMonthDayAndTime.format(when);
    }
}
