package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceDeserializer extends ReferenceTypeDeserializer<AtomicReference<Object>> {
    private static final long serialVersionUID = 1;

    @Deprecated
    public AtomicReferenceDeserializer(JavaType fullType) {
        this(fullType, null, null);
    }

    public AtomicReferenceDeserializer(JavaType fullType, TypeDeserializer typeDeser, JsonDeserializer<?> deser) {
        super(fullType, typeDeser, deser);
    }

    public AtomicReferenceDeserializer withResolved(TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser) {
        return new AtomicReferenceDeserializer(this._fullType, typeDeser, valueDeser);
    }

    public AtomicReference<Object> getNullValue(DeserializationContext ctxt) {
        return new AtomicReference();
    }

    public AtomicReference<Object> referenceValue(Object contents) {
        return new AtomicReference(contents);
    }
}
