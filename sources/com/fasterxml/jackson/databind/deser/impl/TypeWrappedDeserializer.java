package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

public final class TypeWrappedDeserializer extends JsonDeserializer<Object> implements Serializable {
    private static final long serialVersionUID = 1;
    protected final JsonDeserializer<Object> _deserializer;
    protected final TypeDeserializer _typeDeserializer;

    public TypeWrappedDeserializer(TypeDeserializer typeDeser, JsonDeserializer<?> deser) {
        this._typeDeserializer = typeDeser;
        this._deserializer = deser;
    }

    public final Class<?> handledType() {
        return this._deserializer.handledType();
    }

    public final JsonDeserializer<?> getDelegatee() {
        return this._deserializer.getDelegatee();
    }

    public final Collection<Object> getKnownPropertyNames() {
        return this._deserializer.getKnownPropertyNames();
    }

    public final Object getNullValue(DeserializationContext ctxt) throws JsonMappingException {
        return this._deserializer.getNullValue(ctxt);
    }

    public final Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
        return this._deserializer.getEmptyValue(ctxt);
    }

    public final Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return this._deserializer.deserializeWithType(jp, ctxt, this._typeDeserializer);
    }

    public final Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        throw new IllegalStateException("Type-wrapped deserializer's deserializeWithType should never get called");
    }

    public final Object deserialize(JsonParser jp, DeserializationContext ctxt, Object intoValue) throws IOException {
        return this._deserializer.deserialize(jp, ctxt, intoValue);
    }
}
