package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;

public final class TypeWrappedSerializer extends JsonSerializer<Object> {
    protected final JsonSerializer<Object> _serializer;
    protected final TypeSerializer _typeSerializer;

    public TypeWrappedSerializer(TypeSerializer typeSer, JsonSerializer<?> ser) {
        this._typeSerializer = typeSer;
        this._serializer = ser;
    }

    public final void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        this._serializer.serializeWithType(value, jgen, provider, this._typeSerializer);
    }

    public final void serializeWithType(Object value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        this._serializer.serializeWithType(value, jgen, provider, typeSer);
    }

    public final Class<Object> handledType() {
        return Object.class;
    }

    public final JsonSerializer<Object> valueSerializer() {
        return this._serializer;
    }

    public final TypeSerializer typeSerializer() {
        return this._typeSerializer;
    }
}
