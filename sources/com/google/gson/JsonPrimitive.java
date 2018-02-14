package com.google.gson;

import com.google.gson.internal.LazilyParsedNumber;
import java.math.BigInteger;

public final class JsonPrimitive extends JsonElement {
    private static final Class<?>[] PRIMITIVE_TYPES = new Class[]{Integer.TYPE, Long.TYPE, Short.TYPE, Float.TYPE, Double.TYPE, Byte.TYPE, Boolean.TYPE, Character.TYPE, Integer.class, Long.class, Short.class, Float.class, Double.class, Byte.class, Boolean.class, Character.class};
    private Object value;

    public JsonPrimitive(Boolean bool) {
        setValue(bool);
    }

    public JsonPrimitive(Number number) {
        setValue(number);
    }

    public JsonPrimitive(String string) {
        setValue(string);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setValue(java.lang.Object r8) {
        /*
        r7 = this;
        r2 = 1;
        r1 = 0;
        r3 = r8 instanceof java.lang.Character;
        if (r3 == 0) goto L_0x0013;
    L_0x0006:
        r8 = (java.lang.Character) r8;
        r0 = r8.charValue();
        r1 = java.lang.String.valueOf(r0);
        r7.value = r1;
    L_0x0012:
        return;
    L_0x0013:
        r3 = r8 instanceof java.lang.Number;
        if (r3 != 0) goto L_0x001e;
    L_0x0017:
        r3 = r8 instanceof java.lang.String;
        if (r3 == 0) goto L_0x0025;
    L_0x001b:
        r3 = r2;
    L_0x001c:
        if (r3 == 0) goto L_0x001f;
    L_0x001e:
        r1 = r2;
    L_0x001f:
        com.google.gson.internal.C$Gson$Preconditions.checkArgument(r1);
        r7.value = r8;
        goto L_0x0012;
    L_0x0025:
        r4 = r8.getClass();
        r5 = PRIMITIVE_TYPES;
        r3 = r1;
    L_0x002c:
        r6 = 16;
        if (r3 >= r6) goto L_0x003d;
    L_0x0030:
        r6 = r5[r3];
        r6 = r6.isAssignableFrom(r4);
        if (r6 == 0) goto L_0x003a;
    L_0x0038:
        r3 = r2;
        goto L_0x001c;
    L_0x003a:
        r3 = r3 + 1;
        goto L_0x002c;
    L_0x003d:
        r3 = r1;
        goto L_0x001c;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.gson.JsonPrimitive.setValue(java.lang.Object):void");
    }

    public final boolean isBoolean() {
        return this.value instanceof Boolean;
    }

    public final boolean isNumber() {
        return this.value instanceof Number;
    }

    public final Number getAsNumber() {
        return this.value instanceof String ? new LazilyParsedNumber((String) this.value) : (Number) this.value;
    }

    public final boolean isString() {
        return this.value instanceof String;
    }

    public final int hashCode() {
        if (this.value == null) {
            return 31;
        }
        long value;
        if (isIntegral(this)) {
            value = getAsNumber().longValue();
            return (int) ((value >>> 32) ^ value);
        } else if (!(this.value instanceof Number)) {
            return this.value.hashCode();
        } else {
            value = Double.doubleToLongBits(getAsNumber().doubleValue());
            return (int) ((value >>> 32) ^ value);
        }
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        JsonPrimitive other = (JsonPrimitive) obj;
        if (this.value == null) {
            if (other.value != null) {
                return false;
            }
            return true;
        } else if (isIntegral(this) && isIntegral(other)) {
            if (getAsNumber().longValue() != other.getAsNumber().longValue()) {
                return false;
            }
            return true;
        } else if (!(this.value instanceof Number) || !(other.value instanceof Number)) {
            return this.value.equals(other.value);
        } else {
            double a = getAsNumber().doubleValue();
            double b = other.getAsNumber().doubleValue();
            if (a == b) {
                return true;
            }
            if (Double.isNaN(a) && Double.isNaN(b)) {
                return true;
            }
            return false;
        }
    }

    private static boolean isIntegral(JsonPrimitive primitive) {
        if (!(primitive.value instanceof Number)) {
            return false;
        }
        Number number = primitive.value;
        if ((number instanceof BigInteger) || (number instanceof Long) || (number instanceof Integer) || (number instanceof Short) || (number instanceof Byte)) {
            return true;
        }
        return false;
    }

    public final boolean getAsBoolean() {
        if (this.value instanceof Boolean) {
            return ((Boolean) this.value).booleanValue();
        }
        return Boolean.parseBoolean(getAsString());
    }

    public final String getAsString() {
        if (this.value instanceof Number) {
            return getAsNumber().toString();
        }
        if (this.value instanceof Boolean) {
            return ((Boolean) this.value).toString();
        }
        return (String) this.value;
    }

    public final double getAsDouble() {
        return this.value instanceof Number ? getAsNumber().doubleValue() : Double.parseDouble(getAsString());
    }

    public final long getAsLong() {
        return this.value instanceof Number ? getAsNumber().longValue() : Long.parseLong(getAsString());
    }

    public final int getAsInt() {
        return this.value instanceof Number ? getAsNumber().intValue() : Integer.parseInt(getAsString());
    }
}
