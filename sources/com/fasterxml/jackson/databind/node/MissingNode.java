package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;

public final class MissingNode extends ValueNode {
    private static final MissingNode instance = new MissingNode();

    private MissingNode() {
    }

    public final <T extends JsonNode> T deepCopy() {
        return this;
    }

    public static MissingNode getInstance() {
        return instance;
    }

    public final JsonNodeType getNodeType() {
        return JsonNodeType.MISSING;
    }

    public final JsonToken asToken() {
        return JsonToken.NOT_AVAILABLE;
    }

    public final String asText() {
        return "";
    }

    public final String asText(String defaultValue) {
        return defaultValue;
    }

    public final void serialize(JsonGenerator jg, SerializerProvider provider) throws IOException, JsonProcessingException {
        jg.writeNull();
    }

    public final void serializeWithType(JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonProcessingException {
        g.writeNull();
    }

    public final boolean equals(Object o) {
        return o == this;
    }

    public final String toString() {
        return "";
    }

    public final int hashCode() {
        return JsonNodeType.MISSING.ordinal();
    }
}
