package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;

public class JsonNodeDeserializer extends BaseNodeDeserializer<JsonNode> {
    private static final JsonNodeDeserializer instance = new JsonNodeDeserializer();

    static final class ArrayDeserializer extends BaseNodeDeserializer<ArrayNode> {
        protected static final ArrayDeserializer _instance = new ArrayDeserializer();
        private static final long serialVersionUID = 1;

        protected ArrayDeserializer() {
            super(ArrayNode.class);
        }

        public static ArrayDeserializer getInstance() {
            return _instance;
        }

        public final ArrayNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.isExpectedStartArrayToken()) {
                return deserializeArray(p, ctxt, ctxt.getNodeFactory());
            }
            return (ArrayNode) ctxt.handleUnexpectedToken(ArrayNode.class, p);
        }
    }

    static final class ObjectDeserializer extends BaseNodeDeserializer<ObjectNode> {
        protected static final ObjectDeserializer _instance = new ObjectDeserializer();
        private static final long serialVersionUID = 1;

        protected ObjectDeserializer() {
            super(ObjectNode.class);
        }

        public static ObjectDeserializer getInstance() {
            return _instance;
        }

        public final ObjectNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.isExpectedStartObjectToken() || p.hasToken(JsonToken.FIELD_NAME)) {
                return deserializeObject(p, ctxt, ctxt.getNodeFactory());
            }
            if (p.hasToken(JsonToken.END_OBJECT)) {
                return ctxt.getNodeFactory().objectNode();
            }
            return (ObjectNode) ctxt.handleUnexpectedToken(ObjectNode.class, p);
        }
    }

    public /* bridge */ /* synthetic */ Object deserializeWithType(JsonParser x0, DeserializationContext x1, TypeDeserializer x2) throws IOException {
        return super.deserializeWithType(x0, x1, x2);
    }

    public /* bridge */ /* synthetic */ boolean isCachable() {
        return super.isCachable();
    }

    protected JsonNodeDeserializer() {
        super(JsonNode.class);
    }

    public static JsonDeserializer<? extends JsonNode> getDeserializer(Class<?> nodeClass) {
        if (nodeClass == ObjectNode.class) {
            return ObjectDeserializer.getInstance();
        }
        if (nodeClass == ArrayNode.class) {
            return ArrayDeserializer.getInstance();
        }
        return instance;
    }

    public JsonNode getNullValue(DeserializationContext ctxt) {
        return NullNode.getInstance();
    }

    @Deprecated
    public JsonNode getNullValue() {
        return NullNode.getInstance();
    }

    public JsonNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        switch (p.getCurrentTokenId()) {
            case 1:
                return deserializeObject(p, ctxt, ctxt.getNodeFactory());
            case 3:
                return deserializeArray(p, ctxt, ctxt.getNodeFactory());
            default:
                return deserializeAny(p, ctxt, ctxt.getNodeFactory());
        }
    }
}
