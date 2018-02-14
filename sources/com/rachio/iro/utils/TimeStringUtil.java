package com.rachio.iro.utils;

import java.util.Calendar;
import java.util.Date;

public class TimeStringUtil {
    public static final String getStringForNumberOfHoursMinutesAndSeconds(int seconds, boolean shortFormat) {
        StringBuilder sb = new StringBuilder();
        int remaining = seconds % 3600;
        int hours = (seconds - remaining) / 3600;
        seconds = remaining;
        remaining %= 60;
        int minutes = (seconds - remaining) / 60;
        seconds = remaining;
        String minutesSuffix = shortFormat ? "mins" : "minutes";
        if (minutes == 1) {
            minutesSuffix = shortFormat ? "min" : "minute";
        }
        String secondsSuffix = shortFormat ? "secs" : "seconds";
        if (hours == 0 && minutes == 0 && seconds == 0) {
            return "0";
        }
        if (hours > 0) {
            sb.append(hours + " hour");
            if (hours > 1) {
                sb.append("s");
            }
        }
        if (minutes > 0) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(minutes + " " + minutesSuffix);
        }
        if (seconds > 0) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(seconds + " " + secondsSuffix);
        }
        return sb.toString();
    }

    public static final String getStringForNumberOfHoursMinutesAndSecondsCompact(int seconds) {
        int remaining = seconds % 3600;
        int hours = (seconds - remaining) / 3600;
        seconds = remaining;
        remaining %= 60;
        int minutes = (seconds - remaining) / 60;
        seconds = remaining;
        if (hours > 0) {
            if (seconds > 0) {
                return hours + "h " + minutes + "m " + seconds + "s";
            }
            return hours + "h " + minutes + "m";
        } else if (seconds > 0) {
            return minutes + "m " + seconds + "s";
        } else {
            return minutes + "m";
        }
    }

    public static final String getTimeOfDay(int startHour, int startMinute) {
        startHour = (startHour + ((startMinute - (startMinute % 60)) / 60)) % 24;
        String suffix = "AM";
        if (startHour == 0) {
            startHour = 12;
        } else if (startHour >= 12) {
            suffix = "PM";
            if (startHour == 12) {
                startHour = 12;
            } else {
                startHour -= 12;
            }
        }
        return String.format("%d:%02d %s", new Object[]{Integer.valueOf(startHour), Integer.valueOf(minutes), suffix});
    }

    public static final String getStringForHoursAndMinutesFromSecondsRounded(int seconds, boolean compact) {
        int looseSeconds = seconds % 60;
        int rounded = seconds - looseSeconds;
        if (looseSeconds > 0) {
            rounded += 60;
        }
        return getStringForNumberOfHoursMinutesAndSeconds(rounded, true);
    }

    public static final String getDisplayDate$47d5fde(Date eventDate) {
        Calendar eventCal = Calendar.getInstance();
        Calendar referenceCal = Calendar.getInstance();
        eventCal.setTime(eventDate);
        if (eventCal.get(1) == referenceCal.get(1) && eventCal.get(6) == referenceCal.get(6)) {
            return "Today";
        }
        referenceCal.add(5, -1);
        if (eventCal.get(1) == referenceCal.get(1) && eventCal.get(6) == referenceCal.get(6)) {
            return "Yesterday";
        }
        referenceCal.add(5, 2);
        if (eventCal.get(1) == referenceCal.get(1) && eventCal.get(6) == referenceCal.get(6)) {
            return "Tomorrow";
        }
        if (eventCal.get(1) == referenceCal.get(1)) {
            return DateFormats.monthAndDay.format(eventDate);
        }
        return DateFormats.monthDayAndYear.format(eventDate);
    }

    public static final String getDisplayDateTimeOfEvent(Date eventDate) {
        return getDisplayDate$47d5fde(eventDate) + " at " + DateFormats.time.format(eventDate);
    }
}
