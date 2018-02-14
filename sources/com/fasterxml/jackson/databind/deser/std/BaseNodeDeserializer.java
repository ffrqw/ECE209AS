package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.RawValue;
import java.io.IOException;

/* compiled from: JsonNodeDeserializer */
abstract class BaseNodeDeserializer<T extends JsonNode> extends StdDeserializer<T> {
    public BaseNodeDeserializer(Class<T> vc) {
        super((Class) vc);
    }

    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromAny(p, ctxt);
    }

    public boolean isCachable() {
        return true;
    }

    @Deprecated
    protected void _reportProblem(JsonParser p, String msg) throws JsonMappingException {
        throw JsonMappingException.from(p, msg);
    }

    protected void _handleDuplicateField(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory, String fieldName, ObjectNode objectNode, JsonNode oldValue, JsonNode newValue) throws JsonProcessingException {
        if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)) {
            ctxt.reportMappingException("Duplicate field '%s' for ObjectNode: not allowed when FAIL_ON_READING_DUP_TREE_KEY enabled", fieldName);
        }
    }

    protected final ObjectNode deserializeObject(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
        String key;
        JsonToken t;
        ObjectNode node = nodeFactory.objectNode();
        if (p.isExpectedStartObjectToken()) {
            key = p.nextFieldName();
        } else {
            t = p.getCurrentToken();
            if (t == JsonToken.END_OBJECT) {
                return node;
            }
            if (t != JsonToken.FIELD_NAME) {
                return (ObjectNode) ctxt.handleUnexpectedToken(handledType(), p);
            }
            key = p.getCurrentName();
        }
        while (key != null) {
            t = p.nextToken();
            if (t == null) {
                throw ctxt.mappingException("Unexpected end-of-input when binding data into ObjectNode");
            }
            JsonNode value;
            switch (t.id()) {
                case 1:
                    value = deserializeObject(p, ctxt, nodeFactory);
                    break;
                case 3:
                    value = deserializeArray(p, ctxt, nodeFactory);
                    break;
                case 6:
                    value = nodeFactory.textNode(p.getText());
                    break;
                case 7:
                    value = _fromInt(p, ctxt, nodeFactory);
                    break;
                case 9:
                    value = nodeFactory.booleanNode(true);
                    break;
                case 10:
                    value = nodeFactory.booleanNode(false);
                    break;
                case 11:
                    value = nodeFactory.nullNode();
                    break;
                case 12:
                    value = _fromEmbedded(p, ctxt, nodeFactory);
                    break;
                default:
                    value = deserializeAny(p, ctxt, nodeFactory);
                    break;
            }
            JsonNode old = node.replace(key, value);
            if (old != null) {
                _handleDuplicateField(p, ctxt, nodeFactory, key, node, old, value);
            }
            key = p.nextFieldName();
        }
        return node;
    }

    protected final ArrayNode deserializeArray(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
        ArrayNode node = nodeFactory.arrayNode();
        while (true) {
            switch (p.nextToken().id()) {
                case 1:
                    node.add(deserializeObject(p, ctxt, nodeFactory));
                    break;
                case 3:
                    node.add(deserializeArray(p, ctxt, nodeFactory));
                    break;
                case 4:
                    return node;
                case 6:
                    node.add(nodeFactory.textNode(p.getText()));
                    break;
                case 7:
                    node.add(_fromInt(p, ctxt, nodeFactory));
                    break;
                case 9:
                    node.add(nodeFactory.booleanNode(true));
                    break;
                case 10:
                    node.add(nodeFactory.booleanNode(false));
                    break;
                case 11:
                    node.add(nodeFactory.nullNode());
                    break;
                case 12:
                    node.add(_fromEmbedded(p, ctxt, nodeFactory));
                    break;
                default:
                    node.add(deserializeAny(p, ctxt, nodeFactory));
                    break;
            }
        }
    }

    protected final JsonNode deserializeAny(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
        switch (p.getCurrentTokenId()) {
            case 1:
            case 2:
            case 5:
                return deserializeObject(p, ctxt, nodeFactory);
            case 3:
                return deserializeArray(p, ctxt, nodeFactory);
            case 6:
                return nodeFactory.textNode(p.getText());
            case 7:
                return _fromInt(p, ctxt, nodeFactory);
            case 8:
                return _fromFloat(p, ctxt, nodeFactory);
            case 9:
                return nodeFactory.booleanNode(true);
            case 10:
                return nodeFactory.booleanNode(false);
            case 11:
                return nodeFactory.nullNode();
            case 12:
                return _fromEmbedded(p, ctxt, nodeFactory);
            default:
                return (JsonNode) ctxt.handleUnexpectedToken(handledType(), p);
        }
    }

    protected final JsonNode _fromInt(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
        NumberType nt;
        int feats = ctxt.getDeserializationFeatures();
        if ((F_MASK_INT_COERCIONS & feats) == 0) {
            nt = p.getNumberType();
        } else if (DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.enabledIn(feats)) {
            nt = NumberType.BIG_INTEGER;
        } else if (DeserializationFeature.USE_LONG_FOR_INTS.enabledIn(feats)) {
            nt = NumberType.LONG;
        } else {
            nt = p.getNumberType();
        }
        if (nt == NumberType.INT) {
            return nodeFactory.numberNode(p.getIntValue());
        }
        if (nt == NumberType.LONG) {
            return nodeFactory.numberNode(p.getLongValue());
        }
        return nodeFactory.numberNode(p.getBigIntegerValue());
    }

    protected final JsonNode _fromFloat(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
        NumberType nt = p.getNumberType();
        if (nt == NumberType.BIG_DECIMAL) {
            return nodeFactory.numberNode(p.getDecimalValue());
        }
        if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
            double d = p.getDoubleValue();
            if (Double.isInfinite(d) || Double.isNaN(d)) {
                return nodeFactory.numberNode(d);
            }
            return nodeFactory.numberNode(p.getDecimalValue());
        } else if (nt == NumberType.FLOAT) {
            return nodeFactory.numberNode(p.getFloatValue());
        } else {
            return nodeFactory.numberNode(p.getDoubleValue());
        }
    }

    protected final JsonNode _fromEmbedded(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
        Object ob = p.getEmbeddedObject();
        if (ob == null) {
            return nodeFactory.nullNode();
        }
        if (ob.getClass() == byte[].class) {
            return nodeFactory.binaryNode((byte[]) ob);
        }
        if (ob instanceof RawValue) {
            return nodeFactory.rawValueNode((RawValue) ob);
        }
        if (ob instanceof JsonNode) {
            return (JsonNode) ob;
        }
        return nodeFactory.pojoNode(ob);
    }
}
