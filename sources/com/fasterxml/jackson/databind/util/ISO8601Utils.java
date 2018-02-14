package com.fasterxml.jackson.databind.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class ISO8601Utils {
    @Deprecated
    private static final String GMT_ID = "GMT";
    @Deprecated
    private static final TimeZone TIMEZONE_GMT = TimeZone.getTimeZone(GMT_ID);
    private static final TimeZone TIMEZONE_UTC;
    private static final TimeZone TIMEZONE_Z;
    private static final String UTC_ID = "UTC";

    static {
        TimeZone timeZone = TimeZone.getTimeZone(UTC_ID);
        TIMEZONE_UTC = timeZone;
        TIMEZONE_Z = timeZone;
    }

    @Deprecated
    public static TimeZone timeZoneGMT() {
        return TIMEZONE_GMT;
    }

    public static String format(Date date) {
        return format(date, false, TIMEZONE_UTC);
    }

    public static String format(Date date, boolean millis) {
        return format(date, millis, TIMEZONE_UTC);
    }

    public static String format(Date date, boolean millis, TimeZone tz) {
        Calendar calendar = new GregorianCalendar(tz, Locale.US);
        calendar.setTime(date);
        StringBuilder formatted = new StringBuilder(((millis ? 4 : 0) + 19) + (tz.getRawOffset() == 0 ? 1 : 6));
        padInt(formatted, calendar.get(1), 4);
        formatted.append('-');
        padInt(formatted, calendar.get(2) + 1, 2);
        formatted.append('-');
        padInt(formatted, calendar.get(5), 2);
        formatted.append('T');
        padInt(formatted, calendar.get(11), 2);
        formatted.append(':');
        padInt(formatted, calendar.get(12), 2);
        formatted.append(':');
        padInt(formatted, calendar.get(13), 2);
        if (millis) {
            formatted.append('.');
            padInt(formatted, calendar.get(14), 3);
        }
        int offset = tz.getOffset(calendar.getTimeInMillis());
        if (offset != 0) {
            int hours = Math.abs((offset / 60000) / 60);
            int minutes = Math.abs((offset / 60000) % 60);
            formatted.append(offset < 0 ? '-' : '+');
            padInt(formatted, hours, 2);
            formatted.append(':');
            padInt(formatted, minutes, 2);
        } else {
            formatted.append('Z');
        }
        return formatted.toString();
    }

    public static Date parse(String date, ParsePosition pos) throws ParseException {
        Exception fail;
        String input;
        String msg;
        ParseException ex;
        try {
            int index = pos.getIndex();
            int i = index + 4;
            int year = parseInt(date, index, i);
            if (checkOffset(date, i, '-')) {
                i++;
            }
            index = i + 2;
            int month = parseInt(date, i, index);
            if (checkOffset(date, index, '-')) {
                i = index + 1;
            } else {
                i = index;
            }
            index = i + 2;
            int day = parseInt(date, i, index);
            int hour = 0;
            int minutes = 0;
            int seconds = 0;
            int milliseconds = 0;
            boolean hasT = checkOffset(date, index, 'T');
            Calendar calendar;
            if (hasT || date.length() > index) {
                if (hasT) {
                    index++;
                    i = index + 2;
                    hour = parseInt(date, index, i);
                    if (checkOffset(date, i, ':')) {
                        i++;
                    }
                    index = i + 2;
                    minutes = parseInt(date, i, index);
                    if (checkOffset(date, index, ':')) {
                        i = index + 1;
                    } else {
                        i = index;
                    }
                    if (date.length() > i) {
                        char c = date.charAt(i);
                        if (!(c == 'Z' || c == '+' || c == '-')) {
                            index = i + 2;
                            seconds = parseInt(date, i, index);
                            if (seconds > 59 && seconds < 63) {
                                seconds = 59;
                            }
                            if (checkOffset(date, index, '.')) {
                                index++;
                                int endOffset = indexOfNonDigit(date, index + 1);
                                int parseEndOffset = Math.min(endOffset, index + 3);
                                int fraction = parseInt(date, index, parseEndOffset);
                                switch (parseEndOffset - index) {
                                    case 1:
                                        milliseconds = fraction * 100;
                                        break;
                                    case 2:
                                        milliseconds = fraction * 10;
                                        break;
                                    default:
                                        milliseconds = fraction;
                                        break;
                                }
                                index = endOffset;
                            }
                        }
                    }
                    index = i;
                }
                if (date.length() <= index) {
                    throw new IllegalArgumentException("No time zone indicator");
                }
                TimeZone timezone;
                char timezoneIndicator = date.charAt(index);
                if (timezoneIndicator == 'Z') {
                    timezone = TIMEZONE_Z;
                    index++;
                } else if (timezoneIndicator == '+' || timezoneIndicator == '-') {
                    String timezoneOffset = date.substring(index);
                    index += timezoneOffset.length();
                    if ("+0000".equals(timezoneOffset) || "+00:00".equals(timezoneOffset)) {
                        timezone = TIMEZONE_Z;
                    } else {
                        String timezoneId = new StringBuilder(GMT_ID).append(timezoneOffset).toString();
                        timezone = TimeZone.getTimeZone(timezoneId);
                        String act = timezone.getID();
                        if (!(act.equals(timezoneId) || act.replace(":", "").equals(timezoneId))) {
                            throw new IndexOutOfBoundsException("Mismatching time zone indicator: " + timezoneId + " given, resolves to " + timezone.getID());
                        }
                    }
                } else {
                    throw new IndexOutOfBoundsException("Invalid time zone indicator '" + timezoneIndicator + "'");
                }
                calendar = new GregorianCalendar(timezone);
                calendar.setLenient(false);
                calendar.set(1, year);
                calendar.set(2, month - 1);
                calendar.set(5, day);
                calendar.set(11, hour);
                calendar.set(12, minutes);
                calendar.set(13, seconds);
                calendar.set(14, milliseconds);
                pos.setIndex(index);
                return calendar.getTime();
            }
            calendar = new GregorianCalendar(year, month - 1, day);
            pos.setIndex(index);
            return calendar.getTime();
        } catch (Exception e) {
            fail = e;
            input = date == null ? null : "\"" + date + '\"';
            msg = fail.getMessage();
            if (msg == null || msg.isEmpty()) {
                msg = "(" + fail.getClass().getName() + ")";
            }
            ex = new ParseException("Failed to parse date " + input + ": " + msg, pos.getIndex());
            ex.initCause(fail);
            throw ex;
        } catch (Exception e2) {
            fail = e2;
            if (date == null) {
            }
            msg = fail.getMessage();
            msg = "(" + fail.getClass().getName() + ")";
            ex = new ParseException("Failed to parse date " + input + ": " + msg, pos.getIndex());
            ex.initCause(fail);
            throw ex;
        } catch (Exception e22) {
            fail = e22;
            if (date == null) {
            }
            msg = fail.getMessage();
            msg = "(" + fail.getClass().getName() + ")";
            ex = new ParseException("Failed to parse date " + input + ": " + msg, pos.getIndex());
            ex.initCause(fail);
            throw ex;
        }
    }

    private static boolean checkOffset(String value, int offset, char expected) {
        return offset < value.length() && value.charAt(offset) == expected;
    }

    private static int parseInt(String value, int beginIndex, int endIndex) throws NumberFormatException {
        if (beginIndex < 0 || endIndex > value.length() || beginIndex > endIndex) {
            throw new NumberFormatException(value);
        }
        int digit;
        int i;
        int i2 = beginIndex;
        int result = 0;
        if (beginIndex < endIndex) {
            i2++;
            digit = Character.digit(value.charAt(beginIndex), 10);
            if (digit < 0) {
                throw new NumberFormatException("Invalid number: " + value.substring(beginIndex, endIndex));
            }
            result = -digit;
            i = i2;
        } else {
            i = i2;
        }
        while (i < endIndex) {
            i2 = i + 1;
            digit = Character.digit(value.charAt(i), 10);
            if (digit < 0) {
                throw new NumberFormatException("Invalid number: " + value.substring(beginIndex, endIndex));
            }
            result = (result * 10) - digit;
            i = i2;
        }
        return -result;
    }

    private static void padInt(StringBuilder buffer, int value, int length) {
        String strValue = Integer.toString(value);
        for (int i = length - strValue.length(); i > 0; i--) {
            buffer.append('0');
        }
        buffer.append(strValue);
    }

    private static int indexOfNonDigit(String string, int offset) {
        int i = offset;
        while (i < string.length()) {
            char c = string.charAt(i);
            if (c < '0' || c > '9') {
                return i;
            }
            i++;
        }
        return string.length();
    }

    public static void main(String[] args) {
        while (true) {
            long start = System.currentTimeMillis();
            long msecs = System.currentTimeMillis() - start;
            System.out.println("Pow (" + test1(250000, 3) + ") -> " + msecs + " ms");
            start = System.currentTimeMillis();
            msecs = System.currentTimeMillis() - start;
            System.out.println("Iter (" + test2(250000, 3) + ") -> " + msecs + " ms");
        }
    }

    static int test1(int reps, int pow) {
        int resp = 3;
        while (true) {
            reps--;
            if (reps < 0) {
                return resp;
            }
            resp = (int) Math.pow(10.0d, (double) pow);
        }
    }

    static int test2(int reps, int pow) {
        int resp = 3;
        while (true) {
            reps--;
            if (reps < 0) {
                return resp;
            }
            resp = 10;
            int p = pow;
            while (true) {
                p--;
                if (p > 0) {
                    resp *= 10;
                }
            }
        }
    }
}
