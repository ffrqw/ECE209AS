package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualKeyDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

@JacksonStdImpl
public class MapEntryDeserializer extends ContainerDeserializerBase<Entry<Object, Object>> implements ContextualDeserializer {
    private static final long serialVersionUID = 1;
    protected final KeyDeserializer _keyDeserializer;
    protected final JavaType _type;
    protected final JsonDeserializer<Object> _valueDeserializer;
    protected final TypeDeserializer _valueTypeDeserializer;

    public MapEntryDeserializer(JavaType type, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser) {
        super(type);
        if (type.containedTypeCount() != 2) {
            throw new IllegalArgumentException("Missing generic type information for " + type);
        }
        this._type = type;
        this._keyDeserializer = keyDeser;
        this._valueDeserializer = valueDeser;
        this._valueTypeDeserializer = valueTypeDeser;
    }

    protected MapEntryDeserializer(MapEntryDeserializer src) {
        super(src._type);
        this._type = src._type;
        this._keyDeserializer = src._keyDeserializer;
        this._valueDeserializer = src._valueDeserializer;
        this._valueTypeDeserializer = src._valueTypeDeserializer;
    }

    protected MapEntryDeserializer(MapEntryDeserializer src, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser) {
        super(src._type);
        this._type = src._type;
        this._keyDeserializer = keyDeser;
        this._valueDeserializer = valueDeser;
        this._valueTypeDeserializer = valueTypeDeser;
    }

    protected MapEntryDeserializer withResolved(KeyDeserializer keyDeser, TypeDeserializer valueTypeDeser, JsonDeserializer<?> valueDeser) {
        return (this._keyDeserializer == keyDeser && this._valueDeserializer == valueDeser && this._valueTypeDeserializer == valueTypeDeser) ? this : new MapEntryDeserializer(this, keyDeser, (JsonDeserializer) valueDeser, valueTypeDeser);
    }

    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        KeyDeserializer kd = this._keyDeserializer;
        if (kd == null) {
            kd = ctxt.findKeyDeserializer(this._type.containedType(0), property);
        } else if (kd instanceof ContextualKeyDeserializer) {
            kd = ((ContextualKeyDeserializer) kd).createContextual(ctxt, property);
        }
        JsonDeserializer<?> vd = findConvertingContentDeserializer(ctxt, property, this._valueDeserializer);
        JavaType contentType = this._type.containedType(1);
        if (vd == null) {
            vd = ctxt.findContextualValueDeserializer(contentType, property);
        } else {
            vd = ctxt.handleSecondaryContextualization(vd, property, contentType);
        }
        TypeDeserializer vtd = this._valueTypeDeserializer;
        if (vtd != null) {
            vtd = vtd.forProperty(property);
        }
        return withResolved(kd, vtd, vd);
    }

    public JavaType getContentType() {
        return this._type.containedType(1);
    }

    public JsonDeserializer<Object> getContentDeserializer() {
        return this._valueDeserializer;
    }

    public Entry<Object, Object> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.getCurrentToken();
        if (t != JsonToken.START_OBJECT && t != JsonToken.FIELD_NAME && t != JsonToken.END_OBJECT) {
            return (Entry) _deserializeFromEmpty(p, ctxt);
        }
        if (t == JsonToken.START_OBJECT) {
            t = p.nextToken();
        }
        if (t == JsonToken.FIELD_NAME) {
            KeyDeserializer keyDes = this._keyDeserializer;
            JsonDeserializer<Object> valueDes = this._valueDeserializer;
            TypeDeserializer typeDeser = this._valueTypeDeserializer;
            String keyStr = p.getCurrentName();
            Object key = keyDes.deserializeKey(keyStr, ctxt);
            Object value = null;
            try {
                if (p.nextToken() == JsonToken.VALUE_NULL) {
                    value = valueDes.getNullValue(ctxt);
                } else if (typeDeser == null) {
                    value = valueDes.deserialize(p, ctxt);
                } else {
                    value = valueDes.deserializeWithType(p, ctxt, typeDeser);
                }
            } catch (Exception e) {
                wrapAndThrow(e, Entry.class, keyStr);
            }
            t = p.nextToken();
            if (t == JsonToken.END_OBJECT) {
                return new SimpleEntry(key, value);
            }
            if (t == JsonToken.FIELD_NAME) {
                ctxt.reportMappingException("Problem binding JSON into Map.Entry: more than one entry in JSON (second field: '" + p.getCurrentName() + "')", new Object[0]);
                return null;
            }
            ctxt.reportMappingException("Problem binding JSON into Map.Entry: unexpected content after JSON Object entry: " + t, new Object[0]);
            return null;
        } else if (t != JsonToken.END_OBJECT) {
            return (Entry) ctxt.handleUnexpectedToken(handledType(), p);
        } else {
            ctxt.reportMappingException("Can not deserialize a Map.Entry out of empty JSON Object", new Object[0]);
            return null;
        }
    }

    public Entry<Object, Object> deserialize(JsonParser p, DeserializationContext ctxt, Entry<Object, Object> entry) throws IOException {
        throw new IllegalStateException("Can not update Map.Entry values");
    }

    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return typeDeserializer.deserializeTypedFromObject(p, ctxt);
    }

    public JavaType getValueType() {
        return this._type;
    }
}
