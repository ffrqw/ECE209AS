package com.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonIgnoreProperties {

    public static class Value implements Serializable {
        protected static final Value EMPTY = new Value(Collections.emptySet(), false, false, false, true);
        protected final boolean _allowGetters;
        protected final boolean _allowSetters;
        protected final boolean _ignoreUnknown;
        protected final Set<String> _ignored;
        protected final boolean _merge;

        protected Value(Set<String> ignored, boolean ignoreUnknown, boolean allowGetters, boolean allowSetters, boolean merge) {
            if (ignored == null) {
                this._ignored = Collections.emptySet();
            } else {
                this._ignored = ignored;
            }
            this._ignoreUnknown = ignoreUnknown;
            this._allowGetters = allowGetters;
            this._allowSetters = allowSetters;
            this._merge = merge;
        }

        public static Value from(JsonIgnoreProperties src) {
            if (src == null) {
                return null;
            }
            return construct(_asSet(src.value()), src.ignoreUnknown(), src.allowGetters(), src.allowSetters(), false);
        }

        public static Value construct(Set<String> ignored, boolean ignoreUnknown, boolean allowGetters, boolean allowSetters, boolean merge) {
            if (_empty(ignored, ignoreUnknown, allowGetters, allowSetters, merge)) {
                return EMPTY;
            }
            if (_empty(ignored, ignoreUnknown, allowGetters, allowSetters, merge)) {
                return EMPTY;
            }
            return new Value(ignored, ignoreUnknown, allowGetters, allowSetters, merge);
        }

        public static Value empty() {
            return EMPTY;
        }

        public static Value merge(Value base, Value overrides) {
            return base == null ? overrides : base.withOverrides(overrides);
        }

        public static Value forIgnoredProperties(String... propNames) {
            if (propNames.length == 0) {
                return EMPTY;
            }
            return EMPTY.withIgnored(_asSet(propNames));
        }

        public Value withOverrides(Value overrides) {
            if (overrides == null || overrides == EMPTY) {
                return this;
            }
            if (!overrides._merge) {
                return overrides;
            }
            if (_equals(this, overrides)) {
                return this;
            }
            boolean ignoreUnknown;
            boolean allowGetters;
            boolean allowSetters;
            Set<String> ignored = _merge(this._ignored, overrides._ignored);
            if (this._ignoreUnknown || overrides._ignoreUnknown) {
                ignoreUnknown = true;
            } else {
                ignoreUnknown = false;
            }
            if (this._allowGetters || overrides._allowGetters) {
                allowGetters = true;
            } else {
                allowGetters = false;
            }
            if (this._allowSetters || overrides._allowSetters) {
                allowSetters = true;
            } else {
                allowSetters = false;
            }
            return construct(ignored, ignoreUnknown, allowGetters, allowSetters, true);
        }

        public Value withIgnored(Set<String> ignored) {
            return construct(ignored, this._ignoreUnknown, this._allowGetters, this._allowSetters, this._merge);
        }

        public Value withIgnoreUnknown() {
            return this._ignoreUnknown ? this : construct(this._ignored, true, this._allowGetters, this._allowSetters, this._merge);
        }

        public Value withoutIgnoreUnknown() {
            return !this._ignoreUnknown ? this : construct(this._ignored, false, this._allowGetters, this._allowSetters, this._merge);
        }

        public Set<String> getIgnored() {
            return this._ignored;
        }

        public Set<String> findIgnoredForSerialization() {
            if (this._allowGetters) {
                return Collections.emptySet();
            }
            return this._ignored;
        }

        public Set<String> findIgnoredForDeserialization() {
            if (this._allowSetters) {
                return Collections.emptySet();
            }
            return this._ignored;
        }

        public boolean getIgnoreUnknown() {
            return this._ignoreUnknown;
        }

        public boolean getAllowGetters() {
            return this._allowGetters;
        }

        public boolean getAllowSetters() {
            return this._allowSetters;
        }

        public String toString() {
            return String.format("[ignored=%s,ignoreUnknown=%s,allowGetters=%s,allowSetters=%s,merge=%s]", new Object[]{this._ignored, Boolean.valueOf(this._ignoreUnknown), Boolean.valueOf(this._allowGetters), Boolean.valueOf(this._allowSetters), Boolean.valueOf(this._merge)});
        }

        public int hashCode() {
            return (this._merge ? 11 : -13) + (((this._ignored.size() + (this._ignoreUnknown ? 1 : -3)) + (this._allowGetters ? 3 : -7)) + (this._allowSetters ? 7 : -11));
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o.getClass() == getClass() && _equals(this, (Value) o)) {
                return true;
            }
            return false;
        }

        private static boolean _equals(Value a, Value b) {
            return a._ignoreUnknown == b._ignoreUnknown && a._merge == b._merge && a._allowGetters == b._allowGetters && a._allowSetters == b._allowSetters && a._ignored.equals(b._ignored);
        }

        private static Set<String> _asSet(String[] v) {
            if (v == null || v.length == 0) {
                return Collections.emptySet();
            }
            Set<String> s = new HashSet(v.length);
            String[] arr$ = v;
            int len$ = v.length;
            for (int i$ = 0; i$ < len$; i$++) {
                s.add(arr$[i$]);
            }
            return s;
        }

        private static Set<String> _merge(Set<String> s1, Set<String> s2) {
            if (s1.isEmpty()) {
                return s2;
            }
            if (s2.isEmpty()) {
                return s1;
            }
            HashSet<String> result = new HashSet(s1.size() + s2.size());
            result.addAll(s1);
            result.addAll(s2);
            return result;
        }

        private static boolean _empty(Set<String> ignored, boolean ignoreUnknown, boolean allowGetters, boolean allowSetters, boolean merge) {
            if (ignoreUnknown != EMPTY._ignoreUnknown || allowGetters != EMPTY._allowGetters || allowSetters != EMPTY._allowSetters || merge != EMPTY._merge) {
                return false;
            }
            if (ignored == null || ignored.size() == 0) {
                return true;
            }
            return false;
        }
    }

    boolean allowGetters() default false;

    boolean allowSetters() default false;

    boolean ignoreUnknown() default false;

    String[] value() default {};
}
