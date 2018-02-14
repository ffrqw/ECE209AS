package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.core.io.NumberInput;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class StdDateFormat extends DateFormat {
    protected static final String[] ALL_FORMATS = new String[]{DATE_FORMAT_STR_ISO8601, DATE_FORMAT_STR_ISO8601_Z, DATE_FORMAT_STR_RFC1123, DATE_FORMAT_STR_PLAIN};
    protected static final DateFormat DATE_FORMAT_ISO8601;
    protected static final DateFormat DATE_FORMAT_ISO8601_Z;
    protected static final DateFormat DATE_FORMAT_PLAIN;
    protected static final DateFormat DATE_FORMAT_RFC1123;
    public static final String DATE_FORMAT_STR_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    protected static final String DATE_FORMAT_STR_ISO8601_Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    protected static final String DATE_FORMAT_STR_PLAIN = "yyyy-MM-dd";
    protected static final String DATE_FORMAT_STR_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final Locale DEFAULT_LOCALE = Locale.US;
    private static final TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone("UTC");
    public static final StdDateFormat instance = new StdDateFormat();
    protected transient DateFormat _formatISO8601;
    protected transient DateFormat _formatISO8601_z;
    protected transient DateFormat _formatPlain;
    protected transient DateFormat _formatRFC1123;
    protected Boolean _lenient;
    protected final Locale _locale;
    protected transient TimeZone _timezone;

    static {
        DateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_STR_RFC1123, DEFAULT_LOCALE);
        DATE_FORMAT_RFC1123 = simpleDateFormat;
        simpleDateFormat.setTimeZone(DEFAULT_TIMEZONE);
        simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_STR_ISO8601, DEFAULT_LOCALE);
        DATE_FORMAT_ISO8601 = simpleDateFormat;
        simpleDateFormat.setTimeZone(DEFAULT_TIMEZONE);
        simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_STR_ISO8601_Z, DEFAULT_LOCALE);
        DATE_FORMAT_ISO8601_Z = simpleDateFormat;
        simpleDateFormat.setTimeZone(DEFAULT_TIMEZONE);
        simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_STR_PLAIN, DEFAULT_LOCALE);
        DATE_FORMAT_PLAIN = simpleDateFormat;
        simpleDateFormat.setTimeZone(DEFAULT_TIMEZONE);
    }

    public StdDateFormat() {
        this._locale = DEFAULT_LOCALE;
    }

    @Deprecated
    public StdDateFormat(TimeZone tz, Locale loc) {
        this._timezone = tz;
        this._locale = loc;
    }

    protected StdDateFormat(TimeZone tz, Locale loc, Boolean lenient) {
        this._timezone = tz;
        this._locale = loc;
        this._lenient = lenient;
    }

    public static TimeZone getDefaultTimeZone() {
        return DEFAULT_TIMEZONE;
    }

    public StdDateFormat withTimeZone(TimeZone tz) {
        if (tz == null) {
            tz = DEFAULT_TIMEZONE;
        }
        return (tz == this._timezone || tz.equals(this._timezone)) ? this : new StdDateFormat(tz, this._locale, this._lenient);
    }

    public StdDateFormat withLocale(Locale loc) {
        return loc.equals(this._locale) ? this : new StdDateFormat(this._timezone, loc, this._lenient);
    }

    public StdDateFormat clone() {
        return new StdDateFormat(this._timezone, this._locale, this._lenient);
    }

    @Deprecated
    public static DateFormat getISO8601Format(TimeZone tz) {
        return getISO8601Format(tz, DEFAULT_LOCALE);
    }

    public static DateFormat getISO8601Format(TimeZone tz, Locale loc) {
        return _cloneFormat(DATE_FORMAT_ISO8601, DATE_FORMAT_STR_ISO8601, tz, loc, null);
    }

    public static DateFormat getRFC1123Format(TimeZone tz, Locale loc) {
        return _cloneFormat(DATE_FORMAT_RFC1123, DATE_FORMAT_STR_RFC1123, tz, loc, null);
    }

    @Deprecated
    public static DateFormat getRFC1123Format(TimeZone tz) {
        return getRFC1123Format(tz, DEFAULT_LOCALE);
    }

    public TimeZone getTimeZone() {
        return this._timezone;
    }

    public void setTimeZone(TimeZone tz) {
        if (!tz.equals(this._timezone)) {
            _clearFormats();
            this._timezone = tz;
        }
    }

    public void setLenient(boolean enabled) {
        Boolean newValue = Boolean.valueOf(enabled);
        if (this._lenient != newValue) {
            this._lenient = newValue;
            _clearFormats();
        }
    }

    public boolean isLenient() {
        if (this._lenient == null) {
            return true;
        }
        return this._lenient.booleanValue();
    }

    public Date parse(String dateStr) throws ParseException {
        Date dt;
        dateStr = dateStr.trim();
        ParsePosition pos = new ParsePosition(0);
        if (looksLikeISO8601(dateStr)) {
            dt = parseAsISO8601(dateStr, pos, true);
        } else {
            int i = dateStr.length();
            while (true) {
                i--;
                if (i < 0) {
                    break;
                }
                char ch = dateStr.charAt(i);
                if ((ch < '0' || ch > '9') && (i > 0 || ch != '-')) {
                    break;
                }
            }
            if (i >= 0 || !(dateStr.charAt(0) == '-' || NumberInput.inLongRange(dateStr, false))) {
                dt = parseAsRFC1123(dateStr, pos);
            } else {
                dt = new Date(Long.parseLong(dateStr));
            }
        }
        if (dt != null) {
            return dt;
        }
        StringBuilder sb = new StringBuilder();
        for (String f : ALL_FORMATS) {
            if (sb.length() > 0) {
                sb.append("\", \"");
            } else {
                sb.append('\"');
            }
            sb.append(f);
        }
        sb.append('\"');
        throw new ParseException(String.format("Can not parse date \"%s\": not compatible with any of standard forms (%s)", new Object[]{dateStr, sb.toString()}), pos.getErrorIndex());
    }

    public Date parse(String dateStr, ParsePosition pos) {
        if (looksLikeISO8601(dateStr)) {
            try {
                return parseAsISO8601(dateStr, pos, false);
            } catch (ParseException e) {
                return null;
            }
        }
        int i = dateStr.length();
        while (true) {
            i--;
            if (i < 0) {
                break;
            }
            char ch = dateStr.charAt(i);
            if ((ch < '0' || ch > '9') && (i > 0 || ch != '-')) {
                break;
            }
        }
        if (i >= 0 || (dateStr.charAt(0) != '-' && !NumberInput.inLongRange(dateStr, false))) {
            return parseAsRFC1123(dateStr, pos);
        }
        return new Date(Long.parseLong(dateStr));
    }

    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        if (this._formatISO8601 == null) {
            this._formatISO8601 = _cloneFormat(DATE_FORMAT_ISO8601, DATE_FORMAT_STR_ISO8601, this._timezone, this._locale, this._lenient);
        }
        return this._formatISO8601.format(date, toAppendTo, fieldPosition);
    }

    public String toString() {
        String str = "DateFormat " + getClass().getName();
        TimeZone tz = this._timezone;
        if (tz != null) {
            str = str + " (timezone: " + tz + ")";
        }
        return str + "(locale: " + this._locale + ")";
    }

    public boolean equals(Object o) {
        return o == this;
    }

    public int hashCode() {
        return System.identityHashCode(this);
    }

    protected boolean looksLikeISO8601(String dateStr) {
        if (dateStr.length() >= 5 && Character.isDigit(dateStr.charAt(0)) && Character.isDigit(dateStr.charAt(3)) && dateStr.charAt(4) == '-') {
            return true;
        }
        return false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected java.util.Date parseAsISO8601(java.lang.String r14, java.text.ParsePosition r15, boolean r16) throws java.text.ParseException {
        /*
        r13 = this;
        r4 = r14.length();
        r8 = r4 + -1;
        r0 = r14.charAt(r8);
        r8 = 10;
        if (r4 > r8) goto L_0x004c;
    L_0x000e:
        r8 = java.lang.Character.isDigit(r0);
        if (r8 == 0) goto L_0x004c;
    L_0x0014:
        r1 = r13._formatPlain;
        r3 = "yyyy-MM-dd";
        if (r1 != 0) goto L_0x0028;
    L_0x001a:
        r8 = DATE_FORMAT_PLAIN;
        r9 = r13._timezone;
        r10 = r13._locale;
        r11 = r13._lenient;
        r1 = _cloneFormat(r8, r3, r9, r10, r11);
        r13._formatPlain = r1;
    L_0x0028:
        r2 = r1.parse(r14, r15);
        if (r2 != 0) goto L_0x0160;
    L_0x002e:
        r8 = new java.text.ParseException;
        r9 = "Can not parse date \"%s\": while it seems to fit format '%s', parsing fails (leniency? %s)";
        r10 = 3;
        r10 = new java.lang.Object[r10];
        r11 = 0;
        r10[r11] = r14;
        r11 = 1;
        r10[r11] = r3;
        r11 = 2;
        r12 = r13._lenient;
        r10[r11] = r12;
        r9 = java.lang.String.format(r9, r10);
        r10 = r15.getErrorIndex();
        r8.<init>(r9, r10);
        throw r8;
    L_0x004c:
        r8 = 90;
        if (r0 != r8) goto L_0x007f;
    L_0x0050:
        r1 = r13._formatISO8601_z;
        r3 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        if (r1 != 0) goto L_0x0064;
    L_0x0056:
        r8 = DATE_FORMAT_ISO8601_Z;
        r9 = r13._timezone;
        r10 = r13._locale;
        r11 = r13._lenient;
        r1 = _cloneFormat(r8, r3, r9, r10, r11);
        r13._formatISO8601_z = r1;
    L_0x0064:
        r8 = r4 + -4;
        r8 = r14.charAt(r8);
        r9 = 58;
        if (r8 != r9) goto L_0x0028;
    L_0x006e:
        r6 = new java.lang.StringBuilder;
        r6.<init>(r14);
        r8 = r4 + -1;
        r9 = ".000";
        r6.insert(r8, r9);
        r14 = r6.toString();
        goto L_0x0028;
    L_0x007f:
        r8 = hasTimeZone(r14);
        if (r8 == 0) goto L_0x0116;
    L_0x0085:
        r8 = r4 + -3;
        r0 = r14.charAt(r8);
        r8 = 58;
        if (r0 != r8) goto L_0x00d7;
    L_0x008f:
        r6 = new java.lang.StringBuilder;
        r6.<init>(r14);
        r8 = r4 + -3;
        r9 = r4 + -2;
        r6.delete(r8, r9);
        r14 = r6.toString();
    L_0x009f:
        r4 = r14.length();
        r8 = 84;
        r8 = r14.lastIndexOf(r8);
        r8 = r4 - r8;
        r7 = r8 + -6;
        r8 = 12;
        if (r7 >= r8) goto L_0x00bf;
    L_0x00b1:
        r5 = r4 + -5;
        r6 = new java.lang.StringBuilder;
        r6.<init>(r14);
        switch(r7) {
            case 5: goto L_0x0110;
            case 6: goto L_0x010b;
            case 7: goto L_0x00bb;
            case 8: goto L_0x0105;
            case 9: goto L_0x00ff;
            case 10: goto L_0x00f9;
            case 11: goto L_0x00f3;
            default: goto L_0x00bb;
        };
    L_0x00bb:
        r14 = r6.toString();
    L_0x00bf:
        r1 = r13._formatISO8601;
        r3 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        r8 = r13._formatISO8601;
        if (r8 != 0) goto L_0x0028;
    L_0x00c7:
        r8 = DATE_FORMAT_ISO8601;
        r9 = r13._timezone;
        r10 = r13._locale;
        r11 = r13._lenient;
        r1 = _cloneFormat(r8, r3, r9, r10, r11);
        r13._formatISO8601 = r1;
        goto L_0x0028;
    L_0x00d7:
        r8 = 43;
        if (r0 == r8) goto L_0x00df;
    L_0x00db:
        r8 = 45;
        if (r0 != r8) goto L_0x009f;
    L_0x00df:
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r8 = r8.append(r14);
        r9 = "00";
        r8 = r8.append(r9);
        r14 = r8.toString();
        goto L_0x009f;
    L_0x00f3:
        r8 = 48;
        r6.insert(r5, r8);
        goto L_0x00bb;
    L_0x00f9:
        r8 = "00";
        r6.insert(r5, r8);
        goto L_0x00bb;
    L_0x00ff:
        r8 = "000";
        r6.insert(r5, r8);
        goto L_0x00bb;
    L_0x0105:
        r8 = ".000";
        r6.insert(r5, r8);
        goto L_0x00bb;
    L_0x010b:
        r8 = "00.000";
        r6.insert(r5, r8);
    L_0x0110:
        r8 = ":00.000";
        r6.insert(r5, r8);
        goto L_0x00bb;
    L_0x0116:
        r6 = new java.lang.StringBuilder;
        r6.<init>(r14);
        r8 = 84;
        r8 = r14.lastIndexOf(r8);
        r8 = r4 - r8;
        r7 = r8 + -1;
        r8 = 12;
        if (r7 >= r8) goto L_0x0131;
    L_0x0129:
        switch(r7) {
            case 9: goto L_0x015a;
            case 10: goto L_0x0155;
            case 11: goto L_0x0150;
            default: goto L_0x012c;
        };
    L_0x012c:
        r8 = ".000";
        r6.append(r8);
    L_0x0131:
        r8 = 90;
        r6.append(r8);
        r14 = r6.toString();
        r1 = r13._formatISO8601_z;
        r3 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        if (r1 != 0) goto L_0x0028;
    L_0x0140:
        r8 = DATE_FORMAT_ISO8601_Z;
        r9 = r13._timezone;
        r10 = r13._locale;
        r11 = r13._lenient;
        r1 = _cloneFormat(r8, r3, r9, r10, r11);
        r13._formatISO8601_z = r1;
        goto L_0x0028;
    L_0x0150:
        r8 = 48;
        r6.append(r8);
    L_0x0155:
        r8 = 48;
        r6.append(r8);
    L_0x015a:
        r8 = 48;
        r6.append(r8);
        goto L_0x0131;
    L_0x0160:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.util.StdDateFormat.parseAsISO8601(java.lang.String, java.text.ParsePosition, boolean):java.util.Date");
    }

    protected Date parseAsRFC1123(String dateStr, ParsePosition pos) {
        if (this._formatRFC1123 == null) {
            this._formatRFC1123 = _cloneFormat(DATE_FORMAT_RFC1123, DATE_FORMAT_STR_RFC1123, this._timezone, this._locale, this._lenient);
        }
        return this._formatRFC1123.parse(dateStr, pos);
    }

    private static final boolean hasTimeZone(String str) {
        int len = str.length();
        if (len >= 6) {
            char c = str.charAt(len - 6);
            if (c == '+' || c == '-') {
                return true;
            }
            c = str.charAt(len - 5);
            if (c == '+' || c == '-') {
                return true;
            }
            c = str.charAt(len - 3);
            if (c == '+' || c == '-') {
                return true;
            }
        }
        return false;
    }

    private static final DateFormat _cloneFormat(DateFormat df, String format, TimeZone tz, Locale loc, Boolean lenient) {
        if (loc.equals(DEFAULT_LOCALE)) {
            df = (DateFormat) df.clone();
            if (tz != null) {
                df.setTimeZone(tz);
            }
        } else {
            df = new SimpleDateFormat(format, loc);
            if (tz == null) {
                tz = DEFAULT_TIMEZONE;
            }
            df.setTimeZone(tz);
        }
        if (lenient != null) {
            df.setLenient(lenient.booleanValue());
        }
        return df;
    }

    protected void _clearFormats() {
        this._formatRFC1123 = null;
        this._formatISO8601 = null;
        this._formatISO8601_z = null;
        this._formatPlain = null;
    }
}
