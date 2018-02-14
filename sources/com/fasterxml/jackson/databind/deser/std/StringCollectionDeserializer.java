package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat.Feature;
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
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import java.util.Collection;

@JacksonStdImpl
public final class StringCollectionDeserializer extends ContainerDeserializerBase<Collection<String>> implements ContextualDeserializer {
    private static final long serialVersionUID = 1;
    protected final JavaType _collectionType;
    protected final JsonDeserializer<Object> _delegateDeserializer;
    protected final Boolean _unwrapSingle;
    protected final JsonDeserializer<String> _valueDeserializer;
    protected final ValueInstantiator _valueInstantiator;

    public StringCollectionDeserializer(JavaType collectionType, JsonDeserializer<?> valueDeser, ValueInstantiator valueInstantiator) {
        this(collectionType, valueInstantiator, null, valueDeser, null);
    }

    protected StringCollectionDeserializer(JavaType collectionType, ValueInstantiator valueInstantiator, JsonDeserializer<?> delegateDeser, JsonDeserializer<?> valueDeser, Boolean unwrapSingle) {
        super(collectionType);
        this._collectionType = collectionType;
        this._valueDeserializer = valueDeser;
        this._valueInstantiator = valueInstantiator;
        this._delegateDeserializer = delegateDeser;
        this._unwrapSingle = unwrapSingle;
    }

    protected final StringCollectionDeserializer withResolved(JsonDeserializer<?> delegateDeser, JsonDeserializer<?> valueDeser, Boolean unwrapSingle) {
        return (this._unwrapSingle == unwrapSingle && this._valueDeserializer == valueDeser && this._delegateDeserializer == delegateDeser) ? this : new StringCollectionDeserializer(this._collectionType, this._valueInstantiator, delegateDeser, valueDeser, unwrapSingle);
    }

    public final boolean isCachable() {
        return this._valueDeserializer == null && this._delegateDeserializer == null;
    }

    public final JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JsonDeserializer<Object> delegate = null;
        if (!(this._valueInstantiator == null || this._valueInstantiator.getDelegateCreator() == null)) {
            delegate = findDeserializer(ctxt, this._valueInstantiator.getDelegateType(ctxt.getConfig()), property);
        }
        JsonDeserializer<?> valueDeser = this._valueDeserializer;
        JavaType valueType = this._collectionType.getContentType();
        if (valueDeser == null) {
            valueDeser = findConvertingContentDeserializer(ctxt, property, valueDeser);
            if (valueDeser == null) {
                valueDeser = ctxt.findContextualValueDeserializer(valueType, property);
            }
        } else {
            valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, valueType);
        }
        Boolean unwrapSingle = findFormatFeature(ctxt, property, Collection.class, Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        if (isDefaultDeserializer(valueDeser)) {
            valueDeser = null;
        }
        return withResolved(delegate, valueDeser, unwrapSingle);
    }

    public final JavaType getContentType() {
        return this._collectionType.getContentType();
    }

    public final JsonDeserializer<Object> getContentDeserializer() {
        return this._valueDeserializer;
    }

    public final Collection<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (this._delegateDeserializer != null) {
            return (Collection) this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
        }
        return deserialize(p, ctxt, (Collection) this._valueInstantiator.createUsingDefault(ctxt));
    }

    public final Collection<String> deserialize(JsonParser p, DeserializationContext ctxt, Collection<String> result) throws IOException {
        if (!p.isExpectedStartArrayToken()) {
            return handleNonArray(p, ctxt, result);
        }
        if (this._valueDeserializer != null) {
            return deserializeUsingCustom(p, ctxt, result, this._valueDeserializer);
        }
        while (true) {
            try {
                String value = p.nextTextValue();
                if (value != null) {
                    result.add(value);
                } else {
                    JsonToken t = p.getCurrentToken();
                    if (t == JsonToken.END_ARRAY) {
                        return result;
                    }
                    if (t != JsonToken.VALUE_NULL) {
                        value = _parseString(p, ctxt);
                    }
                    result.add(value);
                }
            } catch (Throwable e) {
                throw JsonMappingException.wrapWithPath(e, (Object) result, result.size());
            }
        }
    }

    private Collection<String> deserializeUsingCustom(JsonParser p, DeserializationContext ctxt, Collection<String> result, JsonDeserializer<String> deser) throws IOException {
        while (true) {
            String value;
            if (p.nextTextValue() == null) {
                JsonToken t = p.getCurrentToken();
                if (t == JsonToken.END_ARRAY) {
                    return result;
                }
                value = t == JsonToken.VALUE_NULL ? (String) deser.getNullValue(ctxt) : (String) deser.deserialize(p, ctxt);
            } else {
                value = (String) deser.deserialize(p, ctxt);
            }
            result.add(value);
        }
    }

    public final Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }

    private final Collection<String> handleNonArray(JsonParser p, DeserializationContext ctxt, Collection<String> result) throws IOException {
        boolean canWrap = this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        if (!canWrap) {
            return (Collection) ctxt.handleUnexpectedToken(this._collectionType.getRawClass(), p);
        }
        JsonDeserializer<String> valueDes = this._valueDeserializer;
        String value = p.getCurrentToken() == JsonToken.VALUE_NULL ? valueDes == null ? null : (String) valueDes.getNullValue(ctxt) : valueDes == null ? _parseString(p, ctxt) : (String) valueDes.deserialize(p, ctxt);
        result.add(value);
        return result;
    }
}
