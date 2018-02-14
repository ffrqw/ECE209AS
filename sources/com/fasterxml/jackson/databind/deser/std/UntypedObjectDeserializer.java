package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.ObjectBuffer;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JacksonStdImpl
public class UntypedObjectDeserializer extends StdDeserializer<Object> implements ContextualDeserializer, ResolvableDeserializer {
    protected static final Object[] NO_OBJECTS = new Object[0];
    @Deprecated
    public static final UntypedObjectDeserializer instance = new UntypedObjectDeserializer(null, null);
    private static final long serialVersionUID = 1;
    protected JsonDeserializer<Object> _listDeserializer;
    protected JavaType _listType;
    protected JsonDeserializer<Object> _mapDeserializer;
    protected JavaType _mapType;
    protected JsonDeserializer<Object> _numberDeserializer;
    protected JsonDeserializer<Object> _stringDeserializer;

    @JacksonStdImpl
    public static class Vanilla extends StdDeserializer<Object> {
        private static final long serialVersionUID = 1;
        public static final Vanilla std = new Vanilla();

        public Vanilla() {
            super(Object.class);
        }

        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            switch (p.getCurrentTokenId()) {
                case 1:
                    if (p.nextToken() == JsonToken.END_OBJECT) {
                        return new LinkedHashMap(2);
                    }
                    break;
                case 2:
                    return new LinkedHashMap(2);
                case 3:
                    if (p.nextToken() == JsonToken.END_ARRAY) {
                        if (ctxt.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)) {
                            return UntypedObjectDeserializer.NO_OBJECTS;
                        }
                        return new ArrayList(2);
                    } else if (ctxt.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)) {
                        return mapArrayToArray(p, ctxt);
                    } else {
                        return mapArray(p, ctxt);
                    }
                case 5:
                    break;
                case 6:
                    return p.getText();
                case 7:
                    if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
                        return _coerceIntegral(p, ctxt);
                    }
                    return p.getNumberValue();
                case 8:
                    if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                        return p.getDecimalValue();
                    }
                    return Double.valueOf(p.getDoubleValue());
                case 9:
                    return Boolean.TRUE;
                case 10:
                    return Boolean.FALSE;
                case 11:
                    return null;
                case 12:
                    return p.getEmbeddedObject();
                default:
                    return ctxt.handleUnexpectedToken(Object.class, p);
            }
            return mapObject(p, ctxt);
        }

        public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
            switch (p.getCurrentTokenId()) {
                case 1:
                case 3:
                case 5:
                    return typeDeserializer.deserializeTypedFromAny(p, ctxt);
                case 6:
                    return p.getText();
                case 7:
                    if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
                        return p.getBigIntegerValue();
                    }
                    return p.getNumberValue();
                case 8:
                    if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                        return p.getDecimalValue();
                    }
                    return Double.valueOf(p.getDoubleValue());
                case 9:
                    return Boolean.TRUE;
                case 10:
                    return Boolean.FALSE;
                case 11:
                    return null;
                case 12:
                    return p.getEmbeddedObject();
                default:
                    return ctxt.handleUnexpectedToken(Object.class, p);
            }
        }

        protected Object mapArray(JsonParser p, DeserializationContext ctxt) throws IOException {
            Object value = deserialize(p, ctxt);
            if (p.nextToken() == JsonToken.END_ARRAY) {
                ArrayList<Object> l = new ArrayList(2);
                l.add(value);
                return l;
            }
            Object value2 = deserialize(p, ctxt);
            if (p.nextToken() == JsonToken.END_ARRAY) {
                l = new ArrayList(2);
                l.add(value);
                l.add(value2);
                return l;
            }
            ObjectBuffer buffer = ctxt.leaseObjectBuffer();
            Object[] values = buffer.resetAndStart();
            int ptr = 0 + 1;
            values[0] = value;
            ptr++;
            values[1] = value2;
            int totalSize = 2;
            while (true) {
                value = deserialize(p, ctxt);
                totalSize++;
                if (ptr >= values.length) {
                    values = buffer.appendCompletedChunk(values);
                    ptr = 0;
                }
                int ptr2 = ptr + 1;
                values[ptr] = value;
                if (p.nextToken() == JsonToken.END_ARRAY) {
                    List result = new ArrayList(totalSize);
                    buffer.completeAndClearBuffer(values, ptr2, result);
                    return result;
                }
                ptr = ptr2;
            }
        }

        protected Object mapObject(JsonParser p, DeserializationContext ctxt) throws IOException {
            String key1 = p.getText();
            p.nextToken();
            Object value1 = deserialize(p, ctxt);
            String key2 = p.nextFieldName();
            if (key2 == null) {
                LinkedHashMap<String, Object> result = new LinkedHashMap(2);
                result.put(key1, value1);
                return result;
            }
            p.nextToken();
            Object value2 = deserialize(p, ctxt);
            String key = p.nextFieldName();
            if (key == null) {
                result = new LinkedHashMap(4);
                result.put(key1, value1);
                result.put(key2, value2);
                return result;
            }
            result = new LinkedHashMap();
            result.put(key1, value1);
            result.put(key2, value2);
            do {
                p.nextToken();
                result.put(key, deserialize(p, ctxt));
                key = p.nextFieldName();
            } while (key != null);
            return result;
        }

        protected Object[] mapArrayToArray(JsonParser p, DeserializationContext ctxt) throws IOException {
            ObjectBuffer buffer = ctxt.leaseObjectBuffer();
            Object[] values = buffer.resetAndStart();
            int ptr = 0;
            while (true) {
                Object value = deserialize(p, ctxt);
                if (ptr >= values.length) {
                    values = buffer.appendCompletedChunk(values);
                    ptr = 0;
                }
                int ptr2 = ptr + 1;
                values[ptr] = value;
                if (p.nextToken() == JsonToken.END_ARRAY) {
                    return buffer.completeAndClearBuffer(values, ptr2);
                }
                ptr = ptr2;
            }
        }
    }

    @Deprecated
    public UntypedObjectDeserializer() {
        this(null, null);
    }

    public UntypedObjectDeserializer(JavaType listType, JavaType mapType) {
        super(Object.class);
        this._listType = listType;
        this._mapType = mapType;
    }

    public UntypedObjectDeserializer(UntypedObjectDeserializer base, JsonDeserializer<?> mapDeser, JsonDeserializer<?> listDeser, JsonDeserializer<?> stringDeser, JsonDeserializer<?> numberDeser) {
        super(Object.class);
        this._mapDeserializer = mapDeser;
        this._listDeserializer = listDeser;
        this._stringDeserializer = stringDeser;
        this._numberDeserializer = numberDeser;
        this._listType = base._listType;
        this._mapType = base._mapType;
    }

    public void resolve(DeserializationContext ctxt) throws JsonMappingException {
        JavaType obType = ctxt.constructType(Object.class);
        JavaType stringType = ctxt.constructType(String.class);
        TypeFactory tf = ctxt.getTypeFactory();
        if (this._listType == null) {
            this._listDeserializer = _clearIfStdImpl(_findCustomDeser(ctxt, tf.constructCollectionType(List.class, obType)));
        } else {
            this._listDeserializer = _findCustomDeser(ctxt, this._listType);
        }
        if (this._mapType == null) {
            this._mapDeserializer = _clearIfStdImpl(_findCustomDeser(ctxt, tf.constructMapType(Map.class, stringType, obType)));
        } else {
            this._mapDeserializer = _findCustomDeser(ctxt, this._mapType);
        }
        this._stringDeserializer = _clearIfStdImpl(_findCustomDeser(ctxt, stringType));
        this._numberDeserializer = _clearIfStdImpl(_findCustomDeser(ctxt, tf.constructType((Type) Number.class)));
        JavaType unknown = TypeFactory.unknownType();
        this._mapDeserializer = ctxt.handleSecondaryContextualization(this._mapDeserializer, null, unknown);
        this._listDeserializer = ctxt.handleSecondaryContextualization(this._listDeserializer, null, unknown);
        this._stringDeserializer = ctxt.handleSecondaryContextualization(this._stringDeserializer, null, unknown);
        this._numberDeserializer = ctxt.handleSecondaryContextualization(this._numberDeserializer, null, unknown);
    }

    protected JsonDeserializer<Object> _findCustomDeser(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
        return ctxt.findNonContextualValueDeserializer(type);
    }

    protected JsonDeserializer<Object> _clearIfStdImpl(JsonDeserializer<Object> deser) {
        return ClassUtil.isJacksonStdImpl((Object) deser) ? null : deser;
    }

    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        if (this._stringDeserializer == null && this._numberDeserializer == null && this._mapDeserializer == null && this._listDeserializer == null && getClass() == UntypedObjectDeserializer.class) {
            return Vanilla.std;
        }
        return this;
    }

    protected JsonDeserializer<?> _withResolved(JsonDeserializer<?> mapDeser, JsonDeserializer<?> listDeser, JsonDeserializer<?> stringDeser, JsonDeserializer<?> numberDeser) {
        return new UntypedObjectDeserializer(this, mapDeser, listDeser, stringDeser, numberDeser);
    }

    public boolean isCachable() {
        return true;
    }

    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        switch (p.getCurrentTokenId()) {
            case 1:
            case 2:
            case 5:
                if (this._mapDeserializer != null) {
                    return this._mapDeserializer.deserialize(p, ctxt);
                }
                return mapObject(p, ctxt);
            case 3:
                if (ctxt.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)) {
                    return mapArrayToArray(p, ctxt);
                }
                if (this._listDeserializer != null) {
                    return this._listDeserializer.deserialize(p, ctxt);
                }
                return mapArray(p, ctxt);
            case 6:
                if (this._stringDeserializer != null) {
                    return this._stringDeserializer.deserialize(p, ctxt);
                }
                return p.getText();
            case 7:
                if (this._numberDeserializer != null) {
                    return this._numberDeserializer.deserialize(p, ctxt);
                }
                if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
                    return _coerceIntegral(p, ctxt);
                }
                return p.getNumberValue();
            case 8:
                if (this._numberDeserializer != null) {
                    return this._numberDeserializer.deserialize(p, ctxt);
                }
                if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                    return p.getDecimalValue();
                }
                return Double.valueOf(p.getDoubleValue());
            case 9:
                return Boolean.TRUE;
            case 10:
                return Boolean.FALSE;
            case 11:
                return null;
            case 12:
                return p.getEmbeddedObject();
            default:
                return ctxt.handleUnexpectedToken(Object.class, p);
        }
    }

    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        switch (p.getCurrentTokenId()) {
            case 1:
            case 3:
            case 5:
                return typeDeserializer.deserializeTypedFromAny(p, ctxt);
            case 6:
                if (this._stringDeserializer != null) {
                    return this._stringDeserializer.deserialize(p, ctxt);
                }
                return p.getText();
            case 7:
                if (this._numberDeserializer != null) {
                    return this._numberDeserializer.deserialize(p, ctxt);
                }
                if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
                    return _coerceIntegral(p, ctxt);
                }
                return p.getNumberValue();
            case 8:
                if (this._numberDeserializer != null) {
                    return this._numberDeserializer.deserialize(p, ctxt);
                }
                if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                    return p.getDecimalValue();
                }
                return Double.valueOf(p.getDoubleValue());
            case 9:
                return Boolean.TRUE;
            case 10:
                return Boolean.FALSE;
            case 11:
                return null;
            case 12:
                return p.getEmbeddedObject();
            default:
                return ctxt.handleUnexpectedToken(Object.class, p);
        }
    }

    protected Object mapArray(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.nextToken() == JsonToken.END_ARRAY) {
            return new ArrayList(2);
        }
        Object value = deserialize(p, ctxt);
        if (p.nextToken() == JsonToken.END_ARRAY) {
            Object l = new ArrayList(2);
            l.add(value);
            return l;
        }
        Object value2 = deserialize(p, ctxt);
        if (p.nextToken() == JsonToken.END_ARRAY) {
            l = new ArrayList(2);
            l.add(value);
            l.add(value2);
            return l;
        }
        ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        Object[] values = buffer.resetAndStart();
        int ptr = 0 + 1;
        values[0] = value;
        ptr++;
        values[1] = value2;
        int totalSize = 2;
        while (true) {
            value = deserialize(p, ctxt);
            totalSize++;
            if (ptr >= values.length) {
                values = buffer.appendCompletedChunk(values);
                ptr = 0;
            }
            int ptr2 = ptr + 1;
            values[ptr] = value;
            if (p.nextToken() == JsonToken.END_ARRAY) {
                List result = new ArrayList(totalSize);
                buffer.completeAndClearBuffer(values, ptr2, result);
                return result;
            }
            ptr = ptr2;
        }
    }

    protected Object mapObject(JsonParser p, DeserializationContext ctxt) throws IOException {
        String key1;
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            key1 = p.nextFieldName();
        } else if (t == JsonToken.FIELD_NAME) {
            key1 = p.getCurrentName();
        } else if (t != JsonToken.END_OBJECT) {
            return ctxt.handleUnexpectedToken(handledType(), p);
        } else {
            key1 = null;
        }
        if (key1 == null) {
            return new LinkedHashMap(2);
        }
        p.nextToken();
        Object value1 = deserialize(p, ctxt);
        String key2 = p.nextFieldName();
        if (key2 == null) {
            Object result = new LinkedHashMap(2);
            result.put(key1, value1);
            return result;
        }
        p.nextToken();
        Object value2 = deserialize(p, ctxt);
        String key = p.nextFieldName();
        if (key == null) {
            result = new LinkedHashMap(4);
            result.put(key1, value1);
            result.put(key2, value2);
            return result;
        }
        result = new LinkedHashMap();
        result.put(key1, value1);
        result.put(key2, value2);
        do {
            p.nextToken();
            result.put(key, deserialize(p, ctxt));
            key = p.nextFieldName();
        } while (key != null);
        return result;
    }

    protected Object[] mapArrayToArray(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.nextToken() == JsonToken.END_ARRAY) {
            return NO_OBJECTS;
        }
        ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        Object[] values = buffer.resetAndStart();
        int ptr = 0;
        while (true) {
            Object value = deserialize(p, ctxt);
            if (ptr >= values.length) {
                values = buffer.appendCompletedChunk(values);
                ptr = 0;
            }
            int ptr2 = ptr + 1;
            values[ptr] = value;
            if (p.nextToken() == JsonToken.END_ARRAY) {
                return buffer.completeAndClearBuffer(values, ptr2);
            }
            ptr = ptr2;
        }
    }
}
