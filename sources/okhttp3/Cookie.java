package okhttp3;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.internal.Util;
import okhttp3.internal.http.HttpDate;

public final class Cookie {
    private static final Pattern DAY_OF_MONTH_PATTERN = Pattern.compile("(\\d{1,2})[^\\d]*");
    private static final Pattern MONTH_PATTERN = Pattern.compile("(?i)(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec).*");
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d{1,2}):(\\d{1,2}):(\\d{1,2})[^\\d]*");
    private static final Pattern YEAR_PATTERN = Pattern.compile("(\\d{2,4})[^\\d]*");
    private final String domain;
    private final long expiresAt;
    private final boolean hostOnly;
    private final boolean httpOnly;
    private final String name;
    private final String path;
    private final boolean persistent;
    private final boolean secure;
    private final String value;

    private Cookie(String name, String value, long expiresAt, String domain, String path, boolean secure, boolean httpOnly, boolean hostOnly, boolean persistent) {
        this.name = name;
        this.value = value;
        this.expiresAt = expiresAt;
        this.domain = domain;
        this.path = path;
        this.secure = secure;
        this.httpOnly = httpOnly;
        this.hostOnly = hostOnly;
        this.persistent = persistent;
    }

    public final String name() {
        return this.name;
    }

    public final String value() {
        return this.value;
    }

    private static Cookie parse(long currentTimeMillis, HttpUrl url, String setCookie) {
        int limit = setCookie.length();
        int cookiePairEnd = Util.delimiterOffset(setCookie, 0, limit, ';');
        int pairEqualsSign = Util.delimiterOffset(setCookie, 0, cookiePairEnd, '=');
        if (pairEqualsSign == cookiePairEnd) {
            return null;
        }
        String cookieName = Util.trimSubstring(setCookie, 0, pairEqualsSign);
        if (cookieName.isEmpty() || Util.indexOfControlOrNonAscii(cookieName) != -1) {
            return null;
        }
        String cookieValue = Util.trimSubstring(setCookie, pairEqualsSign + 1, cookiePairEnd);
        if (Util.indexOfControlOrNonAscii(cookieValue) != -1) {
            return null;
        }
        String domain;
        long expiresAt = 253402300799999L;
        long deltaSeconds = -1;
        String path = null;
        boolean secureOnly = false;
        boolean httpOnly = false;
        boolean hostOnly = true;
        boolean persistent = false;
        int pos = cookiePairEnd + 1;
        String domain2 = null;
        while (pos < limit) {
            int attributePairEnd = Util.delimiterOffset(setCookie, pos, limit, ';');
            int attributeEqualsSign = Util.delimiterOffset(setCookie, pos, attributePairEnd, '=');
            String attributeName = Util.trimSubstring(setCookie, pos, attributeEqualsSign);
            String attributeValue = attributeEqualsSign < attributePairEnd ? Util.trimSubstring(setCookie, attributeEqualsSign + 1, attributePairEnd) : "";
            if (attributeName.equalsIgnoreCase("expires")) {
                try {
                    int length = attributeValue.length();
                    int dateCharacterOffset = dateCharacterOffset(attributeValue, 0, length, false);
                    int i = -1;
                    int i2 = -1;
                    int i3 = -1;
                    int i4 = -1;
                    int i5 = -1;
                    int i6 = -1;
                    Matcher matcher = TIME_PATTERN.matcher(attributeValue);
                    while (dateCharacterOffset < length) {
                        int dateCharacterOffset2 = dateCharacterOffset(attributeValue, dateCharacterOffset + 1, length, true);
                        matcher.region(dateCharacterOffset, dateCharacterOffset2);
                        if (i == -1 && matcher.usePattern(TIME_PATTERN).matches()) {
                            i = Integer.parseInt(matcher.group(1));
                            i2 = Integer.parseInt(matcher.group(2));
                            i3 = Integer.parseInt(matcher.group(3));
                        } else {
                            if (i4 == -1) {
                                if (matcher.usePattern(DAY_OF_MONTH_PATTERN).matches()) {
                                    i4 = Integer.parseInt(matcher.group(1));
                                }
                            }
                            if (i5 == -1 && matcher.usePattern(MONTH_PATTERN).matches()) {
                                i5 = MONTH_PATTERN.pattern().indexOf(matcher.group(1).toLowerCase(Locale.US)) / 4;
                            } else if (i6 == -1 && matcher.usePattern(YEAR_PATTERN).matches()) {
                                i6 = Integer.parseInt(matcher.group(1));
                            }
                        }
                        dateCharacterOffset = dateCharacterOffset(attributeValue, dateCharacterOffset2 + 1, length, false);
                    }
                    if (i6 >= 70 && i6 <= 99) {
                        i6 += 1900;
                    }
                    if (i6 >= 0 && i6 <= 69) {
                        i6 += 2000;
                    }
                    if (i6 < 1601) {
                        throw new IllegalArgumentException();
                    } else if (i5 == -1) {
                        throw new IllegalArgumentException();
                    } else if (i4 <= 0 || i4 > 31) {
                        throw new IllegalArgumentException();
                    } else if (i < 0 || i > 23) {
                        throw new IllegalArgumentException();
                    } else if (i2 < 0 || i2 > 59) {
                        throw new IllegalArgumentException();
                    } else if (i3 < 0 || i3 > 59) {
                        throw new IllegalArgumentException();
                    } else {
                        Calendar gregorianCalendar = new GregorianCalendar(Util.UTC);
                        gregorianCalendar.setLenient(false);
                        gregorianCalendar.set(1, i6);
                        gregorianCalendar.set(2, i5 - 1);
                        gregorianCalendar.set(5, i4);
                        gregorianCalendar.set(11, i);
                        gregorianCalendar.set(12, i2);
                        gregorianCalendar.set(13, i3);
                        gregorianCalendar.set(14, 0);
                        expiresAt = gregorianCalendar.getTimeInMillis();
                        persistent = true;
                        domain = domain2;
                    }
                } catch (IllegalArgumentException e) {
                    domain = domain2;
                }
            } else {
                if (attributeName.equalsIgnoreCase("max-age")) {
                    try {
                        deltaSeconds = parseMaxAge(attributeValue);
                        persistent = true;
                        domain = domain2;
                    } catch (NumberFormatException e2) {
                        domain = domain2;
                    }
                } else {
                    if (attributeName.equalsIgnoreCase("domain")) {
                        try {
                            if (attributeValue.endsWith(".")) {
                                throw new IllegalArgumentException();
                            }
                            if (attributeValue.startsWith(".")) {
                                attributeValue = attributeValue.substring(1);
                            }
                            domain = Util.domainToAscii(attributeValue);
                            if (domain == null) {
                                throw new IllegalArgumentException();
                            }
                            hostOnly = false;
                        } catch (IllegalArgumentException e3) {
                            domain = domain2;
                        }
                    } else {
                        if (attributeName.equalsIgnoreCase("path")) {
                            path = attributeValue;
                            domain = domain2;
                        } else {
                            if (attributeName.equalsIgnoreCase("secure")) {
                                secureOnly = true;
                                domain = domain2;
                            } else {
                                if (attributeName.equalsIgnoreCase("httponly")) {
                                    httpOnly = true;
                                    domain = domain2;
                                } else {
                                    domain = domain2;
                                }
                            }
                        }
                    }
                }
            }
            pos = attributePairEnd + 1;
            domain2 = domain;
        }
        if (deltaSeconds == Long.MIN_VALUE) {
            expiresAt = Long.MIN_VALUE;
        } else if (deltaSeconds != -1) {
            expiresAt = currentTimeMillis + (deltaSeconds <= 9223372036854775L ? deltaSeconds * 1000 : Long.MAX_VALUE);
            if (expiresAt < currentTimeMillis || expiresAt > 253402300799999L) {
                expiresAt = 253402300799999L;
            }
        }
        if (domain2 == null) {
            domain = url.host;
        } else {
            Object obj;
            String str = url.host;
            if (str.equals(domain2)) {
                obj = 1;
            } else if (str.endsWith(domain2) && str.charAt((str.length() - domain2.length()) - 1) == '.' && !Util.verifyAsIpAddress(str)) {
                obj = 1;
            } else {
                obj = null;
            }
            if (obj == null) {
                return null;
            }
            domain = domain2;
        }
        if (path == null || !path.startsWith("/")) {
            String encodedPath = url.encodedPath();
            int lastSlash = encodedPath.lastIndexOf(47);
            path = lastSlash != 0 ? encodedPath.substring(0, lastSlash) : "/";
        }
        return new Cookie(cookieName, cookieValue, expiresAt, domain, path, secureOnly, httpOnly, hostOnly, persistent);
    }

    private static int dateCharacterOffset(String input, int pos, int limit, boolean invert) {
        for (int i = pos; i < limit; i++) {
            boolean dateCharacter;
            boolean z;
            int c = input.charAt(i);
            if ((c >= 32 || c == 9) && c < 127 && ((c < 48 || c > 57) && ((c < 97 || c > 122) && ((c < 65 || c > 90) && c != 58)))) {
                dateCharacter = false;
            } else {
                dateCharacter = true;
            }
            if (invert) {
                z = false;
            } else {
                z = true;
            }
            if (dateCharacter == z) {
                return i;
            }
        }
        return limit;
    }

    private static long parseMaxAge(String s) {
        try {
            long parsed = Long.parseLong(s);
            if (parsed <= 0) {
                return Long.MIN_VALUE;
            }
            return parsed;
        } catch (NumberFormatException e) {
            if (s.matches("-?\\d+")) {
                return s.startsWith("-") ? Long.MIN_VALUE : Long.MAX_VALUE;
            } else {
                throw e;
            }
        }
    }

    public static List<Cookie> parseAll(HttpUrl url, Headers headers) {
        List<String> cookieStrings = headers.values("Set-Cookie");
        List<Cookie> cookies = null;
        int size = cookieStrings.size();
        for (int i = 0; i < size; i++) {
            Cookie cookie = parse(System.currentTimeMillis(), url, (String) cookieStrings.get(i));
            if (cookie != null) {
                if (cookies == null) {
                    cookies = new ArrayList();
                }
                cookies.add(cookie);
            }
        }
        if (cookies != null) {
            return Collections.unmodifiableList(cookies);
        }
        return Collections.emptyList();
    }

    public final boolean equals(Object other) {
        if (!(other instanceof Cookie)) {
            return false;
        }
        Cookie that = (Cookie) other;
        if (that.name.equals(this.name) && that.value.equals(this.value) && that.domain.equals(this.domain) && that.path.equals(this.path) && that.expiresAt == this.expiresAt && that.secure == this.secure && that.httpOnly == this.httpOnly && that.persistent == this.persistent && that.hostOnly == this.hostOnly) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        int i;
        int i2 = 0;
        int hashCode = (((((((((this.name.hashCode() + 527) * 31) + this.value.hashCode()) * 31) + this.domain.hashCode()) * 31) + this.path.hashCode()) * 31) + ((int) (this.expiresAt ^ (this.expiresAt >>> 32)))) * 31;
        if (this.secure) {
            i = 0;
        } else {
            i = 1;
        }
        hashCode = (hashCode + i) * 31;
        if (this.httpOnly) {
            i = 0;
        } else {
            i = 1;
        }
        hashCode = (hashCode + i) * 31;
        if (this.persistent) {
            i = 0;
        } else {
            i = 1;
        }
        i = (hashCode + i) * 31;
        if (!this.hostOnly) {
            i2 = 1;
        }
        return i + i2;
    }

    public final String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.name);
        stringBuilder.append('=');
        stringBuilder.append(this.value);
        if (this.persistent) {
            if (this.expiresAt == Long.MIN_VALUE) {
                stringBuilder.append("; max-age=0");
            } else {
                stringBuilder.append("; expires=").append(HttpDate.format(new Date(this.expiresAt)));
            }
        }
        if (!this.hostOnly) {
            stringBuilder.append("; domain=");
            stringBuilder.append(this.domain);
        }
        stringBuilder.append("; path=").append(this.path);
        if (this.secure) {
            stringBuilder.append("; secure");
        }
        if (this.httpOnly) {
            stringBuilder.append("; httponly");
        }
        return stringBuilder.toString();
    }
}
