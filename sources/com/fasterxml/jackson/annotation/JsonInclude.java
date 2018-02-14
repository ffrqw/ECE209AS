package com.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonInclude {

    public enum Include {
        ALWAYS,
        NON_NULL,
        NON_ABSENT,
        NON_EMPTY,
        NON_DEFAULT,
        USE_DEFAULTS
    }

    public static class Value implements Serializable {
        protected static final Value EMPTY = new Value(Include.USE_DEFAULTS, Include.USE_DEFAULTS);
        protected final Include _contentInclusion;
        protected final Include _valueInclusion;

        protected Value(Include vi, Include ci) {
            if (vi == null) {
                vi = Include.USE_DEFAULTS;
            }
            this._valueInclusion = vi;
            if (ci == null) {
                ci = Include.USE_DEFAULTS;
            }
            this._contentInclusion = ci;
        }

        public static Value empty() {
            return EMPTY;
        }

        public static Value merge(Value base, Value overrides) {
            return base == null ? overrides : base.withOverrides(overrides);
        }

        public Value withOverrides(Value overrides) {
            if (overrides == null || overrides == EMPTY) {
                return this;
            }
            boolean viDiff;
            boolean ciDiff;
            Include vi = overrides._valueInclusion;
            Include ci = overrides._contentInclusion;
            if (vi == this._valueInclusion || vi == Include.USE_DEFAULTS) {
                viDiff = false;
            } else {
                viDiff = true;
            }
            if (ci == this._contentInclusion || ci == Include.USE_DEFAULTS) {
                ciDiff = false;
            } else {
                ciDiff = true;
            }
            if (viDiff) {
                if (!ciDiff) {
                    return new Value(vi, this._contentInclusion);
                }
                this(vi, ci);
                return this;
            } else if (ciDiff) {
                return new Value(this._valueInclusion, ci);
            } else {
                return this;
            }
        }

        public static Value construct(Include valueIncl, Include contentIncl) {
            if ((valueIncl == Include.USE_DEFAULTS || valueIncl == null) && (contentIncl == Include.USE_DEFAULTS || contentIncl == null)) {
                return EMPTY;
            }
            return new Value(valueIncl, contentIncl);
        }

        public Value withValueInclusion(Include incl) {
            return incl == this._valueInclusion ? this : new Value(incl, this._contentInclusion);
        }

        public Include getValueInclusion() {
            return this._valueInclusion;
        }

        public Include getContentInclusion() {
            return this._contentInclusion;
        }

        public String toString() {
            return String.format("[value=%s,content=%s]", new Object[]{this._valueInclusion, this._contentInclusion});
        }

        public int hashCode() {
            return (this._valueInclusion.hashCode() << 2) + this._contentInclusion.hashCode();
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
            if (other._valueInclusion == this._valueInclusion && other._contentInclusion == this._contentInclusion) {
                return true;
            }
            return false;
        }
    }

    Include content() default Include.ALWAYS;

    Include value() default Include.ALWAYS;
}
