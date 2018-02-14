package com.google.gson.internal;

import java.math.BigDecimal;

public final class LazilyParsedNumber extends Number {
    private final String value;

    public LazilyParsedNumber(String value) {
        this.value = value;
    }

    public final int intValue() {
        try {
            return Integer.parseInt(this.value);
        } catch (NumberFormatException e) {
            try {
                return (int) Long.parseLong(this.value);
            } catch (NumberFormatException e2) {
                return new BigDecimal(this.value).intValue();
            }
        }
    }

    public final long longValue() {
        try {
            return Long.parseLong(this.value);
        } catch (NumberFormatException e) {
            return new BigDecimal(this.value).longValue();
        }
    }

    public final float floatValue() {
        return Float.parseFloat(this.value);
    }

    public final double doubleValue() {
        return Double.parseDouble(this.value);
    }

    public final String toString() {
        return this.value;
    }

    public final int hashCode() {
        return this.value.hashCode();
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LazilyParsedNumber)) {
            return false;
        }
        LazilyParsedNumber other = (LazilyParsedNumber) obj;
        if (this.value == other.value || this.value.equals(other.value)) {
            return true;
        }
        return false;
    }
}
