package com.google.gson;

public final class JsonNull extends JsonElement {
    public static final JsonNull INSTANCE = new JsonNull();

    public final int hashCode() {
        return JsonNull.class.hashCode();
    }

    public final boolean equals(Object other) {
        return this == other || (other instanceof JsonNull);
    }
}
