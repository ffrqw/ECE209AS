package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import java.util.EnumSet;

public class EnumSetDeserializer extends StdDeserializer<EnumSet<?>> implements ContextualDeserializer {
    private static final long serialVersionUID = 1;
    protected final Class<Enum> _enumClass;
    protected JsonDeserializer<Enum<?>> _enumDeserializer;
    protected final JavaType _enumType;
    protected final Boolean _unwrapSingle;

    public EnumSetDeserializer(JavaType enumType, JsonDeserializer<?> deser) {
        super(EnumSet.class);
        this._enumType = enumType;
        this._enumClass = enumType.getRawClass();
        if (this._enumClass.isEnum()) {
            this._enumDeserializer = deser;
            this._unwrapSingle = null;
            return;
        }
        throw new IllegalArgumentException("Type " + enumType + " not Java Enum type");
    }

    protected EnumSetDeserializer(EnumSetDeserializer base, JsonDeserializer<?> deser, Boolean unwrapSingle) {
        super(EnumSet.class);
        this._enumType = base._enumType;
        this._enumClass = base._enumClass;
        this._enumDeserializer = deser;
        this._unwrapSingle = unwrapSingle;
    }

    public EnumSetDeserializer withDeserializer(JsonDeserializer<?> deser) {
        return this._enumDeserializer == deser ? this : new EnumSetDeserializer(this, deser, this._unwrapSingle);
    }

    public EnumSetDeserializer withResolved(JsonDeserializer<?> deser, Boolean unwrapSingle) {
        return (this._unwrapSingle == unwrapSingle && this._enumDeserializer == deser) ? this : new EnumSetDeserializer(this, deser, unwrapSingle);
    }

    public boolean isCachable() {
        if (this._enumType.getValueHandler() != null) {
            return false;
        }
        return true;
    }

    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        Boolean unwrapSingle = findFormatFeature(ctxt, property, EnumSet.class, Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        JsonDeserializer<?> deser = this._enumDeserializer;
        if (deser == null) {
            deser = ctxt.findContextualValueDeserializer(this._enumType, property);
        } else {
            deser = ctxt.handleSecondaryContextualization(deser, property, this._enumType);
        }
        return withResolved(deser, unwrapSingle);
    }

    public EnumSet<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (!p.isExpectedStartArrayToken()) {
            return handleNonArray(p, ctxt);
        }
        EnumSet<?> result = constructSet();
        while (true) {
            try {
                JsonToken t = p.nextToken();
                if (t == JsonToken.END_ARRAY) {
                    return result;
                }
                if (t == JsonToken.VALUE_NULL) {
                    return (EnumSet) ctxt.handleUnexpectedToken(this._enumClass, p);
                }
                Enum<?> value = (Enum) this._enumDeserializer.deserialize(p, ctxt);
                if (value != null) {
                    result.add(value);
                }
            } catch (Throwable e) {
                throw JsonMappingException.wrapWithPath(e, (Object) result, result.size());
            }
        }
    }

    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }

    private EnumSet constructSet() {
        return EnumSet.noneOf(this._enumClass);
    }

    protected EnumSet<?> handleNonArray(JsonParser p, DeserializationContext ctxt) throws IOException {
        boolean canWrap = this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        if (!canWrap) {
            return (EnumSet) ctxt.handleUnexpectedToken(EnumSet.class, p);
        }
        Object result = constructSet();
        if (p.hasToken(JsonToken.VALUE_NULL)) {
            return (EnumSet) ctxt.handleUnexpectedToken(this._enumClass, p);
        }
        try {
            Enum<?> value = (Enum) this._enumDeserializer.deserialize(p, ctxt);
            if (value != null) {
                result.add(value);
            }
            return result;
        } catch (Throwable e) {
            throw JsonMappingException.wrapWithPath(e, result, result.size());
        }
    }
}
