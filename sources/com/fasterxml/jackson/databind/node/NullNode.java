package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public final class NullNode extends ValueNode {
    public static final NullNode instance = new NullNode();

    private NullNode() {
    }

    public static NullNode getInstance() {
        return instance;
    }

    public final JsonNodeType getNodeType() {
        return JsonNodeType.NULL;
    }

    public final JsonToken asToken() {
        return JsonToken.VALUE_NULL;
    }

    public final String asText(String defaultValue) {
        return defaultValue;
    }

    public final String asText() {
        return "null";
    }

    public final void serialize(JsonGenerator g, SerializerProvider provider) throws IOException {
        provider.defaultSerializeNull(g);
    }

    public final boolean equals(Object o) {
        return o == this;
    }

    public final int hashCode() {
        return JsonNodeType.NULL.ordinal();
    }
}
