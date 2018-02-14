package com.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Locale;
import java.util.TimeZone;

@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonFormat {

    public enum Feature {
        ACCEPT_SINGLE_VALUE_AS_ARRAY,
        ACCEPT_CASE_INSENSITIVE_PROPERTIES,
        WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS,
        WRITE_DATES_WITH_ZONE_ID,
        WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED,
        WRITE_SORTED_MAP_ENTRIES,
        ADJUST_DATES_TO_CONTEXT_TIME_ZONE
    }

    public static class Features {
        private static final Features EMPTY = new Features(0, 0);
        private final int _disabled;
        private final int _enabled;

        private Features(int e, int d) {
            this._enabled = e;
            this._disabled = d;
        }

        public static Features empty() {
            return EMPTY;
        }

        public static Features construct(JsonFormat f) {
            return construct(f.with(), f.without());
        }

        public static Features construct(Feature[] enabled, Feature[] disabled) {
            int i$;
            int e = 0;
            Feature[] arr$ = enabled;
            for (i$ = 0; i$ < enabled.length; i$++) {
                e |= 1 << arr$[i$].ordinal();
            }
            int d = 0;
            arr$ = disabled;
            for (i$ = 0; i$ < disabled.length; i$++) {
                d |= 1 << arr$[i$].ordinal();
            }
            return new Features(e, d);
        }

        public Features withOverrides(Features overrides) {
            if (overrides == null) {
                return this;
            }
            int overrideD = overrides._disabled;
            int overrideE = overrides._enabled;
            if (overrideD == 0 && overrideE == 0) {
                return this;
            }
            if (this._enabled == 0 && this._disabled == 0) {
                return overrides;
            }
            int newE = (this._enabled & (overrideD ^ -1)) | overrideE;
            int newD = (this._disabled & (overrideE ^ -1)) | overrideD;
            if (newE == this._enabled && newD == this._disabled) {
                return this;
            }
            this(newE, newD);
            return this;
        }

        public Boolean get(Feature f) {
            int mask = 1 << f.ordinal();
            if ((this._disabled & mask) != 0) {
                return Boolean.FALSE;
            }
            if ((this._enabled & mask) != 0) {
                return Boolean.TRUE;
            }
            return null;
        }

        public int hashCode() {
            return this._disabled + this._enabled;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o.getClass() != getClass()) {
                return false;
            }
            Features other = (Features) o;
            if (other._enabled == this._enabled && other._disabled == this._disabled) {
                return true;
            }
            return false;
        }
    }

    public enum Shape {
        ANY,
        NATURAL,
        SCALAR,
        ARRAY,
        OBJECT,
        NUMBER,
        NUMBER_FLOAT,
        NUMBER_INT,
        STRING,
        BOOLEAN;

        public final boolean isNumeric() {
            return this == NUMBER || this == NUMBER_INT || this == NUMBER_FLOAT;
        }
    }

    public static class Value implements Serializable {
        private static final Value EMPTY = new Value();
        private final Features _features;
        private final Locale _locale;
        private final String _pattern;
        private final Shape _shape;
        private transient TimeZone _timezone;
        private final String _timezoneStr;

        public Value() {
            this("", Shape.ANY, "", "", Features.empty());
        }

        public Value(JsonFormat ann) {
            this(ann.pattern(), ann.shape(), ann.locale(), ann.timezone(), Features.construct(ann));
        }

        public Value(String p, Shape sh, String localeStr, String tzStr, Features f) {
            Locale locale = (localeStr == null || localeStr.length() == 0 || "##default".equals(localeStr)) ? null : new Locale(localeStr);
            String str = (tzStr == null || tzStr.length() == 0 || "##default".equals(tzStr)) ? null : tzStr;
            this(p, sh, locale, str, null, f);
        }

        public Value(String p, Shape sh, Locale l, String tzStr, TimeZone tz, Features f) {
            this._pattern = p;
            if (sh == null) {
                sh = Shape.ANY;
            }
            this._shape = sh;
            this._locale = l;
            this._timezone = tz;
            this._timezoneStr = tzStr;
            if (f == null) {
                f = Features.empty();
            }
            this._features = f;
        }

        public static final Value empty() {
            return EMPTY;
        }

        public final Value withOverrides(Value overrides) {
            if (overrides == null || overrides == EMPTY) {
                return this;
            }
            if (this == EMPTY) {
                return overrides;
            }
            TimeZone tz;
            String p = overrides._pattern;
            if (p == null || p.isEmpty()) {
                p = this._pattern;
            }
            Shape sh = overrides._shape;
            if (sh == Shape.ANY) {
                sh = this._shape;
            }
            Locale l = overrides._locale;
            if (l == null) {
                l = this._locale;
            }
            Features f = this._features;
            if (f == null) {
                f = overrides._features;
            } else {
                f = f.withOverrides(overrides._features);
            }
            String tzStr = overrides._timezoneStr;
            if (tzStr == null || tzStr.isEmpty()) {
                tzStr = this._timezoneStr;
                tz = this._timezone;
            } else {
                tz = overrides._timezone;
            }
            return new Value(p, sh, l, tzStr, tz, f);
        }

        public String getPattern() {
            return this._pattern;
        }

        public Shape getShape() {
            return this._shape;
        }

        public Locale getLocale() {
            return this._locale;
        }

        public TimeZone getTimeZone() {
            TimeZone tz = this._timezone;
            if (tz == null) {
                if (this._timezoneStr == null) {
                    return null;
                }
                tz = TimeZone.getTimeZone(this._timezoneStr);
                this._timezone = tz;
            }
            return tz;
        }

        public boolean hasShape() {
            return this._shape != Shape.ANY;
        }

        public boolean hasPattern() {
            return this._pattern != null && this._pattern.length() > 0;
        }

        public boolean hasLocale() {
            return this._locale != null;
        }

        public boolean hasTimeZone() {
            return (this._timezone == null && (this._timezoneStr == null || this._timezoneStr.isEmpty())) ? false : true;
        }

        public Boolean getFeature(Feature f) {
            return this._features.get(f);
        }

        public String toString() {
            return String.format("[pattern=%s,shape=%s,locale=%s,timezone=%s]", new Object[]{this._pattern, this._shape, this._locale, this._timezoneStr});
        }

        public int hashCode() {
            int hash = this._timezoneStr == null ? 1 : this._timezoneStr.hashCode();
            if (this._pattern != null) {
                hash ^= this._pattern.hashCode();
            }
            hash += this._shape.hashCode();
            if (this._locale != null) {
                hash ^= this._locale.hashCode();
            }
            return hash + this._features.hashCode();
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o.getClass() != getClass()) {
                return false;
            }
            Value other = (Value) o;
            if (this._shape != other._shape || !this._features.equals(other._features)) {
                return false;
            }
            if (_equal(this._timezoneStr, other._timezoneStr) && _equal(this._pattern, other._pattern) && _equal(this._timezone, other._timezone) && _equal(this._locale, other._locale)) {
                return true;
            }
            return false;
        }

        private static <T> boolean _equal(T value1, T value2) {
            if (value1 == null) {
                if (value2 == null) {
                    return true;
                }
                return false;
            } else if (value2 != null) {
                return value1.equals(value2);
            } else {
                return false;
            }
        }
    }

    String locale() default "##default";

    String pattern() default "";

    Shape shape() default Shape.ANY;

    String timezone() default "##default";

    Feature[] with() default {};

    Feature[] without() default {};
}
