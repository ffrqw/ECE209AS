package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;

public abstract class ReferenceTypeDeserializer<T> extends StdDeserializer<T> implements ContextualDeserializer {
    private static final long serialVersionUID = 1;
    protected final JavaType _fullType;
    protected final JsonDeserializer<?> _valueDeserializer;
    protected final TypeDeserializer _valueTypeDeserializer;

    public abstract T getNullValue(DeserializationContext deserializationContext);

    public abstract T referenceValue(Object obj);

    protected abstract ReferenceTypeDeserializer<T> withResolved(TypeDeserializer typeDeserializer, JsonDeserializer<?> jsonDeserializer);

    public ReferenceTypeDeserializer(JavaType fullType, TypeDeserializer typeDeser, JsonDeserializer<?> deser) {
        super(fullType);
        this._fullType = fullType;
        this._valueDeserializer = deser;
        this._valueTypeDeserializer = typeDeser;
    }

    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JsonDeserializer<?> deser = this._valueDeserializer;
        if (deser == null) {
            deser = ctxt.findContextualValueDeserializer(this._fullType.getReferencedType(), property);
        } else {
            deser = ctxt.handleSecondaryContextualization(deser, property, this._fullType.getReferencedType());
        }
        TypeDeserializer typeDeser = this._valueTypeDeserializer;
        if (typeDeser != null) {
            typeDeser = typeDeser.forProperty(property);
        }
        return (deser == this._valueDeserializer && typeDeser == this._valueTypeDeserializer) ? this : withResolved(typeDeser, deser);
    }

    public JavaType getValueType() {
        return this._fullType;
    }

    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return referenceValue(this._valueTypeDeserializer == null ? this._valueDeserializer.deserialize(p, ctxt) : this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer));
    }

    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        if (p.getCurrentToken() == JsonToken.VALUE_NULL) {
            return getNullValue(ctxt);
        }
        if (this._valueTypeDeserializer == null) {
            return deserialize(p, ctxt);
        }
        return referenceValue(this._valueTypeDeserializer.deserializeTypedFromAny(p, ctxt));
    }
}
